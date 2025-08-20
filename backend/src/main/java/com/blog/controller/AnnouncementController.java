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
import java.util.List;

/**
 * 用户公告控制器
 */
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private UserService userService;
    
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
        try {
            Long userId = getCurrentUserId();
            List<AnnouncementDTO> result = announcementService.getUnreadAnnouncementsForUser(userId);
            return Result.success(result);
        } catch (Exception e) {
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
            announcementService.markAnnouncementsAsRead(userId, announcementIds);
            return Result.success("公告已批量标记为已读", null);
        } catch (Exception e) {
            return Result.error("批量标记已读失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public Result<AnnouncementDTO> getAnnouncement(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long userId = getCurrentUserId();
            AnnouncementDTO result = announcementService.getAnnouncementById(id, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取公告详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据类型获取公告
     */
    @GetMapping("/type/{type}")
    public Result<List<AnnouncementDTO>> getAnnouncementsByType(
            @PathVariable String type,
            HttpServletRequest request) {
        try {
            Long userId = getCurrentUserId();
            List<AnnouncementDTO> result = announcementService.getAnnouncementsByType(type, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取公告失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户未读公告数量
     */
    @GetMapping("/unread/count")
    public Result<Long> getUnreadAnnouncementCount(HttpServletRequest request) {
        try {
            Long userId = getCurrentUserId();
            Long count = announcementService.countUnreadAnnouncementsForUser(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("获取未读公告数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取重要公告（高优先级且置顶）
     */
    @GetMapping("/important")
    public Result<List<AnnouncementDTO>> getImportantAnnouncements(HttpServletRequest request) {
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