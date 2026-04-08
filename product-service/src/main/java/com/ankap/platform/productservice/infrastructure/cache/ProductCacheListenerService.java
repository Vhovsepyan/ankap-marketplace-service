package com.ankap.platform.productservice.infrastructure.cache;

import com.ankap.platform.productservice.application.port.out.ProductCachePort;
import com.ankap.platform.productservice.domain.event.ProductDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class ProductCacheListenerService {

    private static final Logger log = LoggerFactory.getLogger(ProductCacheListenerService.class);
    private final ProductCachePort cachePort;

    public ProductCacheListenerService(ProductCachePort cachePort) {
        this.cachePort = cachePort;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductDeleted(ProductDeletedEvent event) {
        log.info("Transaction committed successfully. Updating cache for product: {}", event.productId());
        try {
            cachePort.evict(event.productId());
        } catch (Exception e) {
            log.error("Failed to update cache after product save", e);
        }
    }
}