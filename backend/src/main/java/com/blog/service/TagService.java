package com.blog.service;

import com.blog.dto.TagDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {
    
    /**
     * 获取所有标签
     */
    List<TagDTO> getAllTags();
    
    /**
     * 分页获取标签
     */
    Page<TagDTO> getTags(Pageable pageable);
    
    /**
     * 搜索标签
     */
    Page<TagDTO> searchTags(String keyword, Pageable pageable);
    
    /**
     * 根据ID获取标签
     */
    TagDTO getTagById(Long id);
    
    /**
     * 根据slug获取标签
     */
    TagDTO getTagBySlug(String slug);
    
    /**
     * 创建标签
     */
    TagDTO createTag(TagDTO tagDTO);
    
    /**
     * 更新标签
     */
    TagDTO updateTag(Long id, TagDTO tagDTO);
    
    /**
     * 删除标签
     */
    void deleteTag(Long id);
    
    /**
     * 批量删除标签
     */
    void deleteTags(List<Long> ids);
    
    /**
     * 获取热门标签
     */
    List<TagDTO> getPopularTags(int limit);
    
    /**
     * 统计标签数量
     */
    long countTags();
    
    /**
     * 重新计算所有标签的文章数量（只统计已发布的文章）
     */
    void recalculateAllTagArticleCounts();
}