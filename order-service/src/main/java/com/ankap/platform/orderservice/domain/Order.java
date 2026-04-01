package com.ankap.platform.orderservice.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Order {
  private UUID id;
  private UUID productId;
  private String buyerEmail;
  private Integer quantity;
  private BigDecimal totalPrice;
  private String status;
  private Instant createdAt;

  public Order(UUID id, UUID productId, String buyerEmail, Integer quantity, BigDecimal totalPrice, String status, Instant createdAt) {
    this.id = id;
    this.productId = productId;
    this.buyerEmail = buyerEmail;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.status = status;
    this.createdAt = createdAt;
  }

  // Getters
  public UUID getId() { return id; }
  public UUID getProductId() { return productId; }
  public String getBuyerEmail() { return buyerEmail; }
  public Integer getQuantity() { return quantity; }
  public BigDecimal getTotalPrice() { return totalPrice; }
  public String getStatus() { return status; }
  public Instant getCreatedAt() { return createdAt; }

  // Business Logic Methods
  public void complete() {
    this.status = "COMPLETED";
  }

  public void cancel() {
    this.status = "CANCELLED";
  }
}