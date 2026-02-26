package com.ankap.platform.marketplace.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "products")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "seller_id", nullable = false)
  private long sellerId;

  @Column(nullable = false, length = 300)
  private String name;

  @Column(name = "price_cents", nullable = false)
  private long priceCents;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  protected Product() {}

  public Product(long sellerId, String name, long priceCents) {
    this.sellerId = sellerId;
    this.name = name;
    this.priceCents = priceCents;
  }

  public Long getId() { return id; }
  public long getSellerId() { return sellerId; }
  public String getName() { return name; }
  public long getPriceCents() { return priceCents; }
}