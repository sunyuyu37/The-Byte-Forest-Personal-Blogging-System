package com.blog.dto.cache;

import com.blog.entity.Article;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章缓存DTO - 专门用于Redis缓存，避免实体类序列化问题
 * 只包含缓存需要的核心字段，不包含复杂的关联关系
 */
public class ArticleCacheDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String coverImage;
    
    // 作者信息（简化）
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    
    // 分类信息（简化）
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    
    private String status;
    private Boolean isTop;
    private Boolean isFeatured;
    private Boolean allowComment;
    
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer wordCount;
    private Integer readingTime;
    
    private String seoTitle;
    private String seoDescription;
    private String seoKeywords;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public ArticleCacheDTO() {}
    
    public ArticleCacheDTO(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.slug = article.getSlug();
        this.summary = article.getSummary();
        this.content = article.getContent();
        this.coverImage = article.getCoverImage();
        
        // 安全地处理作者信息
        if (article.getAuthor() != null) {
            this.authorId = article.getAuthor().getId();
            this.authorName = article.getAuthor().getNickname() != null ? 
                article.getAuthor().getNickname() : article.getAuthor().getUsername();
            this.authorAvatar = article.getAuthor().getAvatar();
        }
        
        // 安全地处理分类信息
        if (article.getCategory() != null) {
            this.categoryId = article.getCategory().getId();
            this.categoryName = article.getCategory().getName();
            this.categorySlug = article.getCategory().getSlug();
        }
        
        this.status = article.getStatus() != null ? article.getStatus().name() : null;
        this.isTop = article.getIsTop();
        this.isFeatured = article.getIsFeatured();
        this.allowComment = article.getAllowComment();
        this.viewCount = article.getViewCount();
        this.likeCount = article.getLikeCount();
        this.commentCount = article.getCommentCount();
        this.wordCount = article.getWordCount();
        this.readingTime = article.getReadingTime();
        this.seoTitle = article.getSeoTitle();
        this.seoDescription = article.getSeoDescription();
        this.seoKeywords = article.getSeoKeywords();
        this.publishedAt = article.getPublishedAt();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public String getCategorySlug() { return categorySlug; }
    public void setCategorySlug(String categorySlug) { this.categorySlug = categorySlug; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Boolean getIsTop() { return isTop; }
    public void setIsTop(Boolean isTop) { this.isTop = isTop; }
    
    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
    
    public Boolean getAllowComment() { return allowComment; }
    public void setAllowComment(Boolean allowComment) { this.allowComment = allowComment; }
    
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    
    public Integer getWordCount() { return wordCount; }
    public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }
    
    public Integer getReadingTime() { return readingTime; }
    public void setReadingTime(Integer readingTime) { this.readingTime = readingTime; }
    
    public String getSeoTitle() { return seoTitle; }
    public void setSeoTitle(String seoTitle) { this.seoTitle = seoTitle; }
    
    public String getSeoDescription() { return seoDescription; }
    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }
    
    public String getSeoKeywords() { return seoKeywords; }
    public void setSeoKeywords(String seoKeywords) { this.seoKeywords = seoKeywords; }
    
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}