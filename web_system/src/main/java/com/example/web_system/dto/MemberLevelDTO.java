package com.example.web_system.dto;

import com.example.web_system.entity.MemberLevel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MemberLevelDTO {
    
    private Long id;
    private String name;
    private Integer discount;
    private BigDecimal minConsumption;
    private Integer pointsRatio;
    private String benefits;
    private String color;
    private Integer sortOrder;
    private Integer status;
    private String statusText;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public MemberLevelDTO() {}
    
    public MemberLevelDTO(MemberLevel memberLevel) {
        this.id = memberLevel.getId();
        this.name = memberLevel.getName();
        this.discount = memberLevel.getDiscount();
        this.minConsumption = memberLevel.getMinConsumption();
        this.pointsRatio = memberLevel.getPointsRatio();
        this.benefits = memberLevel.getBenefits();
        this.color = memberLevel.getColor();
        this.sortOrder = memberLevel.getSortOrder();
        this.status = memberLevel.getStatus();
        this.statusText = memberLevel.getStatus() == 1 ? "启用" : "禁用";
        this.isDefault = memberLevel.getIsDefault();
        this.createdAt = memberLevel.getCreatedAt();
        this.updatedAt = memberLevel.getUpdatedAt();
    }
    
    // 静态工厂方法
    public static MemberLevelDTO fromMemberLevel(MemberLevel memberLevel) {
        return new MemberLevelDTO(memberLevel);
    }
    
    // 转换为实体
    public MemberLevel toMemberLevel() {
        MemberLevel memberLevel = new MemberLevel();
        memberLevel.setId(this.id);
        memberLevel.setName(this.name);
        memberLevel.setDiscount(this.discount);
        memberLevel.setMinConsumption(this.minConsumption);
        memberLevel.setPointsRatio(this.pointsRatio);
        memberLevel.setBenefits(this.benefits);
        memberLevel.setColor(this.color);
        memberLevel.setSortOrder(this.sortOrder);
        memberLevel.setStatus(this.status != null ? this.status : 1);
        memberLevel.setIsDefault(this.isDefault != null ? this.isDefault : false);
        return memberLevel;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getDiscount() {
        return discount;
    }
    
    public void setDiscount(Integer discount) {
        this.discount = discount;
    }
    
    public BigDecimal getMinConsumption() {
        return minConsumption;
    }
    
    public void setMinConsumption(BigDecimal minConsumption) {
        this.minConsumption = minConsumption;
    }
    
    public Integer getPointsRatio() {
        return pointsRatio;
    }
    
    public void setPointsRatio(Integer pointsRatio) {
        this.pointsRatio = pointsRatio;
    }
    
    public String getBenefits() {
        return benefits;
    }
    
    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getStatusText() {
        return statusText;
    }
    
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
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