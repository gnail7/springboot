package com.example.springboot.controller;

import com.example.springboot.entity.BlogTag;
import com.example.springboot.service.BlogTagService;
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
 * 博客标签管理接口
 */
@Tag(name = "博客标签")
@RestController
@RequestMapping("/api/blog/tag")
public class BlogTagController {

    private final BlogTagService tagService;

    public BlogTagController(BlogTagService tagService) {
        this.tagService = tagService;
    }

    @Operation(summary = "标签列表（下拉框/管理页）")
    @GetMapping("/list")
    public Result<List<BlogTag>> list() {
        return Result.success(tagService.list());
    }

    @Operation(summary = "标签详情")
    @GetMapping("/{tagId}")
    public Result<BlogTag> get(@PathVariable Long tagId) {
        return Result.success(tagService.getById(tagId));
    }

    @Operation(summary = "新增标签")
    @PostMapping
    public Result<Long> create(@RequestBody BlogTag tag) {
        return Result.success(tagService.create(tag));
    }

    @Operation(summary = "修改标签")
    @PutMapping("/{tagId}")
    public Result<Void> update(@PathVariable Long tagId, @RequestBody BlogTag tag) {
        tag.setTagId(tagId);
        tagService.update(tag);
        return Result.success();
    }

    @Operation(summary = "删除标签（被文章引用时拒绝）")
    @DeleteMapping("/{tagId}")
    public Result<Void> delete(@PathVariable Long tagId) {
        tagService.delete(tagId);
        return Result.success();
    }
}
