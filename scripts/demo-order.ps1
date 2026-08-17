# End-to-end demo: add a product, order it, and watch the money move through
# LedgerFlow. Mints its own HS256 token with the shared dev secret.
#
#   pwsh scripts/demo-order.ps1            # deterministic: chaos off
#   pwsh scripts/demo-order.ps1 -Chaos     # leave mock-psp failing on purpose
param(
    [switch]$Chaos,
    [int]$Quantity = 2,
    [string]$ProductUrl = "http://localhost:8082",
    [string]$OrderUrl = "http://localhost:8083",
    [string]$LedgerFlowUrl = "http://localhost:8085",
    [string]$MockPspUrl = "http://localhost:9091"
)

$ErrorActionPreference = "Stop"

function ConvertTo-B64Url([byte[]]$bytes) {
    [Convert]::ToBase64String($bytes).TrimEnd('=').Replace('+', '-').Replace('/', '_')
}

function New-Token {
    $secret = "AnkapMarketplaceSuperSecretKey32"
    $now = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
    $header = '{"alg":"HS256","typ":"JWT"}'
    $payload = "{`"sub`":`"demo-buyer@ankap.com`",`"roles`":[`"ADMIN`"],`"iat`":$now,`"exp`":$($now + 3600)}"
    $h = ConvertTo-B64Url ([Text.Encoding]::UTF8.GetBytes($header))
    $p = ConvertTo-B64Url ([Text.Encoding]::UTF8.GetBytes($payload))
    $hmac = [Security.Cryptography.HMACSHA256]::new([Text.Encoding]::UTF8.GetBytes($secret))
    $sig = ConvertTo-B64Url $hmac.ComputeHash([Text.Encoding]::UTF8.GetBytes("$h.$p"))
    "$h.$p.$sig"
}

if (-not $Chaos) {
    Write-Host "Turning mock-psp chaos off so the happy path is deterministic..." -ForegroundColor DarkGray
    Invoke-RestMethod -Uri "$MockPspUrl/admin/chaos" -Method Post -ContentType "application/json" `
        -Body '{"declineRate":0,"errorRate":0,"timeoutRate":0}' | Out-Null
}

$headers = @{ Authorization = "Bearer $(New-Token)"; "Content-Type" = "application/json" }

Write-Host "`n=== 1. Add a product ===" -ForegroundColor Cyan
$sku = "DEMO-$([Guid]::NewGuid().ToString('N').Substring(0,8))"
$product = Invoke-RestMethod -Uri "$ProductUrl/api/products" -Method Post -Headers $headers -Body (@{
    sku = $sku; name = "LedgerFlow Demo Widget"; price = 24.50
    availableQuantity = 10; category = "demo"
} | ConvertTo-Json)
$product | ConvertTo-Json
$stockBefore = $product.availableQuantity

Write-Host "`n=== 2. Place an order for $Quantity ===" -ForegroundColor Cyan
$order = Invoke-RestMethod -Uri "$OrderUrl/api/orders" -Method Post -Headers $headers -Body (@{
    productId = $product.id; quantity = $Quantity
} | ConvertTo-Json)
$order | ConvertTo-Json
$orderId = $order.id
$expectedTotal = [decimal]$product.price * $Quantity
if ([decimal]$order.totalPrice -ne $expectedTotal) {
    throw "FAIL: totalPrice is $($order.totalPrice), expected $expectedTotal"
}

Write-Host "`n=== 3. Wait for the saga to settle ===" -ForegroundColor Cyan
$deadline = (Get-Date).AddSeconds(90)
do {
    Start-Sleep -Seconds 2
    $row = docker exec ankap-postgres psql -U admin -d order_db -t -A -F '|' `
        -c "select status, coalesce(payment_id::text,'-') from orders where id='$orderId';"
    $parts = "$row".Trim() -split '\|'
    $status = $parts[0]; $paymentId = $parts[1]
    Write-Host "  status: $status  payment: $paymentId"
} while ($status -notin @("PAID", "CANCELLED") -and (Get-Date) -lt $deadline)

Write-Host "`n=== 4. The payment in LedgerFlow ===" -ForegroundColor Cyan
if ($paymentId -ne '-') {
    $payment = Invoke-RestMethod -Uri "$LedgerFlowUrl/v1/payments/$paymentId"
    $payment | ConvertTo-Json
    if ($payment.merchantRef -ne $orderId) {
        throw "FAIL: merchantRef '$($payment.merchantRef)' does not correlate to order $orderId"
    }
    Write-Host "PASS: merchantRef correlates back to the order" -ForegroundColor Green
    $expectedMinor = [long][Math]::Round($expectedTotal * 100)
    if ($payment.amountMinor -ne $expectedMinor) {
        throw "FAIL: amountMinor is $($payment.amountMinor), expected $expectedMinor"
    }
    Write-Host "PASS: $expectedTotal charged as $($payment.amountMinor) minor units" -ForegroundColor Green
} else {
    Write-Host "No payment was created." -ForegroundColor Yellow
}

Write-Host "`n=== 5. The double-entry ledger ===" -ForegroundColor Cyan
docker exec ankap-ledgerflow-postgres psql -U ledgerflow -d ledgerflow -c @"
select a.account_key, a.account_type, e.amount_minor, e.currency
from ledger_entry e
join ledger_account a on a.id = e.account_id
join ledger_transaction t on t.id = e.transaction_id
where t.source_id = '$paymentId'
order by e.amount_minor desc;
"@
# Positive is a debit, negative a credit, so a balanced transaction sums to zero.
$balance = docker exec ankap-ledgerflow-postgres psql -U ledgerflow -d ledgerflow -t -A -c @"
select coalesce(sum(e.amount_minor), 0)
from ledger_entry e join ledger_transaction t on t.id = e.transaction_id
where t.source_id = '$paymentId';
"@
if ("$balance".Trim() -ne "0") { throw "FAIL: the ledger entries sum to $balance, not 0 - the books do not balance" }
Write-Host "PASS: debits and credits balance to 0" -ForegroundColor Green

Write-Host "`n=== 6. Stock after the order ===" -ForegroundColor Cyan
$after = Invoke-RestMethod -Uri "$ProductUrl/api/products/$($product.id)"
$expectedStock = if ($status -eq "PAID") { $stockBefore - $Quantity } else { $stockBefore }
Write-Host "  available: $($after.availableQuantity)  (expected $expectedStock for a $status order)"
if ($after.availableQuantity -ne $expectedStock) {
    throw "FAIL: stock is $($after.availableQuantity), expected $expectedStock"
}
Write-Host "PASS: stock matches the order outcome" -ForegroundColor Green

Write-Host "`nOrder $orderId finished as $status" -ForegroundColor Green
