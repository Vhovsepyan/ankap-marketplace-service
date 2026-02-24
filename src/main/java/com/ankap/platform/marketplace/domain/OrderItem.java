package com.ankap.platform.marketplace.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @Column(name = "product_id", nullable = false)
  private long productId;

  @Column(nullable = false)
  private int qty;

  @Column(name = "price_cents", nullable = false)
  private long priceCents;

  protected OrderItem() {}

  public static OrderItem of(long productId, int qty, long priceCents) {
    OrderItem it = new OrderItem();
    it.productId = productId;
    it.qty = qty;
    it.priceCents = priceCents;
    return it;
  }

  void attachTo(Order order) { this.order = order; }

  public long getProductId() { return productId; }
  public int getQty() { return qty; }
  public long getPriceCents() { return priceCents; }
}