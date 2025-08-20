package com.blog.service;

import com.blog.common.PageResult;
import com.blog.dto.NotificationDTO;
import com.blog.entity.Notification;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 创建通知
     */
    Notification createNotification(String title, String content, String type, Long userId, Long relatedId, String relatedType);
    
    /**
     * 获取用户通知列表（分页）
     */
    PageResult<NotificationDTO> getUserNotifications(Long userId, int page, int size, String type, String keyword);
    
    /**
     * 获取用户最新通知
     */
    List<NotificationDTO> getRecentNotifications(Long userId, int limit);
    
    /**
     * 标记通知为已读
     */
    boolean markAsRead(Long notificationId, Long userId);
    
    /**
     * 标记所有通知为已读
     */
    boolean markAllAsRead(Long userId);
    
    /**
     * 删除通知
     */
    boolean deleteNotification(Long notificationId, Long userId);
    
    /**
     * 清空所有通知
     */
    boolean clearAllNotifications(Long userId);
    
    /**
     * 获取未读通知数量
     */
    long getUnreadCount(Long userId);
    
    /**
     * 创建评论通知
     */
    void createCommentNotification(Long articleId, Long commentId, String commenterName, String commentContent, String articleTitle, Long authorId);
    
    /**
     * 创建系统通知
     */
    void createSystemNotification(String title, String content, Long userId);
    
    /**
     * 创建用户通知
     */
    void createUserNotification(String title, String content, Long userId, Long relatedUserId);
}