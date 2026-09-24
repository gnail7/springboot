package com.example.springboot.demohub.service;

import jakarta.annotation.PreDestroy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 一个可直接在线体验的 SSE Demo 实现。
 * 每个请求独立调度，不依赖登录态，也不把高频事件写入数据库。
 */
@Service
public class SseDemoService {

    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(2);

    public SseEmitter open(int eventCount, long intervalMs) {
        int safeEventCount = Math.min(Math.max(eventCount, 1), 20);
        long safeIntervalMs = Math.min(Math.max(intervalMs, 200), 10_000);
        SseEmitter emitter = new SseEmitter((safeEventCount + 2L) * safeIntervalMs);
        AtomicInteger sequence = new AtomicInteger(0);
        AtomicReference<ScheduledFuture<?>> taskRef = new AtomicReference<>();

        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            int current = sequence.incrementAndGet();
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(current))
                        .name("demo-tick")
                        .data(Map.of(
                                "sequence", current,
                                "message", "来自 Spring Boot 的 SSE 事件",
                                "sentAt", Instant.now().toString()
                        ), MediaType.APPLICATION_JSON));

                if (current >= safeEventCount) {
                    taskComplete(taskRef.get(), emitter);
                }
            } catch (IOException | IllegalStateException e) {
                cancel(taskRef.get());
            }
        }, safeIntervalMs, safeIntervalMs, TimeUnit.MILLISECONDS);
        taskRef.set(task);

        emitter.onCompletion(() -> cancel(taskRef.get()));
        emitter.onTimeout(() -> cancel(taskRef.get()));
        emitter.onError(error -> cancel(taskRef.get()));
        return emitter;
    }

    private void taskComplete(ScheduledFuture<?> task, SseEmitter emitter) {
        cancel(task);
        emitter.complete();
    }

    private void cancel(ScheduledFuture<?> task) {
        if (task != null) {
            task.cancel(false);
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
