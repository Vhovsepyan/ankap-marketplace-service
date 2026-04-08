package com.ankap.platform.analyticsservice.service;

import com.ankap.platform.analyticsservice.dto.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AnalyticsDashboardService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsDashboardService.class);

    private final AtomicInteger totalOrders = new AtomicInteger(0);
    private final AtomicInteger totalItemsSold = new AtomicInteger(0);

    @KafkaListener(topics = "order-events", groupId = "analytics-service-group")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Analytics Service received Order ID: {}", event.orderId());
        
        totalOrders.incrementAndGet();
        totalItemsSold.addAndGet(event.quantity());
        
        log.info("Live Stats Updated -> Total Orders: {}, Total Items Sold: {}",
                totalOrders.get(), totalItemsSold.get());
    }

    public int getTotalOrders() {
        return totalOrders.get();
    }

    public int getTotalItemsSold() {
        return totalItemsSold.get();
    }
}