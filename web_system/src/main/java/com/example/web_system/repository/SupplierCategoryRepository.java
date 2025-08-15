package com.example.web_system.repository;

import com.example.web_system.entity.SupplierCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierCategoryRepository extends JpaRepository<SupplierCategory, Long> {
    
    /**
     * 根据分类编码查找
     */
    Optional<SupplierCategory> findByCategoryCode(String categoryCode);
    
    /**
     * 检查分类编码是否存在
     */
    boolean existsByCategoryCode(String categoryCode);
    
    /**
     * 根据分类名称查找
     */
    Optional<SupplierCategory> findByCategoryName(String categoryName);
    
    /**
     * 检查分类名称是否存在
     */
    boolean existsByCategoryName(String categoryName);
    
    /**
     * 根据状态查找
     */
    List<SupplierCategory> findByStatus(SupplierCategory.Status status);
    
    /**
     * 获取启用状态的分类，按排序字段排序
     */
    @Query("SELECT c FROM SupplierCategory c WHERE c.status = 'ACTIVE' ORDER BY c.sortOrder ASC, c.categoryName ASC")
    List<SupplierCategory> findActiveCategories();
    
    /**
     * 多条件搜索
     */
    @Query("SELECT c FROM SupplierCategory c WHERE " +
           "(:categoryCode IS NULL OR c.categoryCode LIKE %:categoryCode%) AND " +
           "(:categoryName IS NULL OR c.categoryName LIKE %:categoryName%) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<SupplierCategory> findByConditions(
            @Param("categoryCode") String categoryCode,
            @Param("categoryName") String categoryName,
            @Param("status") SupplierCategory.Status status,
            Pageable pageable);
    
    /**
     * 获取最大排序值
     */
    @Query("SELECT COALESCE(MAX(c.sortOrder), 0) FROM SupplierCategory c")
    Integer getMaxSortOrder();
}