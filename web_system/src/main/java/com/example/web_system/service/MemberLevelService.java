package com.example.web_system.service;

import com.example.web_system.dto.MemberLevelDTO;
import com.example.web_system.entity.MemberLevel;
import com.example.web_system.repository.MemberLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberLevelService {
    
    @Autowired
    private MemberLevelRepository memberLevelRepository;
    
    /**
     * 获取所有会员等级
     */
    public List<MemberLevelDTO> getAllMemberLevels() {
        List<MemberLevel> levels = memberLevelRepository.findAllByOrderBySortOrderAsc();
        return levels.stream()
                .map(MemberLevelDTO::fromMemberLevel)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取启用状态的会员等级
     */
    public List<MemberLevelDTO> getActiveMemberLevels() {
        List<MemberLevel> levels = memberLevelRepository.findByStatusOrderBySortOrderAsc(1);
        return levels.stream()
                .map(MemberLevelDTO::fromMemberLevel)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取会员等级
     */
    public MemberLevelDTO getMemberLevelById(Long id) {
        MemberLevel level = memberLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("会员等级不存在"));
        return MemberLevelDTO.fromMemberLevel(level);
    }
    
    /**
     * 根据名称获取会员等级
     */
    public MemberLevelDTO getMemberLevelByName(String name) {
        MemberLevel level = memberLevelRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("会员等级不存在"));
        return MemberLevelDTO.fromMemberLevel(level);
    }
    
    /**
     * 创建会员等级
     */
    public MemberLevelDTO createMemberLevel(MemberLevelDTO levelDTO) {
        try {
            System.out.println("开始创建会员等级: " + levelDTO.getName());
            
            // 检查等级名称是否已存在
            if (memberLevelRepository.findByName(levelDTO.getName()).isPresent()) {
                throw new RuntimeException("等级名称已存在");
            }
            
            System.out.println("等级名称检查通过，开始转换DTO");
            MemberLevel level = levelDTO.toMemberLevel();
            
            // 设置时间戳
            level.setCreatedAt(LocalDateTime.now());
            level.setUpdatedAt(LocalDateTime.now());
            
            // 如果没有设置排序，自动设置为最大值+1
            if (level.getSortOrder() == null) {
                System.out.println("获取最大排序值");
                try {
                    Integer maxSort = memberLevelRepository.findMaxSortOrder();
                    level.setSortOrder(maxSort != null ? maxSort + 1 : 1);
                    System.out.println("最大排序值: " + maxSort + ", 设置新排序值: " + level.getSortOrder());
                } catch (Exception e) {
                    System.err.println("获取最大排序值失败，使用默认值: " + e.getMessage());
                    level.setSortOrder(99); // 使用一个默认值
                }
            }
            
            System.out.println("准备保存到数据库，排序值: " + level.getSortOrder());
            MemberLevel savedLevel = memberLevelRepository.save(level);
            
            System.out.println("保存成功，ID: " + savedLevel.getId());
            return MemberLevelDTO.fromMemberLevel(savedLevel);
            
        } catch (Exception e) {
            System.err.println("创建会员等级时发生错误: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * 更新会员等级
     */
    public MemberLevelDTO updateMemberLevel(Long id, MemberLevelDTO levelDTO) {
        MemberLevel existingLevel = memberLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("会员等级不存在"));
        
        // 检查等级名称是否已被其他等级使用
        memberLevelRepository.findByName(levelDTO.getName())
                .ifPresent(level -> {
                    if (!level.getId().equals(id)) {
                        throw new RuntimeException("等级名称已存在");
                    }
                });
        
        // 更新字段
        existingLevel.setName(levelDTO.getName());
        existingLevel.setDiscount(levelDTO.getDiscount());
        existingLevel.setMinConsumption(levelDTO.getMinConsumption());
        existingLevel.setPointsRatio(levelDTO.getPointsRatio());
        existingLevel.setBenefits(levelDTO.getBenefits());
        existingLevel.setColor(levelDTO.getColor());
        existingLevel.setSortOrder(levelDTO.getSortOrder());
        existingLevel.setUpdatedAt(LocalDateTime.now());
        
        MemberLevel savedLevel = memberLevelRepository.save(existingLevel);
        return MemberLevelDTO.fromMemberLevel(savedLevel);
    }
    
    /**
     * 删除会员等级
     */
    public void deleteMemberLevel(Long id) {
        MemberLevel level = memberLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("会员等级不存在"));
        
        // 检查是否为默认等级
        if (level.getIsDefault()) {
            throw new RuntimeException("默认等级不能删除");
        }
        
        // 检查是否有会员使用该等级
        // TODO: 这里需要检查会员表中是否有使用该等级的会员
        
        memberLevelRepository.delete(level);
    }
    
    /**
     * 更新等级状态
     */
    public MemberLevelDTO updateMemberLevelStatus(Long id, Integer status) {
        MemberLevel level = memberLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("会员等级不存在"));
        
        // 默认等级不能停用
        if (level.getIsDefault() && status == 0) {
            throw new RuntimeException("默认等级不能停用");
        }
        
        level.setStatus(status);
        level.setUpdatedAt(LocalDateTime.now());
        
        MemberLevel savedLevel = memberLevelRepository.save(level);
        return MemberLevelDTO.fromMemberLevel(savedLevel);
    }
    
    /**
     * 根据消费金额获取对应的会员等级
     */
    public MemberLevelDTO getLevelByConsumption(java.math.BigDecimal consumption) {
        List<MemberLevel> levels = memberLevelRepository.findByStatusOrderByMinConsumptionDesc(1);
        
        for (MemberLevel level : levels) {
            if (consumption.compareTo(level.getMinConsumption()) >= 0) {
                return MemberLevelDTO.fromMemberLevel(level);
            }
        }
        
        // 如果没有匹配的等级，返回默认等级
        MemberLevel defaultLevel = memberLevelRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new RuntimeException("未找到默认会员等级"));
        return MemberLevelDTO.fromMemberLevel(defaultLevel);
    }
}