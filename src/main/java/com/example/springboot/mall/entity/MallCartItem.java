package com.example.springboot.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mall_cart_item")
public class MallCartItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long cartId;
    private Long memberId;
    private Long skuId;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
