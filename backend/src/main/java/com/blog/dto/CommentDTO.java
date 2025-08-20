package com.blog.dto;

import com.blog.entity.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    
    private Long id;
    
    private Long articleId;
    
    private String articleTitle;
    
    private UserDTO user;
    
    private Long parentId;
    
    private Long replyToId;
    
    private UserDTO replyToUser;
    
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容长度不能超过1000个字符")
    private String content;
    
    private String ipAddress;
    
    private String userAgent;
    
    private Comment.Status status;
    
    private Integer likeCount;
    
    private Integer replyCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 子评论列表
    private List<CommentDTO> replies;
    
    // Constructors
    public CommentDTO() {}
    
    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        
        // 安全地处理文章信息
        try {
            if (comment.getArticle() != null) {
                this.articleId = comment.getArticle().getId();
                this.articleTitle = comment.getArticle().getTitle();
            }
        } catch (Exception e) {
            // 如果文章不存在或无法访问，设置为null
            this.articleId = null;
            this.articleTitle = "文章已删除";
        }
        
        // 安全地处理用户信息
        try {
            this.user = comment.getUser() != null ? new UserDTO(comment.getUser()) : null;
        } catch (Exception e) {
            this.user = null;
        }
        
        this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
        
        // 安全地处理回复用户信息
        try {
            this.replyToId = comment.getReplyTo() != null ? comment.getReplyTo().getId() : null;
            this.replyToUser = comment.getReplyTo() != null ? new UserDTO(comment.getReplyTo()) : null;
        } catch (Exception e) {
            this.replyToId = null;
            this.replyToUser = null;
        }
        this.content = comment.getContent();
        this.ipAddress = comment.getIpAddress();
        this.userAgent = comment.getUserAgent();
        this.status = comment.getStatus();
        this.likeCount = comment.getLikeCount();
        this.replyCount = comment.getReplyCount();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getArticleId() { return articleId; }
    public void setArticleId(Long articleId) { this.articleId = articleId; }
    
    public String getArticleTitle() { return articleTitle; }
    public void setArticleTitle(String articleTitle) { this.articleTitle = articleTitle; }
    
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
    
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    
    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }
    
    public UserDTO getReplyToUser() { return replyToUser; }
    public void setReplyToUser(UserDTO replyToUser) { this.replyToUser = replyToUser; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public Comment.Status getStatus() { return status; }
    public void setStatus(Comment.Status status) { this.status = status; }
    
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    
    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<CommentDTO> getReplies() { return replies; }
    public void setReplies(List<CommentDTO> replies) { this.replies = replies; }
}