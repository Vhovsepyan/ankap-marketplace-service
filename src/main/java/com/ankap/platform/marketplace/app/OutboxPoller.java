// src/main/java/com/ankap/platform/marketplace/app/OutboxPoller.java
package com.ankap.platform.marketplace.app;

import com.ankap.platform.marketplace.domain.OutboxEvent;
import com.ankap.platform.marketplace.infra.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxPoller {

  private static final Logger log = LoggerFactory.getLogger(OutboxPoller.class);

  private final OutboxRepository repo;

  public OutboxPoller(OutboxRepository repo) {
    this.repo = repo;
  }

  @Scheduled(fixedDelay = 2000)
  @Transactional
  public void poll() {
    List<OutboxEvent> batch = repo.findTop50ByStatusOrderByIdAsc(OutboxEvent.Status.NEW);
    if (batch.isEmpty()) {
      return;
    }

    for (OutboxEvent e : batch) {
      try {
        // "Publish" placeholder for now (later: Kafka producer)
        log.info("Publishing outbox event id={} type={} payload={}", e.getId(), e.getEventType(), e.getPayloadJson());
        e.markSent();
      } catch (Exception ex) {
        log.error("Failed publishing outbox event id={}", e.getId(), ex);
        e.markFailed();
      }
    }

    // Force updates (even if dirty-checking is misconfigured)
    repo.saveAll(batch);
  }
}