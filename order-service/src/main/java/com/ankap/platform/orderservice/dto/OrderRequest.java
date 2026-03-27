package com.ankap.platform.orderservice.dto;

import java.util.UUID;

public record OrderRequest(UUID productId, Integer quantity) {
}