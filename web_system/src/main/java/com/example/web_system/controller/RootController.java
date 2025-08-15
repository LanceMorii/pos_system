package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public ApiResponse<Map<String, Object>> root() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "智慧超市系统API");
        result.put("version", "1.0.0");
        result.put("status", "运行中");
        result.put("timestamp", System.currentTimeMillis());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("健康检查", "/test/health");
        endpoints.put("Redis测试", "/test/redis");
        endpoints.put("用户登录", "/auth/login");
        endpoints.put("用户注册", "/auth/register");
        endpoints.put("用户信息", "/auth/info");
        endpoints.put("用户列表", "/user/list");
        
        result.put("endpoints", endpoints);
        
        return ApiResponse.success("欢迎使用智慧超市系统API", result);
    }
}