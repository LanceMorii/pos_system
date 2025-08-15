package com.example.web_system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.order")
public class OrderServiceConfig {
    
    /**
     * 是否使用数据库模式
     * true: 使用真实数据库操作
     * false: 使用模拟数据（默认）
     */
    private boolean useDatabaseMode = true;
    
    public boolean isUseDatabaseMode() {
        return useDatabaseMode;
    }
    
    public void setUseDatabaseMode(boolean useDatabaseMode) {
        this.useDatabaseMode = useDatabaseMode;
    }
}