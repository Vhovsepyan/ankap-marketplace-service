package com.ankap.platform.orderservice.infrastructure.adapter.out.feign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

/**
 * Only the fields this service acts on. LedgerFlow's schema is additive-only, so
 * unknown fields are ignored rather than treated as a breaking change.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LedgerFlowPaymentDto(UUID id, String status, String failureReason) {
}
