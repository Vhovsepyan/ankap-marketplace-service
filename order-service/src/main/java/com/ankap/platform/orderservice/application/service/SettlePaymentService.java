package com.ankap.platform.orderservice.application.service;

import com.ankap.platform.orderservice.application.port.in.SettlePaymentUseCase;
import com.ankap.platform.orderservice.application.port.out.OrderEventPublisherPort;
import com.ankap.platform.orderservice.application.port.out.OrderRepositoryPort;
import com.ankap.platform.orderservice.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class SettlePaymentService implements SettlePaymentUseCase {

    private static final Logger log = LoggerFactory.getLogger(SettlePaymentService.class);

    private final OrderRepositoryPort repositoryPort;
    private final OrderEventPublisherPort eventPublisherPort;

    public SettlePaymentService(OrderRepositoryPort repositoryPort, OrderEventPublisherPort eventPublisherPort) {
        this.repositoryPort = repositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    public void onPaymentCaptured(UUID orderId, UUID paymentId) {
        Optional<Order> found = repositoryPort.findById(orderId);
        if (found.isEmpty()) {
            log.warn("Payment {} captured for unknown order {}", paymentId, orderId);
            return;
        }

        Order order = found.get();
        if (order.isSettled()) {
            log.debug("Order {} is already {}; ignoring duplicate capture of payment {}",
                    orderId, order.getStatus(), paymentId);
            return;
        }

        order.markPaid(paymentId);
        repositoryPort.save(order);
        log.info("Order {} is PAID by LedgerFlow payment {}", orderId, paymentId);
    }

    @Override
    public void onPaymentFailed(UUID orderId, UUID paymentId, String reason) {
        Optional<Order> found = repositoryPort.findById(orderId);
        if (found.isEmpty()) {
            log.warn("Payment {} failed for unknown order {}", paymentId, orderId);
            return;
        }

        Order order = found.get();
        if (order.isSettled()) {
            log.debug("Order {} is already {}; ignoring duplicate failure of payment {}",
                    orderId, order.getStatus(), paymentId);
            return;
        }

        // Read before the cancel: cancelling is what makes the reservation stale, and
        // only an order that actually holds stock should trigger a release.
        boolean holdsStock = order.holdsReservedStock();

        order.cancelForFailedPayment(paymentId);
        repositoryPort.save(order);
        log.warn("Order {} CANCELLED: payment {} failed ({})", orderId, paymentId, reason);

        if (holdsStock) {
            eventPublisherPort.publishInventoryReleaseEvent(order);
        }
    }
}
