package com.blog.service;

import com.blog.dto.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    
    /**
     * 获取所有分类
     */
    List<CategoryDTO> getAllCategories();
    
    /**
     * 根据slug获取分类
     */
    CategoryDTO getCategoryBySlug(String slug);
    
    /**
     * 分页获取分类
     */
    Page<CategoryDTO> getCategories(Pageable pageable);
    
    /**
     * 根据ID获取分类
     */
    CategoryDTO getCategoryById(Long id);
    
    /**
     * 创建分类
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    
    /**
     * 更新分类
     */
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    
    /**
     * 删除分类
     */
    void deleteCategory(Long id);
}