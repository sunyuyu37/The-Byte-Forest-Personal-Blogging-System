package com.blog.dto;

import com.blog.entity.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TagDTO {
    
    private Long id;
    
    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50个字符")
    private String name;
    
    private String slug;
    
    @Size(max = 200, message = "标签描述长度不能超过200个字符")
    private String description;
    
    @Size(max = 7, message = "颜色值长度不能超过7个字符")
    private String color;
    
    private Integer articleCount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Constructors
    public TagDTO() {}
    
    public TagDTO(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.slug = tag.getSlug();
        this.description = tag.getDescription();
        this.color = tag.getColor();
        this.articleCount = tag.getArticleCount();
        this.createdAt = tag.getCreatedAt();
        this.updatedAt = tag.getUpdatedAt();
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
}