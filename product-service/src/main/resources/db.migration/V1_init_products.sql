-- resources.db.migration/V1_init_products.sql
CREATE TABLE products (
                          id UUID PRIMARY KEY,
                          sku VARCHAR(50) UNIQUE NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          available_quantity INT NOT NULL,
                          category VARCHAR(100) NOT NULL,
                          version BIGINT DEFAULT 0 NOT NULL
);

CREATE INDEX idx_product_category ON products(category);
CREATE INDEX idx_product_sku ON products(sku);