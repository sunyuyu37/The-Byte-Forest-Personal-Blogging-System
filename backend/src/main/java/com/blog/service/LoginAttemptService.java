package com.blog.service;

import com.blog.entity.User;
import com.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 登录尝试服务 - 处理登录失败次数限制和账户锁定
 */
@Service
@Transactional
public class LoginAttemptService {
    
    private static final int MAX_ATTEMPTS = 5; // 最大尝试次数
    private static final int LOCKOUT_DURATION_MINUTES = 30; // 锁定时长（分钟）
    
    // 内存中存储登录尝试次数（用户名 -> 尝试次数）
    private final ConcurrentHashMap<String, AtomicInteger> attemptCache = new ConcurrentHashMap<>();
    
    // 内存中存储锁定时间（用户名 -> 锁定到期时间）
    private final ConcurrentHashMap<String, LocalDateTime> lockoutCache = new ConcurrentHashMap<>();
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 记录登录失败
     * @param usernameOrEmail 用户名或邮箱
     */
    public void recordLoginFailure(String usernameOrEmail) {
        AtomicInteger attempts = attemptCache.computeIfAbsent(usernameOrEmail, k -> new AtomicInteger(0));
        int currentAttempts = attempts.incrementAndGet();
        
        if (currentAttempts >= MAX_ATTEMPTS) {
            // 达到最大尝试次数，锁定账户
            LocalDateTime lockoutUntil = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
            lockoutCache.put(usernameOrEmail, lockoutUntil);
            
            // 更新数据库中的锁定状态
            updateUserLockoutStatus(usernameOrEmail, currentAttempts, lockoutUntil);
        } else {
            // 更新数据库中的失败次数
            updateUserFailedAttempts(usernameOrEmail, currentAttempts);
        }
    }
    
    /**
     * 记录登录成功，清除失败记录
     * @param usernameOrEmail 用户名或邮箱
     */
    public void recordLoginSuccess(String usernameOrEmail) {
        attemptCache.remove(usernameOrEmail);
        lockoutCache.remove(usernameOrEmail);
        
        // 清除数据库中的失败记录
        clearUserFailedAttempts(usernameOrEmail);
    }
    
    /**
     * 检查账户是否被锁定
     * @param usernameOrEmail 用户名或邮箱
     * @return 是否被锁定
     */
    public boolean isAccountLocked(String usernameOrEmail) {
        LocalDateTime lockoutUntil = lockoutCache.get(usernameOrEmail);
        if (lockoutUntil != null) {
            if (LocalDateTime.now().isBefore(lockoutUntil)) {
                return true; // 仍在锁定期内
            } else {
                // 锁定期已过，清除锁定状态
                lockoutCache.remove(usernameOrEmail);
                attemptCache.remove(usernameOrEmail);
                clearUserFailedAttempts(usernameOrEmail);
                return false;
            }
        }
        
        // 检查数据库中的锁定状态
        return checkDatabaseLockoutStatus(usernameOrEmail);
    }
    
    /**
     * 获取剩余尝试次数
     * @param usernameOrEmail 用户名或邮箱
     * @return 剩余尝试次数
     */
    public int getRemainingAttempts(String usernameOrEmail) {
        AtomicInteger attempts = attemptCache.get(usernameOrEmail);
        if (attempts == null) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - attempts.get());
    }
    
    /**
     * 获取锁定剩余时间（分钟）
     * @param usernameOrEmail 用户名或邮箱
     * @return 锁定剩余时间，如果未锁定返回0
     */
    public long getLockoutRemainingMinutes(String usernameOrEmail) {
        LocalDateTime lockoutUntil = lockoutCache.get(usernameOrEmail);
        if (lockoutUntil != null && LocalDateTime.now().isBefore(lockoutUntil)) {
            return java.time.Duration.between(LocalDateTime.now(), lockoutUntil).toMinutes();
        }
        return 0;
    }
    
    /**
     * 手动解锁账户（管理员功能）
     * @param usernameOrEmail 用户名或邮箱
     */
    public void unlockAccount(String usernameOrEmail) {
        attemptCache.remove(usernameOrEmail);
        lockoutCache.remove(usernameOrEmail);
        clearUserFailedAttempts(usernameOrEmail);
    }
    
    /**
     * 更新数据库中的失败尝试次数
     */
    private void updateUserFailedAttempts(String usernameOrEmail, int attempts) {
        try {
            userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .ifPresent(user -> {
                    // 这里需要在User实体中添加failedLoginAttempts字段
                    // 暂时跳过数据库更新，只使用内存缓存
                });
        } catch (Exception e) {
            // 记录日志但不影响主流程
            System.err.println("更新失败尝试次数时出错: " + e.getMessage());
        }
    }
    
    /**
     * 更新数据库中的锁定状态
     */
    private void updateUserLockoutStatus(String usernameOrEmail, int attempts, LocalDateTime lockoutUntil) {
        try {
            userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .ifPresent(user -> {
                    // 这里需要在User实体中添加accountLockedUntil字段
                    // 暂时跳过数据库更新，只使用内存缓存
                });
        } catch (Exception e) {
            // 记录日志但不影响主流程
            System.err.println("更新锁定状态时出错: " + e.getMessage());
        }
    }
    
    /**
     * 清除数据库中的失败记录
     */
    private void clearUserFailedAttempts(String usernameOrEmail) {
        try {
            userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .ifPresent(user -> {
                    // 这里需要在User实体中清除失败记录
                    // 暂时跳过数据库更新，只使用内存缓存
                });
        } catch (Exception e) {
            // 记录日志但不影响主流程
            System.err.println("清除失败记录时出错: " + e.getMessage());
        }
    }
    
    /**
     * 检查数据库中的锁定状态
     */
    private boolean checkDatabaseLockoutStatus(String usernameOrEmail) {
        try {
            return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(user -> {
                    // 这里需要检查User实体中的锁定状态
                    // 暂时返回false，只使用内存缓存
                    return false;
                })
                .orElse(false);
        } catch (Exception e) {
            // 记录日志但不影响主流程
            System.err.println("检查锁定状态时出错: " + e.getMessage());
            return false;
        }
    }
}