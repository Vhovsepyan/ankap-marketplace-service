package com.ankap.platform.productservice.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    private boolean isDeleted;

    public Product() {}

    @JsonCreator
    public Product(@JsonProperty("id") UUID id,
                   @JsonProperty("sku") String sku,
                   @JsonProperty("name") String name,
                   @JsonProperty("price") BigDecimal price,
                   @JsonProperty("availableQuantity") int availableQuantity,
                   @JsonProperty("category") String category,
                   @JsonProperty("version") Long version,
                   @JsonProperty("deleted") @JsonAlias("isDeleted") Boolean isDeleted) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.category = category;
        this.version = version;
        this.isDeleted = isDeleted != null && isDeleted;
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public int getAvailableQuantity() { return availableQuantity; }
    public String getCategory() { return category; }
    public Long getVersion() { return version; }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void deductInventory(int quantity) {
        if (this.availableQuantity < quantity) {
            throw new IllegalStateException("Insufficient inventory for product: " + id);
        }
        this.availableQuantity -= quantity;
    }

    public void restoreInventory(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Cannot restore a negative quantity for product: " + id);
        }
        this.availableQuantity += quantity;
    }
}