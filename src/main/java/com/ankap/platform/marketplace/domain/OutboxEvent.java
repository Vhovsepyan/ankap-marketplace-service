// src/main/java/com/ankap/platform/marketplace/domain/OutboxEvent.java
package com.ankap.platform.marketplace.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

  public enum Status { NEW, SENT, FAILED }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_type", nullable = false, length = 120)
  private String eventType;

  @Column(name = "payload_json", nullable = false, columnDefinition = "text")
  private String payloadJson;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private Status status = Status.NEW;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  protected OutboxEvent() {}

  public OutboxEvent(String eventType, String payloadJson) {
    this.eventType = eventType;
    this.payloadJson = payloadJson;
    this.status = Status.NEW;
  }

  public static OutboxEvent productCreated(long productId) {
    return new OutboxEvent("PRODUCT_CREATED", "{\"productId\":" + productId + "}");
  }

  public static OutboxEvent orderCreated(long orderId) {
    return new OutboxEvent("ORDER_CREATED", "{\"orderId\":" + orderId + "}");
  }

  public void markSent() {
    this.status = Status.SENT;
  }

  public void markFailed() {
    this.status = Status.FAILED;
  }

  public Long getId() { return id; }
  public String getEventType() { return eventType; }
  public String getPayloadJson() { return payloadJson; }
  public Status getStatus() { return status; }

  @Override
  public String toString() {
    return "OutboxEvent{id=" + id + ", type=" + eventType + ", status=" + status + "}";
  }
}