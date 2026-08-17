package com.ankap.platform.orderservice.application.port.in;

import java.util.UUID;

public interface PayForOrderUseCase {

    /** Charges an order whose stock has just been reserved. */
    void payFor(UUID orderId);

    /**
     * Captures an authorization that LedgerFlow resolved after the fact.
     *
     * When an authorize call times out, LedgerFlow settles it later and announces
     * `payment.authorized`. The funds are held at that point but nothing has moved,
     * and only the client can capture — so this is what finishes such an order.
     */
    void captureAuthorized(UUID orderId, UUID paymentId);
}
