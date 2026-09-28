package com.example.springboot.mall.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mall_message_consume_record")
public class MallMessageConsumeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String consumerGroup;
    private String eventId;
    private String consumeStatus;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
