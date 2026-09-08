package com.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springboot.entity.BlogCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 博客分类 Mapper（基础 CRUD 由 MyBatis-Plus 生成，查询逻辑在 Service 用 Wrapper 处理）
 */
@Mapper
public interface BlogCategoryMapper extends BaseMapper<BlogCategory> {
}
