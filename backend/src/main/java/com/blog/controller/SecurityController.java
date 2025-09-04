package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.SecurityDTO;
import com.blog.service.SecurityService;
import com.blog.service.UserService;
import com.blog.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/security")
public class SecurityController {
    
    private final SecurityService securityService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    public SecurityController(SecurityService securityService, UserService userService, JwtUtil jwtUtil) {
        this.securityService = securityService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * 获取安全中心概览
     */
    @GetMapping("/overview")
    public Result<SecurityDTO.SecurityOverviewDTO> getSecurityOverview(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            SecurityDTO.SecurityOverviewDTO overview = securityService.getSecurityOverview(userId);
            return Result.success(overview);
        } catch (Exception e) {
            return Result.error("获取安全概览失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户安全设置
     */
    @GetMapping("/settings")
    public Result<SecurityDTO.SecuritySettingsDTO> getSecuritySettings(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            SecurityDTO.SecuritySettingsDTO settings = securityService.getUserSecuritySettings(userId);
            return Result.success(settings);
        } catch (Exception e) {
            return Result.error("获取安全设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新安全设置
     */
    @PutMapping("/settings")
    public Result<SecurityDTO.SecuritySettingsDTO> updateSecuritySettings(
            @Valid @RequestBody SecurityDTO.SecuritySettingsDTO settingsDTO,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            SecurityDTO.SecuritySettingsDTO updatedSettings = securityService.updateSecuritySettings(userId, settingsDTO);
            return Result.success("安全设置更新成功", updatedSettings);
        } catch (Exception e) {
            return Result.error("更新安全设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取登录活动记录
     */
    @GetMapping("/activities")
    public Result<List<SecurityDTO.LoginActivityDTO>> getLoginActivities(
            @RequestParam(defaultValue = "10") int limit,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<SecurityDTO.LoginActivityDTO> activities = securityService.getUserLoginActivities(userId, limit);
            return Result.success(activities);
        } catch (Exception e) {
            return Result.error("获取登录活动失败: " + e.getMessage());
        }
    }
    
    /**
     * 启用/禁用两步验证
     */
    @PostMapping("/two-factor")
    public Result<SecurityDTO.SecuritySettingsDTO> toggleTwoFactor(
            @Valid @RequestBody SecurityDTO.TwoFactorSetupDTO setupDTO,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            SecurityDTO.SecuritySettingsDTO settings = securityService.toggleTwoFactor(userId, setupDTO);
            String msg = settings.getTwoFactorEnabled() ? "两步验证已启用" : "两步验证已禁用";
            return Result.success(msg, settings);
        } catch (Exception e) {
            return Result.error("两步验证设置失败: " + e.getMessage());
        }
    }
    
    /**
     * 结束指定会话
     */
    @DeleteMapping("/sessions/{sessionId}")
    public Result<Void> endSession(
            @PathVariable String sessionId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            securityService.endSession(userId, sessionId);
            return Result.success("会话已结束", null);
        } catch (Exception e) {
            return Result.error("结束会话失败: " + e.getMessage());
        }
    }
    
    /**
     * 结束所有其他会话
     */
    @DeleteMapping("/sessions/others")
    public Result<Void> endAllOtherSessions(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            String currentSessionId = request.getSession().getId();
            securityService.endAllOtherSessions(userId, currentSessionId);
            return Result.success("所有其他会话已结束", null);
        } catch (Exception e) {
            return Result.error("结束会话失败: " + e.getMessage());
        }
    }
    
    /**
     * Token状态监控：返回Token是否有效、是否即将过期、剩余时间等信息
     */
    @GetMapping("/token/status")
    public Result<SecurityDTO.TokenStatusDTO> getTokenStatus(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Result.success(new SecurityDTO.TokenStatusDTO(false, true, true, 0L, null, null, null));
        }
        String token = authHeader.substring(7);
        try {
            String username = jwtUtil.extractUsername(token);
            Date expiration = jwtUtil.extractExpiration(token);
            Date issuedAt = jwtUtil.extractClaim(token, claims -> claims.getIssuedAt());
            
            boolean valid = username != null && jwtUtil.validateToken(token, username);
            long now = System.currentTimeMillis();
            long expMillis = expiration.getTime();
            long remaining = Math.max(0, expMillis - now);
            boolean expired = now >= expMillis;
            boolean expiringSoon = remaining <= 5 * 60 * 1000; // 5分钟
            
            SecurityDTO.TokenStatusDTO dto = new SecurityDTO.TokenStatusDTO(
                true,
                expired,
                expiringSoon,
                remaining,
                username,
                issuedAt != null ? LocalDateTime.ofInstant(issuedAt.toInstant(), ZoneId.systemDefault()) : null,
                expiration != null ? LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault()) : null
            );
            return Result.success(dto);
        } catch (Exception e) {
            return Result.success(new SecurityDTO.TokenStatusDTO(true, true, true, 0L, null, null, null));
        }
    }
    
    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                String username = jwtUtil.extractUsername(token);
                
                if (username == null || !jwtUtil.validateToken(token, username)) {
                    throw new RuntimeException("token已过期或无效");
                }
                
                // 通过username获取userId
                 try {
                     return Long.parseLong(jwtUtil.extractClaim(token, claims -> claims.get("userId", String.class)));
                 } catch (Exception e) {
                     // 如果从token中提取userId失败，则通过UserService查询
                     Long userId = userService.getUserIdByUsername(username);
                     if (userId == null) {
                         throw new RuntimeException("用户不存在");
                     }
                     return userId;
                 }
            }
            throw new RuntimeException("无效的认证令牌");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}