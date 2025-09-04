package com.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * JWT黑名单服务
 * 用于管理被撤销的JWT令牌
 */
@Service
public class JwtBlacklistService {
    
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 将JWT令牌加入黑名单
     * @param jti JWT ID
     * @param expiration 令牌过期时间
     */
    public void addToBlacklist(String jti, Date expiration) {
        String key = BLACKLIST_PREFIX + jti;
        long ttl = expiration.getTime() - System.currentTimeMillis();
        
        if (ttl > 0) {
            // 设置过期时间，避免Redis中存储过期的黑名单记录
            redisTemplate.opsForValue().set(key, "blacklisted", ttl, TimeUnit.MILLISECONDS);
        }
    }
    
    /**
     * 检查JWT令牌是否在黑名单中
     * @param jti JWT ID
     * @return true表示在黑名单中
     */
    public boolean isBlacklisted(String jti) {
        String key = BLACKLIST_PREFIX + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * 从黑名单中移除JWT令牌（一般不需要，因为会自动过期）
     * @param jti JWT ID
     */
    public void removeFromBlacklist(String jti) {
        String key = BLACKLIST_PREFIX + jti;
        redisTemplate.delete(key);
    }
    
    /**
     * 清空用户的所有JWT令牌（用户登出所有设备）
     * @param userId 用户ID
     */
    public void blacklistAllUserTokens(Long userId) {
        String pattern = BLACKLIST_PREFIX + "user:" + userId + ":*";
        redisTemplate.delete(redisTemplate.keys(pattern));
    }
}