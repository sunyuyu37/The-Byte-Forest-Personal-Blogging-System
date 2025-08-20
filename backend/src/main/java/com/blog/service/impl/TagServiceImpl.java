package com.blog.service.impl;

import com.blog.dto.TagDTO;
import com.blog.entity.Article;
import com.blog.entity.Tag;
import com.blog.repository.ArticleTagRepository;
import com.blog.repository.TagRepository;
import com.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;
    
    @Autowired
    private ArticleTagRepository articleTagRepository;

    @Override
    public List<TagDTO> getAllTags() {
        List<Tag> tags = tagRepository.findAllActive();
        return tags.stream()
                .map(TagDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TagDTO> getTags(Pageable pageable) {
        return tagRepository.findAllTags(pageable)
                .map(TagDTO::new);
    }

    @Override
    public Page<TagDTO> searchTags(String keyword, Pageable pageable) {
        return tagRepository.searchTags(keyword, pageable)
                .map(TagDTO::new);
    }

    @Override
    public TagDTO getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在: " + id));
        return new TagDTO(tag);
    }

    @Override
    public TagDTO getTagBySlug(String slug) {
        Tag tag = tagRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("标签不存在: " + slug));
        return new TagDTO(tag);
    }

    @Override
    public TagDTO createTag(TagDTO tagDTO) {
        // 检查标签名称是否已存在
        if (tagRepository.existsByName(tagDTO.getName())) {
            throw new RuntimeException("标签名称已存在: " + tagDTO.getName());
        }

        Tag tag = new Tag();
        tag.setName(tagDTO.getName());
        tag.setSlug(generateTagSlug(tagDTO.getName()));
        tag.setDescription(tagDTO.getDescription());
        tag.setColor(tagDTO.getColor() != null ? tagDTO.getColor() : "#007bff");
        tag.setArticleCount(0);

        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());

        Tag savedTag = tagRepository.save(tag);
        return new TagDTO(savedTag);
    }

    @Override
    public TagDTO updateTag(Long id, TagDTO tagDTO) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在: " + id));

        // 检查标签名称是否已被其他标签使用
        if (!tag.getName().equals(tagDTO.getName()) && tagRepository.existsByName(tagDTO.getName())) {
            throw new RuntimeException("标签名称已存在: " + tagDTO.getName());
        }

        tag.setName(tagDTO.getName());
        tag.setSlug(generateTagSlug(tagDTO.getName()));
        tag.setDescription(tagDTO.getDescription());
        tag.setColor(tagDTO.getColor());
        tag.setUpdatedAt(LocalDateTime.now());

        Tag savedTag = tagRepository.save(tag);
        return new TagDTO(savedTag);
    }

    @Override
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("标签不存在: " + id));
        
        // 硬删除标签
        tagRepository.delete(tag);
    }
    
    /**
     * 重新计算所有标签的文章数量（只统计已发布的文章）
     */
    @Transactional
    public void recalculateAllTagArticleCounts() {
        List<Tag> allTags = tagRepository.findAll();
        
        for (Tag tag : allTags) {
            // 统计该标签下已发布文章的数量
            long publishedArticleCount = articleTagRepository.countByTagAndArticleStatus(
                tag, Article.Status.PUBLISHED);
            
            tag.setArticleCount((int) publishedArticleCount);
            tagRepository.save(tag);
        }
    }

    @Override
    public void deleteTags(List<Long> ids) {
        tagRepository.deleteAllById(ids);
    }

    @Override
    public List<TagDTO> getPopularTags(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Tag> tags = tagRepository.findPopularTags(pageable);
        return tags.stream()
                .map(TagDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public long countTags() {
        return tagRepository.countActiveTags();
    }

    /**
     * 生成标签slug
     */
    private String generateTagSlug(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "tag";
        }
        
        // 生成基础slug
        String baseSlug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\u4e00-\\u9fa5]+", "-") // 支持中文
                .replaceAll("^-+|-+$", "") // 移除开头和结尾的连字符
                .replaceAll("-+", "-"); // 合并多个连字符
        
        if (baseSlug.isEmpty()) {
            baseSlug = "tag";
        }
        
        // 检查标签slug是否已存在，如果存在则添加数字后缀
        String slug = baseSlug;
        int counter = 1;
        while (tagRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }
}