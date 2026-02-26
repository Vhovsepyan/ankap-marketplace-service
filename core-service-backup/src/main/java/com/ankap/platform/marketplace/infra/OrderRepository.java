package com.ankap.platform.marketplace.infra;

import com.ankap.platform.marketplace.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}