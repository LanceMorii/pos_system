package com.example.web_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupCheck implements CommandLineRunner {
    
    @Autowired
    private OrderServiceConfig orderServiceConfig;
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== 智慧超市系统启动检查 ===");
        System.out.println("数据库模式: " + (orderServiceConfig.isUseDatabaseMode() ? "启用" : "禁用"));
        
        if (orderServiceConfig.isUseDatabaseMode()) {
            System.out.println("✅ 系统将使用真实数据库操作");
            System.out.println("✅ 收银台订单将保存到数据库");
            System.out.println("✅ 销售分析将显示真实数据");
        } else {
            System.out.println("⚠️  系统将使用模拟数据模式");
            System.out.println("⚠️  收银台订单不会保存到数据库");
            System.out.println("⚠️  销售分析将显示模拟数据");
        }
        
        System.out.println("=== 启动检查完成 ===");
    }
}