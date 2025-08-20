package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.UserDTO;
import com.blog.entity.User;
import com.blog.service.UserService;
import com.blog.util.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<UserDTO> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            Optional<User> user = userService.findById(userId);
            if (user.isPresent()) {
                return Result.success(new UserDTO(user.get()));
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long currentUserId = getUserIdFromToken(token);
            
            // 只能更新自己的信息
            if (!currentUserId.equals(id)) {
                return Result.error("无权限修改其他用户信息");
            }
            
            UserDTO userDTO = new UserDTO();
            userDTO.setNickname(request.getNickname());
            userDTO.setBio(request.getBio());
            userDTO.setWebsite(request.getWebsite());
            userDTO.setGithub(request.getGithub());
            
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return Result.success("更新成功", updatedUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户头像
     */
    @PutMapping("/{id}/avatar")
    public Result<UserDTO> updateAvatar(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAvatarRequest request,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long currentUserId = getUserIdFromToken(token);
            
            // 只能更新自己的头像
            if (!currentUserId.equals(id)) {
                return Result.error("无权限修改其他用户头像");
            }
            
            UserDTO updatedUser = userService.updateAvatar(id, request.getAvatar());
            return Result.success("头像更新成功", updatedUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/{id}/password")
    public Result<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePasswordRequest request,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long currentUserId = getUserIdFromToken(token);
            
            // 只能修改自己的密码
            if (!currentUserId.equals(id)) {
                return Result.error("无权限修改其他用户密码");
            }
            
            userService.updatePassword(id, request.getOldPassword(), request.getNewPassword());
            return Result.success("密码修改成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户列表（管理员功能）
     */
    @GetMapping
    public Result<Page<UserDTO>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestHeader("Authorization") String token) {
        
        try {
            // 验证管理员权限
            verifyAdminPermission(token);
            
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<UserDTO> users;
            if (keyword != null && !keyword.trim().isEmpty()) {
                users = userService.searchUsers(keyword, pageable);
            } else if (role != null && !role.trim().isEmpty()) {
                User.Role userRole = User.Role.valueOf(role.toUpperCase());
                users = userService.findUsersByRole(userRole, pageable);
            } else {
                users = userService.findUsers(pageable);
            }
            
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<UserDTO> getUser(@PathVariable Long id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                return Result.success(new UserDTO(user.get()));
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建用户（管理员功能）
     */
    @PostMapping
    public Result<UserDTO> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @RequestHeader("Authorization") String token) {
        
        try {
            // 验证管理员权限
            verifyAdminPermission(token);
            
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(request.getUsername());
            userDTO.setNickname(request.getNickname());
            userDTO.setEmail(request.getEmail());
            userDTO.setRole(request.getRole());
            userDTO.setAvatar(request.getAvatar());
            userDTO.setBio(request.getBio());
            userDTO.setStatus(request.isStatus());
            
            UserDTO createdUser = userService.createUser(userDTO, request.getPassword());
            return Result.success("用户创建成功", createdUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户（管理员功能）
     */
    @PutMapping("/admin/{id}")
    public Result<UserDTO> updateUserByAdmin(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserByAdminRequest request,
            @RequestHeader("Authorization") String token) {
        
        try {
            // 验证管理员权限
            verifyAdminPermission(token);
            
            UserDTO userDTO = new UserDTO();
            userDTO.setNickname(request.getNickname());
            userDTO.setEmail(request.getEmail());
            userDTO.setRole(request.getRole());
            userDTO.setAvatar(request.getAvatar());
            userDTO.setBio(request.getBio());
            userDTO.setStatus(request.isStatus());
            
            UserDTO updatedUser = userService.updateUserByAdmin(id, userDTO);
            return Result.success("用户更新成功", updatedUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除用户（管理员功能）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        try {
            // 验证管理员权限
            verifyAdminPermission(token);
            
            userService.deleteUser(id);
            return Result.success("用户删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 切换用户状态（管理员功能）
     */
    @PutMapping("/{id}/status")
    public Result<UserDTO> toggleUserStatus(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        try {
            // 验证管理员权限
            verifyAdminPermission(token);
            
            UserDTO updatedUser = userService.toggleUserStatus(id);
            return Result.success("用户状态更新成功", updatedUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 重置用户密码（管理员功能）
     */
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetUserPassword(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        try {
            // 验证管理员权限
            verifyAdminPermission(token);
            
            userService.resetUserPassword(id);
            return Result.success("密码重置成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量操作用户（管理员功能）
     */
    @PostMapping("/batch")
    public Result<Void> batchOperateUsers(
            @Valid @RequestBody BatchOperateRequest request,
            @RequestHeader("Authorization") String token) {
        
        try {
            // 验证管理员权限
            verifyAdminPermission(token);
            
            switch (request.getOperation()) {
                case "enable":
                    userService.batchEnableUsers(request.getUserIds());
                    break;
                case "disable":
                    userService.batchDisableUsers(request.getUserIds());
                    break;
                case "delete":
                    userService.batchDeleteUsers(request.getUserIds());
                    break;
                default:
                    return Result.error("不支持的操作类型");
            }
            
            return Result.success("批量操作成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 验证管理员权限
     */
    private void verifyAdminPermission(String token) {
        Long userId = getUserIdFromToken(token);
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty() || user.get().getRole() != User.Role.ADMIN) {
            throw new RuntimeException("无管理员权限");
        }
    }
    
    private Long getUserIdFromToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("无效的token");
        }
        
        String jwt = token.substring(7);
        String username = jwtUtil.extractUsername(jwt);
        
        if (username == null || !jwtUtil.validateToken(jwt, username)) {
            throw new RuntimeException("token已过期或无效");
        }
        
        Long userId = userService.getUserIdByUsername(username);
        if (userId == null) {
            throw new RuntimeException("用户不存在");
        }
        
        return userId;
    }
    
    // 请求对象定义
    public static class UpdateUserRequest {
        @Size(max = 50, message = "昵称长度不能超过50个字符")
        private String nickname;
        
        @Size(max = 200, message = "个人简介长度不能超过200个字符")
        private String bio;
        
        @Size(max = 200, message = "网站地址长度不能超过200个字符")
        private String website;
        
        @Size(max = 200, message = "GitHub地址长度不能超过200个字符")
        private String github;
        
        // Getters and Setters
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
        
        public String getGithub() { return github; }
        public void setGithub(String github) { this.github = github; }
    }
    
    public static class UpdateAvatarRequest {
        @NotBlank(message = "头像地址不能为空")
        private String avatar;
        
        // Getters and Setters
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
    }
    
    public static class UpdatePasswordRequest {
        @NotBlank(message = "当前密码不能为空")
        private String oldPassword;
        
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
        private String newPassword;
        
        // Getters and Setters
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
    public static class CreateUserRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
        private String username;
        
        @NotBlank(message = "昵称不能为空")
        @Size(max = 50, message = "昵称长度不能超过50个字符")
        private String nickname;
        
        @NotBlank(message = "邮箱不能为空")
        private String email;
        
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
        private String password;
        
        private User.Role role = User.Role.USER;
        private String avatar;
        private String bio;
        private boolean status = true;
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public User.Role getRole() { return role; }
        public void setRole(User.Role role) { this.role = role; }
        
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        
        public boolean isStatus() { return status; }
        public void setStatus(boolean status) { this.status = status; }
    }
    
    public static class UpdateUserByAdminRequest {
        @Size(max = 50, message = "昵称长度不能超过50个字符")
        private String nickname;
        
        private String email;
        private User.Role role;
        private String avatar;
        private String bio;
        private boolean status;
        
        // Getters and Setters
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public User.Role getRole() { return role; }
        public void setRole(User.Role role) { this.role = role; }
        
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        
        public boolean isStatus() { return status; }
        public void setStatus(boolean status) { this.status = status; }
    }
    
    public static class BatchOperateRequest {
        @NotBlank(message = "操作类型不能为空")
        private String operation;
        
        private java.util.List<Long> userIds;
        
        // Getters and Setters
        public String getOperation() { return operation; }
        public void setOperation(String operation) { this.operation = operation; }
        
        public java.util.List<Long> getUserIds() { return userIds; }
        public void setUserIds(java.util.List<Long> userIds) { this.userIds = userIds; }
    }
}