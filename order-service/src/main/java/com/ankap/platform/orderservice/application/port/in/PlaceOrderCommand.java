package com.ankap.platform.orderservice.application.port.in;

import java.util.UUID;

public record PlaceOrderCommand(UUID productId, Integer quantity, String buyerEmail) {}