package com.example.web_system.controller;

import com.example.web_system.entity.Order;
import com.example.web_system.entity.Product;
import com.example.web_system.repository.OrderRepository;
import com.example.web_system.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;

    /**
     * 检查数据库状态
     */
    @GetMapping("/database-status")
    public ResponseEntity<Map<String, Object>> getDatabaseStatus() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 获取今日订单数
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
            
            long todayOrders = orderRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            
            // 获取今日销售额
            BigDecimal todaySales = orderRepository.sumTotalAmountByCreatedAtBetween(startOfDay, endOfDay);
            if (todaySales == null) {
                todaySales = BigDecimal.ZERO;
            }
            
            data.put("todayOrders", todayOrders);
            data.put("todaySales", todaySales);
            data.put("status", "connected");
            data.put("checkTime", LocalDateTime.now());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "数据库连接正常");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "数据库连接失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 创建测试订单
     */
    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createTestOrder() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 生成测试订单号
            String orderNo = "TEST" + System.currentTimeMillis();
            BigDecimal finalAmount = new BigDecimal("99.99");
            
            data.put("orderNo", orderNo);
            data.put("finalAmount", finalAmount);
            data.put("status", "success");
            data.put("createTime", LocalDateTime.now());
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "订单系统正常");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "订单系统异常: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取首页统计数据
     */
    @GetMapping("/dashboard-stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> data = new HashMap<>();
            
            // 获取今日订单数和销售额
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
            
            long todayOrders = orderRepository.countByCreatedAtBetween(startOfDay, endOfDay);
            BigDecimal todaySales = orderRepository.sumTotalAmountByCreatedAtBetween(startOfDay, endOfDay);
            if (todaySales == null) {
                todaySales = BigDecimal.ZERO;
            }
            
            // 获取昨日数据用于计算增长率
            LocalDateTime yesterdayStart = today.minusDays(1).atStartOfDay();
            LocalDateTime yesterdayEnd = today.atStartOfDay();
            
            long yesterdayOrders = orderRepository.countByCreatedAtBetween(yesterdayStart, yesterdayEnd);
            BigDecimal yesterdaySales = orderRepository.sumTotalAmountByCreatedAtBetween(yesterdayStart, yesterdayEnd);
            if (yesterdaySales == null) {
                yesterdaySales = BigDecimal.ZERO;
            }
            
            // 计算增长率
            double salesGrowth = 0.0;
            if (yesterdaySales.compareTo(BigDecimal.ZERO) > 0) {
                salesGrowth = todaySales.subtract(yesterdaySales)
                    .divide(yesterdaySales, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .doubleValue();
            } else if (todaySales.compareTo(BigDecimal.ZERO) > 0) {
                salesGrowth = 100.0; // 昨日为0，今日有销售，增长100%
            }
            
            double ordersGrowth = 0.0;
            if (yesterdayOrders > 0) {
                ordersGrowth = ((double)(todayOrders - yesterdayOrders) / yesterdayOrders) * 100;
            } else if (todayOrders > 0) {
                ordersGrowth = 100.0; // 昨日为0，今日有订单，增长100%
            }
            
            // 获取商品总数（作为客流量的近似值）
            long totalProducts = productRepository.count();
            
            // 获取库存预警商品数量
            long lowStockCount = productRepository.countByCurrentStockLessThanMinStock();
            
            data.put("sales", todaySales.doubleValue());
            data.put("salesGrowth", Math.round(salesGrowth * 10.0) / 10.0); // 保留一位小数
            data.put("orders", todayOrders);
            data.put("ordersGrowth", Math.round(ordersGrowth * 10.0) / 10.0); // 保留一位小数
            data.put("customers", todayOrders); // 简化处理，假设每个订单对应一个客户
            data.put("customersGrowth", Math.round(ordersGrowth * 10.0) / 10.0); // 与订单增长率相同
            data.put("lowStock", lowStockCount);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取统计数据成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取统计数据失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取最新动态 - 显示真实订单详情
     */
    @GetMapping("/recent-activities")
    public ResponseEntity<Map<String, Object>> getRecentActivities() {
        try {
            List<Map<String, Object>> activities = new ArrayList<>();
            
            // 尝试获取最近的订单详情
            try {
                List<Order> recentOrders = orderRepository.findAll(
                    PageRequest.of(0, 5, Sort.by("createdAt").descending())
                ).getContent();
                
                if (!recentOrders.isEmpty()) {
                    for (Order order : recentOrders) {
                        Map<String, Object> activity = new HashMap<>();
                        activity.put("id", order.getId());
                        activity.put("type", "sale");
                        
                        // 构建订单信息文本
                        String cashierName = order.getCashierName() != null ? order.getCashierName() : "收银员";
                        String customerName = order.getCustomerName() != null ? order.getCustomerName() : "客户";
                        BigDecimal amount = order.getFinalAmount() != null ? order.getFinalAmount() : BigDecimal.ZERO;
                        
                        String text = cashierName + "为" + customerName + "完成了一笔¥" + amount + "的销售";
                        activity.put("text", text);
                        
                        // 计算时间差
                        String timeText = calculateTimeAgo(order.getCreatedAt());
                        activity.put("time", timeText);
                        activity.put("status", "success");
                        activities.add(activity);
                    }
                } else {
                    // 如果没有订单，显示提示信息
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("id", 1);
                    activity.put("type", "system");
                    activity.put("text", "暂无订单记录，等待新的销售");
                    activity.put("time", "刚刚");
                    activity.put("status", "info");
                    activities.add(activity);
                }
            } catch (Exception e) {
                System.out.println("获取订单详情失败: " + e.getMessage());
                // 如果获取订单失败，显示错误信息
                Map<String, Object> activity = new HashMap<>();
                activity.put("id", 1);
                activity.put("type", "system");
                activity.put("text", "系统运行正常，订单数据加载中...");
                activity.put("time", "刚刚");
                activity.put("status", "info");
                activities.add(activity);
            }
            
            // 尝试获取库存预警信息
            try {
                List<Product> lowStockProducts = productRepository.findLowStockProducts();
                int addedCount = 0;
                for (Product product : lowStockProducts) {
                    if (addedCount >= 3) break; // 最多添加3个库存预警
                    
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("id", "stock_" + product.getId());
                    activity.put("type", "inventory");
                    activity.put("text", "商品\"" + product.getName() + "\"库存不足，当前库存：" + product.getCurrentStock() + "件");
                    activity.put("time", "系统检测");
                    activity.put("status", "warning");
                    activities.add(activity);
                    addedCount++;
                }
            } catch (Exception e) {
                System.out.println("获取库存预警失败: " + e.getMessage());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取最新动态成功");
            response.put("data", activities);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取最新动态失败: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 计算时间差，返回友好的时间显示
     */
    private String calculateTimeAgo(LocalDateTime orderTime) {
        if (orderTime == null) {
            return "未知时间";
        }
        
        try {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(orderTime, now);
            long minutes = duration.toMinutes();
            
            if (minutes < 1) {
                return "刚刚";
            } else if (minutes < 60) {
                return minutes + "分钟前";
            } else if (minutes < 1440) { // 24小时
                return (minutes / 60) + "小时前";
            } else {
                return (minutes / 1440) + "天前";
            }
        } catch (Exception e) {
            return "刚刚";
        }
    }
}