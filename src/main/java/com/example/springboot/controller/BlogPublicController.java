package com.example.springboot.controller;

import com.example.springboot.base.PageResult;
import com.example.springboot.entity.BlogCategory;
import com.example.springboot.entity.BlogPost;
import com.example.springboot.entity.BlogTag;
import com.example.springboot.service.BlogCategoryService;
import com.example.springboot.service.BlogPostService;
import com.example.springboot.service.BlogTagService;
import com.example.springboot.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客公开只读接口（不需要登录，用于面向读者的展示站）
 */
@Tag(name = "博客公开接口")
@RestController
@RequestMapping("/api/blog/public")
public class BlogPublicController {

    private final BlogPostService postService;
    private final BlogCategoryService categoryService;
    private final BlogTagService tagService;

    public BlogPublicController(BlogPostService postService,
                                BlogCategoryService categoryService,
                                BlogTagService tagService) {
        this.postService = postService;
        this.categoryService = categoryService;
        this.tagService = tagService;
    }

    @Operation(summary = "已发布文章分页（可按分类/标签/关键词筛选）")
    @GetMapping("/posts")
    public Result<PageResult<BlogPost>> posts(@RequestParam(required = false) Long categoryId,
                                              @RequestParam(required = false) Long tagId,
                                              @RequestParam(required = false) String keyword,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(postService.publicPage(categoryId, tagId, keyword, pageNum, pageSize));
    }

    @Operation(summary = "已发布文章详情")
    @GetMapping("/posts/{postId}")
    public Result<BlogPost> post(@PathVariable Long postId) {
        return Result.success(postService.publicDetail(postId));
    }

    @Operation(summary = "公开分类列表（仅启用）")
    @GetMapping("/categories")
    public Result<List<BlogCategory>> categories() {
        return Result.success(categoryService.publicList());
    }

    @Operation(summary = "公开标签列表（仅启用）")
    @GetMapping("/tags")
    public Result<List<BlogTag>> tags() {
        return Result.success(tagService.publicList());
    }
}
