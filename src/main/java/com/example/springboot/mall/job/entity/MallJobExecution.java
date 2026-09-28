package com.example.springboot.mall.job.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mall_job_execution")
public class MallJobExecution {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String jobName;
    private String executionId;
    private String executionStatus;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer processedCount;
    private Long durationMs;
    private String errorMessage;
    private LocalDateTime createdAt;
}
