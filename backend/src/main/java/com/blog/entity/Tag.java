package com.blog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tag")
@EntityListeners(AuditingEntityListener.class)
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50个字符")
    @Column(unique = true, nullable = false, length = 50)
    private String name;
    
    @NotBlank(message = "标签别名不能为空")
    @Size(max = 50, message = "标签别名长度不能超过50个字符")
    @Column(unique = true, nullable = false, length = 50)
    private String slug;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "颜色格式不正确，请使用十六进制格式（如#FF0000）")
    @Column(length = 7)
    private String color = "#409EFF";
    
    @Column(name = "article_count", nullable = false)
    private Integer articleCount = 0;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    

    
    // 关联关系
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ArticleTag> articleTags;
    
    // Constructors
    public Tag() {}
    
    public Tag(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Integer getArticleCount() { return articleCount; }
    public void setArticleCount(Integer articleCount) { this.articleCount = articleCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    

    
    public List<ArticleTag> getArticleTags() { return articleTags; }
    public void setArticleTags(List<ArticleTag> articleTags) { this.articleTags = articleTags; }
}