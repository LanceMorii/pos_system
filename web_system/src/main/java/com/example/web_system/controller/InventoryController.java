package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.dto.*;
import com.example.web_system.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    /**
     * 获取库存列表
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getInventoryList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String stockStatus) {
        
        try {
            // 页码从0开始
            Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
            
            Page<ProductDTO> inventoryPage = inventoryService.getInventoryList(name, stockStatus, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", inventoryPage.getTotalElements());
            result.put("items", inventoryPage.getContent());
            result.put("currentPage", page);
            result.put("pageSize", limit);
            result.put("totalPages", inventoryPage.getTotalPages());
            
            return ApiResponse.success("获取库存列表成功", result);
        } catch (Exception e) {
            return ApiResponse.error("获取库存列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取库存预警商品列表
     */
    @GetMapping("/low-stock")
    public ApiResponse<List<ProductDTO>> getLowStockProducts() {
        try {
            List<ProductDTO> products = inventoryService.getLowStockProducts();
            return ApiResponse.success("获取库存预警商品成功", products);
        } catch (Exception e) {
            return ApiResponse.error("获取库存预警商品失败: " + e.getMessage());
        }
    }
    
    /**
     * 商品入库
     */
    @PostMapping("/stock-in")
    public ApiResponse<StockRecordDTO> stockIn(@Valid @RequestBody StockInRequest request) {
        try {
            // 暂时使用固定的操作员ID，实际项目中应该从认证信息获取
            Long operatorId = 1L;
            
            StockRecordDTO record = inventoryService.stockIn(request, operatorId);
            return ApiResponse.success("入库成功", record);
        } catch (Exception e) {
            return ApiResponse.error("入库失败: " + e.getMessage());
        }
    }
    
    /**
     * 商品出库
     */
    @PostMapping("/stock-out")
    public ApiResponse<StockRecordDTO> stockOut(@Valid @RequestBody StockOutRequest request) {
        try {
            // 暂时使用固定的操作员ID，实际项目中应该从认证信息获取
            Long operatorId = 1L;
            
            StockRecordDTO record = inventoryService.stockOut(request, operatorId);
            return ApiResponse.success("出库成功", record);
        } catch (Exception e) {
            return ApiResponse.error("出库失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取商品库存记录
     */
    @GetMapping("/records/{productId}")
    public ApiResponse<Map<String, Object>> getStockRecords(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        
        try {
            // 页码从0开始
            Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("createdAt").descending());
            
            Page<StockRecordDTO> recordPage = inventoryService.getStockRecords(
                productId, type, startTime, endTime, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", recordPage.getTotalElements());
            result.put("items", recordPage.getContent());
            result.put("currentPage", page);
            result.put("pageSize", limit);
            result.put("totalPages", recordPage.getTotalPages());
            
            return ApiResponse.success("获取库存记录成功", result);
        } catch (Exception e) {
            return ApiResponse.error("获取库存记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量入库
     */
    @PostMapping("/batch-stock-in")
    public ApiResponse<List<StockRecordDTO>> batchStockIn(@Valid @RequestBody List<StockInRequest> requests) {
        try {
            // 暂时使用固定的操作员ID，实际项目中应该从认证信息获取
            Long operatorId = 1L;
            
            List<StockRecordDTO> records = requests.stream()
                .map(request -> inventoryService.stockIn(request, operatorId))
                .collect(java.util.stream.Collectors.toList());
            
            return ApiResponse.success("批量入库成功", records);
        } catch (Exception e) {
            return ApiResponse.error("批量入库失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量出库
     */
    @PostMapping("/batch-stock-out")
    public ApiResponse<List<StockRecordDTO>> batchStockOut(@Valid @RequestBody List<StockOutRequest> requests) {
        try {
            // 暂时使用固定的操作员ID，实际项目中应该从认证信息获取
            Long operatorId = 1L;
            
            List<StockRecordDTO> records = requests.stream()
                .map(request -> inventoryService.stockOut(request, operatorId))
                .collect(java.util.stream.Collectors.toList());
            
            return ApiResponse.success("批量出库成功", records);
        } catch (Exception e) {
            return ApiResponse.error("批量出库失败: " + e.getMessage());
        }
    }
    
    /**
     * 库存盘点
     */
    @PostMapping("/inventory-check")
    public ApiResponse<String> inventoryCheck(@RequestBody Map<String, Object> request) {
        try {
            Long operatorId = 1L; // 实际项目中从认证信息获取
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> checkItems = (List<Map<String, Object>>) request.get("items");
            String remark = (String) request.get("remark");
            
            inventoryService.inventoryCheck(checkItems, operatorId, remark);
            return ApiResponse.success("库存盘点完成");
        } catch (Exception e) {
            return ApiResponse.error("库存盘点失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取库存统计报表
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getInventoryStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endDate) {
        try {
            Map<String, Object> statistics = inventoryService.getInventoryStatistics(startDate, endDate);
            return ApiResponse.success("获取库存统计成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取库存统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出库存报表
     */
    @GetMapping("/export")
    public ApiResponse<String> exportInventoryReport(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String stockStatus) {
        try {
            String filePath = inventoryService.exportInventoryReport(name, stockStatus);
            return ApiResponse.success("导出成功", filePath);
        } catch (Exception e) {
            return ApiResponse.error("导出失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置库存预警阈值
     */
    @PostMapping("/set-alert-threshold")
    public ApiResponse<String> setAlertThreshold(@RequestBody Map<String, Object> request) {
        try {
            Long productId = Long.valueOf(request.get("productId").toString());
            Integer minStock = Integer.valueOf(request.get("minStock").toString());
            Integer maxStock = Integer.valueOf(request.get("maxStock").toString());
            
            inventoryService.setAlertThreshold(productId, minStock, maxStock);
            return ApiResponse.success("设置预警阈值成功");
        } catch (Exception e) {
            return ApiResponse.error("设置预警阈值失败: " + e.getMessage());
        }
    }
}