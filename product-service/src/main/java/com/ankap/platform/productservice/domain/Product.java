package com.ankap.platform.productservice.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Product {
    private UUID id;
    private String sku;
    private String name;
    private BigDecimal price;
    private int availableQuantity;
    private String category;
    private Long version;

    public Product() {}

    public Product(UUID id, String sku, String name, BigDecimal price, int availableQuantity, String category, Long version) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.category = category;
        this.version = version;
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getAvailableQuantity() { return availableQuantity; }
    public String getCategory() { return category; }
    public Long getVersion() { return version; }

    public void deductInventory(int quantity) {
        if (this.availableQuantity < quantity) {
            throw new IllegalStateException("Insufficient inventory for product: " + id);
        }
        this.availableQuantity -= quantity;
    }
}