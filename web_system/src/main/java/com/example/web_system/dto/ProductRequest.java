package com.example.web_system.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductRequest {
    
    @NotBlank(message = "商品编码不能为空")
    @Size(max = 50, message = "商品编码长度不能超过50个字符")
    private String code;
    
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 200, message = "商品名称长度不能超过200个字符")
    private String name;
    
    @NotNull(message = "商品分类不能为空")
    private Long categoryId;
    
    private Long supplierId;
    
    @Size(max = 50, message = "条形码长度不能超过50个字符")
    private String barcode;
    
    @NotBlank(message = "商品单位不能为空")
    @Size(max = 20, message = "商品单位长度不能超过20个字符")
    private String unit = "个";
    
    @Size(max = 100, message = "商品规格长度不能超过100个字符")
    private String specification;
    
    @NotNull(message = "进价不能为空")
    @DecimalMin(value = "0.00", message = "进价不能小于0")
    @Digits(integer = 8, fraction = 2, message = "进价格式不正确")
    private BigDecimal purchasePrice;
    
    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.00", message = "售价不能小于0")
    @Digits(integer = 8, fraction = 2, message = "售价格式不正确")
    private BigDecimal salePrice;
    
    @DecimalMin(value = "0.00", message = "会员价不能小于0")
    @Digits(integer = 8, fraction = 2, message = "会员价格式不正确")
    private BigDecimal memberPrice;
    
    @NotNull(message = "最低库存不能为空")
    @Min(value = 0, message = "最低库存不能小于0")
    private Integer minStock = 0;
    
    @NotNull(message = "最高库存不能为空")
    @Min(value = 1, message = "最高库存不能小于1")
    private Integer maxStock = 1000;
    
    @NotNull(message = "当前库存不能为空")
    @Min(value = 0, message = "当前库存不能小于0")
    private Integer currentStock = 0;
    
    @NotBlank(message = "商品状态不能为空")
    @Pattern(regexp = "ACTIVE|INACTIVE", message = "商品状态只能是ACTIVE或INACTIVE")
    private String status = "ACTIVE";
    
    private String imageUrl;
    
    private String description;
    
    // Constructors
    public ProductRequest() {}
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public Long getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public String getSpecification() {
        return specification;
    }
    
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
    
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    public BigDecimal getSalePrice() {
        return salePrice;
    }
    
    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }
    
    public BigDecimal getMemberPrice() {
        return memberPrice;
    }
    
    public void setMemberPrice(BigDecimal memberPrice) {
        this.memberPrice = memberPrice;
    }
    
    public Integer getMinStock() {
        return minStock;
    }
    
    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }
    
    public Integer getMaxStock() {
        return maxStock;
    }
    
    public void setMaxStock(Integer maxStock) {
        this.maxStock = maxStock;
    }
    
    public Integer getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}