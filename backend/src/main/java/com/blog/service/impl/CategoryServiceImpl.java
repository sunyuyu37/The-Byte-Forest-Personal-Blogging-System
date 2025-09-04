package com.blog.service.impl;

import com.blog.dto.CategoryDTO;
import com.blog.dto.cache.CategoryCacheDTO;
import com.blog.entity.Category;
import com.blog.repository.CategoryRepository;
import com.blog.service.CategoryService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    // @Cacheable(value = "categories", key = "'all'")
    public List<CategoryDTO> getAllCategories() {
        String cacheKey = "categories:all";
        
        // 尝试从缓存获取
        try {
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            if (cachedData != null) {
                @SuppressWarnings("unchecked")
                List<CategoryCacheDTO> cachedCategories = (List<CategoryCacheDTO>) cachedData;
                return cachedCategories.stream()
                        .map(this::convertCacheDTOToCategoryDTO)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // 缓存读取失败，继续从数据库查询
            System.err.println("缓存读取失败: " + e.getMessage());
        }
        
        // 缓存未命中或读取失败，从数据库查询
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 存入缓存
        try {
            List<CategoryCacheDTO> cacheDTOs = categories.stream()
                    .map(CategoryCacheDTO::new)
                    .collect(Collectors.toList());
            redisTemplate.opsForValue().set(cacheKey, cacheDTOs, 60, TimeUnit.MINUTES);
        } catch (Exception e) {
            // 缓存存储失败，记录日志但不影响正常返回
            System.err.println("缓存存储失败: " + e.getMessage());
        }
        
        return categoryDTOs;
    }

    @Override
    // @Cacheable(value = "categories", key = "'slug:' + #slug")
    public CategoryDTO getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("分类不存在: " + slug));
        return convertToDTO(category);
    }

    @Override
    public Page<CategoryDTO> getCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(this::convertToDTO);
    }

    @Override
    // @Cacheable(value = "categories", key = "#id")
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在: " + id));
        return convertToDTO(category);
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // 清除分类缓存
        clearCategoryCache();
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setSlug(categoryDTO.getSlug());
        category.setDescription(categoryDTO.getDescription());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        // 清除分类缓存
        clearCategoryCache();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在: " + id));
        
        category.setName(categoryDTO.getName());
        category.setSlug(categoryDTO.getSlug());
        category.setDescription(categoryDTO.getDescription());
        category.setUpdatedAt(LocalDateTime.now());
        
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        // 清除分类缓存
        clearCategoryCache();
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("分类不存在: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setArticleCount(category.getArticleCount());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }
    
    /**
     * 将缓存DTO转换为业务DTO
     */
    private CategoryDTO convertCacheDTOToCategoryDTO(CategoryCacheDTO cacheDTO) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(cacheDTO.getId());
        categoryDTO.setName(cacheDTO.getName());
        categoryDTO.setSlug(cacheDTO.getSlug());
        categoryDTO.setDescription(cacheDTO.getDescription());
        categoryDTO.setCoverImage(cacheDTO.getCoverImage());
        categoryDTO.setSortOrder(cacheDTO.getSortOrder());
        categoryDTO.setArticleCount(cacheDTO.getArticleCount());
        categoryDTO.setStatus(cacheDTO.getStatus());
        categoryDTO.setCreatedAt(cacheDTO.getCreatedAt());
        categoryDTO.setUpdatedAt(cacheDTO.getUpdatedAt());
        return categoryDTO;
    }
    
    /**
     * 清除分类缓存
     */
    private void clearCategoryCache() {
        // 清除所有以 "categories:" 开头的缓存键
        Set<String> keys = redisTemplate.keys("categories:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}