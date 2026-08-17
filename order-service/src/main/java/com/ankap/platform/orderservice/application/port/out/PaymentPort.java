package com.ankap.platform.orderservice.application.port.out;

import com.ankap.platform.orderservice.domain.Order;

import java.util.UUID;

public interface PaymentPort {

    /**
     * Takes the money for an order: create, authorize, capture.
     *
     * Never throws for a declined payment — a decline is an outcome, not an error.
     * It throws only when the payment provider could not be reached at all.
     */
    PaymentAttempt charge(Order order);

    /**
     * Captures a payment that is already authorized.
     *
     * Needed when an authorization whose outcome was unknown is resolved by
     * LedgerFlow after the fact: the money is held but nothing has captured it,
     * and capture is the client's move to make.
     */
    PaymentAttempt capture(UUID paymentId);
}
