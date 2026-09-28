package com.example.springboot.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("mall_order")
public class MallOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long memberId;
    private String orderStatus;
    private BigDecimal totalAmount;
    private BigDecimal payableAmount;
    private LocalDateTime expireAt;
    private String idempotencyKey;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
