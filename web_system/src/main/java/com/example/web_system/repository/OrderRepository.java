package com.example.web_system.repository;

import com.example.web_system.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    
    // 根据订单号查找订单
    Optional<Order> findByOrderNo(String orderNo);
    
    // 根据收银员ID查找订单
    Page<Order> findByCashierIdOrderByCreatedAtDesc(Long cashierId, Pageable pageable);
    
    // 根据会员ID查找订单
    Page<Order> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
    
    // 根据状态查找订单
    Page<Order> findByStatusOrderByCreatedAtDesc(Order.Status status, Pageable pageable);
    
    // 根据时间范围查找订单
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startTime AND :endTime ORDER BY o.createdAt DESC")
    Page<Order> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime, 
                                      Pageable pageable);
    
    // 复合查询：根据订单号、收银员、时间范围查找
    @Query("SELECT o FROM Order o WHERE " +
           "(:orderNo IS NULL OR :orderNo = '' OR o.orderNo LIKE CONCAT('%', :orderNo, '%')) AND " +
           "(:cashierId IS NULL OR o.cashierId = :cashierId) AND " +
           "(:startTime IS NULL OR o.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR o.createdAt <= :endTime) " +
           "ORDER BY o.createdAt DESC")
    Page<Order> findOrdersWithFilters(@Param("orderNo") String orderNo,
                                     @Param("cashierId") Long cashierId,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     Pageable pageable);
    
    // 复合查询：根据订单号、收银员、时间范围查找所有订单（不分页，用于导出）
    @Query("SELECT o FROM Order o WHERE " +
           "(:orderNo IS NULL OR :orderNo = '' OR o.orderNo LIKE CONCAT('%', :orderNo, '%')) AND " +
           "(:cashierId IS NULL OR o.cashierId = :cashierId) AND " +
           "(:startTime IS NULL OR o.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR o.createdAt <= :endTime) " +
           "ORDER BY o.createdAt DESC")
    List<Order> findAllOrdersWithFilters(@Param("orderNo") String orderNo,
                                        @Param("cashierId") Long cashierId,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
    
    // 统计今日订单数
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startOfDay AND o.createdAt < :endOfDay AND o.status = :status")
    Long countTodayOrders(@Param("startOfDay") LocalDateTime startOfDay, 
                         @Param("endOfDay") LocalDateTime endOfDay, 
                         @Param("status") Order.Status status);
    
    // 统计今日销售额
    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.createdAt >= :startOfDay AND o.createdAt < :endOfDay AND o.status = :status")
    java.math.BigDecimal getTodaySales(@Param("startOfDay") LocalDateTime startOfDay, 
                                      @Param("endOfDay") LocalDateTime endOfDay, 
                                      @Param("status") Order.Status status);
    
    // 检查订单号是否存在
    boolean existsByOrderNo(String orderNo);
    
    // 统计指定时间范围内的订单数量
    long countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    // 统计指定时间范围内的销售总额
    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.createdAt BETWEEN :startTime AND :endTime")
    java.math.BigDecimal sumTotalAmountByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                                         @Param("endTime") LocalDateTime endTime);
}