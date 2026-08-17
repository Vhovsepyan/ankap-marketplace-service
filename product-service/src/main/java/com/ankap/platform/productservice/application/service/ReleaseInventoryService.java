package com.ankap.platform.productservice.application.service;

import com.ankap.platform.productservice.application.port.in.ReleaseInventoryCommand;
import com.ankap.platform.productservice.application.port.in.ReleaseInventoryUseCase;
import com.ankap.platform.productservice.application.port.out.ProductCachePort;
import com.ankap.platform.productservice.application.port.out.ProductRepositoryPort;
import com.ankap.platform.productservice.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Compensation for a reservation that was made but never paid for. The stock was
 * already deducted by {@link ReserveInventoryService}, so this puts it back.
 */
public class ReleaseInventoryService implements ReleaseInventoryUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReleaseInventoryService.class);
    private final ProductRepositoryPort repositoryPort;
    private final ProductCachePort cachePort;

    public ReleaseInventoryService(ProductRepositoryPort repositoryPort, ProductCachePort cachePort) {
        this.repositoryPort = repositoryPort;
        this.cachePort = cachePort;
    }

    @Override
    public void release(ReleaseInventoryCommand command) {
        Product product = repositoryPort.findById(command.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + command.productId()));

        product.restoreInventory(command.quantity());
        Product savedProduct = repositoryPort.save(product);

        log.info("Released {} unit(s) of product {} after order {} was not paid for. Available now: {}",
                command.quantity(), command.productId(), command.orderId(), savedProduct.getAvailableQuantity());

        // Same reasoning as the reservation path: the write is committed, and a cache
        // outage must not turn a successful release into a failure the consumer retries.
        try {
            cachePort.put(savedProduct);
        } catch (Exception cacheFailure) {
            log.error("Failed to refresh cache after inventory release for product {}",
                    savedProduct.getId(), cacheFailure);
        }
    }
}
