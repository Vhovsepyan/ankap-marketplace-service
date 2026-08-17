package com.ankap.platform.orderservice.application.port.out;

import java.util.UUID;

/**
 * What LedgerFlow said when we tried to charge.
 *
 * PENDING is the important one: the provider call timed out and the outcome is
 * genuinely unknown. LedgerFlow resolves it with its own verification job and
 * publishes the result, so the order waits for the event rather than guessing.
 */
public record PaymentAttempt(UUID paymentId, Outcome outcome, String failureReason) {

    public enum Outcome {
        CAPTURED,
        PENDING,
        FAILED
    }

    public static PaymentAttempt captured(UUID paymentId) {
        return new PaymentAttempt(paymentId, Outcome.CAPTURED, null);
    }

    public static PaymentAttempt pending(UUID paymentId) {
        return new PaymentAttempt(paymentId, Outcome.PENDING, null);
    }

    public static PaymentAttempt failed(UUID paymentId, String reason) {
        return new PaymentAttempt(paymentId, Outcome.FAILED, reason);
    }
}
