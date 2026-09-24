package com.example.springboot.demohub.controller;

import com.example.springboot.demohub.service.SseDemoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/** SSE Demo 的在线体验接口。 */
@RestController
@RequestMapping("/demo-hub/demos/sse-stream")
public class SseDemoController {

    private final SseDemoService sseDemoService;

    public SseDemoController(SseDemoService sseDemoService) {
        this.sseDemoService = sseDemoService;
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
            @RequestParam(defaultValue = "5") int eventCount,
            @RequestParam(defaultValue = "1000") long intervalMs) {
        return sseDemoService.open(eventCount, intervalMs);
    }
}
