package com.blog.repository;

import com.blog.entity.LoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    
    /**
     * 根据用户ID查询登录日志
     */
    Page<LoginLog> findByUserIdOrderByLoginTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 查询用户的当前会话
     */
    List<LoginLog> findByUserIdAndIsCurrentSessionTrue(Long userId);
    
    /**
     * 根据会话ID查询登录日志
     */
    Optional<LoginLog> findBySessionId(String sessionId);
    
    /**
     * 查询指定时间范围内的登录日志
     */
    @Query("SELECT l FROM LoginLog l WHERE l.userId = :userId AND l.loginTime BETWEEN :startTime AND :endTime ORDER BY l.loginTime DESC")
    List<LoginLog> findByUserIdAndLoginTimeBetween(@Param("userId") Long userId, 
                                                   @Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询最近的登录记录
     */
    @Query("SELECT l FROM LoginLog l WHERE l.userId = :userId ORDER BY l.loginTime DESC")
    List<LoginLog> findRecentLoginsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 统计用户活跃会话数量
     */
    @Query("SELECT COUNT(l) FROM LoginLog l WHERE l.userId = :userId AND l.isCurrentSession = true")
    Integer countActiveSessionsByUserId(@Param("userId") Long userId);
    
    /**
     * 结束除指定会话外的所有其他会话
     */
    @Modifying
    @Query("UPDATE LoginLog l SET l.isCurrentSession = false, l.logoutTime = :logoutTime, l.loginStatus = 'LOGOUT' WHERE l.userId = :userId AND l.isCurrentSession = true AND l.sessionId != :currentSessionId")
    void endAllCurrentSessionsExcept(@Param("userId") Long userId, @Param("currentSessionId") String currentSessionId, @Param("logoutTime") LocalDateTime logoutTime);
    
    /**
     * 统计用户失败登录次数
     */
    @Query("SELECT COUNT(l) FROM LoginLog l WHERE l.userId = :userId AND l.loginStatus = 'FAILED' AND l.loginTime > :since")
    Long countFailedLoginsSince(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    /**
     * 结束用户的所有当前会话
     */
    @Modifying
    @Query("UPDATE LoginLog l SET l.isCurrentSession = false, l.logoutTime = :logoutTime, l.loginStatus = 'LOGOUT' WHERE l.userId = :userId AND l.isCurrentSession = true")
    void endAllCurrentSessions(@Param("userId") Long userId, @Param("logoutTime") LocalDateTime logoutTime);
    
    /**
     * 结束指定会话
     */
    @Modifying
    @Query("UPDATE LoginLog l SET l.isCurrentSession = false, l.logoutTime = :logoutTime, l.loginStatus = 'LOGOUT' WHERE l.sessionId = :sessionId")
    void endSession(@Param("sessionId") String sessionId, @Param("logoutTime") LocalDateTime logoutTime);
    
    /**
     * 查询异常登录（不同IP或设备）
     */
    @Query("SELECT l FROM LoginLog l WHERE l.userId = :userId AND (l.ipAddress != :currentIp OR l.deviceInfo != :currentDevice) AND l.loginTime > :since ORDER BY l.loginTime DESC")
    List<LoginLog> findAnomalousLogins(@Param("userId") Long userId, 
                                       @Param("currentIp") String currentIp, 
                                       @Param("currentDevice") String currentDevice, 
                                       @Param("since") LocalDateTime since);
    
    /**
     * 删除过期的登录日志
     */
    @Modifying
    @Query("DELETE FROM LoginLog l WHERE l.loginTime < :expireTime")
    void deleteExpiredLogs(@Param("expireTime") LocalDateTime expireTime);
    
    /**
     * 统计每日登录次数
     */
    @Query("SELECT DATE(l.loginTime) as loginDate, COUNT(l) as loginCount FROM LoginLog l WHERE l.loginTime >= :startDate GROUP BY DATE(l.loginTime) ORDER BY loginDate")
    List<Object[]> getLoginStatsByDate(@Param("startDate") LocalDateTime startDate);
}