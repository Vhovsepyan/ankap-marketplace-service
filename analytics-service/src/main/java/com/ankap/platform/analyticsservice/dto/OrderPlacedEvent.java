package com.ankap.platform.analyticsservice.dto;

public record OrderPlacedEvent(Long orderId, Long productId, Integer quantity) {
}