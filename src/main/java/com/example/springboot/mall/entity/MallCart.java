package com.example.springboot.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mall_cart")
public class MallCart {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long memberId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
