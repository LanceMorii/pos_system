package com.example.web_system.repository;

import com.example.web_system.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    /**
     * 根据供应商编码查找
     */
    Optional<Supplier> findBySupplierCode(String supplierCode);
    
    /**
     * 检查供应商编码是否存在
     */
    boolean existsBySupplierCode(String supplierCode);
    
    /**
     * 根据供应商名称查找
     */
    List<Supplier> findBySupplierNameContaining(String supplierName);
    
    /**
     * 根据状态查找
     */
    List<Supplier> findByStatus(Supplier.Status status);
    
    /**
     * 根据分类查找
     */
    List<Supplier> findByCategory(String category);
    
    /**
     * 根据联系人查找
     */
    List<Supplier> findByContactPersonContaining(String contactPerson);
    
    /**
     * 多条件搜索
     */
    @Query("SELECT s FROM Supplier s WHERE " +
           "(:supplierCode IS NULL OR s.supplierCode LIKE %:supplierCode%) AND " +
           "(:supplierName IS NULL OR s.supplierName LIKE %:supplierName%) AND " +
           "(:contactPerson IS NULL OR s.contactPerson LIKE %:contactPerson%) AND " +
           "(:category IS NULL OR s.category = :category) AND " +
           "(:status IS NULL OR s.status = :status)")
    List<Supplier> findByConditions(
            @Param("supplierCode") String supplierCode,
            @Param("supplierName") String supplierName,
            @Param("contactPerson") String contactPerson,
            @Param("category") String category,
            @Param("status") Supplier.Status status);
    
    /**
     * 获取所有分类
     */
    @Query("SELECT DISTINCT s.category FROM Supplier s WHERE s.category IS NOT NULL ORDER BY s.category")
    List<String> findAllCategories();
    
    /**
     * 统计各状态的供应商数量
     */
    @Query("SELECT s.status, COUNT(s) FROM Supplier s GROUP BY s.status")
    List<Object[]> countByStatus();
    
    /**
     * 获取正常状态的供应商
     */
    @Query("SELECT s FROM Supplier s WHERE s.status = 'ACTIVE' ORDER BY s.supplierName")
    List<Supplier> findActiveSuppliers();
}