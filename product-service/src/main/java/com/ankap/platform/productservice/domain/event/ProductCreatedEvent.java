package com.ankap.platform.productservice.domain.event;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductCreatedEvent(UUID id,
                                  String sku,
                                  String name,
                                  BigDecimal price,
                                  int availableQuantity,
                                  String category,
                                  Long version,
                                  Boolean isDeleted) {
}
