package com.ankap.platform.orderservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
  private UUID paymentId;

  public Order(UUID id, UUID productId, String buyerEmail, Integer quantity, BigDecimal totalPrice, String status, Instant createdAt) {
    this(id, productId, buyerEmail, quantity, totalPrice, status, createdAt, null);
  }

  public Order(UUID id, UUID productId, String buyerEmail, Integer quantity, BigDecimal totalPrice, String status, Instant createdAt, UUID paymentId) {
    this.id = id;
    this.productId = productId;
    this.buyerEmail = buyerEmail;
    this.quantity = quantity;
    this.totalPrice = totalPrice;
    this.status = status;
    this.createdAt = createdAt;
    this.paymentId = paymentId;
  }

  // Getters
  public UUID getId() { return id; }
  public UUID getProductId() { return productId; }
  public String getBuyerEmail() { return buyerEmail; }
  public Integer getQuantity() { return quantity; }
  public BigDecimal getTotalPrice() { return totalPrice; }
  public String getStatus() { return status; }
  public Instant getCreatedAt() { return createdAt; }
  public UUID getPaymentId() { return paymentId; }

  // Business Logic Methods
  public void markStockReserved() {
    this.status = OrderStatus.STOCK_RESERVED;
  }

  /** A payment exists in LedgerFlow but has not settled yet. */
  public void markAwaitingPayment(UUID paymentId) {
    this.paymentId = paymentId;
    this.status = OrderStatus.AWAITING_PAYMENT;
  }

  public void markPaid(UUID paymentId) {
    this.paymentId = paymentId;
    this.status = OrderStatus.PAID;
  }

  public void cancel() {
    this.status = OrderStatus.CANCELLED;
  }

  /**
   * Cancels while keeping the payment that caused it. Without this the order row
   * loses every trace of the failed payment, which is exactly what someone
   * investigating the cancellation needs.
   */
  public void cancelForFailedPayment(UUID paymentId) {
    this.paymentId = paymentId;
    this.status = OrderStatus.CANCELLED;
  }

  // These read as state questions, not fields: keep them out of the API response,
  // where `status` already says everything a client needs.
  @JsonIgnore
  public boolean isPaid() {
    return OrderStatus.PAID.equals(this.status);
  }

  @JsonIgnore
  public boolean isCancelled() {
    return OrderStatus.CANCELLED.equals(this.status);
  }

  /**
   * Payment outcomes arrive over Kafka, which is at-least-once, and the same
   * outcome can also be observed synchronously by the caller that started the
   * payment. Both paths funnel through here so a duplicate is a no-op rather
   * than a second state change.
   */
  @JsonIgnore
  public boolean isSettled() {
    return isPaid() || isCancelled();
  }

  /** True once stock has been deducted, which is what makes a release necessary. */
  @JsonIgnore
  public boolean holdsReservedStock() {
    return OrderStatus.STOCK_RESERVED.equals(this.status)
            || OrderStatus.AWAITING_PAYMENT.equals(this.status);
  }
}
