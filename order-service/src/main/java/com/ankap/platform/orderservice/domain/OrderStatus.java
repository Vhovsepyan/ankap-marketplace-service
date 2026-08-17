package com.ankap.platform.orderservice.domain;

/**
 * Stored as a string on the order row, so the values are the wire format.
 *
 * PENDING ──> STOCK_RESERVED ──> AWAITING_PAYMENT ──> PAID
 *    │              │                    │
 *    └──────────────┴────────────────────┴──> CANCELLED
 */
public final class OrderStatus {

    /** Recorded; stock not deducted yet. */
    public static final String PENDING = "PENDING";
    /** product-service deducted the stock; payment not started. */
    public static final String STOCK_RESERVED = "STOCK_RESERVED";
    /** A LedgerFlow payment exists and its outcome is not in yet. */
    public static final String AWAITING_PAYMENT = "AWAITING_PAYMENT";
    /** LedgerFlow captured the money. Terminal. */
    public static final String PAID = "PAID";
    /** Terminal. Stock, if it was reserved, has been released. */
    public static final String CANCELLED = "CANCELLED";

    private OrderStatus() {
    }
}
