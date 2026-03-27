package com.ankap.platform.orderservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(UUID id, String name, BigDecimal price, Integer availableQuantity) {
}