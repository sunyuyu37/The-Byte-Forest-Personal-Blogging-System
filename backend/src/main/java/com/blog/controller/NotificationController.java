package com.blog.controller;

import com.blog.common.PageResult;
import com.blog.common.Result;
import com.blog.dto.NotificationDTO;
import com.blog.service.NotificationService;
import com.blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取当前用户通知列表（分页）
     */
    @GetMapping
    public Result<PageResult<NotificationDTO>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        System.out.println("=== 进入 getNotifications 方法 ===");
        System.out.println("请求参数 - page: " + page + ", size: " + size + ", type: " + type + ", keyword: " + keyword);
        
        try {
            Long userId = getCurrentUserId();
            System.out.println("当前用户ID: " + userId);
            if (userId == null) {
                System.out.println("用户未登录，返回错误");
                return Result.error("用户未登录");
            }
            
            System.out.println("开始调用 notificationService.getUserNotifications");
            PageResult<NotificationDTO> result = notificationService.getUserNotifications(userId, page, size, type, keyword);
            System.out.println("获取通知列表成功 - 总数: " + result.getTotal());
            return Result.success(result);
        } catch (Exception e) {
            System.err.println("获取通知列表失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取通知列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最新通知（用于头部通知面板）
     */
    @GetMapping("/latest")
    public Result<List<NotificationDTO>> getRecentNotifications(@RequestParam(defaultValue = "10") int limit) {
        System.out.println("=== 进入 getRecentNotifications 方法 ===");
        System.out.println("请求参数 - limit: " + limit);
        
        try {
            Long userId = getCurrentUserId();
            System.out.println("当前用户ID: " + userId);
            if (userId == null) {
                System.out.println("用户未登录，返回错误");
                return Result.error("用户未登录");
            }
            
            System.out.println("开始调用 notificationService.getRecentNotifications");
            List<NotificationDTO> notifications = notificationService.getRecentNotifications(userId, limit);
            System.out.println("获取最新通知成功 - 数量: " + notifications.size());
            return Result.success(notifications);
        } catch (Exception e) {
            System.err.println("获取最新通知失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取最新通知失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        System.out.println("=== 进入 getUnreadCount 方法 ===");
        
        try {
            Long userId = getCurrentUserId();
            System.out.println("当前用户ID: " + userId);
            if (userId == null) {
                System.out.println("用户未登录，返回错误");
                return Result.error("用户未登录");
            }
            
            System.out.println("开始调用 notificationService.getUnreadCount");
            long count = notificationService.getUnreadCount(userId);
            System.out.println("获取未读通知数量成功 - 数量: " + count);
            return Result.success(count);
        } catch (Exception e) {
            System.err.println("获取未读通知数量失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取未读通知数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 标记通知为已读
     */
    @PutMapping("/{id}/read")
    public Result<String> markAsRead(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        boolean success = notificationService.markAsRead(id, userId);
        if (success) {
            return Result.success("标记成功");
        } else {
            return Result.error("标记失败");
        }
    }
    
    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public Result<String> markAllAsRead() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        boolean success = notificationService.markAllAsRead(userId);
        if (success) {
            return Result.success("全部标记成功");
        } else {
            return Result.error("标记失败");
        }
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteNotification(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        boolean success = notificationService.deleteNotification(id, userId);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
    
    /**
     * 清空所有通知
     */
    @DeleteMapping("/clear")
    public Result<String> clearAllNotifications() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        boolean success = notificationService.clearAllNotifications(userId);
        if (success) {
            return Result.success("清空成功");
        } else {
            return Result.error("清空失败");
        }
    }
    
    /**
     * 创建系统通知（管理员接口）
     */
    @PostMapping("/system")
    public Result<String> createSystemNotification(@Valid @RequestBody Map<String, Object> request) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        
        // 这里可以添加管理员权限检查
        
        String title = (String) request.get("title");
        String content = (String) request.get("content");
        Long targetUserId = Long.valueOf(request.get("targetUserId").toString());
        
        notificationService.createSystemNotification(title, content, targetUserId);
        return Result.success("系统通知创建成功");
    }
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            // 通过用户名查找用户ID
            return userService.getUserIdByUsername(username);
        }
        return null;
    }
}