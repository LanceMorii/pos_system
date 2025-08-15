package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.dto.ProductDTO;
import com.example.web_system.dto.ProductRequest;
import com.example.web_system.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    /**
     * 查询商品列表
     */
    @GetMapping("/list")
    public ApiResponse<List<ProductDTO>> getProductList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status) {
        
        try {
            List<ProductDTO> products = productService.getProducts(name, categoryId, status);
            return ApiResponse.success("获取商品列表成功", products);
        } catch (Exception e) {
            return ApiResponse.error("获取商品列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取商品详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductDTO> getProductById(@PathVariable Long id) {
        try {
            ProductDTO product = productService.getProductById(id);
            return ApiResponse.success("获取商品详情成功", product);
        } catch (Exception e) {
            return ApiResponse.error("获取商品详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建商品
     */
    @PostMapping("/add")
    public ApiResponse<ProductDTO> createProduct(@Valid @RequestBody ProductRequest request) {
        try {
            ProductDTO product = productService.createProduct(request);
            return ApiResponse.success("创建商品成功", product);
        } catch (Exception e) {
            return ApiResponse.error("创建商品失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新商品
     */
    @PutMapping("/update/{id}")
    public ApiResponse<ProductDTO> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductRequest request) {
        try {
            ProductDTO product = productService.updateProduct(id, request);
            return ApiResponse.success("更新商品成功", product);
        } catch (Exception e) {
            return ApiResponse.error("更新商品失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ApiResponse.success("删除商品成功", null);
        } catch (Exception e) {
            return ApiResponse.error("删除商品失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除商品
     */
    @DeleteMapping("/batch-delete")
    public ApiResponse<Void> batchDeleteProducts(@RequestBody List<Long> ids) {
        try {
            productService.deleteProducts(ids);
            return ApiResponse.success("批量删除商品成功", null);
        } catch (Exception e) {
            return ApiResponse.error("批量删除商品失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据条形码查找商品
     */
    @GetMapping("/barcode/{barcode}")
    public ApiResponse<ProductDTO> getProductByBarcode(@PathVariable String barcode) {
        try {
            ProductDTO product = productService.getProductByBarcode(barcode);
            return ApiResponse.success("查找商品成功", product);
        } catch (Exception e) {
            return ApiResponse.error("查找商品失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取库存不足的商品列表
     */
    @GetMapping("/low-stock")
    public ApiResponse<List<ProductDTO>> getLowStockProducts() {
        try {
            List<ProductDTO> products = productService.getLowStockProducts();
            return ApiResponse.success("获取库存预警商品成功", products);
        } catch (Exception e) {
            return ApiResponse.error("获取库存预警商品失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新商品库存
     */
    @PutMapping("/stock/{id}")
    public ApiResponse<Void> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity,
            @RequestParam String operation) {
        try {
            productService.updateStock(id, quantity, operation);
            return ApiResponse.success("更新库存成功", null);
        } catch (Exception e) {
            return ApiResponse.error("更新库存失败: " + e.getMessage());
        }
    }
}