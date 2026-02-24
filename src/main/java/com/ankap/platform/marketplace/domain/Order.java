package com.ankap.platform.marketplace.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

  public enum Status { CREATED }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "buyer_id", nullable = false)
  private long buyerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private Status status;

  @Column(name = "total_cents", nullable = false)
  private long totalCents;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  protected Order() {}

  public static Order create(long buyerId, List<OrderItem> items, long totalCents) {
    Order o = new Order();
    o.buyerId = buyerId;
    o.status = Status.CREATED;
    o.totalCents = totalCents;
    for (OrderItem it : items) {
      it.attachTo(o);
      o.items.add(it);
    }
    return o;
  }

  public Long getId() { return id; }
  public long getBuyerId() { return buyerId; }
  public long getTotalCents() { return totalCents; }
  public Status getStatus() { return status; }
  public List<OrderItem> getItems() { return items; }
}