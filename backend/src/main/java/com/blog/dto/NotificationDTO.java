package com.blog.dto;

import com.blog.entity.Notification;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 通知DTO
 */
public class NotificationDTO {
    
    private Long id;
    private String title;
    private String content;
    private String type;
    private Boolean isRead;
    private Long relatedId;
    private String relatedType;
    private String createdAt;
    private String timeAgo;
    
    public NotificationDTO() {}
    
    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.type = notification.getType();
        this.isRead = notification.getIsRead();
        this.relatedId = notification.getRelatedId();
        this.relatedType = notification.getRelatedType();
        this.createdAt = notification.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.timeAgo = calculateTimeAgo(notification.getCreatedAt());
    }
    
    /**
     * 计算时间差
     */
    private String calculateTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();
        
        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 1440) { // 24小时
            return (minutes / 60) + "小时前";
        } else if (minutes < 10080) { // 7天
            return (minutes / 1440) + "天前";
        } else {
            return createdAt.format(DateTimeFormatter.ofPattern("MM-dd"));
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public Long getRelatedId() {
        return relatedId;
    }
    
    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }
    
    public String getRelatedType() {
        return relatedType;
    }
    
    public void setRelatedType(String relatedType) {
        this.relatedType = relatedType;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getTimeAgo() {
        return timeAgo;
    }
    
    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
}