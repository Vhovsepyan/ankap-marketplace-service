package com.ankap.platform.orderservice.dto;

public record OrderRequest(Long productId, Integer quantity) {
}