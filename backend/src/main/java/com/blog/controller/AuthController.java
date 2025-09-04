package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.UserDTO;
import com.blog.service.UserService;
import com.blog.service.SecurityService;
import com.blog.service.RefreshTokenService;
import com.blog.service.CaptchaService;
import com.blog.util.JwtUtil;
import com.blog.annotation.OperationLog;
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
    
    @Autowired
    private RefreshTokenService refreshTokenService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CaptchaService captchaService;
    
    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    public Result<Map<String, String>> getCaptcha() {
        try {
            Map<String, String> captcha = captchaService.generateCaptcha();
            return Result.success("获取验证码成功", captcha);
        } catch (Exception e) {
            return Result.error("获取验证码失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @OperationLog(module = "用户认证", type = com.blog.entity.OperationLog.OperationType.CREATE, description = "用户注册", recordParams = false)
    public Result<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 验证验证码
            if (!captchaService.verifyCaptcha(request.getCaptchaId(), request.getCaptchaCode())) {
                return Result.error("验证码错误或已过期");
            }
            
            UserDTO user = userService.register(request.getUsername(), request.getEmail(), request.getPassword(), request.getNickname());
            
            // 注册成功后清除验证码
            captchaService.clearCaptcha(request.getCaptchaId());
            
            return Result.success("注册成功", user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @OperationLog(module = "用户认证", type = com.blog.entity.OperationLog.OperationType.LOGIN, description = "用户登录", recordParams = false)
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            String token = userService.login(request.getUsernameOrEmail(), request.getPassword());
            
            // 获取用户ID并生成刷新令牌
            Long userId = userService.getUserIdByUsernameOrEmail(request.getUsernameOrEmail());
            String refreshToken = refreshTokenService.generateRefreshToken(userId);
            
            // 记录登录活动
            try {
                String sessionId = httpRequest.getSession().getId();
                securityService.recordLoginActivity(userId, httpRequest, sessionId);
            } catch (Exception e) {
                // 登录日志记录失败不影响登录流程
                System.err.println("记录登录活动失败: " + e.getMessage());
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", token);
            data.put("refreshToken", refreshToken);
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
    public Result<String> sendVerificationEmail(@RequestParam String email) {
        try {
            userService.sendVerificationEmail(email);
            return Result.success("验证邮件已发送");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 刷新访问令牌
     */
    @PostMapping("/refresh")
    public Result<Map<String, Object>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            String newAccessToken = refreshTokenService.refreshToken(request.getRefreshToken());
            
            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", newAccessToken);
            data.put("tokenType", "Bearer");
            
            return Result.success("令牌刷新成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户注销
     */
    @PostMapping("/logout")
    @OperationLog(module = "用户认证", type = com.blog.entity.OperationLog.OperationType.LOGOUT, description = "用户登出")
    public Result<String> logout(@RequestHeader("Authorization") String authHeader,
                              @RequestParam(required = false) String refreshToken) {
        try {
            // 撤销访问令牌
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String accessToken = authHeader.substring(7);
                jwtUtil.revokeToken(accessToken);
            }
            
            // 撤销刷新令牌
            if (refreshToken != null && !refreshToken.trim().isEmpty()) {
                refreshTokenService.revokeRefreshToken(refreshToken);
            }
            
            return Result.success("注销成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 撤销用户所有令牌（强制下线）
     */
    @PostMapping("/revoke-all")
    public Result<String> revokeAllTokens(@RequestHeader("Authorization") String authHeader) {
        try {
            // 从当前令牌中提取用户ID
            String accessToken = authHeader.substring(7);
            Long userId = jwtUtil.extractUserId(accessToken);
            
            if (userId != null) {
                // 撤销用户所有刷新令牌
                refreshTokenService.revokeAllUserRefreshTokens(userId);
                // 注意：这里无法撤销所有已发出的访问令牌，因为它们是无状态的
                // 在实际应用中，可能需要维护一个用户令牌版本号机制
            }
            
            return Result.success("所有令牌已撤销", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 验证邮箱
     */
    @PostMapping("/verify-email")
    public Result<String> verifyEmail(@RequestParam String token) {
        try {
            userService.verifyEmail(token);
            return Result.success("邮箱验证成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestParam String email) {
        try {
            userService.resetPassword(email);
            return Result.success("重置密码邮件已发送");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 确认重置密码
     */
    @PostMapping("/confirm-reset-password")
    public Result<String> confirmResetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            userService.confirmResetPassword(request.getToken(), request.getNewPassword());
            return Result.success("密码重置成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
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
        @Size(min = 8, max = 100, message = "密码长度必须在8-100个字符之间")
        private String password;
        
        @NotBlank(message = "验证码ID不能为空")
        private String captchaId;
        
        @NotBlank(message = "验证码不能为空")
        @Size(min = 4, max = 6, message = "验证码长度必须在4-6个字符之间")
        private String captchaCode;
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getCaptchaId() { return captchaId; }
        public void setCaptchaId(String captchaId) { this.captchaId = captchaId; }
        
        public String getCaptchaCode() { return captchaCode; }
        public void setCaptchaCode(String captchaCode) { this.captchaCode = captchaCode; }
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
        @Size(min = 8, max = 100, message = "密码长度必须在8-100个字符之间")
        private String newPassword;
        
        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
    public static class RefreshTokenRequest {
        @NotBlank(message = "刷新令牌不能为空")
        private String refreshToken;
        
        // Getters and Setters
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
}