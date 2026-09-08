package com.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.entity.BlogCategory;
import com.example.springboot.entity.BlogPost;
import com.example.springboot.exception.BusinessException;
import com.example.springboot.mapper.BlogCategoryMapper;
import com.example.springboot.mapper.BlogPostMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客分类服务：列表、CRUD、删除前校验
 */
@Service
public class BlogCategoryService {

    private final BlogCategoryMapper categoryMapper;
    private final BlogPostMapper postMapper;

    public BlogCategoryService(BlogCategoryMapper categoryMapper, BlogPostMapper postMapper) {
        this.categoryMapper = categoryMapper;
        this.postMapper = postMapper;
    }

    public List<BlogCategory> list() {
        return categoryMapper.selectList(new LambdaQueryWrapper<BlogCategory>()
                .orderByAsc(BlogCategory::getOrderNum)
                .orderByAsc(BlogCategory::getCategoryId));
    }

    /** 公开：仅启用的顶级分类 */
    public List<BlogCategory> publicList() {
        return categoryMapper.selectList(new LambdaQueryWrapper<BlogCategory>()
                .eq(BlogCategory::getStatus, "0")
                .eq(BlogCategory::getParentId, 0L)
                .orderByAsc(BlogCategory::getOrderNum)
                .orderByAsc(BlogCategory::getCategoryId));
    }

    public BlogCategory getById(Long categoryId) {
        BlogCategory category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        return category;
    }

    public Long create(BlogCategory category) {
        if (!StringUtils.hasText(category.getCategoryName())) {
            throw new BusinessException(400, "分类名称不能为空");
        }
        checkNameUnique(category.getCategoryName(), null);
        fillDefault(category);
        category.setCreateTime(LocalDateTime.now());
        categoryMapper.insert(category);
        return category.getCategoryId();
    }

    public void update(BlogCategory category) {
        if (category.getCategoryId() == null) {
            throw new BusinessException(400, "缺少分类ID");
        }
        getById(category.getCategoryId());
        if (!StringUtils.hasText(category.getCategoryName())) {
            throw new BusinessException(400, "分类名称不能为空");
        }
        checkNameUnique(category.getCategoryName(), category.getCategoryId());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(category);
    }

    public void delete(Long categoryId) {
        getById(categoryId);
        Long children = categoryMapper.selectCount(new LambdaQueryWrapper<BlogCategory>()
                .eq(BlogCategory::getParentId, categoryId));
        if (children != null && children > 0) {
            throw new BusinessException(400, "存在子分类，无法删除");
        }
        Long used = postMapper.selectCount(new LambdaQueryWrapper<BlogPost>()
                .eq(BlogPost::getCategoryId, categoryId)
                .eq(BlogPost::getDelFlag, "0"));
        if (used != null && used > 0) {
            throw new BusinessException(400, "该分类下存在文章，无法删除");
        }
        categoryMapper.deleteById(categoryId);
    }

    private void checkNameUnique(String name, Long excludeId) {
        List<BlogCategory> all = categoryMapper.selectList(null);
        for (BlogCategory c : all) {
            if (name.equals(c.getCategoryName())
                    && (excludeId == null || !excludeId.equals(c.getCategoryId()))) {
                throw new BusinessException(400, "分类名称已存在：" + name);
            }
        }
    }

    private void fillDefault(BlogCategory category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getOrderNum() == null) {
            category.setOrderNum(0);
        }
        if (!StringUtils.hasText(category.getStatus())) {
            category.setStatus("0");
        }
    }
}
