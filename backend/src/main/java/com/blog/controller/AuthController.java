package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.UserDTO;
import com.blog.service.UserService;
import com.blog.service.SecurityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SecurityService securityService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        try {
            UserDTO user = userService.register(request.getUsername(), request.getEmail(), request.getPassword(), request.getNickname());
            return Result.success("注册成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            String token = userService.login(request.getUsernameOrEmail(), request.getPassword());
            
            // 记录登录活动
            try {
                Long userId = userService.getUserIdByUsernameOrEmail(request.getUsernameOrEmail());
                String sessionId = httpRequest.getSession().getId();
                securityService.recordLoginActivity(userId, httpRequest, sessionId);
            } catch (Exception e) {
                // 登录日志记录失败不影响登录流程
                System.err.println("记录登录活动失败: " + e.getMessage());
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("tokenType", "Bearer");
            
            return Result.success("登录成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return Result.success(!exists);
    }
    
    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    public Result<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return Result.success(!exists);
    }
    
    /**
     * 发送邮箱验证邮件
     */
    @PostMapping("/send-verification-email")
    public Result<Void> sendVerificationEmail(@RequestParam String email) {
        try {
            userService.sendVerificationEmail(email);
            return new Result<>(200, "验证邮件已发送");
        } catch (Exception e) {
            return new Result<>(500, e.getMessage());
        }
    }
    
    /**
     * 验证邮箱
     */
    @PostMapping("/verify-email")
    public Result<Void> verifyEmail(@RequestParam String token) {
        try {
            userService.verifyEmail(token);
            return new Result<>(200, "邮箱验证成功");
        } catch (Exception e) {
            return new Result<>(500, e.getMessage());
        }
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestParam String email) {
        try {
            userService.resetPassword(email);
            return new Result<>(200, "重置密码邮件已发送");
        } catch (Exception e) {
            return new Result<>(500, e.getMessage());
        }
    }
    
    /**
     * 确认重置密码
     */
    @PostMapping("/confirm-reset-password")
    public Result<Void> confirmResetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            userService.confirmResetPassword(request.getToken(), request.getNewPassword());
            return new Result<>(200, "密码重置成功");
        } catch (Exception e) {
            return new Result<>(500, e.getMessage());
        }
    }
    
    // 内部类定义请求对象
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
        private String username;
        
        @Size(max = 50, message = "昵称长度不能超过50个字符")
        private String nickname;
        
        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;
        
        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
        private String password;
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class LoginRequest {
        @NotBlank(message = "用户名或邮箱不能为空")
        private String usernameOrEmail;
        
        @NotBlank(message = "密码不能为空")
        private String password;
        
        // Getters and Setters
        public String getUsernameOrEmail() { return usernameOrEmail; }
        public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class ResetPasswordRequest {
        @NotBlank(message = "token不能为空")
        private String token;
        
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
        private String newPassword;
        
        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}