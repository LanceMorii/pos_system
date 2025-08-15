package com.example.web_system.repository;

import com.example.web_system.entity.CashierOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CashierOrderRepository extends JpaRepository<CashierOrder, Long> {
    
    /**
     * 根据订单编号查找
     */
    Optional<CashierOrder> findByOrderNo(String orderNo);
    
    /**
     * 根据会员编号查找订单
     */
    List<CashierOrder> findByMemberNoOrderByCreatedAtDesc(String memberNo);
    
    /**
     * 根据收银员查找订单
     */
    List<CashierOrder> findByCashierIdOrderByCreatedAtDesc(String cashierId);
    
    /**
     * 根据状态查找订单
     */
    Page<CashierOrder> findByStatusOrderByCreatedAtDesc(CashierOrder.OrderStatus status, Pageable pageable);
    
    /**
     * 根据时间范围查找订单
     */
    @Query("SELECT o FROM CashierOrder o WHERE o.createdAt BETWEEN :startTime AND :endTime ORDER BY o.createdAt DESC")
    List<CashierOrder> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计今日销售额
     */
    @Query("SELECT COALESCE(SUM(o.payableAmount), 0) FROM CashierOrder o WHERE " +
           "DATE(o.createdAt) = CURRENT_DATE AND o.status = 'PAID'")
    BigDecimal getTodaySalesAmount();
    
    /**
     * 统计今日订单数
     */
    @Query("SELECT COUNT(o) FROM CashierOrder o WHERE " +
           "DATE(o.createdAt) = CURRENT_DATE AND o.status = 'PAID'")
    Long getTodayOrderCount();
    
    /**
     * 统计收银员今日销售额
     */
    @Query("SELECT COALESCE(SUM(o.payableAmount), 0) FROM CashierOrder o WHERE " +
           "o.cashierId = :cashierId AND DATE(o.createdAt) = CURRENT_DATE AND o.status = 'PAID'")
    BigDecimal getCashierTodaySales(@Param("cashierId") String cashierId);
}