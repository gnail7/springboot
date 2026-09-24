package com.example.springboot.demohub.model;

import java.util.Map;

/**
 * Demo Hub 对外展示的统一 Demo 定义。
 *
 * @param code            稳定的 Demo 标识，前端路由和分享链接使用它
 * @param name            展示名称
 * @param description     简要说明
 * @param type            技术主题
 * @param handlerKey      后端处理器标识，便于未来切换到数据库配置
 * @param interactionMode 交互模式
 * @param config          Demo 的非敏感展示配置
 */
public record DemoDefinition(
        String code,
        String name,
        String description,
        DemoType type,
        String handlerKey,
        InteractionMode interactionMode,
        Map<String, Object> config
) {
}
