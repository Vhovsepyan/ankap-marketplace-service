package com.ankap.platform.orderservice.dto;

public record OrderPlacedEvent(Long orderId, Long productId, Integer quantity) {
}