package com.example.web_system.repository;

import com.example.web_system.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // 根据订单ID查找订单明细
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId ORDER BY oi.id")
    List<OrderItem> findByOrderIdOrderById(@Param("orderId") Long orderId);
    
    // 根据商品ID查找订单明细
    List<OrderItem> findByProductIdOrderByIdDesc(Long productId);
    
    // 统计商品销售数量
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi " +
           "JOIN oi.order o WHERE oi.productId = :productId AND o.status = :status")
    Long getTotalSoldQuantity(@Param("productId") Long productId, @Param("status") com.example.web_system.entity.Order.Status status);
    
    // 获取热销商品
    @Query("SELECT oi.productId, oi.productName, SUM(oi.quantity) as totalQuantity " +
           "FROM OrderItem oi JOIN oi.order o " +
           "WHERE o.status = :status AND o.createdAt >= :startTime " +
           "GROUP BY oi.productId, oi.productName " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> getHotSellingProducts(@Param("startTime") java.time.LocalDateTime startTime, @Param("status") com.example.web_system.entity.Order.Status status);
}