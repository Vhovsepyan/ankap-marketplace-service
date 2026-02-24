package com.ankap.platform.marketplace.api;

import com.ankap.platform.marketplace.app.OrderAppService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

  public record PlaceOrderRequest(
      @Positive long buyerId,
      @NotEmpty List<@Valid OrderItemRequest> items
  ) {}

  public record PlaceOrderResponse(long orderId) {}

  @PostMapping
  public ResponseEntity<PlaceOrderResponse> place(
          @RequestHeader("Idempotency-Key") String idempotencyKey,
          @RequestBody @Valid PlaceOrderRequest req) {
    long id = orderAppService.placeOrder(
            idempotencyKey,
        req.buyerId(),
        req.items().stream().map(i -> new OrderAppService.OrderRequestItem(i.productId(), i.qty())).toList()
    );
    return ResponseEntity.ok(new PlaceOrderResponse(id));
  }
}