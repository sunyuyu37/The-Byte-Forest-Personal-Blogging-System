package com.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "article")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "comments", "articleTags", "articleLikes"})
public class Article {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    @Column(nullable = false, length = 200)
    private String title;
    
    @NotBlank(message = "文章别名不能为空")
    @Size(max = 200, message = "别名长度不能超过200个字符")
    @Column(unique = true, nullable = false, length = 200)
    private String slug;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @NotBlank(message = "文章内容不能为空")
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;
    
    @Column(name = "cover_image", length = 500)
    private String coverImage;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DRAFT;
    
    @Column(name = "is_top", nullable = false)
    private Boolean isTop = false;
    
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;
    
    @Column(name = "allow_comment", nullable = false)
    private Boolean allowComment = true;
    
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;
    
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;
    
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;
    
    @Column(name = "word_count", nullable = false)
    private Integer wordCount = 0;
    
    @Column(name = "reading_time", nullable = false)
    private Integer readingTime = 0;
    
    @Column(name = "seo_title", length = 200)
    private String seoTitle;
    
    @Column(name = "seo_description", columnDefinition = "TEXT")
    private String seoDescription;
    
    @Column(name = "seo_keywords", length = 500)
    private String seoKeywords;
    
    // 分类特定字段，使用JSON格式存储
    @Column(name = "category_specific_data", columnDefinition = "JSON")
    private String categorySpecificData;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    

    
    // 关联关系
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Comment> comments;
    
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ArticleTag> articleTags;
    
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ArticleLike> articleLikes;
    
    public enum Status {
        DRAFT, PUBLISHED, ARCHIVED
    }
    
    // Constructors
    public Article() {}
    
    public Article(String title, String slug, String content, User author) {
        this.title = title;
        this.slug = slug;
        this.content = content;
        this.author = author;
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
    
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
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
    
    public String getCategorySpecificData() { return categorySpecificData; }
    public void setCategorySpecificData(String categorySpecificData) { this.categorySpecificData = categorySpecificData; }
    
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    

    
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    
    public List<ArticleTag> getArticleTags() { return articleTags; }
    public void setArticleTags(List<ArticleTag> articleTags) { this.articleTags = articleTags; }
    
    public List<ArticleLike> getArticleLikes() { return articleLikes; }
    public void setArticleLikes(List<ArticleLike> articleLikes) { this.articleLikes = articleLikes; }
}