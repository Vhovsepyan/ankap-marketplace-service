package com.ankap.platform.orderservice.infrastructure.adapter.out.feign;

/**
 * Amounts cross LedgerFlow's API as minor units plus a currency code, never as
 * decimals.
 */
public record CreatePaymentRequestDto(long amountMinor, String currency, String merchantRef) {
}
