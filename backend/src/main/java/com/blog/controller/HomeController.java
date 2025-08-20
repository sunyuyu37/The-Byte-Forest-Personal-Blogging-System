package com.blog.controller;

import com.blog.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Result<Map<String, Object>> home() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "个人博客系统 API 服务");
        data.put("version", "1.0.0");
        data.put("status", "running");
        data.put("timestamp", LocalDateTime.now());
        data.put("endpoints", Map.of(
            "认证", "/auth",
            "文档", "/swagger-ui.html",
            "健康检查", "/health"
        ));
        
        return Result.success(data);
    }

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        
        return Result.success(data);
    }
}