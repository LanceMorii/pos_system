package com.example.web_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户统计信息响应DTO
 * 用于返回用户的登录统计数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {
    
    /**
     * 登录次数
     */
    private Integer loginCount;
    
    /**
     * 最后登录时间
     */
    private String lastLoginTime;
    
    /**
     * 账户创建时间
     */
    private String createdAt;
    
    /**
     * 账户状态
     */
    private String status;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    /**
     * 简化构造函数，只包含基本统计信息
     */
    public UserStatsResponse(Integer loginCount, String lastLoginTime) {
        this.loginCount = loginCount;
        this.lastLoginTime = lastLoginTime;
    }

}