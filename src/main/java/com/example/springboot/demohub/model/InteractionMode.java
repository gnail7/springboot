package com.example.springboot.demohub.model;

/**
 * Demo 的交互方式。它描述运行时需要什么能力，而不是绑定具体业务实现。
 */
public enum InteractionMode {
    STATIC,
    REQUEST,
    STREAM,
    ROOM
}
