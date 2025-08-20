package com.blog.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 公告实体类
 */
@Entity
@Table(name = "personal_blog_announcement")
@EntityListeners(AuditingEntityListener.class)
public class Announcement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 公告标题
     */
    @Column(nullable = false, length = 200)
    private String title;
    
    /**
     * 公告内容
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    /**
     * 公告类型：system(系统公告), notice(通知公告), warning(警告公告), info(信息公告)
     */
    @Column(nullable = false, length = 20)
    private String type = "notice";
    
    /**
     * 优先级：high(高), medium(中), low(低)
     */
    @Column(nullable = false, length = 10)
    private String priority = "medium";
    
    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;
    
    /**
     * 是否置顶
     */
    @Column(nullable = false)
    private Boolean pinned = false;
    
    /**
     * 发布者ID（管理员）
     */
    @Column(name = "publisher_id", nullable = false)
    private Long publisherId;
    
    /**
     * 生效开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    /**
     * 生效结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    // 构造函数
    public Announcement() {}
    
    public Announcement(String title, String content, Long publisherId) {
        this.title = title;
        this.content = content;
        this.publisherId = publisherId;
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
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public Boolean getPinned() {
        return pinned;
    }
    
    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }
    
    public Long getPublisherId() {
        return publisherId;
    }
    
    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
    
    public Boolean getDeleted() {
        return deleted;
    }
    
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}