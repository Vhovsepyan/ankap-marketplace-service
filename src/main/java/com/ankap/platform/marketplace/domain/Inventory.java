package com.ankap.platform.marketplace.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {

  @Id
  @Column(name = "product_id")
  private Long productId;

  @Column(name = "available_qty", nullable = false)
  private int availableQty;

  @Column(name = "reserved_qty", nullable = false)
  private int reservedQty;

  @Version
  @Column(nullable = false)
  private long version;

  protected Inventory() {}

  public Inventory(long productId, int initialQty) {
    this.productId = productId;
    this.availableQty = initialQty;
    this.reservedQty = 0;
    this.version = 0;
  }

  public void reserve(int qty) {
    if (qty <= 0) throw new IllegalArgumentException("qty must be > 0");
    if (availableQty < qty)
      throw new OutOfStockException("not enough stock");
    availableQty -= qty;
    reservedQty += qty;
  }

  public Long getProductId() { return productId; }
}