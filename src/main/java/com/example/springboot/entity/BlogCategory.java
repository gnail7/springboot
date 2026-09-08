package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客分类，对应表 blog_category。
 */
@Data
@TableName("blog_category")
public class BlogCategory {

    @TableId(value = "category_id", type = IdType.AUTO)
    private Long categoryId;

    /** 父分类ID（0=顶级，支持二级） */
    private Long parentId;

    private String categoryName;

    private Integer orderNum;

    private String status;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    private String remark;

    /** 子分类（非数据库字段） */
    @TableField(exist = false)
    private List<BlogCategory> children;
}
