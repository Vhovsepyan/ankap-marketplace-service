package com.ankap.platform.marketplace.api;

import com.ankap.platform.marketplace.app.OrderAppService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

  private final OrderAppService orderAppService;

  public OrderController(OrderAppService orderAppService) {
    this.orderAppService = orderAppService;
  }

  public record OrderItemRequest(
      @Positive long productId,
      @Positive int qty
  ) {}

  // buyerId REMOVED — identity comes ONLY from JWT subject
  public record PlaceOrderRequest(
      @NotEmpty List<@Valid OrderItemRequest> items
  ) {}

  public record PlaceOrderResponse(long orderId) {}

  @PreAuthorize("hasRole('BUYER')")
  @PostMapping
  public ResponseEntity<PlaceOrderResponse> place(
          @AuthenticationPrincipal Jwt jwt,
          @RequestHeader("Idempotency-Key") String idempotencyKey,
          @RequestBody @Valid PlaceOrderRequest req
  ) {
    long buyerId = Long.parseLong(jwt.getSubject());

    long id = orderAppService.placeOrder(
            idempotencyKey,
            buyerId,
            req.items().stream()
                    .map(i -> new OrderAppService.OrderRequestItem(i.productId(), i.qty()))
                    .toList()
    );

    return ResponseEntity.ok(new PlaceOrderResponse(id));
  }
}