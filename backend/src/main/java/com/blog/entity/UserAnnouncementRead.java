package com.blog.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 用户公告已读状态实体类
 */
@Entity
@Table(name = "personal_blog_user_announcement_read", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "announcement_id"}))
@EntityListeners(AuditingEntityListener.class)
public class UserAnnouncementRead {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 公告ID
     */
    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;
    
    /**
     * 已读时间
     */
    @CreatedDate
    @Column(name = "read_at", nullable = false, updatable = false)
    private LocalDateTime readAt;
    
    // 构造函数
    public UserAnnouncementRead() {}
    
    public UserAnnouncementRead(Long userId, Long announcementId) {
        this.userId = userId;
        this.announcementId = announcementId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getAnnouncementId() {
        return announcementId;
    }
    
    public void setAnnouncementId(Long announcementId) {
        this.announcementId = announcementId;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}