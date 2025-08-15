package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.dto.*;
import com.example.web_system.entity.User;
import com.example.web_system.service.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = userService.login(loginRequest);
            return ApiResponse.success("登录成功", loginResponse);
        } catch (Exception e) {
            // 对于认证失败，我们仍然返回200状态码，但在响应体中标明错误
            return ApiResponse.error(401, e.getMessage());
        }
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.register(registerRequest);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("realName", user.getRealName());
            result.put("role", user.getRole());
            
            return ApiResponse.success("注册成功", result);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            userService.logout(username);
            return ApiResponse.success("登出成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/login-count")
    public ApiResponse<Long> getLoginCount(@RequestParam String username) {
        try {
            Long count = userService.getLoginCount(username);
            return ApiResponse.success("获取成功", count);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    @GetMapping("/info")
    public ApiResponse<?> getUserInfo(@RequestParam(required = false) String username) {
        try {
            // 如果没有提供用户名，尝试从SecurityContext中获取
            if (username == null || username.isEmpty()) {
                username = SecurityContextHolder.getContext().getAuthentication().getName();
                if ("anonymousUser".equals(username)) {
                    return ApiResponse.error(401, "未登录或登录已过期");
                }
            }
            
            User user = userService.findByUsername(username);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("phone", user.getPhone());
            userInfo.put("email", user.getEmail());
            userInfo.put("role", user.getRole());
            userInfo.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
            
            // 不再提供权限信息，简化系统
            
            return ApiResponse.success("获取用户信息成功", userInfo);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    @PutMapping("/profile")
    public ApiResponse<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) {
                return ApiResponse.error(401, "未登录或登录已过期");
            }
            
            User user = userService.updateProfile(username, request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("realName", user.getRealName());
            result.put("phone", user.getPhone());
            result.put("email", user.getEmail());
            result.put("role", user.getRole());
            
            return ApiResponse.success("更新个人信息成功", result);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) {
                return ApiResponse.error(401, "未登录或登录已过期");
            }
            
            userService.changePassword(username, request);
            
            return ApiResponse.success("密码修改成功", null);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @GetMapping("/stats")
    public ApiResponse<UserStatsResponse> getUserStats() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) {
                return ApiResponse.error(401, "未登录或登录已过期");
            }
            
            UserStatsResponse stats = userService.getUserStats(username);
            
            return ApiResponse.success("获取用户统计成功", stats);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    @GetMapping("/permissions")
    public ApiResponse<Map<String, Object>> getUserPermissions() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) {
                return ApiResponse.error(401, "未登录或登录已过期");
            }
            
            User user = userService.findByUsername(username);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }
            
            // 简化权限信息，只返回基本角色信息
            Map<String, Object> result = new HashMap<>();
            result.put("role", user.getRole());
            result.put("roleName", getRoleName(user.getRole()));
            
            return ApiResponse.success("获取用户权限成功", result);
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