package com.ankap.platform.marketplace.app;

import com.ankap.platform.marketplace.domain.*;
import com.ankap.platform.marketplace.infra.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderAppService {

  private final ProductRepository productRepo;
  private final InventoryRepository inventoryRepo;
  private final OrderRepository orderRepo;
  private final OutboxRepository outboxRepo;
  private final IdempotencyKeyRepository idemRepo;

  public OrderAppService(ProductRepository productRepo,
                         InventoryRepository inventoryRepo,
                         OrderRepository orderRepo,
                         OutboxRepository outboxRepo,
                         IdempotencyKeyRepository idemRepo) {
    this.productRepo = productRepo;
    this.inventoryRepo = inventoryRepo;
    this.orderRepo = orderRepo;
    this.outboxRepo = outboxRepo;
    this.idemRepo = idemRepo;
  }

  public record OrderRequestItem(long productId, int qty) {}

  @Transactional
  public long placeOrder(String idempotencyKey, long buyerId, List<OrderRequestItem> items) {

    if (idempotencyKey == null || idempotencyKey.isBlank())
      throw new IllegalArgumentException("Idempotency-Key header is required");

    return idemRepo.findById(idempotencyKey)
            .map(IdempotencyKey::getOrderId)
            .orElseGet(() -> {
              long orderId = placeOrderInternal(buyerId, items);
              idemRepo.save(new IdempotencyKey(idempotencyKey, orderId));
              return orderId;
            });
  }

  /**
   * Actual business logic (NO @Transactional here)
   */
  private long placeOrderInternal(long buyerId, List<OrderRequestItem> items) {

    if (buyerId <= 0) throw new IllegalArgumentException("buyerId must be > 0");
    if (items == null || items.isEmpty()) throw new IllegalArgumentException("items required");

    Map<Long, Product> products =
            productRepo.findAllById(items.stream().map(OrderRequestItem::productId).toList())
                    .stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));

    List<OrderItem> orderItems = new ArrayList<>();
    long total = 0;

    for (OrderRequestItem it : items) {
      if (it.qty() <= 0) throw new IllegalArgumentException("qty must be > 0");

      Product p = products.get(it.productId());
      if (p == null)
        throw new IllegalArgumentException("product not found: " + it.productId());

      Inventory inv = inventoryRepo.findByProductIdForUpdate(it.productId())
              .orElseThrow(() -> new IllegalArgumentException("inventory missing: " + it.productId()));

      inv.reserve(it.qty());

      total += p.getPriceCents() * (long) it.qty();
      orderItems.add(OrderItem.of(it.productId(), it.qty(), p.getPriceCents()));
    }

    Order order = Order.create(buyerId, orderItems, total);
    order = orderRepo.save(order);

    outboxRepo.save(OutboxEvent.orderCreated(order.getId()));

    return order.getId();
  }
}