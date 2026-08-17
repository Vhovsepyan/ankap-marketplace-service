package com.ankap.platform.orderservice.application.port.in;

import java.util.UUID;

/**
 * Applies a final payment outcome to an order. Both the synchronous reply from
 * LedgerFlow and the payment-events topic land here, and Kafka is at-least-once,
 * so every method must be safe to call more than once for the same order.
 */
public interface SettlePaymentUseCase {

    void onPaymentCaptured(UUID orderId, UUID paymentId);

    void onPaymentFailed(UUID orderId, UUID paymentId, String reason);
}
