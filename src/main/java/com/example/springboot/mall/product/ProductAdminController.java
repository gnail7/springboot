package com.example.springboot.mall.product;

import com.example.springboot.mall.entity.MallCategory;
import com.example.springboot.mall.entity.MallProductSku;
import com.example.springboot.mall.entity.MallProductSpu;
import com.example.springboot.utils.Result;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mall/admin/products")
@PreAuthorize("hasAuthority('product:manage') or hasAuthority('*:*:*')")
public class ProductAdminController {
    private final ProductService productService;

    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/categories")
    public Result<MallCategory> createCategory(@RequestBody MallCategory category) {
        return Result.success(productService.createCategory(category));
    }

    @PostMapping("/spu")
    public Result<MallProductSpu> createSpu(@RequestBody MallProductSpu spu) {
        return Result.success(productService.createSpu(spu));
    }

    @PostMapping("/sku")
    public Result<MallProductSku> createSku(@RequestBody MallProductSku sku) {
        return Result.success(productService.createSku(sku));
    }

    @PutMapping("/spu/{id}/status")
    public Result<Void> updateSpuStatus(@PathVariable Long id, @RequestParam @NotBlank String status) {
        productService.updateSpuStatus(id, status);
        return Result.success();
    }

    @PutMapping("/sku/{id}/status")
    public Result<Void> updateSkuStatus(@PathVariable Long id, @RequestParam @NotBlank String status) {
        productService.updateSkuStatus(id, status);
        return Result.success();
    }
}
