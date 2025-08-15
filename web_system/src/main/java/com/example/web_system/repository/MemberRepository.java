package com.example.web_system.repository;

import com.example.web_system.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * 根据会员编号查找
     */
    Optional<Member> findByMemberNo(String memberNo);
    
    /**
     * 根据手机号查找
     */
    Optional<Member> findByPhone(String phone);
    
    /**
     * 检查会员编号是否存在
     */
    boolean existsByMemberNo(String memberNo);
    
    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 根据状态查找
     */
    List<Member> findByStatus(Integer status);
    
    /**
     * 根据会员等级查找
     */
    List<Member> findByLevel(String level);
    
    /**
     * 多条件搜索
     */
    @Query("SELECT m FROM Member m WHERE " +
           "(:name IS NULL OR m.name LIKE %:name%) AND " +
           "(:phone IS NULL OR m.phone LIKE %:phone%) AND " +
           "(:level IS NULL OR m.level = :level) AND " +
           "(:status IS NULL OR m.status = :status)")
    Page<Member> findByConditions(
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("level") String level,
            @Param("status") Integer status,
            Pageable pageable);
    
    /**
     * 统计各等级会员数量
     */
    @Query("SELECT m.level, COUNT(m) FROM Member m GROUP BY m.level")
    List<Object[]> countByLevel();
    
    /**
     * 统计各状态会员数量
     */
    @Query("SELECT m.status, COUNT(m) FROM Member m GROUP BY m.status")
    List<Object[]> countByStatus();
    
    /**
     * 获取消费排行榜
     */
    @Query("SELECT m FROM Member m WHERE m.status = 1 ORDER BY m.totalConsumption DESC")
    List<Member> findTopConsumers(Pageable pageable);
    
    /**
     * 获取积分排行榜
     */
    @Query("SELECT m FROM Member m WHERE m.status = 1 ORDER BY m.points DESC")
    List<Member> findTopPointsMembers(Pageable pageable);
    
    /**
     * 根据生日月份查找会员（生日提醒）
     */
    @Query("SELECT m FROM Member m WHERE MONTH(m.birthday) = :month AND m.status = 1")
    List<Member> findByBirthdayMonth(@Param("month") int month);
    
    /**
     * 计算总消费金额
     */
    @Query("SELECT SUM(m.totalConsumption) FROM Member m WHERE m.status = 1")
    BigDecimal getTotalConsumption();
    
    /**
     * 计算平均消费金额
     */
    @Query("SELECT AVG(m.totalConsumption) FROM Member m WHERE m.status = 1 AND m.totalConsumption > 0")
    BigDecimal getAverageConsumption();
    
    /**
     * 获取本月新增会员数量
     */
    @Query("SELECT COUNT(m) FROM Member m WHERE YEAR(m.createdAt) = YEAR(CURRENT_DATE) AND MONTH(m.createdAt) = MONTH(CURRENT_DATE)")
    Long getNewMembersThisMonth();
    
    /**
     * 获取活跃会员数量（有消费记录的会员）
     */
    @Query("SELECT COUNT(m) FROM Member m WHERE m.status = 1 AND m.totalConsumption > 0")
    Long getActiveMembersCount();
}