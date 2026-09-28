package com.example.springboot.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mall_product_spu")
public class MallProductSpu {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String title;
    private String subtitle;
    private String brand;
    private String detail;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
