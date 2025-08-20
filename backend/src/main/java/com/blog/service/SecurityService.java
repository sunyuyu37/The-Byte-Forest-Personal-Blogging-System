package com.blog.service;

import com.blog.dto.SecurityDTO;
import com.blog.entity.LoginLog;
import com.blog.entity.SecuritySettings;
import com.blog.entity.User;
import com.blog.repository.LoginLogRepository;
import com.blog.repository.SecuritySettingsRepository;
import com.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@Transactional
public class SecurityService {
    
    private final SecuritySettingsRepository securitySettingsRepository;
    private final LoginLogRepository loginLogRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public SecurityService(SecuritySettingsRepository securitySettingsRepository,
                          LoginLogRepository loginLogRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.securitySettingsRepository = securitySettingsRepository;
        this.loginLogRepository = loginLogRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 获取用户安全设置
     */
    public SecurityDTO.SecuritySettingsDTO getUserSecuritySettings(Long userId) {
        Optional<SecuritySettings> settings = securitySettingsRepository.findByUserId(userId);
        
        if (settings.isPresent()) {
            SecuritySettings s = settings.get();
            return new SecurityDTO.SecuritySettingsDTO(
                s.getTwoFactorEnabled(),
                s.getTwoFactorMethod(),
                s.getLoginNotificationEnabled(),
                s.getAnomalyProtectionEnabled(),
                s.getSessionTimeoutMinutes(),
                s.getPasswordChangeRequired(),
                s.getLastPasswordChange(),
                s.getFailedLoginAttempts(),
                s.getAccountLockedUntil()
            );
        } else {
            // 创建默认安全设置
            return createDefaultSecuritySettings(userId);
        }
    }
    
    /**
     * 创建默认安全设置
     */
    private SecurityDTO.SecuritySettingsDTO createDefaultSecuritySettings(Long userId) {
        SecuritySettings settings = new SecuritySettings();
        settings.setUserId(userId);
        settings.setTwoFactorEnabled(false);
        settings.setTwoFactorMethod(SecuritySettings.TwoFactorMethod.NONE);
        settings.setLoginNotificationEnabled(true);
        settings.setAnomalyProtectionEnabled(true);
        settings.setSessionTimeoutMinutes(30);
        settings.setPasswordChangeRequired(false);
        settings.setFailedLoginAttempts(0);
        settings.setCreatedAt(LocalDateTime.now());
        settings.setUpdatedAt(LocalDateTime.now());
        
        SecuritySettings saved = securitySettingsRepository.save(settings);
        
        return new SecurityDTO.SecuritySettingsDTO(
            saved.getTwoFactorEnabled(),
            saved.getTwoFactorMethod(),
            saved.getLoginNotificationEnabled(),
            saved.getAnomalyProtectionEnabled(),
            saved.getSessionTimeoutMinutes(),
            saved.getPasswordChangeRequired(),
            saved.getLastPasswordChange(),
            saved.getFailedLoginAttempts(),
            saved.getAccountLockedUntil()
        );
    }
    
    /**
     * 获取用户登录活动记录
     */
    public List<SecurityDTO.LoginActivityDTO> getUserLoginActivities(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<LoginLog> logs = loginLogRepository.findRecentLoginsByUserId(userId, pageable);
        
        return logs.stream().map(log -> new SecurityDTO.LoginActivityDTO(
            log.getId(),
            log.getDeviceInfo(),
            log.getLocation(),
            log.getIpAddress(),
            log.getLoginTime(),
            log.getLogoutTime(),
            log.getIsCurrentSession(),
            log.getSessionId()
        )).collect(Collectors.toList());
    }
    
    /**
     * 获取安全中心概览
     */
    public SecurityDTO.SecurityOverviewDTO getSecurityOverview(Long userId) {
        SecurityDTO.SecuritySettingsDTO settings = getUserSecuritySettings(userId);
        List<SecurityDTO.LoginActivityDTO> recentActivities = getUserLoginActivities(userId, 10);
        Integer activeSessions = loginLogRepository.countActiveSessionsByUserId(userId);
        Boolean hasAnomalousActivity = checkAnomalousActivity(userId);
        
        return new SecurityDTO.SecurityOverviewDTO(
            settings,
            recentActivities,
            activeSessions,
            settings.getLastPasswordChange(),
            hasAnomalousActivity
        );
    }
    
    /**
     * 更新安全设置
     */
    public SecurityDTO.SecuritySettingsDTO updateSecuritySettings(Long userId, SecurityDTO.SecuritySettingsDTO dto) {
        SecuritySettings settings = securitySettingsRepository.findByUserId(userId)
            .orElse(new SecuritySettings());
        
        if (settings.getId() == null) {
            settings.setUserId(userId);
            settings.setCreatedAt(LocalDateTime.now());
        }
        
        settings.setTwoFactorEnabled(dto.getTwoFactorEnabled());
        settings.setTwoFactorMethod(dto.getTwoFactorMethod());
        settings.setLoginNotificationEnabled(dto.getLoginNotificationEnabled());
        settings.setAnomalyProtectionEnabled(dto.getAnomalyProtectionEnabled());
        settings.setSessionTimeoutMinutes(dto.getSessionTimeoutMinutes());
        settings.setUpdatedAt(LocalDateTime.now());
        
        SecuritySettings saved = securitySettingsRepository.save(settings);
        
        return new SecurityDTO.SecuritySettingsDTO(
            saved.getTwoFactorEnabled(),
            saved.getTwoFactorMethod(),
            saved.getLoginNotificationEnabled(),
            saved.getAnomalyProtectionEnabled(),
            saved.getSessionTimeoutMinutes(),
            saved.getPasswordChangeRequired(),
            saved.getLastPasswordChange(),
            saved.getFailedLoginAttempts(),
            saved.getAccountLockedUntil()
        );
    }
    
    /**
     * 启用/禁用两步验证
     */
    public SecurityDTO.SecuritySettingsDTO toggleTwoFactor(Long userId, SecurityDTO.TwoFactorSetupDTO setupDTO) {
        SecuritySettings settings = securitySettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("安全设置不存在"));
        
        if (setupDTO.getMethod() == SecuritySettings.TwoFactorMethod.NONE) {
            // 禁用两步验证
            settings.setTwoFactorEnabled(false);
            settings.setTwoFactorMethod(SecuritySettings.TwoFactorMethod.NONE);
            settings.setTwoFactorSecret(null);
        } else {
            // 启用两步验证
            settings.setTwoFactorEnabled(true);
            settings.setTwoFactorMethod(setupDTO.getMethod());
            
            if (setupDTO.getMethod() == SecuritySettings.TwoFactorMethod.TOTP) {
                // 生成TOTP密钥
                String secret = generateTOTPSecret();
                settings.setTwoFactorSecret(secret);
            }
        }
        
        settings.setUpdatedAt(LocalDateTime.now());
        SecuritySettings saved = securitySettingsRepository.save(settings);
        
        return new SecurityDTO.SecuritySettingsDTO(
            saved.getTwoFactorEnabled(),
            saved.getTwoFactorMethod(),
            saved.getLoginNotificationEnabled(),
            saved.getAnomalyProtectionEnabled(),
            saved.getSessionTimeoutMinutes(),
            saved.getPasswordChangeRequired(),
            saved.getLastPasswordChange(),
            saved.getFailedLoginAttempts(),
            saved.getAccountLockedUntil()
        );
    }
    
    /**
     * 记录登录活动
     */
    public void recordLoginActivity(Long userId, HttpServletRequest request, String sessionId) {
        LoginLog log = new LoginLog();
        log.setUserId(userId);
        log.setIpAddress(getClientIpAddress(request));
        log.setUserAgent(request.getHeader("User-Agent"));
        log.setDeviceInfo(parseDeviceInfo(request.getHeader("User-Agent")));
        log.setLocation(getLocationFromIP(log.getIpAddress()));
        log.setLoginTime(LocalDateTime.now());
        log.setSessionId(sessionId);
        log.setIsCurrentSession(true);
        log.setLoginStatus(LoginLog.LoginStatus.SUCCESS);
        log.setCreatedAt(LocalDateTime.now());
        
        loginLogRepository.save(log);
        
        // 更新用户最后登录信息
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(log.getIpAddress());
            userRepository.save(user);
        }
    }
    
    /**
     * 结束会话
     */
    public void endSession(Long userId, String sessionId) {
        Optional<LoginLog> logOpt = loginLogRepository.findBySessionId(sessionId);
        if (logOpt.isPresent()) {
            LoginLog log = logOpt.get();
            log.setLogoutTime(LocalDateTime.now());
            log.setIsCurrentSession(false);
            log.setLoginStatus(LoginLog.LoginStatus.LOGOUT);
            loginLogRepository.save(log);
        }
    }
    
    /**
     * 结束所有其他会话
     */
    public void endAllOtherSessions(Long userId, String currentSessionId) {
        loginLogRepository.endAllCurrentSessionsExcept(userId, currentSessionId, LocalDateTime.now());
    }
    
    /**
     * 检查异常活动
     */
    private Boolean checkAnomalousActivity(Long userId) {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        List<LoginLog> recentLogs = loginLogRepository.findByUserIdAndLoginTimeBetween(userId, oneDayAgo, LocalDateTime.now());
        
        // 检查是否有来自不同地理位置的登录
        long distinctLocations = recentLogs.stream()
            .map(LoginLog::getLocation)
            .distinct()
            .count();
        
        return distinctLocations > 2; // 如果24小时内从超过2个不同位置登录，认为是异常
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * 解析设备信息
     */
    private String parseDeviceInfo(String userAgent) {
        if (userAgent == null) return "未知设备";
        
        if (userAgent.contains("Mobile") || userAgent.contains("Android") || userAgent.contains("iPhone")) {
            return "移动设备";
        } else if (userAgent.contains("Tablet") || userAgent.contains("iPad")) {
            return "平板设备";
        } else {
            return "桌面设备";
        }
    }
    
    /**
     * 根据IP获取位置信息（简化实现）
     */
    private String getLocationFromIP(String ipAddress) {
        // 这里应该调用IP地理位置服务，暂时返回默认值
        if (ipAddress.startsWith("192.168.") || ipAddress.startsWith("10.") || ipAddress.startsWith("127.")) {
            return "本地网络";
        }
        return "未知位置";
    }
    
    /**
     * 生成TOTP密钥
     */
    private String generateTOTPSecret() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}