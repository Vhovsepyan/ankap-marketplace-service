package com.ankap.platform.productservice.service;

import com.ankap.platform.productservice.dto.OrderPlacedEvent;
import com.ankap.platform.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final ProductRepository productRepository;

    public OrderEventConsumer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 🎧 LISTENS TO THE KAFKA TOPIC
    @KafkaListener(topics = "order-events", groupId = "product-service-group")
    @Transactional
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for Order ID: {} and Product ID: {}", event.orderId(), event.productId());

        productRepository.findById(event.productId()).ifPresentOrElse(product -> {
            
            // Deduct the stock
            int newStock = product.getStockQuantity() - event.quantity();
            if (newStock >= 0) {
                product.setStockQuantity(newStock);
                productRepository.save(product);
                log.info("✅ Successfully reduced stock for Product {}. New stock: {}", product.getId(), newStock);
            } else {
                log.error("❌ Failed to reduce stock for Product {}. Insufficient stock!", product.getId());
                // In a real system, we would publish a "StockFailedEvent" here to tell the Order Service to cancel the order!
            }
            
        }, () -> log.error("❌ Product {} not found during stock deduction!", event.productId()));
    }
}