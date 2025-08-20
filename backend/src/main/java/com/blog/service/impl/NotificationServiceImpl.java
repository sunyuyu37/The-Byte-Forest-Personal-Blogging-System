package com.blog.service.impl;

import com.blog.common.PageResult;
import com.blog.dto.NotificationDTO;
import com.blog.entity.Notification;
import com.blog.repository.NotificationRepository;
import com.blog.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Override
    public Notification createNotification(String title, String content, String type, Long userId, Long relatedId, String relatedType) {
        Notification notification = new Notification(title, content, type, userId);
        notification.setRelatedId(relatedId);
        notification.setRelatedType(relatedType);
        return notificationRepository.save(notification);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResult<NotificationDTO> getUserNotifications(Long userId, int page, int size, String type, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage;
        
        if (StringUtils.hasText(keyword)) {
            // 按关键词搜索
            notificationPage = notificationRepository.findByUserIdAndKeyword(userId, keyword, pageable);
        } else if ("unread".equals(type)) {
            // 获取未读通知
            notificationPage = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable);
        } else if (StringUtils.hasText(type) && !"all".equals(type)) {
            // 按类型筛选
            notificationPage = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type, pageable);
        } else {
            // 获取所有通知
            notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }
        
        List<NotificationDTO> notificationDTOs = notificationPage.getContent().stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
        
        return new PageResult<>(
                notificationDTOs,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalElements()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getRecentNotifications(Long userId, int limit) {
        List<Notification> notifications;
        if (limit == 5) {
            notifications = notificationRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
        } else {
            Pageable pageable = PageRequest.of(0, limit);
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).getContent();
        }
        
        return notifications.stream()
                .map(NotificationDTO::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean markAsRead(Long notificationId, Long userId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            // 验证通知属于当前用户
            if (notification.getUserId().equals(userId)) {
                notification.setIsRead(true);
                notificationRepository.save(notification);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean markAllAsRead(Long userId) {
        int updatedCount = notificationRepository.markAllAsReadByUserId(userId);
        return updatedCount > 0;
    }
    
    @Override
    public boolean deleteNotification(Long notificationId, Long userId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            // 验证通知属于当前用户
            if (notification.getUserId().equals(userId)) {
                notificationRepository.delete(notification);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean clearAllNotifications(Long userId) {
        int deletedCount = notificationRepository.deleteAllByUserId(userId);
        return deletedCount > 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    @Override
    public void createCommentNotification(Long articleId, Long commentId, String commenterName, String commentContent, String articleTitle, Long authorId) {
        String title = "新的评论待审核";
        // 将具体评论内容带上，必要时截断避免过长
        String preview = commentContent == null ? "" : commentContent.trim();
        if (preview.length() > 120) {
            preview = preview.substring(0, 120) + "...";
        }
        String content = String.format("用户\"%s\"在文章\"%s\"下发表了新评论：%s", commenterName, articleTitle, preview);
        createNotification(title, content, "comment", authorId, commentId, "comment");
    }
    
    @Override
    public void createSystemNotification(String title, String content, Long userId) {
        createNotification(title, content, "system", userId, null, null);
    }
    
    @Override
    public void createUserNotification(String title, String content, Long userId, Long relatedUserId) {
        createNotification(title, content, "user", userId, relatedUserId, "user");
    }
}