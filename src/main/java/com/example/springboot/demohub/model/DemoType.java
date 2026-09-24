package com.example.springboot.demohub.model;

/**
 * Demo 的技术主题。新增 Demo 时优先复用已有类型，只有目录展示和统计需要区分时才新增枚举值。
 */
public enum DemoType {
    SSE,
    CONCURRENCY,
    REDIS,
    MESSAGE_QUEUE,
    DATABASE,
    HTTP,
    OTHER
}
