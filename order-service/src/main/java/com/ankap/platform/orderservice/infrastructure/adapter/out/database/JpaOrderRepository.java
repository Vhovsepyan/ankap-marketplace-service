package com.ankap.platform.orderservice.infrastructure.adapter.out.database;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {}