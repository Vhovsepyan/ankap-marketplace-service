package com.ankap.platform.orderservice.repository;

import com.ankap.platform.orderservice.infrastructure.adapter.out.database.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}