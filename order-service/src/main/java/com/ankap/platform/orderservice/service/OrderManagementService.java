package com.ankap.platform.orderservice.service;

import com.ankap.platform.orderservice.client.ProductClient;
import com.ankap.platform.orderservice.dto.OrderPlacedEvent; // <-- NEW
import com.ankap.platform.orderservice.dto.OrderRequest;
import com.ankap.platform.orderservice.dto.ProductDto;
import com.ankap.platform.orderservice.entity.Order;
import com.ankap.platform.orderservice.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate; // <-- NEW
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class OrderManagementService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderManagementService(OrderRepository orderRepository, ProductClient productClient, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public Order placeOrder(OrderRequest request, String buyerEmail) {

        ProductDto product = productClient.getProductById(request.productId());

        if (product.availableQuantity() < request.quantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock");
        }

        BigDecimal totalPrice = product.price().multiply(BigDecimal.valueOf(request.quantity()));

        Order order = new Order();
        order.setProductId(product.id());
        order.setBuyerEmail(buyerEmail);
        order.setQuantity(request.quantity());
        order.setTotalPrice(totalPrice);
        order.setStatus("PENDING");

        Order savedOrder = orderRepository.save(order);

        OrderPlacedEvent event = new OrderPlacedEvent(savedOrder.getId(), savedOrder.getProductId(), savedOrder.getQuantity());
        kafkaTemplate.send("order-events", event);

        return savedOrder;
    }
}