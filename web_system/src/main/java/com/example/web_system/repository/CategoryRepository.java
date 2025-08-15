package com.example.web_system.repository;

import com.example.web_system.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // 根据名称查找分类
    Optional<Category> findByName(String name);
    
    // 检查分类名称是否存在
    boolean existsByName(String name);
    
    // 根据父分类ID查找子分类
    List<Category> findByParentId(Long parentId);
    
    // 根据状态查找分类
    List<Category> findByStatus(Category.Status status);
    
    // 根据层级查找分类
    List<Category> findByLevel(Integer level);
    
    // 查找所有根分类（父分类为空）
    List<Category> findByParentIdIsNull();
    
    // 根据状态和层级查找分类，按排序字段排序
    @Query("SELECT c FROM Category c WHERE c.status = :status AND c.level = :level ORDER BY c.sortOrder ASC, c.name ASC")
    List<Category> findByStatusAndLevelOrderBySortOrder(Category.Status status, Integer level);
    
    // 查找所有活跃分类，按排序字段排序
    @Query("SELECT c FROM Category c WHERE c.status = 'ACTIVE' ORDER BY c.level ASC, c.sortOrder ASC, c.name ASC")
    List<Category> findAllActiveOrderBySortOrder();
    
    // 根据状态统计分类数量
    long countByStatus(Category.Status status);
}