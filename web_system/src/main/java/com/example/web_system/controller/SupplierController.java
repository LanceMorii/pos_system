package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.dto.SupplierDTO;
import com.example.web_system.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/supplier")
@CrossOrigin(origins = "*")
public class SupplierController {
    
    @Autowired
    private SupplierService supplierService;
    
    /**
     * 获取供应商列表
     */
    @GetMapping("/list")
    public ApiResponse<List<SupplierDTO>> getSupplierList(
            @RequestParam(required = false) String supplierCode,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String contactPerson,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        
        try {
            List<SupplierDTO> suppliers = supplierService.getSupplierList(
                supplierCode, supplierName, contactPerson, category, status);
            
            return ApiResponse.success("获取供应商列表成功", suppliers);
        } catch (Exception e) {
            return ApiResponse.error("获取供应商列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取供应商详情
     */
    @GetMapping("/{id}")
    public ApiResponse<SupplierDTO> getSupplierById(@PathVariable Long id) {
        try {
            SupplierDTO supplier = supplierService.getSupplierById(id);
            return ApiResponse.success("获取供应商详情成功", supplier);
        } catch (Exception e) {
            return ApiResponse.error("获取供应商详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据供应商编码获取供应商详情
     */
    @GetMapping("/code/{supplierCode}")
    public ApiResponse<SupplierDTO> getSupplierByCode(@PathVariable String supplierCode) {
        try {
            SupplierDTO supplier = supplierService.getSupplierByCode(supplierCode);
            return ApiResponse.success("获取供应商详情成功", supplier);
        } catch (Exception e) {
            return ApiResponse.error("获取供应商详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建供应商
     */
    @PostMapping
    public ApiResponse<SupplierDTO> createSupplier(@RequestBody SupplierDTO supplierDTO) {
        try {
            SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);
            return ApiResponse.success("创建供应商成功", createdSupplier);
        } catch (Exception e) {
            return ApiResponse.error("创建供应商失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新供应商
     */
    @PutMapping("/{id}")
    public ApiResponse<SupplierDTO> updateSupplier(@PathVariable Long id, @RequestBody SupplierDTO supplierDTO) {
        try {
            SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
            return ApiResponse.success("更新供应商成功", updatedSupplier);
        } catch (Exception e) {
            return ApiResponse.error("更新供应商失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除供应商
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteSupplier(@PathVariable Long id) {
        try {
            supplierService.deleteSupplier(id);
            return ApiResponse.success("删除供应商成功");
        } catch (Exception e) {
            return ApiResponse.error("删除供应商失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新供应商状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<SupplierDTO> updateSupplierStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            SupplierDTO updatedSupplier = supplierService.updateSupplierStatus(id, status);
            return ApiResponse.success("更新供应商状态成功", updatedSupplier);
        } catch (Exception e) {
            return ApiResponse.error("更新供应商状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有分类
     */
    @GetMapping("/categories")
    public ApiResponse<List<String>> getAllCategories() {
        try {
            List<String> categories = supplierService.getAllCategories();
            return ApiResponse.success("获取分类列表成功", categories);
        } catch (Exception e) {
            return ApiResponse.error("获取分类列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取正常状态的供应商列表（用于下拉选择）
     */
    @GetMapping("/active")
    public ApiResponse<List<SupplierDTO>> getActiveSuppliers() {
        try {
            List<SupplierDTO> suppliers = supplierService.getActiveSuppliers();
            return ApiResponse.success("获取正常供应商列表成功", suppliers);
        } catch (Exception e) {
            return ApiResponse.error("获取正常供应商列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取供应商统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getSupplierStatistics() {
        try {
            Map<String, Object> statistics = supplierService.getSupplierStatistics();
            return ApiResponse.success("获取供应商统计成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取供应商统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量导入供应商
     */
    @PostMapping("/batch-import")
    public ApiResponse<Map<String, Object>> batchImportSuppliers(@RequestBody List<SupplierDTO> supplierDTOs) {
        try {
            Map<String, Object> result = supplierService.batchImportSuppliers(supplierDTOs);
            return ApiResponse.success("批量导入完成", result);
        } catch (Exception e) {
            return ApiResponse.error("批量导入失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出供应商数据
     */
    @GetMapping("/export")
    public void exportSupplierData(
            @RequestParam(required = false) String supplierCode,
            @RequestParam(required = false) String supplierName,
            @RequestParam(required = false) String contactPerson,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            jakarta.servlet.http.HttpServletResponse response) {
        
        try {
            System.out.println("开始Excel导出，参数: supplierCode=" + supplierCode + 
                             ", supplierName=" + supplierName + ", category=" + category);
            
            // 设置响应头，确保前端能正确识别
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            
            // 设置Excel文件名
            String excelFileName = "供应商数据_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + java.net.URLEncoder.encode(excelFileName, "UTF-8") + "\"");
            
            // 调用服务层导出数据
            supplierService.exportSuppliersToExcel(supplierCode, supplierName, contactPerson, category, status, response.getOutputStream());
            
            System.out.println("Excel导出完成");
            
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
    
    /**
     * 测试接口 - 验证服务是否正常
     */
    @GetMapping("/test")
    public ApiResponse<String> testApi() {
        return ApiResponse.success("供应商服务正常运行");
    }
}