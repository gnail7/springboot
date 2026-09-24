package com.example.springboot.demohub.controller;

import com.example.springboot.demohub.model.DemoDefinition;
import com.example.springboot.demohub.service.DemoCatalog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Demo Hub 公共目录接口。
 *
 * <p>故意不放在 /api 下：当前项目的 JWT 拦截器只拦截 /api/**，
 * 因此 Demo Hub 可以在没有登录态的情况下直接在线体验。</p>
 */
@RestController
@RequestMapping("/demo-hub")
public class DemoHubController {

    private final DemoCatalog demoCatalog;

    public DemoHubController(DemoCatalog demoCatalog) {
        this.demoCatalog = demoCatalog;
    }

    @GetMapping("/demos")
    public List<DemoDefinition> list() {
        return demoCatalog.listPublished();
    }

    @GetMapping("/demos/{code}")
    public DemoDefinition get(@PathVariable String code) {
        return demoCatalog.getRequired(code);
    }
}
