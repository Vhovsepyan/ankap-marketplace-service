package com.ankap.platform.productservice.domain.event;

import java.util.UUID;

public record ProductDeletedEvent(UUID productId) {
}