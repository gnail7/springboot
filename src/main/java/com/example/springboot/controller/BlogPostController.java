package com.example.springboot.controller;

import com.example.springboot.base.PageResult;
import com.example.springboot.entity.BlogPost;
import com.example.springboot.service.BlogPostService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博客文章管理接口
 */
@Tag(name = "博客文章")
@RestController
@RequestMapping("/api/blog/post")
public class BlogPostController {

    private final BlogPostService postService;

    public BlogPostController(BlogPostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "分页查询文章")
    @GetMapping("/page")
    public PageResult<BlogPost> page(@RequestParam(required = false) String title,
                                     @RequestParam(required = false) Long categoryId,
                                     @RequestParam(required = false) String status,
                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        return postService.page(title, categoryId, status, pageNum, pageSize);
    }

    @Operation(summary = "文章详情（含标签）")
    @GetMapping("/{postId}")
    public Result<BlogPost> get(@PathVariable Long postId) {
        return Result.success(postService.getById(postId));
    }

    @Operation(summary = "新增文章（body 可带 tagIds）")
    @PostMapping
    public Result<Long> create(@RequestBody BlogPost post) {
        return Result.success(postService.create(post));
    }

    @Operation(summary = "修改文章（覆盖式保存标签）")
    @PutMapping("/{postId}")
    public Result<Void> update(@PathVariable Long postId, @RequestBody BlogPost post) {
        post.setPostId(postId);
        postService.update(post);
        return Result.success();
    }

    @Operation(summary = "删除文章（软删除并清理标签关联）")
    @DeleteMapping("/{postId}")
    public Result<Void> delete(@PathVariable Long postId) {
        postService.delete(postId);
        return Result.success();
    }
}
