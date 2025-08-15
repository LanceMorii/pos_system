package com.example.web_system.repository;

import com.example.web_system.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 根据商品编码查找
    Optional<Product> findByCode(String code);
    
    // 根据条形码查找
    Optional<Product> findByBarcode(String barcode);
    
    // 检查商品编码是否存在
    boolean existsByCode(String code);
    
    // 检查条形码是否存在
    boolean existsByBarcode(String barcode);
    
    // 根据分类ID查找商品
    List<Product> findByCategoryId(Long categoryId);
    
    // 根据状态查找商品
    List<Product> findByStatus(Product.Status status);
    
    // 查询商品（支持名称和分类筛选）
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR p.name LIKE %:name%) AND " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<Product> findProductsWithFilters(
        @Param("name") String name,
        @Param("categoryId") Long categoryId,
        @Param("status") Product.Status status,
        Pageable pageable
    );

    // 非分页版本，兼容现有调用
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR p.name LIKE %:name%) AND " +
           "(:categoryId IS NULL OR p.categoryId = :categoryId) AND " +
           "(:status IS NULL OR p.status = :status)")
    List<Product> findProductsWithFilters(
        @Param("name") String name,
        @Param("categoryId") Long categoryId,
        @Param("status") Product.Status status
    );
    
    // 查找库存不足的商品
    @Query("SELECT p FROM Product p WHERE p.currentStock <= p.minStock AND p.status = 'ACTIVE'")
    List<Product> findLowStockProducts();
    
    // 根据商品名称模糊查询
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // 根据分类和状态查询
    List<Product> findByCategoryIdAndStatus(Long categoryId, Product.Status status);
    
    // 统计商品总数
    long countByStatus(Product.Status status);
    
    // 统计分类下的商品数量
    long countByCategoryId(Long categoryId);
    
    // 根据库存数量和状态统计
    long countByCurrentStockAndStatus(int currentStock, Product.Status status);
    
    // 根据名称和状态查询
    List<Product> findByNameContainingAndStatus(String name, Product.Status status);
    
    // 统计库存预警商品数量
    @Query("SELECT COUNT(p) FROM Product p WHERE p.currentStock <= p.minStock AND p.status = 'ACTIVE'")
    long countByCurrentStockLessThanMinStock();
}