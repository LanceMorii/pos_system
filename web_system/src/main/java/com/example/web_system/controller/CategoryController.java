package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.dto.CategoryDTO;
import com.example.web_system.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
@CrossOrigin(origins = "*")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * 获取所有活跃分类
     */
    @GetMapping("/list")
    public ApiResponse<List<CategoryDTO>> getAllCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getAllActiveCategories();
            return ApiResponse.success("获取分类列表成功", categories);
        } catch (Exception e) {
            return ApiResponse.error("获取分类列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取分类详情
     */
    @GetMapping("/{id}")
    public ApiResponse<CategoryDTO> getCategoryById(@PathVariable Long id) {
        try {
            CategoryDTO category = categoryService.getCategoryById(id);
            return ApiResponse.success("获取分类详情成功", category);
        } catch (Exception e) {
            return ApiResponse.error("获取分类详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建分类
     */
    @PostMapping("/add")
    public ApiResponse<CategoryDTO> createCategory(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String description = request.get("description");
            
            if (name == null || name.trim().isEmpty()) {
                return ApiResponse.error("分类名称不能为空");
            }
            
            CategoryDTO category = categoryService.createCategory(name.trim(), description);
            return ApiResponse.success("创建分类成功", category);
        } catch (Exception e) {
            return ApiResponse.error("创建分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新分类
     */
    @PutMapping("/update/{id}")
    public ApiResponse<CategoryDTO> updateCategory(
            @PathVariable Long id, 
            @RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String description = request.get("description");
            
            if (name == null || name.trim().isEmpty()) {
                return ApiResponse.error("分类名称不能为空");
            }
            
            CategoryDTO category = categoryService.updateCategory(id, name.trim(), description);
            return ApiResponse.success("更新分类成功", category);
        } catch (Exception e) {
            return ApiResponse.error("更新分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除分类
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ApiResponse.success("删除分类成功", null);
        } catch (Exception e) {
            return ApiResponse.error("删除分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取根分类列表
     */
    @GetMapping("/root")
    public ApiResponse<List<CategoryDTO>> getRootCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getRootCategories();
            return ApiResponse.success("获取根分类列表成功", categories);
        } catch (Exception e) {
            return ApiResponse.error("获取根分类列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据父分类ID获取子分类
     */
    @GetMapping("/sub/{parentId}")
    public ApiResponse<List<CategoryDTO>> getSubCategories(@PathVariable Long parentId) {
        try {
            List<CategoryDTO> categories = categoryService.getSubCategories(parentId);
            return ApiResponse.success("获取子分类列表成功", categories);
        } catch (Exception e) {
            return ApiResponse.error("获取子分类列表失败: " + e.getMessage());
        }
    }
}