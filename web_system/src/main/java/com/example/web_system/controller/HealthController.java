package com.example.web_system.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@CrossOrigin(origins = "*")
public class HealthController {

    @GetMapping("/check")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "服务正常运行");
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "智慧超市系统");
        return response;
    }
    
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取系统信息成功");
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "智慧超市管理系统");
        data.put("version", "1.0.0");
        data.put("description", "基于Spring Boot + Vue的超市管理系统");
        data.put("author", "Kiro AI");
        
        response.put("data", data);
        return response;
    }
}