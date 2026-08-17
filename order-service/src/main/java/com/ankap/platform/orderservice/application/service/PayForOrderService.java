package com.ankap.platform.orderservice.application.service;

import com.ankap.platform.orderservice.application.port.in.PayForOrderUseCase;
import com.ankap.platform.orderservice.application.port.in.SettlePaymentUseCase;
import com.ankap.platform.orderservice.application.port.out.OrderRepositoryPort;
import com.ankap.platform.orderservice.application.port.out.PaymentAttempt;
import com.ankap.platform.orderservice.application.port.out.PaymentPort;
import com.ankap.platform.orderservice.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PayForOrderService implements PayForOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(PayForOrderService.class);

    private final OrderRepositoryPort repositoryPort;
    private final PaymentPort paymentPort;
    private final SettlePaymentUseCase settlePaymentUseCase;

    public PayForOrderService(OrderRepositoryPort repositoryPort,
                              PaymentPort paymentPort,
                              SettlePaymentUseCase settlePaymentUseCase) {
        this.repositoryPort = repositoryPort;
        this.paymentPort = paymentPort;
        this.settlePaymentUseCase = settlePaymentUseCase;
    }

    @Override
    public void payFor(UUID orderId) {
        Order order = repositoryPort.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.isSettled()) {
            log.info("Order {} is already {}; ignoring a repeated inventory-reserved event",
                    orderId, order.getStatus());
            return;
        }

        order.markStockReserved();
        Order reserved = repositoryPort.save(order);

        // A failure here means LedgerFlow could not be reached at all. The stock stays
        // reserved and the order stays STOCK_RESERVED rather than being cancelled on
        // the strength of a network error, and the exception is left to the consumer.
        PaymentAttempt attempt = paymentPort.charge(reserved);

        switch (attempt.outcome()) {
            case CAPTURED -> settlePaymentUseCase.onPaymentCaptured(orderId, attempt.paymentId());
            case FAILED -> settlePaymentUseCase.onPaymentFailed(orderId, attempt.paymentId(), attempt.failureReason());
            case PENDING -> {
                reserved.markAwaitingPayment(attempt.paymentId());
                repositoryPort.save(reserved);
                log.info("Order {} is awaiting payment {}; the outcome will arrive on payment-events",
                        orderId, attempt.paymentId());
            }
        }
    }

    @Override
    public void captureAuthorized(UUID orderId, UUID paymentId) {
        Order order = repositoryPort.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (order.isSettled()) {
            log.debug("Order {} is already {}; nothing to capture", orderId, order.getStatus());
            return;
        }

        log.info("Capturing authorization {} for order {}", paymentId, orderId);
        PaymentAttempt attempt = paymentPort.capture(paymentId);

        switch (attempt.outcome()) {
            case CAPTURED -> settlePaymentUseCase.onPaymentCaptured(orderId, paymentId);
            case FAILED -> settlePaymentUseCase.onPaymentFailed(orderId, paymentId, attempt.failureReason());
            case PENDING -> {
                order.markAwaitingPayment(paymentId);
                repositoryPort.save(order);
                log.info("Capture of payment {} is pending; waiting for the event", paymentId);
            }
        }
    }
}
