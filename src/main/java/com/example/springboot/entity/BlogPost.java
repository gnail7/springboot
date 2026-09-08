package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客文章，对应表 blog_post。
 */
@Data
@TableName("blog_post")
public class BlogPost {

    @TableId(value = "post_id", type = IdType.AUTO)
    private Long postId;

    /** 所属分类 */
    private Long categoryId;

    /** 作者ID（关联 sys_user，可选） */
    private Long authorId;

    private String title;

    private String slug;

    private String summary;

    /** 正文（Markdown） */
    private String content;

    private String cover;

    /** 状态（0草稿 1已发布 2下架） */
    private String status;

    private Integer isTop;

    private Integer isRecommend;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Integer wordCount;

    private LocalDateTime publishedTime;

    private String delFlag;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    private String remark;

    /** 分类名称（联表查询，非数据库字段） */
    @TableField(exist = false)
    private String categoryName;

    /** 关联标签 id（非数据库字段） */
    @TableField(exist = false)
    private List<Long> tagIds;
}
