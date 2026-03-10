package com.ankap.platform.orderservice.repository;

import com.ankap.platform.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}