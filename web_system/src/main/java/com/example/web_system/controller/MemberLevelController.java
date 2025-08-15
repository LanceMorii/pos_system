package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.dto.MemberLevelDTO;
import com.example.web_system.service.MemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member-level")
@CrossOrigin(origins = "*")
public class MemberLevelController {
    
    @Autowired
    private MemberLevelService memberLevelService;
    
    /**
     * 获取所有会员等级
     */
    @GetMapping("/list")
    public ApiResponse<List<MemberLevelDTO>> getMemberLevels() {
        try {
            List<MemberLevelDTO> levels = memberLevelService.getAllMemberLevels();
            return ApiResponse.success("获取会员等级列表成功", levels);
        } catch (Exception e) {
            return ApiResponse.error("获取会员等级列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取启用状态的会员等级
     */
    @GetMapping("/active")
    public ApiResponse<List<MemberLevelDTO>> getActiveMemberLevels() {
        try {
            List<MemberLevelDTO> levels = memberLevelService.getActiveMemberLevels();
            return ApiResponse.success("获取启用会员等级成功", levels);
        } catch (Exception e) {
            return ApiResponse.error("获取启用会员等级失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取等级详情
     */
    @GetMapping("/{id}")
    public ApiResponse<MemberLevelDTO> getMemberLevelById(@PathVariable Long id) {
        try {
            MemberLevelDTO level = memberLevelService.getMemberLevelById(id);
            return ApiResponse.success("获取等级详情成功", level);
        } catch (Exception e) {
            return ApiResponse.error("获取等级详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建会员等级
     */
    @PostMapping
    public ApiResponse<MemberLevelDTO> createMemberLevel(@RequestBody MemberLevelDTO levelDTO) {
        try {
            // 添加详细的日志记录
            System.out.println("接收到创建等级请求: " + levelDTO.getName());
            System.out.println("折扣率: " + levelDTO.getDiscount());
            System.out.println("最低消费: " + levelDTO.getMinConsumption());
            
            MemberLevelDTO createdLevel = memberLevelService.createMemberLevel(levelDTO);
            return ApiResponse.success("创建会员等级成功", createdLevel);
        } catch (Exception e) {
            // 打印详细的错误信息
            e.printStackTrace();
            System.err.println("创建会员等级失败: " + e.getMessage());
            return ApiResponse.error("创建会员等级失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新会员等级
     */
    @PutMapping("/{id}")
    public ApiResponse<MemberLevelDTO> updateMemberLevel(@PathVariable Long id, @RequestBody MemberLevelDTO levelDTO) {
        try {
            MemberLevelDTO updatedLevel = memberLevelService.updateMemberLevel(id, levelDTO);
            return ApiResponse.success("更新会员等级成功", updatedLevel);
        } catch (Exception e) {
            return ApiResponse.error("更新会员等级失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除会员等级
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteMemberLevel(@PathVariable Long id) {
        try {
            memberLevelService.deleteMemberLevel(id);
            return ApiResponse.success("删除会员等级成功");
        } catch (Exception e) {
            return ApiResponse.error("删除会员等级失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新等级状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<MemberLevelDTO> updateMemberLevelStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer status = request.get("status");
            MemberLevelDTO updatedLevel = memberLevelService.updateMemberLevelStatus(id, status);
            return ApiResponse.success("更新等级状态成功", updatedLevel);
        } catch (Exception e) {
            return ApiResponse.error("更新等级状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试接口
     */
    @GetMapping("/test")
    public ApiResponse<String> testApi() {
        return ApiResponse.success("会员等级服务正常运行");
    }
    
    /**
     * 测试数据库连接
     */
    @GetMapping("/test-db")
    public ApiResponse<Map<String, Object>> testDatabase() {
        try {
            List<MemberLevelDTO> levels = memberLevelService.getAllMemberLevels();
            
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("database", "connected");
            result.put("levelCount", levels.size());
            result.put("status", "success");
            result.put("levels", levels);
            
            return ApiResponse.success("数据库连接正常", result);
        } catch (Exception e) {
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("database", "error");
            result.put("error", e.getMessage());
            result.put("status", "failed");
            
            e.printStackTrace();
            return ApiResponse.error("数据库连接失败: " + e.getMessage());
        }
    }
    
    /**
     * 简化的创建等级测试方法
     */
    @PostMapping("/test-create")
    public ApiResponse<String> testCreateLevel(@RequestBody Map<String, Object> data) {
        try {
            System.out.println("接收到测试创建请求: " + data);
            
            // 简单验证数据
            String name = (String) data.get("name");
            if (name == null || name.trim().isEmpty()) {
                return ApiResponse.error("等级名称不能为空");
            }
            
            // 检查是否已存在
            if (memberLevelService.getAllMemberLevels().stream()
                .anyMatch(level -> level.getName().equals(name))) {
                return ApiResponse.error("等级名称已存在");
            }
            
            return ApiResponse.success("测试创建成功，数据验证通过");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("测试创建失败: " + e.getMessage());
        }
    }
}