package com.example.web_system.service;

import com.example.web_system.dto.*;
import com.example.web_system.entity.User;
import com.example.web_system.repository.UserRepository;
import com.example.web_system.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private RedisService redisService;
    
    public LoginResponse login(LoginRequest loginRequest) {
        // 直接从数据库查找用户信息，避免Redis缓存序列化问题
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        User user = userOptional.get();
        
        if (user.getStatus() == User.Status.INACTIVE) {
            throw new RuntimeException("账户已被禁用");
        }
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().name());
        
        // 缓存token，有效期24小时
        String tokenCacheKey = "token:" + user.getUsername();
        redisService.setString(tokenCacheKey, token, 24, TimeUnit.HOURS);
        
        // 记录登录次数
        String loginCountKey = "login_count:" + user.getUsername();
        redisService.increment(loginCountKey);
        redisService.expire(loginCountKey, 1, TimeUnit.DAYS);
        
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRealName(), user.getRole());
    }
    
    public User register(RegisterRequest registerRequest) {
        // 验证密码确认
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (registerRequest.getPhone() != null && !registerRequest.getPhone().isEmpty() 
            && userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }
        
        // 检查邮箱是否已存在
        if (registerRequest.getEmail() != null && !registerRequest.getEmail().isEmpty() 
            && userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRealName(registerRequest.getRealName());
        user.setPhone(registerRequest.getPhone());
        user.setEmail(registerRequest.getEmail());
        user.setRole(registerRequest.getRole());
        user.setStatus(User.Status.ACTIVE);
        
        User savedUser = userRepository.save(user);
        
        // 清除可能存在的缓存
        String userCacheKey = "user:" + savedUser.getUsername();
        redisService.delete(userCacheKey);
        
        return savedUser;
    }
    
    public User findByUsername(String username) {
        // 直接从数据库查找，避免Redis缓存序列化问题
        return userRepository.findByUsername(username).orElse(null);
    }
    
    /**
     * 用户登出，清除缓存
     */
    public void logout(String username) {
        String tokenCacheKey = "token:" + username;
        String userCacheKey = "user:" + username;
        
        redisService.delete(tokenCacheKey);
        redisService.delete(userCacheKey);
    }
    
    /**
     * 获取用户登录次数
     */
    public Long getLoginCount(String username) {
        String loginCountKey = "login_count:" + username;
        String count = redisService.getString(loginCountKey);
        return count != null ? Long.parseLong(count) : 0L;
    }
    
    /**
     * 查询用户列表
     */
    public List<User> findUsers(String username, String realName, User.Role role, User.Status status) {
        username = username == null ? "" : username;
        realName = realName == null ? "" : realName;
        
        if (role != null && status != null) {
            return userRepository.findByUsernameContainingAndRealNameContainingAndRoleAndStatus(
                    username, realName, role, status);
        } else if (role != null) {
            return userRepository.findByUsernameContainingAndRealNameContainingAndRole(
                    username, realName, role);
        } else if (status != null) {
            return userRepository.findByUsernameContainingAndRealNameContainingAndStatus(
                    username, realName, status);
        } else {
            return userRepository.findByUsernameContainingAndRealNameContaining(
                    username, realName);
        }
    }
    
    /**
     * 更新用户信息
     */
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 如果修改了用户名，检查是否已存在
        if (!existingUser.getUsername().equals(user.getUsername()) && 
                userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 如果修改了手机号，检查是否已存在
        if (user.getPhone() != null && !user.getPhone().isEmpty() && 
                !user.getPhone().equals(existingUser.getPhone()) && 
                userRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }
        
        // 如果修改了邮箱，检查是否已存在
        if (user.getEmail() != null && !user.getEmail().isEmpty() && 
                !user.getEmail().equals(existingUser.getEmail()) && 
                userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 更新用户信息
        existingUser.setUsername(user.getUsername());
        existingUser.setRealName(user.getRealName());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        existingUser.setStatus(user.getStatus());
        
        // 如果提供了新密码，则更新密码
        if (StringUtils.hasText(user.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        User updatedUser = userRepository.save(existingUser);
        
        // 清除缓存
        String userCacheKey = "user:" + updatedUser.getUsername();
        redisService.delete(userCacheKey);
        
        return updatedUser;
    }
    
    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 清除缓存
        String userCacheKey = "user:" + user.getUsername();
        String tokenCacheKey = "token:" + user.getUsername();
        redisService.delete(userCacheKey);
        redisService.delete(tokenCacheKey);
        
        userRepository.deleteById(id);
    }
    
    /**
     * 更新个人信息
     */
    public User updateProfile(Long userId, com.example.web_system.dto.UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查手机号是否已被其他用户使用
        if (request.getPhone() != null && !request.getPhone().isEmpty() && 
                !request.getPhone().equals(user.getPhone()) && 
                userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("手机号已被其他用户使用");
        }
        
        // 检查邮箱是否已被其他用户使用
        if (request.getEmail() != null && !request.getEmail().isEmpty() && 
                !request.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被其他用户使用");
        }
        
        // 更新用户信息
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        
        return userRepository.save(user);
    }
    
    /**
     * 修改密码
     */
    public void changePassword(Long userId, com.example.web_system.dto.ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 验证新密码确认
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的新密码不一致");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // 清除该用户的所有token缓存，强制重新登录
        String tokenCacheKey = "token:" + user.getUsername();
        redisService.delete(tokenCacheKey);
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
    
    /**
     * 更新用户个人信息
     */
    public User updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查手机号是否已被其他用户使用
        if (request.getPhone() != null && !request.getPhone().isEmpty() && 
                !request.getPhone().equals(user.getPhone()) && 
                userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("手机号已被其他用户使用");
        }
        
        // 检查邮箱是否已被其他用户使用
        if (request.getEmail() != null && !request.getEmail().isEmpty() && 
                !request.getEmail().equals(user.getEmail()) && 
                userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被其他用户使用");
        }
        
        // 更新用户信息
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        
        return userRepository.save(user);
    }
    
    /**
     * 修改用户密码
     */
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("原密码不正确");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // 清除相关缓存，强制用户重新登录
        String tokenCacheKey = "token:" + username;
        String userCacheKey = "user:" + username;
        redisService.delete(tokenCacheKey);
        redisService.delete(userCacheKey);
    }
    
    /**
     * 获取用户统计信息
     */
    public UserStatsResponse getUserStats(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 获取登录次数
        Long loginCount = getLoginCount(username);
        
        // 获取最后登录时间（简化处理，使用更新时间）
        String lastLoginTime = user.getUpdatedAt() != null ? 
                user.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : 
                "从未登录";
        
        return new UserStatsResponse(loginCount.intValue(), lastLoginTime);
    }

    /**
     * 获取收银员列表
     */
    public List<User> findCashiers() {
        return userRepository.findByRoleAndStatus(User.Role.CASHIER, User.Status.ACTIVE);
    }
}