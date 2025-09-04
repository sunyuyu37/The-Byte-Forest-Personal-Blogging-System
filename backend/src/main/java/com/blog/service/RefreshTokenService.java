package com.blog.service;

import com.blog.util.JwtUtil;
import com.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * 刷新令牌服务
 * 用于管理JWT刷新令牌的生成、验证和撤销
 */
@Service
public class RefreshTokenService {
    
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final SecureRandom secureRandom = new SecureRandom();
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    @Value("${jwt.refresh.expiration:604800000}") // 默认7天
    private Long refreshTokenExpiration;
    
    /**
     * 生成刷新令牌
     * @param userId 用户ID
     * @return 刷新令牌
     */
    public String generateRefreshToken(Long userId) {
        // 生成随机令牌
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        
        // 存储到Redis，关联用户ID
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, userId.toString(), refreshTokenExpiration, TimeUnit.MILLISECONDS);
        
        return refreshToken;
    }
    
    /**
     * 验证刷新令牌并获取用户ID
     * @param refreshToken 刷新令牌
     * @return 用户ID，如果令牌无效则返回null
     */
    public Long validateRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        Object userIdObj = redisTemplate.opsForValue().get(key);
        
        if (userIdObj != null) {
            try {
                return Long.parseLong(userIdObj.toString());
            } catch (NumberFormatException e) {
                // 删除无效的令牌
                redisTemplate.delete(key);
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * 撤销刷新令牌
     * @param refreshToken 刷新令牌
     */
    public void revokeRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.delete(key);
    }
    
    /**
     * 撤销用户的所有刷新令牌
     * @param userId 用户ID
     */
    public void revokeAllUserRefreshTokens(Long userId) {
        String pattern = REFRESH_TOKEN_PREFIX + "*";
        redisTemplate.keys(pattern).forEach(key -> {
            Object userIdObj = redisTemplate.opsForValue().get(key);
            if (userIdObj != null && userId.toString().equals(userIdObj.toString())) {
                redisTemplate.delete(key);
            }
        });
    }
    
    /**
     * 刷新访问令牌（使用刷新令牌生成新的访问令牌）
     * @param oldRefreshToken 旧的刷新令牌
     * @return 新的访问令牌，如果旧令牌无效则抛出异常
     */
    public String refreshToken(String oldRefreshToken) {
        Long userId = validateRefreshToken(oldRefreshToken);
        if (userId == null) {
            throw new RuntimeException("刷新令牌无效或已过期");
        }
        
        try {
            // 获取用户信息
            String username = userService.getUsernameById(userId);
            if (username == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 生成新的访问令牌
            return jwtUtil.generateToken(username, userId);
        } catch (Exception e) {
            throw new RuntimeException("生成访问令牌失败: " + e.getMessage());
        }
    }
}