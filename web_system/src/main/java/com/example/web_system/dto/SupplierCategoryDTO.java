package com.example.web_system.dto;

import com.example.web_system.entity.SupplierCategory;
import java.time.LocalDateTime;

public class SupplierCategoryDTO {
    
    private Long id;
    private String categoryCode;
    private String categoryName;
    private String description;
    private Integer sortOrder;
    private String status;
    private String statusText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public SupplierCategoryDTO() {}
    
    public SupplierCategoryDTO(SupplierCategory category) {
        this.id = category.getId();
        this.categoryCode = category.getCategoryCode();
        this.categoryName = category.getCategoryName();
        this.description = category.getDescription();
        this.sortOrder = category.getSortOrder();
        this.status = category.getStatus().name();
        this.statusText = category.getStatus().getDescription();
        this.createdAt = category.getCreatedAt();
        this.updatedAt = category.getUpdatedAt();
    }
    
    // 静态工厂方法
    public static SupplierCategoryDTO fromCategory(SupplierCategory category) {
        return new SupplierCategoryDTO(category);
    }
    
    // 转换为实体
    public SupplierCategory toCategory() {
        SupplierCategory category = new SupplierCategory();
        category.setId(this.id);
        category.setCategoryCode(this.categoryCode);
        category.setCategoryName(this.categoryName);
        category.setDescription(this.description);
        category.setSortOrder(this.sortOrder);
        if (this.status != null) {
            category.setStatus(SupplierCategory.Status.valueOf(this.status));
        }
        return category;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCategoryCode() {
        return categoryCode;
    }
    
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStatusText() {
        return statusText;
    }
    
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}