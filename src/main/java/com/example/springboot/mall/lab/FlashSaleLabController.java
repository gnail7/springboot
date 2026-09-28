package com.example.springboot.mall.lab;

import com.example.springboot.utils.Result;
import jakarta.validation.constraints.Min;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("local")
@RestController
@RequestMapping("/api/mall/lab/flash-sales")
public class FlashSaleLabController {
    private final FlashSaleService flashSaleService;

    public FlashSaleLabController(FlashSaleService flashSaleService) {
        this.flashSaleService = flashSaleService;
    }

    @PostMapping("/{activityId}/preheat")
    public Result<Void> preheat(@PathVariable Long activityId, @RequestParam @Min(0) int stock) {
        flashSaleService.preheat(activityId, stock);
        return Result.success();
    }

    @PostMapping("/{activityId}/orders")
    public Result<FlashSaleService.FlashSaleResult> order(@PathVariable Long activityId,
                                                          @RequestParam Long memberId,
                                                          @RequestParam(defaultValue = "1") @Min(1) int quantity) {
        return Result.success(flashSaleService.tryOrder(activityId, memberId, quantity));
    }
}
