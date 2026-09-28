package com.example.springboot.mall.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mall_outbox_event")
public class MallOutboxEvent {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String eventId;
    private String eventType;
    private String aggregateId;
    private Integer payloadVersion;
    private String payloadJson;
    private String traceId;
    private String publishStatus;
    private Integer retryCount;
    private LocalDateTime nextRetryAt;
    private LocalDateTime publishedAt;
    private String lastError;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
