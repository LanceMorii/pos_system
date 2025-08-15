package com.example.web_system.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cashier_orders")
public class CashierOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String orderNo; // 订单编号
    
    @Column(length = 50)
    private String memberNo; // 会员编号（可选）
    
    @Column(length = 100)
    private String memberName; // 会员姓名（可选）
    
    @Column(nullable = false)
    private Integer totalQuantity = 0; // 商品总数量
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO; // 订单总金额
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO; // 折扣金额
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal payableAmount = BigDecimal.ZERO; // 应付金额
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal paidAmount = BigDecimal.ZERO; // 实付金额
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal changeAmount = BigDecimal.ZERO; // 找零金额
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod = PaymentMethod.CASH; // 支付方式
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING; // 订单状态
    
    @Column(length = 50)
    private String cashierId; // 收银员ID
    
    @Column(length = 100)
    private String cashierName; // 收银员姓名
    
    @Column(length = 500)
    private String remark; // 备注
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // 订单明细（一对多关系）
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CashierOrderItem> orderItems;
    
    public enum PaymentMethod {
        CASH("现金"),
        WECHAT("微信支付"),
        ALIPAY("支付宝"),
        CARD("银行卡"),
        MEMBER_BALANCE("会员余额");
        
        private final String description;
        
        PaymentMethod(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    public enum OrderStatus {
        PENDING("待支付"),
        PAID("已支付"),
        CANCELLED("已取消"),
        REFUNDED("已退款");
        
        private final String description;
        
        OrderStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
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
    
    public String getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
    
    public String getMemberNo() {
        return memberNo;
    }
    
    public void setMemberNo(String memberNo) {
        this.memberNo = memberNo;
    }
    
    public String getMemberName() {
        return memberName;
    }
    
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    
    public Integer getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getPayableAmount() {
        return payableAmount;
    }
    
    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }
    
    public BigDecimal getPaidAmount() {
        return paidAmount;
    }
    
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }
    
    public BigDecimal getChangeAmount() {
        return changeAmount;
    }
    
    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public String getCashierId() {
        return cashierId;
    }
    
    public void setCashierId(String cashierId) {
        this.cashierId = cashierId;
    }
    
    public String getCashierName() {
        return cashierName;
    }
    
    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
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
    
    public List<CashierOrderItem> getOrderItems() {
        return orderItems;
    }
    
    public void setOrderItems(List<CashierOrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}