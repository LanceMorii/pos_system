package com.example.web_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WebSystemApplication {


    public static void main(String[] args) {
        SpringApplication.run(WebSystemApplication.class, args);
        System.out.println("Maven智慧超市系统后端启动成功！");
        System.out.println("访问地址: http://localhost:8080/api");
    }

    }
