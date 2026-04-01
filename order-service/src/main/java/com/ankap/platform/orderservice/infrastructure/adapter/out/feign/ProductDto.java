package com.ankap.platform.orderservice.infrastructure.adapter.out.feign;
import java.math.BigDecimal;
import java.util.UUID;
public record ProductDto(UUID id, BigDecimal price, int availableQuantity) {}