package com.example.web_system.controller;

import com.example.web_system.common.ApiResponse;
import com.example.web_system.dto.RegisterRequest;
import com.example.web_system.dto.UserDTO;
import com.example.web_system.entity.User;
import com.example.web_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ApiResponse<?> getUserList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        
        try {
            // 转换角色和状态参数
            User.Role roleEnum = null;
            if (role != null && !role.isEmpty()) {
                try {
                    roleEnum = User.Role.valueOf(role.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // 忽略无效的角色参数
                }
            }
            
            User.Status statusEnum = null;
            if (status != null && !status.isEmpty()) {
                try {
                    statusEnum = "active".equalsIgnoreCase(status) ? User.Status.ACTIVE : User.Status.INACTIVE;
                } catch (IllegalArgumentException e) {
                    // 忽略无效的状态参数
                }
            }
            
            List<User> users = userService.findUsers(username, realName, roleEnum, statusEnum);
            
            // 转换为DTO避免LocalDateTime序列化问题
            List<UserDTO> userDTOs = users.stream()
                    .map(UserDTO::fromUser)
                    .collect(Collectors.toList());
            
            return ApiResponse.success("获取用户列表成功", userDTOs);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ApiResponse<?> addUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.register(registerRequest);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("realName", user.getRealName());
            result.put("role", user.getRole());
            
            return ApiResponse.success("添加用户成功", result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ApiResponse<?> updateUser(@Valid @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", updatedUser.getId());
            result.put("username", updatedUser.getUsername());
            result.put("realName", updatedUser.getRealName());
            result.put("role", updatedUser.getRole());
            result.put("status", updatedUser.getStatus());
            
            return ApiResponse.success("更新用户成功", result);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ApiResponse.success("删除用户成功", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取收银员列表
     */
    @GetMapping("/cashiers")
    public ApiResponse<?> getCashierList() {
        try {
            List<User> cashiers = userService.findCashiers();
            
            List<Map<String, Object>> cashierList = cashiers.stream()
                    .map(user -> {
                        Map<String, Object> cashier = new HashMap<>();
                        cashier.put("id", user.getId());
                        cashier.put("username", user.getUsername());
                        cashier.put("name", user.getRealName());
                        cashier.put("phone", user.getPhone());
                        return cashier;
                    })
                    .collect(Collectors.toList());
            
            return ApiResponse.success("获取收银员列表成功", cashierList);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}