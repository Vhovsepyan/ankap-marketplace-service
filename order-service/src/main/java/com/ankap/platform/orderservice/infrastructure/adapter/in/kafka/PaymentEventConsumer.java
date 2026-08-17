package com.ankap.platform.orderservice.infrastructure.adapter.in.kafka;

import com.ankap.platform.orderservice.application.port.in.PayForOrderUseCase;
import com.ankap.platform.orderservice.application.port.in.SettlePaymentUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Consumes LedgerFlow's published payment outcomes.
 *
 * This is what makes a pending capture resolvable: when the provider call timed
 * out, LedgerFlow's verification job settles the payment later and publishes the
 * result here, so an order never has to guess whether money moved.
 *
 * The order id travels as LedgerFlow's `merchantRef`, which is what this service
 * set when creating the payment.
 */
@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private static final String EVENT_TYPE_HEADER = "event-type";
    private static final String PAYMENT_CAPTURED = "payment.captured";
    private static final String PAYMENT_FAILED = "payment.failed";
    private static final String PAYMENT_AUTHORIZED = "payment.authorized";

    private final SettlePaymentUseCase settlePaymentUseCase;
    private final PayForOrderUseCase payForOrderUseCase;
    private final ObjectMapper objectMapper;

    public PaymentEventConsumer(SettlePaymentUseCase settlePaymentUseCase,
                                PayForOrderUseCase payForOrderUseCase,
                                ObjectMapper objectMapper) {
        this.settlePaymentUseCase = settlePaymentUseCase;
        this.payForOrderUseCase = payForOrderUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "${ledgerflow.payment-events-topic:payment-events}",
            groupId = "order-service-payments",
            containerFactory = "paymentEventListenerContainerFactory")
    public void handlePaymentEvent(ConsumerRecord<String, String> record) throws Exception {
        String eventType = headerValue(record, EVENT_TYPE_HEADER);
        if (eventType == null) {
            log.warn("Dropping a payment event with no {} header (payment {})", EVENT_TYPE_HEADER, record.key());
            return;
        }

        JsonNode event = objectMapper.readTree(record.value());
        String merchantRef = event.path("merchantRef").asText(null);
        if (merchantRef == null || merchantRef.isBlank()) {
            log.warn("Payment event {} carries no merchantRef; it did not come from this marketplace", eventType);
            return;
        }

        UUID orderId;
        try {
            orderId = UUID.fromString(merchantRef);
        } catch (IllegalArgumentException notOurs) {
            log.debug("Ignoring payment event {} for merchantRef '{}', which is not an order id",
                    eventType, merchantRef);
            return;
        }

        UUID paymentId = UUID.fromString(event.path("paymentId").asText());

        switch (eventType) {
            case PAYMENT_CAPTURED -> settlePaymentUseCase.onPaymentCaptured(orderId, paymentId);
            case PAYMENT_FAILED -> settlePaymentUseCase.onPaymentFailed(
                    orderId, paymentId, event.path("reason").asText("payment failed"));
            // Nothing captures on LedgerFlow's side, so an authorization that was
            // resolved after a timeout would sit on held funds forever if this did
            // not finish it. When the synchronous flow already captured, the order
            // is settled and this is a no-op.
            case PAYMENT_AUTHORIZED -> payForOrderUseCase.captureAuthorized(orderId, paymentId);
            default -> log.debug("Ignoring unknown payment event type {}", eventType);
        }
    }

    private String headerValue(ConsumerRecord<String, String> record, String name) {
        Header header = record.headers().lastHeader(name);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }
}
