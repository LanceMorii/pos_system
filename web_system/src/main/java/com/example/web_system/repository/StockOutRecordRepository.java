package com.example.web_system.repository;

import com.example.web_system.entity.StockOutRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockOutRecordRepository extends JpaRepository<StockOutRecord, Long> {
    
    // 根据商品ID查找出库记录
    List<StockOutRecord> findByProductId(Long productId);
    
    // 根据商品ID分页查找出库记录
    Page<StockOutRecord> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
    
    // 根据出库类型查找记录
    List<StockOutRecord> findByType(StockOutRecord.Type type);
    
    // 根据操作员ID查找记录
    List<StockOutRecord> findByOperatorId(Long operatorId);
    
    // 根据时间范围查找记录
    @Query("SELECT s FROM StockOutRecord s WHERE s.createdAt BETWEEN :startTime AND :endTime ORDER BY s.createdAt DESC")
    List<StockOutRecord> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 根据商品ID和时间范围查找记录
    @Query("SELECT s FROM StockOutRecord s WHERE s.productId = :productId AND s.createdAt BETWEEN :startTime AND :endTime ORDER BY s.createdAt DESC")
    Page<StockOutRecord> findByProductIdAndCreatedAtBetween(
        @Param("productId") Long productId, 
        @Param("startTime") LocalDateTime startTime, 
        @Param("endTime") LocalDateTime endTime, 
        Pageable pageable
    );
    
    // 检查记录号是否存在
    boolean existsByRecordNo(String recordNo);
}