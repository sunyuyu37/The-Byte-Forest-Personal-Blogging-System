package com.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 通知实体类
 */
@Entity
@Table(name = "personal_blog_notification")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 通知标题
     */
    @Column(nullable = false, length = 200)
    private String title;
    
    /**
     * 通知内容
     */
    @Column(columnDefinition = "TEXT")
    private String content;
    
    /**
     * 通知类型：comment(评论), system(系统), user(用户), success(成功), warning(警告), info(信息)
     */
    @Column(nullable = false, length = 20)
    private String type;
    
    /**
     * 接收用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 是否已读
     */
    @Column(nullable = false)
    private Boolean isRead = false;
    
    /**
     * 相关实体ID（如评论ID、文章ID等）
     */
    @Column(name = "related_id")
    private Long relatedId;
    
    /**
     * 相关实体类型（如comment、article等）
     */
    @Column(name = "related_type", length = 50)
    private String relatedType;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Notification(String title, String content, String type, Long userId) {
        this();
        this.title = title;
        this.content = content;
        this.type = type;
        this.userId = userId;
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
        this.updatedAt = LocalDateTime.now();
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}