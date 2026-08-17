package com.ankap.platform.orderservice.infrastructure.adapter.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

/**
 * LedgerFlow's payment API. authorize and capture answer 200 when the payment
 * settled and 202 when the outcome is not in yet, so both return ResponseEntity —
 * the status code carries meaning the body does not.
 */
@FeignClient(name = "ledgerflow", url = "${ledgerflow.url}")
public interface LedgerFlowClient {

    @PostMapping("/v1/payments")
    LedgerFlowPaymentDto create(@RequestHeader("X-Merchant-Id") UUID merchantId,
                                @RequestHeader("Idempotency-Key") String idempotencyKey,
                                @RequestBody CreatePaymentRequestDto request);

    @PostMapping("/v1/payments/{paymentId}/authorize")
    ResponseEntity<LedgerFlowPaymentDto> authorize(@PathVariable("paymentId") UUID paymentId);

    @PostMapping("/v1/payments/{paymentId}/capture")
    ResponseEntity<LedgerFlowPaymentDto> capture(@PathVariable("paymentId") UUID paymentId);

    @GetMapping("/v1/payments/{paymentId}")
    LedgerFlowPaymentDto get(@PathVariable("paymentId") UUID paymentId);
}
