package com.ankap.platform.orderservice.controller;

import com.ankap.platform.orderservice.dto.OrderRequest;
import com.ankap.platform.orderservice.entity.Order;
import com.ankap.platform.orderservice.service.OrderManagementService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderManagementService orderManagementService;

    public OrderController(OrderManagementService orderManagementService) {
        this.orderManagementService = orderManagementService;
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request, @AuthenticationPrincipal Jwt jwt) {
        String buyerEmail = jwt.getSubject();
        return orderManagementService.placeOrder(request, buyerEmail);
    }
}