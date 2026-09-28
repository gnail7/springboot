package com.example.springboot.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mall_inventory")
public class MallInventory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long skuId;
    private Integer availableStock;
    private Integer lockedStock;
    private Integer soldStock;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
