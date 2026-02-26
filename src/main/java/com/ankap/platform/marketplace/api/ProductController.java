package com.ankap.platform.marketplace.api;

import com.ankap.platform.marketplace.app.ProductAppService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

  private final ProductAppService productAppService;

  public ProductController(ProductAppService productAppService) {
    this.productAppService = productAppService;
  }

  // sellerId REMOVED — identity comes ONLY from JWT subject
  public record CreateProductRequest(
      @NotBlank @Size(max = 300) String name,
      @Positive long priceCents,
      @Min(0) int initialQty
  ) {}

  public record CreateProductResponse(long productId) {}

  @PreAuthorize("hasRole('SELLER')")
  @PostMapping
  public ResponseEntity<CreateProductResponse> create(
          @AuthenticationPrincipal Jwt jwt,
          @RequestBody @Valid CreateProductRequest req
  ) {
    long sellerId = Long.parseLong(jwt.getSubject());
    long id = productAppService.createProduct(sellerId, req.name(), req.priceCents(), req.initialQty());
    return ResponseEntity.ok(new CreateProductResponse(id));
  }
}