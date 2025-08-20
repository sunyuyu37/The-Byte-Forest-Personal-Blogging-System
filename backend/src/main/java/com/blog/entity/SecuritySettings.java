package com.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_settings")
public class SecuritySettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(name = "two_factor_enabled")
    private Boolean twoFactorEnabled = false;
    
    @Column(name = "two_factor_method", length = 20)
    @Enumerated(EnumType.STRING)
    private TwoFactorMethod twoFactorMethod;
    
    @Column(name = "two_factor_secret", length = 100)
    private String twoFactorSecret;
    
    @Column(name = "login_notification_enabled")
    private Boolean loginNotificationEnabled = true;
    
    @Column(name = "anomaly_protection_enabled")
    private Boolean anomalyProtectionEnabled = true;
    
    @Column(name = "session_timeout_minutes")
    private Integer sessionTimeoutMinutes = 120;
    
    @Column(name = "password_change_required")
    private Boolean passwordChangeRequired = false;
    
    @Column(name = "last_password_change")
    private LocalDateTime lastPasswordChange;
    
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum TwoFactorMethod {
        NONE, SMS, TOTP, AUTHENTICATOR, EMAIL
    }
    
    // 构造函数
    public SecuritySettings() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public SecuritySettings(Long userId) {
        this();
        this.userId = userId;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }
    
    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }
    
    public TwoFactorMethod getTwoFactorMethod() {
        return twoFactorMethod;
    }
    
    public void setTwoFactorMethod(TwoFactorMethod twoFactorMethod) {
        this.twoFactorMethod = twoFactorMethod;
    }
    
    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }
    
    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }
    
    public Boolean getLoginNotificationEnabled() {
        return loginNotificationEnabled;
    }
    
    public void setLoginNotificationEnabled(Boolean loginNotificationEnabled) {
        this.loginNotificationEnabled = loginNotificationEnabled;
    }
    
    public Boolean getAnomalyProtectionEnabled() {
        return anomalyProtectionEnabled;
    }
    
    public void setAnomalyProtectionEnabled(Boolean anomalyProtectionEnabled) {
        this.anomalyProtectionEnabled = anomalyProtectionEnabled;
    }
    
    public Integer getSessionTimeoutMinutes() {
        return sessionTimeoutMinutes;
    }
    
    public void setSessionTimeoutMinutes(Integer sessionTimeoutMinutes) {
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
    }
    
    public Boolean getPasswordChangeRequired() {
        return passwordChangeRequired;
    }
    
    public void setPasswordChangeRequired(Boolean passwordChangeRequired) {
        this.passwordChangeRequired = passwordChangeRequired;
    }
    
    public LocalDateTime getLastPasswordChange() {
        return lastPasswordChange;
    }
    
    public void setLastPasswordChange(LocalDateTime lastPasswordChange) {
        this.lastPasswordChange = lastPasswordChange;
    }
    
    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    
    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }
    
    public LocalDateTime getAccountLockedUntil() {
        return accountLockedUntil;
    }
    
    public void setAccountLockedUntil(LocalDateTime accountLockedUntil) {
        this.accountLockedUntil = accountLockedUntil;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}