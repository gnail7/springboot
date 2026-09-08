package com.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springboot.entity.BlogTag;
import com.example.springboot.exception.BusinessException;
import com.example.springboot.mapper.BlogPostMapper;
import com.example.springboot.mapper.BlogTagMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客标签服务：列表、CRUD、删除前校验
 */
@Service
public class BlogTagService {

    private final BlogTagMapper tagMapper;
    private final BlogPostMapper postMapper;

    public BlogTagService(BlogTagMapper tagMapper, BlogPostMapper postMapper) {
        this.tagMapper = tagMapper;
        this.postMapper = postMapper;
    }

    public List<BlogTag> list() {
        return tagMapper.selectList(new LambdaQueryWrapper<BlogTag>()
                .orderByAsc(BlogTag::getOrderNum)
                .orderByAsc(BlogTag::getTagId));
    }

    /** 公开：仅启用的标签 */
    public List<BlogTag> publicList() {
        return tagMapper.selectList(new LambdaQueryWrapper<BlogTag>()
                .eq(BlogTag::getStatus, "0")
                .orderByAsc(BlogTag::getOrderNum)
                .orderByAsc(BlogTag::getTagId));
    }

    public BlogTag getById(Long tagId) {
        BlogTag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BusinessException(404, "标签不存在");
        }
        return tag;
    }

    public Long create(BlogTag tag) {
        if (!StringUtils.hasText(tag.getTagName())) {
            throw new BusinessException(400, "标签名称不能为空");
        }
        checkNameUnique(tag.getTagName(), null);
        fillDefault(tag);
        tag.setCreateTime(LocalDateTime.now());
        tagMapper.insert(tag);
        return tag.getTagId();
    }

    public void update(BlogTag tag) {
        if (tag.getTagId() == null) {
            throw new BusinessException(400, "缺少标签ID");
        }
        getById(tag.getTagId());
        if (!StringUtils.hasText(tag.getTagName())) {
            throw new BusinessException(400, "标签名称不能为空");
        }
        checkNameUnique(tag.getTagName(), tag.getTagId());
        tag.setUpdateTime(LocalDateTime.now());
        tagMapper.updateById(tag);
    }

    public void delete(Long tagId) {
        getById(tagId);
        Long used = postMapper.countPostByTagId(tagId);
        if (used != null && used > 0) {
            throw new BusinessException(400, "该标签已被文章引用，无法删除");
        }
        tagMapper.deleteById(tagId);
    }

    private void checkNameUnique(String name, Long excludeId) {
        List<BlogTag> all = tagMapper.selectList(null);
        for (BlogTag t : all) {
            if (name.equals(t.getTagName())
                    && (excludeId == null || !excludeId.equals(t.getTagId()))) {
                throw new BusinessException(400, "标签名称已存在：" + name);
            }
        }
    }

    private void fillDefault(BlogTag tag) {
        if (tag.getOrderNum() == null) {
            tag.setOrderNum(0);
        }
        if (!StringUtils.hasText(tag.getStatus())) {
            tag.setStatus("0");
        }
    }
}
