package com.ankap.platform.orderservice.service;

import com.ankap.platform.orderservice.client.ProductClient;
import com.ankap.platform.orderservice.dto.OrderRequest;
import com.ankap.platform.orderservice.dto.ProductDto;
import com.ankap.platform.orderservice.entity.Order;
import com.ankap.platform.orderservice.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class OrderManagementService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderManagementService(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    @Transactional
    public Order placeOrder(OrderRequest request, String buyerEmail) {

        ProductDto product;
        try {
            // Ask the Product Service for details
            product = productClient.getProductById(request.productId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found or Product Service is down");
        }

        if (product.stockQuantity() < request.quantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock available");
        }

        BigDecimal totalPrice = product.price().multiply(BigDecimal.valueOf(request.quantity()));

        Order order = new Order();
        order.setProductId(product.id());
        order.setBuyerEmail(buyerEmail);
        order.setQuantity(request.quantity());
        order.setTotalPrice(totalPrice);
        order.setStatus("PENDING");

        return orderRepository.save(order);
    }
}