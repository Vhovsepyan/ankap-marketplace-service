package com.ankap.platform.productservice.infrastructure.adapter.out.cache;

import com.ankap.platform.productservice.application.port.out.ProductCachePort;
import com.ankap.platform.productservice.domain.Product;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class RedisProductCacheAdapter implements ProductCachePort {

    private final RedisTemplate<String, Product> redisTemplate;
    private static final String CACHE_KEY_PREFIX = "product:";
    private static final Duration TTL = Duration.ofHours(1);

    public RedisProductCacheAdapter(RedisTemplate<String, Product> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<Product> get(UUID id) {
        Product product = redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + id);
        return Optional.ofNullable(product);
    }

    @Override
    public void put(Product product) {
        if (product.getId() != null) {
            redisTemplate.opsForValue().set(CACHE_KEY_PREFIX + product.getId(), product, TTL);
        }
    }

    @Override
    public void evict(UUID id) {
        redisTemplate.delete(CACHE_KEY_PREFIX + id);
    }

    @Override
    public List<Product> getAllProducts() {
        Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<Product> products = redisTemplate.opsForValue().multiGet(keys);
        if (products == null) {
            return Collections.emptyList();
        }
        return products.stream().filter(Objects::nonNull).toList();
    }
}