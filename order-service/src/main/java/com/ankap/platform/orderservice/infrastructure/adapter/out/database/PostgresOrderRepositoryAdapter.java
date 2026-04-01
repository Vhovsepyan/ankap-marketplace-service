package com.ankap.platform.orderservice.infrastructure.adapter.out.database;

import com.ankap.platform.orderservice.application.port.out.OrderRepositoryPort;
import com.ankap.platform.orderservice.domain.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PostgresOrderRepositoryAdapter implements OrderRepositoryPort {

    private final JpaOrderRepository jpaRepository;

    public PostgresOrderRepositoryAdapter(JpaOrderRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getId(), order.getProductId(), order.getBuyerEmail(),
                order.getQuantity(), order.getTotalPrice(), order.getStatus(), order.getCreatedAt()
        );
        OrderEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private Order toDomain(OrderEntity entity) {
        return new Order(
                entity.getId(), entity.getProductId(), entity.getBuyerEmail(),
                entity.getQuantity(), entity.getTotalPrice(), entity.getStatus(), entity.getCreatedAt()
        );
    }
}