package com.example.web_system.repository;

import com.example.web_system.entity.MemberLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberLevelRepository extends JpaRepository<MemberLevel, Long> {
    
    /**
     * 根据名称查找等级
     */
    Optional<MemberLevel> findByName(String name);
    
    /**
     * 根据状态查找等级，按排序升序
     */
    List<MemberLevel> findByStatusOrderBySortOrderAsc(Integer status);
    
    /**
     * 查找所有等级，按排序升序
     */
    List<MemberLevel> findAllByOrderBySortOrderAsc();
    
    /**
     * 根据状态查找等级，按最低消费降序
     */
    List<MemberLevel> findByStatusOrderByMinConsumptionDesc(Integer status);
    
    /**
     * 查找默认等级
     */
    Optional<MemberLevel> findByIsDefaultTrue();
    
    /**
     * 获取最大排序值
     */
    @Query("SELECT MAX(m.sortOrder) FROM MemberLevel m")
    Integer findMaxSortOrder();
    
    /**
     * 根据最低消费金额查找等级
     */
    List<MemberLevel> findByMinConsumptionLessThanEqualAndStatusOrderByMinConsumptionDesc(BigDecimal consumption, Integer status);
}