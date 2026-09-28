package com.example.springboot.mall.product;

import com.example.springboot.mall.entity.MallCategory;
import com.example.springboot.mall.entity.MallProductSpu;
import com.example.springboot.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mall/public")
public class ProductPublicController {
    private final ProductService productService;

    public ProductPublicController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/categories")
    public Result<List<MallCategory>> categories() {
        return Result.success(productService.categories());
    }

    @GetMapping("/products")
    public Result<List<MallProductSpu>> products(@RequestParam(required = false) Long categoryId) {
        return Result.success(productService.products(categoryId));
    }

    @GetMapping("/products/{skuId}")
    public Result<ProductService.ProductDetail> detail(@org.springframework.web.bind.annotation.PathVariable Long skuId) {
        return Result.success(productService.detail(skuId));
    }
}
