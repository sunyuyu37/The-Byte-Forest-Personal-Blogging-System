package com.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_logs")
public class LoginLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "device_info", length = 200)
    private String deviceInfo;
    
    @Column(name = "location", length = 100)
    private String location;
    
    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;
    
    @Column(name = "logout_time")
    private LocalDateTime logoutTime;
    
    @Column(name = "session_id", length = 100)
    private String sessionId;
    
    @Column(name = "is_current_session")
    private Boolean isCurrentSession = false;
    
    @Column(name = "login_status", length = 20)
    @Enumerated(EnumType.STRING)
    private LoginStatus loginStatus = LoginStatus.SUCCESS;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    public enum LoginStatus {
        SUCCESS, FAILED, LOGOUT
    }
    
    // 构造函数
    public LoginLog() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public LoginLog(Long userId, String ipAddress, String userAgent, String deviceInfo, String location, String sessionId) {
        this();
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.deviceInfo = deviceInfo;
        this.location = location;
        this.sessionId = sessionId;
        this.loginTime = LocalDateTime.now();
        this.isCurrentSession = true;
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
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getDeviceInfo() {
        return deviceInfo;
    }
    
    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public LocalDateTime getLoginTime() {
        return loginTime;
    }
    
    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }
    
    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }
    
    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public Boolean getIsCurrentSession() {
        return isCurrentSession;
    }
    
    public void setIsCurrentSession(Boolean isCurrentSession) {
        this.isCurrentSession = isCurrentSession;
    }
    
    public LoginStatus getLoginStatus() {
        return loginStatus;
    }
    
    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
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