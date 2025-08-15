package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.dto.MemberDTO;
import com.example.web_system.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/member")
@CrossOrigin(origins = "*")
public class MemberController {
    
    @Autowired
    private MemberService memberService;
    
    /**
     * 获取会员列表
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> getMemberList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Integer status) {
        
        try {
            // 页码从0开始
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createdAt").descending());
            
            Page<MemberDTO> memberPage = memberService.getMemberList(name, phone, level, status, pageable);
            
            Map<String, Object> result = new HashMap<>();
            result.put("total", memberPage.getTotalElements());
            result.put("list", memberPage.getContent());
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);
            result.put("totalPages", memberPage.getTotalPages());
            
            return ApiResponse.success("获取会员列表成功", result);
        } catch (Exception e) {
            return ApiResponse.error("获取会员列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取会员详情
     */
    @GetMapping("/{id}")
    public ApiResponse<MemberDTO> getMemberById(@PathVariable Long id) {
        try {
            MemberDTO member = memberService.getMemberById(id);
            return ApiResponse.success("获取会员详情成功", member);
        } catch (Exception e) {
            return ApiResponse.error("获取会员详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据会员编号获取会员详情
     */
    @GetMapping("/no/{memberNo}")
    public ApiResponse<MemberDTO> getMemberByNo(@PathVariable String memberNo) {
        try {
            MemberDTO member = memberService.getMemberByNo(memberNo);
            return ApiResponse.success("获取会员详情成功", member);
        } catch (Exception e) {
            return ApiResponse.error("获取会员详情失败: " + e.getMessage());
        }
    }

    /**
     * 根据手机号获取会员详情
     */
    @GetMapping("/phone/{phone}")
    public ApiResponse<MemberDTO> getMemberByPhone(@PathVariable String phone) {
        try {
            MemberDTO member = memberService.getMemberByPhone(phone);
            return ApiResponse.success("获取会员详情成功", member);
        } catch (Exception e) {
            return ApiResponse.error("获取会员详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建会员
     */
    @PostMapping
    public ApiResponse<MemberDTO> createMember(@RequestBody MemberDTO memberDTO) {
        try {
            MemberDTO createdMember = memberService.createMember(memberDTO);
            return ApiResponse.success("创建会员成功", createdMember);
        } catch (Exception e) {
            return ApiResponse.error("创建会员失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新会员
     */
    @PutMapping("/{id}")
    public ApiResponse<MemberDTO> updateMember(@PathVariable Long id, @RequestBody MemberDTO memberDTO) {
        try {
            MemberDTO updatedMember = memberService.updateMember(id, memberDTO);
            return ApiResponse.success("更新会员成功", updatedMember);
        } catch (Exception e) {
            return ApiResponse.error("更新会员失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除会员
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteMember(@PathVariable Long id) {
        try {
            memberService.deleteMember(id);
            return ApiResponse.success("删除会员成功");
        } catch (Exception e) {
            return ApiResponse.error("删除会员失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新会员状态
     */
    @PutMapping("/{id}/status")
    public ApiResponse<MemberDTO> updateMemberStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        try {
            Integer status = request.get("status");
            MemberDTO updatedMember = memberService.updateMemberStatus(id, status);
            return ApiResponse.success("更新会员状态成功", updatedMember);
        } catch (Exception e) {
            return ApiResponse.error("更新会员状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 会员充值
     */
    @PostMapping("/{id}/recharge")
    public ApiResponse<MemberDTO> recharge(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String remark = (String) request.get("remark");
            
            MemberDTO updatedMember = memberService.recharge(id, amount, remark);
            return ApiResponse.success("充值成功", updatedMember);
        } catch (Exception e) {
            return ApiResponse.error("充值失败: " + e.getMessage());
        }
    }
    
    /**
     * 积分调整
     */
    @PostMapping("/{id}/points")
    public ApiResponse<MemberDTO> adjustPoints(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Integer points = Integer.parseInt(request.get("points").toString());
            String type = (String) request.get("type"); // add 或 subtract
            String remark = (String) request.get("remark");
            
            MemberDTO updatedMember = memberService.adjustPoints(id, points, type, remark);
            return ApiResponse.success("积分调整成功", updatedMember);
        } catch (Exception e) {
            return ApiResponse.error("积分调整失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取会员统计信息
     */
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getMemberStatistics() {
        try {
            Map<String, Object> statistics = memberService.getMemberStatistics();
            return ApiResponse.success("获取会员统计成功", statistics);
        } catch (Exception e) {
            return ApiResponse.error("获取会员统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试接口 - 验证服务是否正常
     */
    @GetMapping("/test")
    public ApiResponse<String> testApi() {
        return ApiResponse.success("会员服务正常运行");
    }
    
    /**
     * 测试数据库连接
     */
    @GetMapping("/test-db")
    public ApiResponse<Map<String, Object>> testDatabase() {
        try {
            long count = memberService.getMemberList("", "", "", null, 
                org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements();
            
            Map<String, Object> result = new HashMap<>();
            result.put("database", "connected");
            result.put("memberCount", count);
            result.put("status", "success");
            
            return ApiResponse.success("数据库连接正常", result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("database", "error");
            result.put("error", e.getMessage());
            result.put("status", "failed");
            
            return ApiResponse.error("数据库连接失败: " + e.getMessage());
        }
    }
}