package com.example.web_system.repository;

import com.example.web_system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    Optional<User> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    boolean existsByPhone(String phone);
    
    boolean existsByEmail(String email);
    
    List<User> findByUsernameContainingAndRealNameContaining(String username, String realName);
    
    List<User> findByUsernameContainingAndRealNameContainingAndRole(String username, String realName, User.Role role);
    
    List<User> findByUsernameContainingAndRealNameContainingAndStatus(String username, String realName, User.Status status);
    
    List<User> findByUsernameContainingAndRealNameContainingAndRoleAndStatus(String username, String realName, User.Role role, User.Status status);
    
    Optional<User> findByRealName(String realName);
    
    List<User> findByRoleAndStatus(User.Role role, User.Status status);
}