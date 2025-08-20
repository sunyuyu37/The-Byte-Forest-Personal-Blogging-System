package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.CategoryDTO;
import com.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类
     */
    @GetMapping
    public Result<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 根据slug获取分类
     */
    @GetMapping("/{slug}")
    public Result<CategoryDTO> getCategoryBySlug(@PathVariable String slug) {
        CategoryDTO category = categoryService.getCategoryBySlug(slug);
        return Result.success(category);
    }
}