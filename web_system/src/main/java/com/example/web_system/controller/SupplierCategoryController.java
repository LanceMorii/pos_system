package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.dto.SupplierCategoryDTO;
import com.example.web_system.service.SupplierCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/supplier-category")
@CrossOrigin(origins = "*")
public class SupplierCategoryController {
    
    @Autowired
    private SupplierCategoryService categoryService;
    
    /**
     * 获取分类列表
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getCategoryList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String status) {
        
        try {
            // 页码从0开始
            Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("sortOrder").ascending());
            
            Page<SupplierCategoryDTO> categoryPage = categoryService.getCategoryList(
                categoryCode, categoryName, status, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", categoryPage.getTotalElements());
            result.put("items", categoryPage.getContent());
            result.put("currentPage", page);
            result.put("pageSize", limit);
            result.put("totalPages", categoryPage.getTotalPages());
            
            return ApiResponse.success("获取分类列表成功", result);
        } catch (Exception e) {
            return ApiResponse.error("获取分类列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public ApiResponse<SupplierCategoryDTO> getCategoryById(@PathVariable Long id) {
        try {
            SupplierCategoryDTO category = categoryService.getCategoryById(id);
            return ApiResponse.success("获取分类详情成功", category);
        } catch (Exception e) {
            return ApiResponse.error("获取分类详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建分类
     */
    @PostMapping
    public ApiResponse<SupplierCategoryDTO> createCategory(@RequestBody SupplierCategoryDTO categoryDTO) {
        try {
            SupplierCategoryDTO createdCategory = categoryService.createCategory(categoryDTO);
            return ApiResponse.success("创建分类成功", createdCategory);
        } catch (Exception e) {
            return ApiResponse.error("创建分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public ApiResponse<SupplierCategoryDTO> updateCategory(@PathVariable Long id, @RequestBody SupplierCategoryDTO categoryDTO) {
        try {
            SupplierCategoryDTO updatedCategory = categoryService.updateCategory(id, categoryDTO);
            return ApiResponse.success("更新分类成功", updatedCategory);
        } catch (Exception e) {
            return ApiResponse.error("更新分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ApiResponse.success("删除分类成功");
        } catch (Exception e) {
            return ApiResponse.error("删除分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新分类状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<SupplierCategoryDTO> updateCategoryStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            SupplierCategoryDTO updatedCategory = categoryService.updateCategoryStatus(id, status);
            return ApiResponse.success("更新分类状态成功", updatedCategory);
        } catch (Exception e) {
            return ApiResponse.error("更新分类状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取启用状态的分类列表（用于下拉选择）
     */
    @GetMapping("/active")
    public ApiResponse<List<SupplierCategoryDTO>> getActiveCategories() {
        try {
            List<SupplierCategoryDTO> categories = categoryService.getActiveCategories();
            return ApiResponse.success("获取启用分类列表成功", categories);
        } catch (Exception e) {
            return ApiResponse.error("获取启用分类列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试接口 - 验证服务是否正常
     */
    @GetMapping("/test")
    public ApiResponse<String> testApi() {
        return ApiResponse.success("分类管理服务正常运行");
    }
}