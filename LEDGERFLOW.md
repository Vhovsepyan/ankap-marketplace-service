# Paying for orders through LedgerFlow

The marketplace does not move money itself. When an order's stock has been
reserved, order-service charges the buyer through
[LedgerFlow](../ledgerflow) — a payment orchestration service that authorizes and
captures against a provider and records every movement as double-entry
bookkeeping. The marketplace never writes to LedgerFlow's database; it talks to
its REST API and listens to the events it publishes.

## The flow

```
POST /api/orders
  │  price and stock checked over Feign, order saved PENDING
  └─ order-events ─────────────────> product-service deducts stock
                                        │
        ┌───────────────────────────────┴─────────────────┐
        │ inventory-reserved-topic       inventory-failed-topic
        ▼                                                 ▼
   order STOCK_RESERVED                              order CANCELLED
        │                                     (nothing was deducted,
        │  POST /v1/payments                   so nothing to release)
        │  POST /v1/payments/{id}/authorize
        │  POST /v1/payments/{id}/capture
        ▼
   ┌────────────────┬──────────────────────┬─────────────────────┐
   │ 200 CAPTURED   │ 202 Accepted         │ 200 FAILED          │
   ▼                ▼                      ▼
 order PAID    order AWAITING_PAYMENT   order CANCELLED
                    │                       └─ inventory-release-topic
                    │                            └─ stock handed back
                    │  LedgerFlow's verification job settles it later
                    └─ payment-events ─> order PAID or CANCELLED
```

`AWAITING_PAYMENT` is the state that matters. When a provider call times out,
the outcome is genuinely unknown — LedgerFlow refuses to guess, resolves it with
its own verification job, and publishes the result. The order waits for that
event instead of assuming either way.

One consequence is easy to miss: when the resolved outcome is
`payment.authorized`, the funds are held but **nothing has captured them**, and
LedgerFlow never captures on its own — capture is the client's move. So
`PaymentEventConsumer` treats `payment.authorized` as a signal to capture, which
is what finishes an order whose authorization was resolved after the fact.
Without it, such an order would sit in `AWAITING_PAYMENT` on held funds forever.

## Order statuses

| Status | Meaning |
|--------|---------|
| `PENDING` | Recorded; stock not deducted yet |
| `STOCK_RESERVED` | product-service deducted the stock; payment not started |
| `AWAITING_PAYMENT` | A LedgerFlow payment exists, outcome not in yet |
| `PAID` | LedgerFlow captured the money. Terminal |
| `CANCELLED` | Terminal. Reserved stock, if any, has been released |

## How the two are wired

| Concern | Choice |
|---------|--------|
| Driving the payment | Feign, `LedgerFlowClient`, same pattern as `ProductClient` |
| Learning the outcome | Kafka `payment-events`, consumed by `PaymentEventConsumer` |
| Correlation | The order id is LedgerFlow's `merchantRef` |
| Idempotency | The order id is the `Idempotency-Key`, so a redelivered event replays the same payment instead of creating a second one |
| Amounts | `totalPrice` is converted to minor units using the currency's fraction digits; LedgerFlow never takes decimals |
| Merchant | `ledgerflow.merchant-id`, seeded into LedgerFlow by the `ledgerflow-merchant-seed` container |

The two share this stack's Kafka broker — that is what lets order-service
consume `payment-events` directly. LedgerFlow keeps its own Postgres 17
(`ledgerflow-postgres`, published on 5433) because its schema is its own.

## Running it

LedgerFlow is built from `../ledgerflow`, so both repos must be checked out
side by side.

```bash
docker compose up -d --build
```

Ports: gateway 8080, product 8082, order 8083, **ledgerflow 8085**,
**mock-psp 9091**, ledgerflow's Postgres 5433.

### Port conflict with LedgerFlow's own compose

LedgerFlow's repo has its own `compose.yml` that publishes Postgres on 5432 and
Kafka on 9092 — the same host ports this stack uses. Both cannot run at once.
When running the integrated stack, stop the standalone one first:

```bash
docker compose -p ledgerflow -f ../ledgerflow/compose.yml down
```

### The mock provider fails on purpose

`mock-psp` declines 10% of calls, errors on 10% and times out on 10% by default.
That is what makes `AWAITING_PAYMENT` and the release compensation observable.
To make the happy path deterministic while testing:

```bash
curl -X POST http://localhost:9091/admin/chaos \
  -H 'Content-Type: application/json' \
  -d '{"declineRate":0,"errorRate":0,"timeoutRate":0}'
```

## Trying it end to end

```bash
# 1. A token. Any HS256 JWT signed with the shared dev secret works.
#    (scripts/demo-order.ps1 mints one and runs all of this.)

# 2. Add a product
curl -X POST http://localhost:8082/api/products \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"sku":"SKU-1","name":"Widget","price":49.99,"availableQuantity":10,"category":"tools"}'

# 3. Order it
curl -X POST http://localhost:8083/api/orders \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"productId":"<id from step 2>","quantity":2}'

# 4. Watch it settle: PENDING -> STOCK_RESERVED -> PAID
#    and the money land in LedgerFlow's ledger
curl http://localhost:8085/v1/payments/<paymentId>
```

## Two things worth knowing before changing this

**Feign's circuit breaker has a one-second time limit by default.** Enabling
`spring.cloud.openfeign.circuitbreaker.enabled` wraps every call in a
Resilience4j `TimeLimiter` whose default `timeout-duration` is 1s. A payment is
not a fast call — LedgerFlow retries the provider up to three times with backoff
before answering — so the default cancelled calls that were about to succeed and
left payments stranded mid-flight. `resilience4j.timelimiter.configs.default` in
`application.properties` raises it to 20s. Removing that puts the bug back.

**A payment step cannot always be retried.** Re-driving `authorize` on a payment
that is already `AUTHORIZATION_PENDING` gets a `409 invalid-state-transition`,
because LedgerFlow's state machine refuses the move. `FeignPaymentAdapter.drive`
handles this by re-reading the payment: pending or terminal states are mapped to
an outcome, and anything still drivable rethrows so the consumer's retry can try
again.

## Known gaps

- If LedgerFlow is unreachable, the order stays `STOCK_RESERVED` after the
  consumer's retries are exhausted. That is deliberate — an order is not
  cancelled on the strength of a network error — but nothing sweeps those
  orders up yet.
- Refunds are not wired. LedgerFlow supports `REFUNDED`, and a cancelled order
  that was already `PAID` should trigger one; today it cannot happen because
  cancellation only precedes capture.
- The saga has no timeout of its own. It relies on LedgerFlow eventually
  publishing an outcome for every payment it accepted.
