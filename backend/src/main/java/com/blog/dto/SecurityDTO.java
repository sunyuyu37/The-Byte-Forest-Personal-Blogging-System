package com.blog.dto;

import com.blog.entity.SecuritySettings;
import java.time.LocalDateTime;
import java.util.List;

public class SecurityDTO {
    
    // 登录活动DTO
    public static class LoginActivityDTO {
        private Long id;
        private String device;
        private String location;
        private String ipAddress;
        private LocalDateTime loginTime;
        private LocalDateTime logoutTime;
        private Boolean isCurrentSession;
        private String sessionId;
        
        // 构造函数
        public LoginActivityDTO() {}
        
        public LoginActivityDTO(Long id, String device, String location, String ipAddress, 
                               LocalDateTime loginTime, LocalDateTime logoutTime, 
                               Boolean isCurrentSession, String sessionId) {
            this.id = id;
            this.device = device;
            this.location = location;
            this.ipAddress = ipAddress;
            this.loginTime = loginTime;
            this.logoutTime = logoutTime;
            this.isCurrentSession = isCurrentSession;
            this.sessionId = sessionId;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getDevice() { return device; }
        public void setDevice(String device) { this.device = device; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        
        public LocalDateTime getLoginTime() { return loginTime; }
        public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }
        
        public LocalDateTime getLogoutTime() { return logoutTime; }
        public void setLogoutTime(LocalDateTime logoutTime) { this.logoutTime = logoutTime; }
        
        public Boolean getIsCurrentSession() { return isCurrentSession; }
        public void setIsCurrentSession(Boolean isCurrentSession) { this.isCurrentSession = isCurrentSession; }
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
    
    // 安全设置DTO
    public static class SecuritySettingsDTO {
        private Boolean twoFactorEnabled;
        private SecuritySettings.TwoFactorMethod twoFactorMethod;
        private Boolean loginNotificationEnabled;
        private Boolean anomalyProtectionEnabled;
        private Integer sessionTimeoutMinutes;
        private Boolean passwordChangeRequired;
        private LocalDateTime lastPasswordChange;
        private Integer failedLoginAttempts;
        private LocalDateTime accountLockedUntil;
        
        // 构造函数
        public SecuritySettingsDTO() {}
        
        public SecuritySettingsDTO(Boolean twoFactorEnabled, SecuritySettings.TwoFactorMethod twoFactorMethod,
                                  Boolean loginNotificationEnabled, Boolean anomalyProtectionEnabled,
                                  Integer sessionTimeoutMinutes, Boolean passwordChangeRequired,
                                  LocalDateTime lastPasswordChange, Integer failedLoginAttempts,
                                  LocalDateTime accountLockedUntil) {
            this.twoFactorEnabled = twoFactorEnabled;
            this.twoFactorMethod = twoFactorMethod;
            this.loginNotificationEnabled = loginNotificationEnabled;
            this.anomalyProtectionEnabled = anomalyProtectionEnabled;
            this.sessionTimeoutMinutes = sessionTimeoutMinutes;
            this.passwordChangeRequired = passwordChangeRequired;
            this.lastPasswordChange = lastPasswordChange;
            this.failedLoginAttempts = failedLoginAttempts;
            this.accountLockedUntil = accountLockedUntil;
        }
        
        // Getters and Setters
        public Boolean getTwoFactorEnabled() { return twoFactorEnabled; }
        public void setTwoFactorEnabled(Boolean twoFactorEnabled) { this.twoFactorEnabled = twoFactorEnabled; }
        
        public SecuritySettings.TwoFactorMethod getTwoFactorMethod() { return twoFactorMethod; }
        public void setTwoFactorMethod(SecuritySettings.TwoFactorMethod twoFactorMethod) { this.twoFactorMethod = twoFactorMethod; }
        
        public Boolean getLoginNotificationEnabled() { return loginNotificationEnabled; }
        public void setLoginNotificationEnabled(Boolean loginNotificationEnabled) { this.loginNotificationEnabled = loginNotificationEnabled; }
        
        public Boolean getAnomalyProtectionEnabled() { return anomalyProtectionEnabled; }
        public void setAnomalyProtectionEnabled(Boolean anomalyProtectionEnabled) { this.anomalyProtectionEnabled = anomalyProtectionEnabled; }
        
        public Integer getSessionTimeoutMinutes() { return sessionTimeoutMinutes; }
        public void setSessionTimeoutMinutes(Integer sessionTimeoutMinutes) { this.sessionTimeoutMinutes = sessionTimeoutMinutes; }
        
        public Boolean getPasswordChangeRequired() { return passwordChangeRequired; }
        public void setPasswordChangeRequired(Boolean passwordChangeRequired) { this.passwordChangeRequired = passwordChangeRequired; }
        
        public LocalDateTime getLastPasswordChange() { return lastPasswordChange; }
        public void setLastPasswordChange(LocalDateTime lastPasswordChange) { this.lastPasswordChange = lastPasswordChange; }
        
        public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
        public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
        
        public LocalDateTime getAccountLockedUntil() { return accountLockedUntil; }
        public void setAccountLockedUntil(LocalDateTime accountLockedUntil) { this.accountLockedUntil = accountLockedUntil; }
    }
    
    // 两步验证设置请求DTO
    public static class TwoFactorSetupDTO {
        @jakarta.validation.constraints.NotNull(message = "两步验证方法不能为空")
        private SecuritySettings.TwoFactorMethod method;
        
        @jakarta.validation.constraints.Pattern(regexp = "^[1-9]\\d{10}$", message = "手机号格式不正确")
        private String phoneNumber; // 用于SMS
        
        @jakarta.validation.constraints.Size(min = 6, max = 6, message = "验证码必须为6位数字")
        private String verificationCode;
        
        // 构造函数
        public TwoFactorSetupDTO() {}
        
        // Getters and Setters
        public SecuritySettings.TwoFactorMethod getMethod() { return method; }
        public void setMethod(SecuritySettings.TwoFactorMethod method) { this.method = method; }
        
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        
        public String getVerificationCode() { return verificationCode; }
        public void setVerificationCode(String verificationCode) { this.verificationCode = verificationCode; }
    }
    
    // 安全中心概览DTO
    public static class SecurityOverviewDTO {
        private SecuritySettingsDTO settings;
        private List<LoginActivityDTO> recentActivities;
        private Integer totalActiveSessions;
        private LocalDateTime lastPasswordChange;
        private Boolean hasAnomalousActivity;
        
        // 构造函数
        public SecurityOverviewDTO() {}
        
        public SecurityOverviewDTO(SecuritySettingsDTO settings, List<LoginActivityDTO> recentActivities,
                                  Integer totalActiveSessions, LocalDateTime lastPasswordChange,
                                  Boolean hasAnomalousActivity) {
            this.settings = settings;
            this.recentActivities = recentActivities;
            this.totalActiveSessions = totalActiveSessions;
            this.lastPasswordChange = lastPasswordChange;
            this.hasAnomalousActivity = hasAnomalousActivity;
        }
        
        // Getters and Setters
        public SecuritySettingsDTO getSettings() { return settings; }
        public void setSettings(SecuritySettingsDTO settings) { this.settings = settings; }
        
        public List<LoginActivityDTO> getRecentActivities() { return recentActivities; }
        public void setRecentActivities(List<LoginActivityDTO> recentActivities) { this.recentActivities = recentActivities; }
        
        public Integer getTotalActiveSessions() { return totalActiveSessions; }
        public void setTotalActiveSessions(Integer totalActiveSessions) { this.totalActiveSessions = totalActiveSessions; }
        
        public LocalDateTime getLastPasswordChange() { return lastPasswordChange; }
        public void setLastPasswordChange(LocalDateTime lastPasswordChange) { this.lastPasswordChange = lastPasswordChange; }
        
        public Boolean getHasAnomalousActivity() { return hasAnomalousActivity; }
        public void setHasAnomalousActivity(Boolean hasAnomalousActivity) { this.hasAnomalousActivity = hasAnomalousActivity; }
    }
    
    // Token状态监控DTO
    public static class TokenStatusDTO {
        private Boolean hasToken;
        private Boolean isTokenExpired;
        private Boolean isTokenExpiringSoon;
        private Long remainingTimeMillis;
        private String username;
        private LocalDateTime tokenIssuedAt;
        private LocalDateTime tokenExpiresAt;
        
        // 构造函数
        public TokenStatusDTO() {}
        
        public TokenStatusDTO(Boolean hasToken, Boolean isTokenExpired, Boolean isTokenExpiringSoon,
                             Long remainingTimeMillis, String username, LocalDateTime tokenIssuedAt,
                             LocalDateTime tokenExpiresAt) {
            this.hasToken = hasToken;
            this.isTokenExpired = isTokenExpired;
            this.isTokenExpiringSoon = isTokenExpiringSoon;
            this.remainingTimeMillis = remainingTimeMillis;
            this.username = username;
            this.tokenIssuedAt = tokenIssuedAt;
            this.tokenExpiresAt = tokenExpiresAt;
        }
        
        // Getters and Setters
        public Boolean getHasToken() { return hasToken; }
        public void setHasToken(Boolean hasToken) { this.hasToken = hasToken; }
        
        public Boolean getIsTokenExpired() { return isTokenExpired; }
        public void setIsTokenExpired(Boolean isTokenExpired) { this.isTokenExpired = isTokenExpired; }
        
        public Boolean getIsTokenExpiringSoon() { return isTokenExpiringSoon; }
        public void setIsTokenExpiringSoon(Boolean isTokenExpiringSoon) { this.isTokenExpiringSoon = isTokenExpiringSoon; }
        
        public Long getRemainingTimeMillis() { return remainingTimeMillis; }
        public void setRemainingTimeMillis(Long remainingTimeMillis) { this.remainingTimeMillis = remainingTimeMillis; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public LocalDateTime getTokenIssuedAt() { return tokenIssuedAt; }
        public void setTokenIssuedAt(LocalDateTime tokenIssuedAt) { this.tokenIssuedAt = tokenIssuedAt; }
        
        public LocalDateTime getTokenExpiresAt() { return tokenExpiresAt; }
        public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) { this.tokenExpiresAt = tokenExpiresAt; }
    }
}