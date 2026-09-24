package com.example.springboot.demohub.service;

import com.example.springboot.demohub.model.DemoDefinition;
import com.example.springboot.demohub.model.DemoType;
import com.example.springboot.demohub.model.InteractionMode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Demo 目录服务。
 *
 * <p>当前使用代码注册，方便项目还没有执行数据库迁移时直接启动体验；
 * SQL 目录结构已同时提供，后续可将这里替换为 Mapper，而不影响 Controller 和前端协议。</p>
 */
@Service
public class DemoCatalog {

    private final List<DemoDefinition> definitions = List.of(
            new DemoDefinition(
                    "sse-stream",
                    "SSE 实时事件流",
                    "打开连接后，服务端每秒推送一条事件，观察浏览器如何接收服务端消息。",
                    DemoType.SSE,
                    "sse-demo",
                    InteractionMode.STREAM,
                    Map.of("eventCount", 5, "intervalMs", 1000)
            ),
            new DemoDefinition(
                    "scan-concurrency",
                    "扫码并发体验",
                    "多人扫码加入同一个房间后，由服务端统一触发并发请求。",
                    DemoType.CONCURRENCY,
                    "concurrency-demo",
                    InteractionMode.ROOM,
                    Map.of("maxParticipants", 20, "ttlSeconds", 600)
            )
    );

    public List<DemoDefinition> listPublished() {
        return definitions;
    }

    public DemoDefinition getRequired(String code) {
        return definitions.stream()
                .filter(definition -> definition.code().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Demo 不存在: " + code));
    }
}
