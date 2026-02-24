package com.ankap.platform.marketplace.api;

import com.ankap.platform.marketplace.app.ProductAppService;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

  private final ProductAppService productAppService;

  public ProductController(ProductAppService productAppService) {
    this.productAppService = productAppService;
  }

  public record CreateProductRequest(
      @Positive long sellerId,
      @NotBlank @Size(max = 300) String name,
      @Positive long priceCents,
      @Min(0) int initialQty
  ) {}

  public record CreateProductResponse(long productId) {}

  @PostMapping
  public ResponseEntity<CreateProductResponse> create(@RequestBody @Validated CreateProductRequest req) {
    long id = productAppService.createProduct(req.sellerId(), req.name(), req.priceCents(), req.initialQty());
    return ResponseEntity.ok(new CreateProductResponse(id));
  }
}