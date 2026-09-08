package com.example.springboot.controller;

import com.example.springboot.entity.BlogCategory;
import com.example.springboot.service.BlogCategoryService;
import com.example.springboot.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客分类管理接口
 */
@Tag(name = "博客分类")
@RestController
@RequestMapping("/api/blog/category")
public class BlogCategoryController {

    private final BlogCategoryService categoryService;

    public BlogCategoryController(BlogCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "分类列表（下拉框/管理页）")
    @GetMapping("/list")
    public Result<List<BlogCategory>> list() {
        return Result.success(categoryService.list());
    }

    @Operation(summary = "分类详情")
    @GetMapping("/{categoryId}")
    public Result<BlogCategory> get(@PathVariable Long categoryId) {
        return Result.success(categoryService.getById(categoryId));
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Long> create(@RequestBody BlogCategory category) {
        return Result.success(categoryService.create(category));
    }

    @Operation(summary = "修改分类")
    @PutMapping("/{categoryId}")
    public Result<Void> update(@PathVariable Long categoryId, @RequestBody BlogCategory category) {
        category.setCategoryId(categoryId);
        categoryService.update(category);
        return Result.success();
    }

    @Operation(summary = "删除分类（存在子分类/文章时拒绝）")
    @DeleteMapping("/{categoryId}")
    public Result<Void> delete(@PathVariable Long categoryId) {
        categoryService.delete(categoryId);
        return Result.success();
    }
}
