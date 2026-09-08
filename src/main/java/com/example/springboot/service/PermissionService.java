package com.example.springboot.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限校验服务（供 @PreAuthorize 使用）
 *
 * <p>权限来源：JwtInterceptor 在解析 token 后，把当前用户的权限码（sys_menu.perms）
 * 写入 SecurityContext 的 authorities；超级管理员（role_key=admin）额外获得
 * 通配权限 {@code *:*:*}。</p>
 *
 * <p>用法：{@code @PreAuthorize("@permissionService.hasPerm('system:user:add')")}</p>
 */
@Service
public class PermissionService {

    /** 超级管理员通配权限（与前端 utils/permission.js 约定一致） */
    private static final String ALL_PERMISSION = "*:*:*";

    /**
     * 当前用户是否拥有指定权限码（支持 *:*:* 通配与 xxx:* 段通配）
     *
     * @param permission 权限码，如 system:user:add
     */
    public boolean hasPerm(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Collection<String> owned = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return matchPermission(owned, permission);
    }

    /** 单个权限码与已拥有权限匹配（逐段通配比较，逻辑同前端） */
    private boolean matchPermission(Collection<String> owned, String code) {
        if (code == null || code.isEmpty()) {
            return true;
        }
        if (owned.contains(ALL_PERMISSION)) {
            return true;
        }
        String[] segments = code.split(":");
        return owned.stream().anyMatch(pattern -> {
            String[] parts = pattern.split(":");
            if (parts.length != segments.length) {
                return false;
            }
            for (int i = 0; i < parts.length; i++) {
                if (!"*".equals(parts[i]) && !parts[i].equals(segments[i])) {
                    return false;
                }
            }
            return true;
        });
    }
}
