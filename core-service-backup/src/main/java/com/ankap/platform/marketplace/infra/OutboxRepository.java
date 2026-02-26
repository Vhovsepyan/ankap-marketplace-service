// src/main/java/com/ankap/platform/marketplace/infra/OutboxRepository.java
package com.ankap.platform.marketplace.infra;

import com.ankap.platform.marketplace.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
  List<OutboxEvent> findTop50ByStatusOrderByIdAsc(OutboxEvent.Status status);
}