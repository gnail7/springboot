package com.example.springboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 博客标签，对应表 blog_tag。
 */
@Data
@TableName("blog_tag")
public class BlogTag {

    @TableId(value = "tag_id", type = IdType.AUTO)
    private Long tagId;

    private String tagName;

    private Integer orderNum;

    private String status;

    private String createBy;

    private LocalDateTime createTime;

    private String updateBy;

    private LocalDateTime updateTime;

    private String remark;
}
