package com.blog.controller;

import com.blog.dto.AnnouncementDTO;
import com.blog.service.AnnouncementService;
import com.blog.service.UserService;
import com.blog.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户公告控制器
 */
@RestController
@RequestMapping("/announcements")
public class AnnouncementController {
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 测试端点
     */
    @GetMapping("/test")
    public Result<String> testEndpoint() {
        System.out.println("=== 测试端点被访问 ===");
        return Result.success("测试成功");
    }
    
    /**
     * 获取当前活跃的公告
     */
    @GetMapping("/active")
    public Result<List<AnnouncementDTO>> getActiveAnnouncements(HttpServletRequest request) {
        try {
            List<AnnouncementDTO> result = announcementService.getActiveAnnouncements();
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取公告失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户未读公告
     */
    @GetMapping("/unread")
    public Result<List<AnnouncementDTO>> getUnreadAnnouncements(HttpServletRequest request) {
        System.out.println("=== 进入 getUnreadAnnouncements 方法 ===");
        try {
            Long userId = getCurrentUserId();
            System.out.println("当前用户ID: " + userId);
            if (userId == null) {
                System.out.println("用户未登录，返回所有活跃公告");
                // 未登录用户返回所有活跃公告
                List<AnnouncementDTO> result = announcementService.getActiveAnnouncements();
                System.out.println("获取活跃公告成功 - 数量: " + (result != null ? result.size() : 0));
                return Result.success(result);
            }
            
            System.out.println("开始调用 announcementService.getUnreadAnnouncementsForUser");
            List<AnnouncementDTO> result = announcementService.getUnreadAnnouncementsForUser(userId);
            System.out.println("获取未读公告成功 - 数量: " + (result != null ? result.size() : 0));
            return Result.success(result);
        } catch (Exception e) {
            System.err.println("获取未读公告失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取未读公告失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记公告为已读
     */
    @PostMapping("/{id}/read")
    public Result<Void> markAnnouncementAsRead(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            announcementService.markAnnouncementAsRead(userId, id);
            return Result.success("公告已标记为已读", null);
        } catch (Exception e) {
            return Result.error("标记已读失败: " + e.getMessage());
        }
    }

    /**
     * 批量标记公告为已读
     */
    @PostMapping("/read-batch")
    public Result<Void> markAnnouncementsAsRead(
            @RequestBody List<Long> announcementIds,
            HttpServletRequest request) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            if (announcementIds == null || announcementIds.isEmpty()) {
                return Result.error("公告ID列表不能为空");
            }
            announcementService.markAnnouncementsAsRead(userId, announcementIds);
            return Result.success("批量标记已读成功", null);
        } catch (Exception e) {
            return Result.error("批量标记已读失败: " + e.getMessage());
        }
    }
    /**
     * 获取所有重要公告（示例）
     */
    @GetMapping("/important")
    public Result<List<AnnouncementDTO>> getImportantAnnouncements() {
        try {
            List<AnnouncementDTO> result = announcementService.getActiveAnnouncements()
                .stream()
                .filter(announcement -> "HIGH".equals(announcement.getPriority()) && announcement.getPinned())
                .toList();
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取重要公告失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            return userService.getUserIdByUsername(username);
        }
        return null;
    }
}