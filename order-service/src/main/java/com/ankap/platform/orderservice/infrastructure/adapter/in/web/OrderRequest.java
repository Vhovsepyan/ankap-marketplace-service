package com.ankap.platform.orderservice.infrastructure.adapter.in.web;

import java.util.UUID;

public record OrderRequest(UUID productId, Integer quantity) {}