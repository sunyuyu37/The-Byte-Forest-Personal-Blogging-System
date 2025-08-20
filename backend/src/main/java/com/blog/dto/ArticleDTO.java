package com.blog.dto;

import com.blog.entity.Article;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleDTO {
    
    private Long id;
    
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;
    
    private String slug;
    
    @Size(max = 500, message = "摘要长度不能超过500个字符")
    private String summary;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    private String coverImage;
    
    private UserDTO author;
    
    private Long authorId;
    
    private CategoryDTO category;
    
    private Long categoryId;
    
    private List<TagDTO> tags;
    
    private String status;
    
    private Boolean isTop;
    
    private Boolean isFeatured;
    
    private Boolean allowComment;
    
    private Integer viewCount;
    
    private Integer likeCount;
    
    private Integer commentCount;
    
    private Integer wordCount;
    
    private Integer readingTime;
    
    @Size(max = 200, message = "SEO标题长度不能超过200个字符")
    private String seoTitle;
    
    @Size(max = 500, message = "SEO描述长度不能超过500个字符")
    private String seoDescription;
    
    @Size(max = 200, message = "SEO关键词长度不能超过200个字符")
    private String seoKeywords;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 是否已点赞（用于前端显示）
    private Boolean isLiked;
    
    // 额外字段用于前端表单
    private Boolean isRecommend;
    private String keywords;
    private String description;
    
    // 分类特定数据字段
    private String categorySpecificData;
    
    // Constructors
    public ArticleDTO() {}
    
    public ArticleDTO(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.slug = article.getSlug();
        this.summary = article.getSummary();
        this.content = article.getContent();
        this.coverImage = article.getCoverImage();
        this.author = article.getAuthor() != null ? new UserDTO(article.getAuthor()) : null;
        this.authorId = article.getAuthor() != null ? article.getAuthor().getId() : null;
        this.category = article.getCategory() != null ? new CategoryDTO(article.getCategory()) : null;
        this.categoryId = article.getCategory() != null ? article.getCategory().getId() : null;
        // 加载标签
        if (article.getArticleTags() != null) {
            this.tags = article.getArticleTags().stream()
                    .map(articleTag -> new TagDTO(articleTag.getTag()))
                    .collect(Collectors.toList());
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
        this.categorySpecificData = article.getCategorySpecificData();
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
    
    public UserDTO getAuthor() { return author; }
    public void setAuthor(UserDTO author) { this.author = author; }
    
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    
    public CategoryDTO getCategory() { return category; }
    public void setCategory(CategoryDTO category) { this.category = category; }
    
    public List<TagDTO> getTags() { return tags; }
    public void setTags(List<TagDTO> tags) { this.tags = tags; }
    
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
    
    public Boolean getIsLiked() { return isLiked; }
    public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public Boolean getIsRecommend() { return isRecommend; }
    public void setIsRecommend(Boolean isRecommend) { this.isRecommend = isRecommend; }
    
    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategorySpecificData() { return categorySpecificData; }
    public void setCategorySpecificData(String categorySpecificData) { this.categorySpecificData = categorySpecificData; }
}