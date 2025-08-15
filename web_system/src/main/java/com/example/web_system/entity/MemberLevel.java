package com.example.web_system.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_levels")
public class MemberLevel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String name;
    
    @Column(nullable = false)
    private Integer discount = 100; // 折扣率，100表示无折扣
    
    @Column(precision = 10, scale = 2)
    private BigDecimal minConsumption = BigDecimal.ZERO; // 升级所需最低消费
    
    @Column(nullable = false)
    private Integer pointsRatio = 100; // 积分比例，100表示1:1
    
    @Column(length = 500)
    private String benefits; // 会员权益描述
    
    @Column(length = 20)
    private String color = "#67C23A"; // 标签颜色
    
    @Column(nullable = false)
    private Integer status = 1; // 1-启用, 0-停用
    
    @Column(nullable = false)
    private Boolean isDefault = false; // 是否为默认等级
    
    @Column(nullable = false)
    private Integer sortOrder = 1; // 排序
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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