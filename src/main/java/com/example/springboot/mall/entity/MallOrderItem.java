package com.example.springboot.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("mall_order_item")
public class MallOrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String orderNo;
    private Long skuId;
    private Long spuId;
    private String skuCode;
    private String productTitle;
    private String skuSnapshot;
    private Integer quantity;
    private BigDecimal salePrice;
    private BigDecimal itemAmount;
    private LocalDateTime createdAt;
}
