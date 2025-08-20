package com.blog.repository;

import com.blog.entity.SecuritySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SecuritySettingsRepository extends JpaRepository<SecuritySettings, Long> {
    
    /**
     * 根据用户ID查询安全设置
     */
    Optional<SecuritySettings> findByUserId(Long userId);
    
    /**
     * 查询启用了两步验证的用户
     */
    @Query("SELECT s FROM SecuritySettings s WHERE s.twoFactorEnabled = true")
    List<SecuritySettings> findUsersWithTwoFactorEnabled();
    
    /**
     * 查询需要修改密码的用户
     */
    @Query("SELECT s FROM SecuritySettings s WHERE s.passwordChangeRequired = true")
    List<SecuritySettings> findUsersRequiringPasswordChange();
    
    /**
     * 查询账户被锁定的用户
     */
    @Query("SELECT s FROM SecuritySettings s WHERE s.accountLockedUntil IS NOT NULL AND s.accountLockedUntil > :currentTime")
    List<SecuritySettings> findLockedAccounts(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 查询密码过期的用户（超过指定天数未修改密码）
     */
    @Query("SELECT s FROM SecuritySettings s WHERE s.lastPasswordChange IS NOT NULL AND s.lastPasswordChange < :expireDate")
    List<SecuritySettings> findUsersWithExpiredPasswords(@Param("expireDate") LocalDateTime expireDate);
    
    /**
     * 统计启用两步验证的用户数量
     */
    @Query("SELECT COUNT(s) FROM SecuritySettings s WHERE s.twoFactorEnabled = true")
    Long countUsersWithTwoFactorEnabled();
    
    /**
     * 统计被锁定的账户数量
     */
    @Query("SELECT COUNT(s) FROM SecuritySettings s WHERE s.accountLockedUntil IS NOT NULL AND s.accountLockedUntil > :currentTime")
    Long countLockedAccounts(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 检查用户是否存在安全设置
     */
    boolean existsByUserId(Long userId);
}