package com.ankap.platform.productservice.dto;

public record OrderPlacedEvent(Long orderId, Long productId, Integer quantity) {
}