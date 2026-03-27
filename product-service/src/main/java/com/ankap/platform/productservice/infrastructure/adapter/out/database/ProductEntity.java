package com.ankap.platform.productservice.infrastructure.adapter.out.database;

import jakarta.persistence.*; // Make sure to import GenerationType and GeneratedValue
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // 🌟 ADD THIS LINE
    private UUID id;

    private String sku;
    private String name;
    private BigDecimal price;
    private int availableQuantity;
    private String category;

    @Version
    private Long version;

    public ProductEntity() {}

    public ProductEntity(UUID id, String sku, String name, BigDecimal price, int availableQuantity, String category, Long version) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.category = category;
        this.version = version;
    }

    // ... (Keep all your existing getters here)
    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getAvailableQuantity() { return availableQuantity; }
    public String getCategory() { return category; }
    public Long getVersion() { return version; }
}