package com.example.web_system.dto;

import com.example.web_system.entity.Supplier;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public class SupplierDTO {
    
    private Long id;
    private String supplierCode;
    private String supplierName;
    private String contactPerson;
    private String contactPhone;
    private String email;
    private String address;
    private String category;
    private String status;
    private String statusText;
    private BigDecimal creditLimit;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public SupplierDTO() {}
    
    public SupplierDTO(Supplier supplier) {
        this.id = supplier.getId();
        this.supplierCode = supplier.getSupplierCode();
        this.supplierName = supplier.getSupplierName();
        this.contactPerson = supplier.getContactPerson();
        this.contactPhone = supplier.getContactPhone();
        this.email = supplier.getEmail();
        this.address = supplier.getAddress();
        this.category = supplier.getCategory();
        this.status = supplier.getStatus().name();
        this.statusText = supplier.getStatus().getDescription();
        this.creditLimit = supplier.getCreditLimit();
        this.remark = supplier.getRemark();
        this.createdAt = supplier.getCreatedAt();
        this.updatedAt = supplier.getUpdatedAt();
    }
    
    // 静态工厂方法
    public static SupplierDTO fromSupplier(Supplier supplier) {
        return new SupplierDTO(supplier);
    }
    
    // 转换为实体
    public Supplier toSupplier() {
        Supplier supplier = new Supplier();
        supplier.setId(this.id);
        supplier.setSupplierCode(this.supplierCode);
        supplier.setSupplierName(this.supplierName);
        supplier.setContactPerson(this.contactPerson);
        supplier.setContactPhone(this.contactPhone);
        supplier.setEmail(this.email);
        supplier.setAddress(this.address);
        supplier.setCategory(this.category);
        if (this.status != null) {
            supplier.setStatus(Supplier.Status.valueOf(this.status));
        }
        supplier.setCreditLimit(this.creditLimit);
        supplier.setRemark(this.remark);
        return supplier;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSupplierCode() {
        return supplierCode;
    }
    
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
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
    
    public BigDecimal getCreditLimit() {
        return creditLimit;
    }
    
    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
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