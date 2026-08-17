package com.ankap.platform.orderservice.infrastructure.adapter.out.feign;

import com.ankap.platform.orderservice.application.port.out.PaymentAttempt;
import com.ankap.platform.orderservice.application.port.out.PaymentPort;
import com.ankap.platform.orderservice.domain.Order;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class FeignPaymentAdapter implements PaymentPort {

    private static final Logger log = LoggerFactory.getLogger(FeignPaymentAdapter.class);

    private static final String STATUS_AUTHORIZED = "AUTHORIZED";
    private static final String STATUS_CAPTURED = "CAPTURED";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_CANCELED = "CANCELED";
    private static final String STATUS_AUTHORIZATION_PENDING = "AUTHORIZATION_PENDING";
    private static final String STATUS_CAPTURE_PENDING = "CAPTURE_PENDING";

    private final LedgerFlowClient ledgerFlowClient;
    private final UUID merchantId;
    private final Currency currency;

    public FeignPaymentAdapter(LedgerFlowClient ledgerFlowClient,
                               @Value("${ledgerflow.merchant-id}") UUID merchantId,
                               @Value("${ledgerflow.currency:USD}") String currencyCode) {
        this.ledgerFlowClient = ledgerFlowClient;
        this.merchantId = merchantId;
        this.currency = Currency.getInstance(currencyCode);
    }

    @Override
    public PaymentAttempt charge(Order order) {
        // The order id is a stable idempotency key: a redelivered inventory-reserved
        // event replays the same create and gets the same payment back, never a second one.
        String idempotencyKey = order.getId().toString();

        LedgerFlowPaymentDto created = ledgerFlowClient.create(
                merchantId,
                idempotencyKey,
                new CreatePaymentRequestDto(
                        toMinorUnits(order.getTotalPrice()),
                        currency.getCurrencyCode(),
                        order.getId().toString()));

        UUID paymentId = created.id();
        log.info("Created LedgerFlow payment {} for order {} ({} {})",
                paymentId, order.getId(), order.getTotalPrice(), currency.getCurrencyCode());

        // A replayed create can hand back a payment that already moved past CREATED.
        PaymentAttempt settled = settledOutcome(paymentId, created.status(), created.failureReason());
        if (settled != null) {
            return settled;
        }

        if (!STATUS_AUTHORIZED.equals(created.status())) {
            PaymentAttempt authorizeOutcome = drive(paymentId, () -> ledgerFlowClient.authorize(paymentId));
            if (authorizeOutcome != null) {
                return authorizeOutcome;
            }
        }

        PaymentAttempt captureOutcome = drive(paymentId, () -> ledgerFlowClient.capture(paymentId));
        if (captureOutcome != null) {
            return captureOutcome;
        }

        // Capture answered 200 with a status that is neither CAPTURED nor a failure.
        // Nothing is owed and nothing is settled: wait for the event rather than guess.
        log.warn("Capture of payment {} for order {} returned an unsettled status; waiting for the event",
                paymentId, order.getId());
        return PaymentAttempt.pending(paymentId);
    }

    @Override
    public PaymentAttempt capture(UUID paymentId) {
        PaymentAttempt outcome = drive(paymentId, () -> ledgerFlowClient.capture(paymentId));
        return outcome != null ? outcome : PaymentAttempt.pending(paymentId);
    }

    /**
     * Runs one step of the payment and, if LedgerFlow rejects it, asks what state the
     * payment is actually in.
     *
     * A step is rejected with 409 when the payment cannot make that transition — which
     * is exactly what happens after an earlier attempt left it AUTHORIZATION_PENDING or
     * CAPTURE_PENDING. Re-driving is impossible and also unnecessary: LedgerFlow will
     * resolve it and publish the outcome. Any other state is still drivable, so the
     * exception is rethrown and the consumer's retry gets another go.
     */
    private PaymentAttempt drive(UUID paymentId, Supplier<ResponseEntity<LedgerFlowPaymentDto>> call) {
        try {
            return step(paymentId, call.get());
        } catch (FeignException rejected) {
            LedgerFlowPaymentDto current = ledgerFlowClient.get(paymentId);
            PaymentAttempt settled = settledOutcome(paymentId, current.status(), current.failureReason());
            if (settled != null) {
                log.info("Payment {} had already settled as {}", paymentId, current.status());
                return settled;
            }
            if (isPending(current.status())) {
                log.info("Payment {} is {} and cannot be re-driven; waiting for the event",
                        paymentId, current.status());
                return PaymentAttempt.pending(paymentId);
            }
            throw rejected;
        }
    }

    private boolean isPending(String status) {
        return STATUS_AUTHORIZATION_PENDING.equals(status) || STATUS_CAPTURE_PENDING.equals(status);
    }

    /**
     * @return the outcome when the call settled the payment or left it pending,
     *         or null when the payment moved forward and the next step should run.
     */
    private PaymentAttempt step(UUID paymentId, ResponseEntity<LedgerFlowPaymentDto> response) {
        if (response.getStatusCode() == HttpStatus.ACCEPTED) {
            log.info("Payment {} is pending at the provider; LedgerFlow will resolve it and publish the outcome",
                    paymentId);
            return PaymentAttempt.pending(paymentId);
        }

        LedgerFlowPaymentDto body = response.getBody();
        if (body == null) {
            log.warn("Payment {} returned {} with no body; waiting for the event",
                    paymentId, response.getStatusCode());
            return PaymentAttempt.pending(paymentId);
        }

        return settledOutcome(paymentId, body.status(), body.failureReason());
    }

    private PaymentAttempt settledOutcome(UUID paymentId, String status, String failureReason) {
        if (STATUS_CAPTURED.equals(status)) {
            return PaymentAttempt.captured(paymentId);
        }
        if (STATUS_FAILED.equals(status) || STATUS_CANCELED.equals(status)) {
            return PaymentAttempt.failed(paymentId,
                    failureReason != null ? failureReason : "Payment " + status);
        }
        return null;
    }

    private long toMinorUnits(BigDecimal amount) {
        return amount.movePointRight(currency.getDefaultFractionDigits())
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }
}
