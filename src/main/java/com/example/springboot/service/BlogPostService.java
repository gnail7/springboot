package com.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.springboot.base.PageResult;
import com.example.springboot.entity.BlogPost;
import com.example.springboot.exception.BusinessException;
import com.example.springboot.mapper.BlogPostMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客文章服务：分页、详情（含标签）、新增/修改/删除，文章的标签为覆盖式保存
 */
@Service
public class BlogPostService {

    private final BlogPostMapper postMapper;

    public BlogPostService(BlogPostMapper postMapper) {
        this.postMapper = postMapper;
    }

    public PageResult<BlogPost> page(String title, Long categoryId, String status,
                                     Integer pageNum, Integer pageSize) {
        int pn = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        int ps = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        List<BlogPost> records = postMapper.page(title, categoryId, status, (pn - 1) * ps, ps);
        Long total = postMapper.count(title, categoryId, status);
        return new PageResult<>(records, total, (long) pn, (long) ps);
    }

    public BlogPost getById(Long postId) {
        BlogPost post = postMapper.selectDetail(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在");
        }
        post.setTagIds(postMapper.selectTagIdsByPostId(postId));
        return post;
    }

    public Long create(BlogPost post) {
        if (!StringUtils.hasText(post.getTitle())) {
            throw new BusinessException(400, "文章标题不能为空");
        }
        fillDefault(post);
        post.setCreateTime(LocalDateTime.now());
        if ("1".equals(post.getStatus()) && post.getPublishedTime() == null) {
            post.setPublishedTime(LocalDateTime.now());
        }
        postMapper.insert(post);
        saveTags(post);
        return post.getPostId();
    }

    public void update(BlogPost post) {
        if (post.getPostId() == null) {
            throw new BusinessException(400, "缺少文章ID");
        }
        getById(post.getPostId());
        if (!StringUtils.hasText(post.getTitle())) {
            throw new BusinessException(400, "文章标题不能为空");
        }
        post.setUpdateTime(LocalDateTime.now());
        if ("1".equals(post.getStatus()) && post.getPublishedTime() == null) {
            post.setPublishedTime(LocalDateTime.now());
        }
        postMapper.updateById(post);
        // 覆盖式保存标签
        postMapper.deletePostTagByPostId(post.getPostId());
        saveTags(post);
    }

    public void delete(Long postId) {
        getById(postId);
        // 软删除
        postMapper.update(null, new LambdaUpdateWrapper<BlogPost>()
                .eq(BlogPost::getPostId, postId)
                .set(BlogPost::getDelFlag, "1")
                .set(BlogPost::getUpdateTime, LocalDateTime.now()));
        postMapper.deletePostTagByPostId(postId);
    }

    private void saveTags(BlogPost post) {
        if (post.getTagIds() != null && !post.getTagIds().isEmpty()) {
            postMapper.insertPostTag(post.getPostId(), post.getTagIds());
        }
    }

    /* ---------- 公开只读 ---------- */

    public PageResult<BlogPost> publicPage(Long categoryId, Long tagId, String keyword,
                                           Integer pageNum, Integer pageSize) {
        int pn = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        int ps = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        List<BlogPost> records = postMapper.selectPublicPage(categoryId, tagId, keyword, (pn - 1) * ps, ps);
        populateTagIds(records);
        Long total = postMapper.countPublic(categoryId, tagId, keyword);
        return new PageResult<>(records, total, (long) pn, (long) ps);
    }

    public BlogPost publicDetail(Long postId) {
        BlogPost post = postMapper.selectPublicDetail(postId);
        if (post == null) {
            throw new BusinessException(404, "文章不存在或未发布");
        }
        post.setTagIds(postMapper.selectTagIdsByPostId(postId));
        return post;
    }

    private void populateTagIds(List<BlogPost> posts) {
        if (posts == null) {
            return;
        }
        posts.forEach(post -> post.setTagIds(postMapper.selectTagIdsByPostId(post.getPostId())));
    }

    private void fillDefault(BlogPost post) {
        if (!StringUtils.hasText(post.getStatus())) {
            post.setStatus("0");
        }
        if (!StringUtils.hasText(post.getDelFlag())) {
            post.setDelFlag("0");
        }
        if (post.getIsTop() == null) {
            post.setIsTop(0);
        }
        if (post.getIsRecommend() == null) {
            post.setIsRecommend(0);
        }
        if (post.getViewCount() == null) {
            post.setViewCount(0);
        }
        if (post.getLikeCount() == null) {
            post.setLikeCount(0);
        }
        if (post.getCommentCount() == null) {
            post.setCommentCount(0);
        }
        if (post.getWordCount() == null) {
            post.setWordCount(0);
        }
    }
}
