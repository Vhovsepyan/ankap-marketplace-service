package com.ankap.platform.marketplace.app;

import com.ankap.platform.marketplace.domain.Inventory;
import com.ankap.platform.marketplace.domain.OutboxEvent;
import com.ankap.platform.marketplace.domain.Product;
import com.ankap.platform.marketplace.infra.InventoryRepository;
import com.ankap.platform.marketplace.infra.OutboxRepository;
import com.ankap.platform.marketplace.infra.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductAppService {

  private final ProductRepository productRepo;
  private final InventoryRepository inventoryRepo;
  private final OutboxRepository outboxRepo;

  public ProductAppService(ProductRepository productRepo,
                           InventoryRepository inventoryRepo,
                           OutboxRepository outboxRepo) {
    this.productRepo = productRepo;
    this.inventoryRepo = inventoryRepo;
    this.outboxRepo = outboxRepo;
  }

  @Transactional
  public long createProduct(long sellerId, String name, long priceCents, int initialQty) {
    if (priceCents <= 0) throw new IllegalArgumentException("priceCents must be > 0");
    if (initialQty < 0) throw new IllegalArgumentException("initialQty must be >= 0");

    Product p = productRepo.save(new Product(sellerId, name, priceCents));
    inventoryRepo.save(new Inventory(p.getId(), initialQty));
    outboxRepo.save(OutboxEvent.productCreated(p.getId()));
    return p.getId();
  }
}