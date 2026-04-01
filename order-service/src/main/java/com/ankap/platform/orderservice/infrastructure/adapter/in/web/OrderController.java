package com.ankap.platform.orderservice.infrastructure.adapter.in.web;

import com.ankap.platform.orderservice.application.port.in.PlaceOrderCommand;
import com.ankap.platform.orderservice.application.port.in.PlaceOrderUseCase;
import com.ankap.platform.orderservice.domain.Order;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;

    public OrderController(PlaceOrderUseCase placeOrderUseCase) {
        this.placeOrderUseCase = placeOrderUseCase;
    }

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request, @AuthenticationPrincipal Jwt jwt) {
        String buyerEmail = jwt.getSubject();
        PlaceOrderCommand command = new PlaceOrderCommand(request.productId(), request.quantity(), buyerEmail);
        return placeOrderUseCase.placeOrder(command);
    }
}