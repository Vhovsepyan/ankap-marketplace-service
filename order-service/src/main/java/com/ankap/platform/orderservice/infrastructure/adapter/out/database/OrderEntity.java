package com.ankap.platform.orderservice.infrastructure.adapter.out.database;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private UUID productId;
  private String buyerEmail;
  private Integer quantity;
  private BigDecimal totalPrice;
  private String status;
  private Instant createdAt;
  private UUID paymentId;

  // Default constructor for JPA
  public OrderEntity() {}

  public OrderEntity(UUID id, UUID productId, String buyerEmail, Integer quantity, BigDecimal totalPrice, String status, Instant createdAt, UUID paymentId) {
    this.id = id;
    this.productId = productId;
    this.buyerEmail = buyerEmail;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.status = status;
    this.createdAt = createdAt;
    this.paymentId = paymentId;
  }

  public UUID getId() { return id; }
  public UUID getProductId() { return productId; }
  public String getBuyerEmail() { return buyerEmail; }
  public Integer getQuantity() { return quantity; }
  public BigDecimal getTotalPrice() { return totalPrice; }
  public String getStatus() { return status; }
  public Instant getCreatedAt() { return createdAt; }
  public UUID getPaymentId() { return paymentId; }
}