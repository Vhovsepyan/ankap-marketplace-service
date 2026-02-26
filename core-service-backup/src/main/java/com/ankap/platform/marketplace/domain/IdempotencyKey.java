package com.ankap.platform.marketplace.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey {

  @Id
  @Column(name = "idempotency_key", length = 80)
  private String idempotencyKey;

  @Column(name = "order_id", nullable = false)
  private long orderId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  protected IdempotencyKey() {}

  public IdempotencyKey(String idempotencyKey, long orderId) {
    this.idempotencyKey = idempotencyKey;
    this.orderId = orderId;
  }

  public String getIdempotencyKey() { return idempotencyKey; }
  public long getOrderId() { return orderId; }
}