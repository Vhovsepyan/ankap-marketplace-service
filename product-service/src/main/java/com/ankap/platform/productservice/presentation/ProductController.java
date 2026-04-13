package com.ankap.platform.productservice.presentation;

import com.ankap.platform.productservice.application.port.in.product.GetProductUseCase;
import com.ankap.platform.productservice.application.port.in.product.CreateProductUseCase;
import com.ankap.platform.productservice.application.port.in.product.DeleteProductUseCase;
import com.ankap.platform.productservice.domain.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final GetProductUseCase getProductUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    public ProductController(GetProductUseCase getProductUseCase, CreateProductUseCase createProductUseCase, DeleteProductUseCase deleteProductUseCase) {
        this.getProductUseCase = getProductUseCase;
        this.createProductUseCase = createProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        log.info("Fetching all products...");
        return getProductUseCase.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable UUID id) {
        log.info("Fetching product {}...", id);
        return getProductUseCase.getProduct(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product, @AuthenticationPrincipal Jwt jwt) {
        return createProductUseCase.save(product);
    }

    @DeleteMapping("/{productId}")
    public void deleteProductById(@PathVariable UUID productId) {
        log.info("Soft deleting product {}...", productId);
        deleteProductUseCase.softDeleteProductById(productId);
    }
}