package com.example.springboot.mall.cart;

import com.example.springboot.mall.entity.MallCartItem;
import com.example.springboot.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mall/member/cart/items")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Result<List<MallCartItem>> list(HttpServletRequest request) {
        return Result.success(cartService.list(memberId(request)));
    }

    @PostMapping
    public Result<MallCartItem> add(HttpServletRequest request, @Valid @RequestBody CartItemRequest body) {
        return Result.success(cartService.add(memberId(request), body.skuId(), body.quantity()));
    }

    @PutMapping("/{skuId}")
    public Result<MallCartItem> update(HttpServletRequest request, @PathVariable Long skuId,
                                       @Valid @RequestBody QuantityRequest body) {
        return Result.success(cartService.update(memberId(request), skuId, body.quantity()));
    }

    @DeleteMapping("/{skuId}")
    public Result<Void> delete(HttpServletRequest request, @PathVariable Long skuId) {
        cartService.delete(memberId(request), skuId);
        return Result.success();
    }

    private Long memberId(HttpServletRequest request) {
        return (Long) request.getAttribute("memberId");
    }

    public record CartItemRequest(@NotNull Long skuId, @NotNull @Min(1) @Max(999) Integer quantity) {
    }

    public record QuantityRequest(@NotNull @Min(1) @Max(999) Integer quantity) {
    }
}
