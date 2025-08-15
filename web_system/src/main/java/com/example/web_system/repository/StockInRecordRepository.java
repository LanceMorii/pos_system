package com.example.web_system.repository;

import com.example.web_system.entity.StockInRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockInRecordRepository extends JpaRepository<StockInRecord, Long> {
    
    // 根据商品ID查找入库记录
    List<StockInRecord> findByProductId(Long productId);
    
    // 根据商品ID分页查找入库记录
    Page<StockInRecord> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
    
    // 根据入库类型查找记录
    List<StockInRecord> findByType(StockInRecord.Type type);
    
    // 根据操作员ID查找记录
    List<StockInRecord> findByOperatorId(Long operatorId);
    
    // 根据时间范围查找记录
    @Query("SELECT s FROM StockInRecord s WHERE s.createdAt BETWEEN :startTime AND :endTime ORDER BY s.createdAt DESC")
    List<StockInRecord> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 根据商品ID和时间范围查找记录
    @Query("SELECT s FROM StockInRecord s WHERE s.productId = :productId AND s.createdAt BETWEEN :startTime AND :endTime ORDER BY s.createdAt DESC")
    Page<StockInRecord> findByProductIdAndCreatedAtBetween(
        @Param("productId") Long productId, 
        @Param("startTime") LocalDateTime startTime, 
        @Param("endTime") LocalDateTime endTime, 
        Pageable pageable
    );
    
    // 检查记录号是否存在
    boolean existsByRecordNo(String recordNo);
}