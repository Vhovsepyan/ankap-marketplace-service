package com.ankap.platform.productservice.application.port.in;

import java.util.UUID;

public record ReleaseInventoryCommand(
        UUID orderId,
        UUID productId,
        int quantity
) {}
