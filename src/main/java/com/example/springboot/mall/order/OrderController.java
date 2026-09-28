package com.example.springboot.mall.order;

import com.example.springboot.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mall/member/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<OrderService.OrderView> create(HttpServletRequest request,
                                                 @Valid @RequestBody CreateOrderBody body) {
        return Result.success(orderService.create(memberId(request),
                new OrderService.CreateOrderRequest(body.idempotencyKey(), body.items())));
    }

    @GetMapping
    public Result<List<OrderService.OrderView>> list(HttpServletRequest request,
                                                      @RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "20") int size) {
        return Result.success(orderService.list(memberId(request), page, size));
    }

    @GetMapping("/{orderNo}")
    public Result<OrderService.OrderView> detail(HttpServletRequest request, @PathVariable String orderNo) {
        return Result.success(orderService.detail(memberId(request), orderNo));
    }

    @PostMapping("/{orderNo}/cancel")
    public Result<Void> cancel(HttpServletRequest request, @PathVariable String orderNo) {
        orderService.cancel(memberId(request), orderNo);
        return Result.success();
    }

    private Long memberId(HttpServletRequest request) {
        return (Long) request.getAttribute("memberId");
    }

    public record CreateOrderBody(@NotBlank String idempotencyKey,
                                  @NotEmpty List<OrderService.OrderItemRequest> items) {
    }
}
