package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.dto.OrderDTO;
import com.example.web_system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getOrderList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String cashier,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        try {
            // 页码从0开始
            Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
            
            Page<OrderDTO> orderPage = orderService.getOrderList(orderNo, cashier, startTime, endTime, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", orderPage.getTotalElements());
            result.put("items", orderPage.getContent());
            result.put("currentPage", page);
            result.put("pageSize", limit);
            result.put("totalPages", orderPage.getTotalPages());
            
            return ApiResponse.success("获取订单列表成功", result);
        } catch (Exception e) {
            return ApiResponse.error("获取订单列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public ApiResponse<OrderDTO> getOrderById(@PathVariable Long id) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            return ApiResponse.success("获取订单详情成功", order);
        } catch (Exception e) {
            return ApiResponse.error("获取订单详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据订单号获取订单详情
     */
    @GetMapping("/orderNo/{orderNo}")
    public ApiResponse<OrderDTO> getOrderByOrderNo(@PathVariable String orderNo) {
        try {
            OrderDTO order = orderService.getOrderByOrderNo(orderNo);
            return ApiResponse.success("获取订单详情成功", order);
        } catch (Exception e) {
            return ApiResponse.error("获取订单详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新订单
     */
    @PutMapping("/{id}")
    public ApiResponse<OrderDTO> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        try {
            OrderDTO updatedOrder = orderService.updateOrder(id, orderDTO);
            return ApiResponse.success("订单更新成功", updatedOrder);
        } catch (Exception e) {
            return ApiResponse.error("订单更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除订单（仅允许删除已取消或已退款）
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ApiResponse.success("删除成功");
        } catch (Exception e) {
            return ApiResponse.error("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    public ApiResponse<String> cancelOrder(@PathVariable Long id) {
        try {
            orderService.cancelOrder(id);
            return ApiResponse.success("订单取消成功");
        } catch (Exception e) {
            return ApiResponse.error("订单取消失败: " + e.getMessage());
        }
    }
    
    /**
     * 订单退款
     */
    @PutMapping("/{id}/refund")
    public ApiResponse<OrderDTO> refundOrder(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            OrderDTO refundedOrder = orderService.refundOrder(id, request);
            return ApiResponse.success("订单退款成功", refundedOrder);
        } catch (Exception e) {
            return ApiResponse.error("订单退款失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取订单统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getOrderStatistics() {
        try {
            Map<String, Object> statistics = orderService.getOrderStatistics();
            return ApiResponse.success("获取订单统计成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取订单统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建订单
     */
    @PostMapping
    public ApiResponse<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            OrderDTO createdOrder = orderService.createOrder(orderDTO);
            return ApiResponse.success("订单创建成功", createdOrder);
        } catch (Exception e) {
            return ApiResponse.error("订单创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试接口 - 验证服务是否正常
     */
    @GetMapping("/test")
    public ApiResponse<String> testApi() {
        return ApiResponse.success("订单服务正常运行");
    }
    
    /**
     * 导出订单数据
     */
    @GetMapping("/export")
    public void exportOrderData(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String cashier,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
            jakarta.servlet.http.HttpServletResponse response) {
        
        try {
            // 直接使用CSV格式导出
            System.out.println("开始CSV导出，参数: orderNo=" + orderNo + ", cashier=" + cashier);
            
            // 设置响应头，确保前端能正确识别
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            
            // 设置CSV文件名
            String csvFileName = "销售订单数据_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(csvFileName, "UTF-8") + "\"");
            
            // 调用服务层导出数据
            orderService.exportOrderData(orderNo, cashier, startTime, endTime, response.getOutputStream());
            
            System.out.println("CSV导出完成");
            
        } catch (Exception e) {
            // 如果导出失败，返回错误信息
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            try {
                response.getWriter().write("{\"code\": 500, \"message\": \"导出失败: " + e.getMessage() + "\"}");
            } catch (java.io.IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}