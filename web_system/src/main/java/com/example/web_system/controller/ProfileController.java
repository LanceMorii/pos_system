package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.dto.ChangePasswordRequest;
import com.example.web_system.dto.UpdateProfileRequest;
import com.example.web_system.dto.UserProfileDTO;
import com.example.web_system.entity.User;
import com.example.web_system.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户个人信息
     */
    @GetMapping("/info")
    public ApiResponse<UserProfileDTO> getProfile(HttpServletRequest request) {
        try {
            User currentUser = (User) request.getAttribute("currentUser");
            if (currentUser == null) {
                return ApiResponse.error(401, "未登录");
            }
            
            UserProfileDTO profile = UserProfileDTO.fromUser(currentUser);
            // 获取登录次数
            Long loginCount = userService.getLoginCount(currentUser.getUsername());
            profile.setLoginCount(loginCount);
            
            return ApiResponse.success("获取个人信息成功", profile);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新个人信息
     */
    @PutMapping("/update")
    public ApiResponse<UserProfileDTO> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        try {
            User currentUser = (User) httpRequest.getAttribute("currentUser");
            if (currentUser == null) {
                return ApiResponse.error(401, "未登录");
            }
            
            User updatedUser = userService.updateProfile(currentUser.getId(), request);
            UserProfileDTO profile = UserProfileDTO.fromUser(updatedUser);
            
            return ApiResponse.success("更新个人信息成功", profile);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public ApiResponse<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        try {
            User currentUser = (User) httpRequest.getAttribute("currentUser");
            if (currentUser == null) {
                return ApiResponse.error(401, "未登录");
            }
            
            userService.changePassword(currentUser.getId(), request);
            return ApiResponse.success("修改密码成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户权限信息
     */
    @GetMapping("/permissions")
    public ApiResponse<?> getPermissions(HttpServletRequest request) {
        try {
            User currentUser = (User) request.getAttribute("currentUser");
            if (currentUser == null) {
                return ApiResponse.error(401, "未登录");
            }
            
            // 简化权限信息，只返回基本角色信息
            Map<String, Object> result = new HashMap<>();
            result.put("role", currentUser.getRole());
            result.put("roleName", getRoleName(currentUser.getRole()));
            
            return ApiResponse.success("获取权限信息成功", result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    private String getRoleName(User.Role role) {
        switch (role) {
            case ADMIN: return "系统管理员";
            case MANAGER: return "店长";
            case CASHIER: return "收银员";
            case WAREHOUSE: return "库管员";
            default: return "未知角色";
        }
    }
}