package com.example.web_system.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cashier_order_items")
public class CashierOrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CashierOrder order; // 关联订单
    
    @Column(nullable = false)
    private Long productId; // 商品ID
    
    @Column(nullable = false, length = 50)
    private String productCode; // 商品编码
    
    @Column(nullable = false, length = 200)
    private String productName; // 商品名称
    
    @Column(length = 50)
    private String specification; // 规格
    
    @Column(length = 20)
    private String unit; // 单位
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice; // 单价
    
    @Column(nullable = false)
    private Integer quantity; // 数量
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal; // 小计
    
    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO; // 折扣金额
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal actualAmount; // 实际金额
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public CashierOrder getOrder() {
        return order;
    }
    
    public void setOrder(CashierOrder order) {
        this.order = order;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getSpecification() {
        return specification;
    }
    
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getActualAmount() {
        return actualAmount;
    }
    
    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }
}