package com.blog.util;

import com.blog.service.JwtBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    @Autowired
    private JwtBlacklistService jwtBlacklistService;
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public String extractJwtId(String token) {
        return extractClaim(token, Claims::getId);
    }
    
    public Long extractUserId(String token) {
        String userIdStr = extractClaim(token, claims -> claims.get("userId", String.class));
        return userIdStr != null ? Long.parseLong(userIdStr) : null;
    }
    
    public Long getUserIdFromToken(String token) {
        return extractUserId(token);
    }
    
    public String getUsernameFromToken(String token) {
        return extractUsername(token);
    }
    
    public Boolean validateToken(String token) {
        try {
            String username = extractUsername(token);
            return username != null && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * 生成唯一的JWT ID
     */
    private String generateJwtId() {
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, null);
    }
    
    public String generateToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        return createToken(claims, username, userId);
    }
    
    private String createToken(Map<String, Object> claims, String subject, Long userId) {
        String jwtId = generateJwtId();
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setId(jwtId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            final String jwtId = extractJwtId(token);
            
            // 检查用户名匹配、令牌未过期、且不在黑名单中
            return (extractedUsername.equals(username) 
                    && !isTokenExpired(token) 
                    && !jwtBlacklistService.isBlacklisted(jwtId));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 撤销JWT令牌（加入黑名单）
     */
    public void revokeToken(String token) {
        try {
            String jwtId = extractJwtId(token);
            Date expiration = extractExpiration(token);
            jwtBlacklistService.addToBlacklist(jwtId, expiration);
        } catch (Exception e) {
            // 令牌解析失败，忽略
        }
    }
    
    /**
     * 检查令牌是否被撤销
     */
    public Boolean isTokenRevoked(String token) {
        try {
            String jwtId = extractJwtId(token);
            return jwtBlacklistService.isBlacklisted(jwtId);
        } catch (Exception e) {
            return true; // 解析失败视为已撤销
        }
    }
}