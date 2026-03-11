package com.ankap.platform.orderservice.client;

import com.ankap.platform.orderservice.dto.ProductDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public ProductDto getProductById(Long id) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE, 
                "Product Catalog is temporarily unavailable. Please try again later."
        );
    }
}