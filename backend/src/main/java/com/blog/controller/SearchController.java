package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.TagDTO;
import com.blog.dto.CategoryDTO;
import com.blog.dto.ArticleDTO;
import com.blog.service.TagService;
import com.blog.service.CategoryService;
import com.blog.service.ArticleService;
import com.blog.service.TextAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private TagService tagService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ArticleService articleService;
    
    @Autowired
    private TextAnalysisService textAnalysisService;

    /**
     * 获取热门搜索关键词
     */
    @GetMapping("/hot-keywords")
    public Result<List<String>> getHotKeywords() {
        try {
            List<String> hotKeywords = new ArrayList<>();
            
            // 1. 获取热门标签作为关键词
            List<TagDTO> popularTags = tagService.getPopularTags(8);
            hotKeywords.addAll(popularTags.stream()
                .map(TagDTO::getName)
                .collect(Collectors.toList()));
            
            // 2. 获取分类名称作为关键词
            List<CategoryDTO> categories = categoryService.getAllCategories();
            hotKeywords.addAll(categories.stream()
                .map(CategoryDTO::getName)
                .collect(Collectors.toList()));
            
            // 3. 从最近文章中提取关键词
            List<ArticleDTO> recentArticles = articleService.getRecentArticles(20);
            for (ArticleDTO article : recentArticles) {
                if (StringUtils.hasText(article.getContent())) {
                    List<String> extractedKeywords = textAnalysisService.extractKeywords(
                        article.getTitle() + " " + article.getSummary(), 3);
                    hotKeywords.addAll(extractedKeywords);
                }
            }
            
            // 去重并限制数量
            return Result.success(hotKeywords.stream()
                .distinct()
                .filter(StringUtils::hasText)
                .limit(10)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            // 如果出现异常，返回备用关键词
            List<String> fallbackKeywords = Arrays.asList(
                "Vue", "JavaScript", "Spring Boot", "React", "Node.js", 
                "Python", "Java", "前端开发", "后端开发", "数据库"
            );
            return Result.success(fallbackKeywords);
        }
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    public Result<List<String>> getSearchSuggestions(@RequestParam String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Result.success(new ArrayList<>());
        }
        
        try {
            Set<String> suggestions = new LinkedHashSet<>();
            String lowerKeyword = keyword.toLowerCase();
            
            // 1. 从标签中匹配
            Pageable tagPageable = PageRequest.of(0, 5);
            Page<TagDTO> matchingTags = tagService.searchTags(keyword, tagPageable);
            suggestions.addAll(matchingTags.getContent().stream()
                .map(TagDTO::getName)
                .collect(Collectors.toList()));
            
            // 2. 从分类中匹配
            List<CategoryDTO> categories = categoryService.getAllCategories();
            suggestions.addAll(categories.stream()
                .filter(cat -> cat.getName().toLowerCase().contains(lowerKeyword))
                .map(CategoryDTO::getName)
                .collect(Collectors.toList()));
            
            // 3. 从文章标题中匹配
            Pageable articlePageable = PageRequest.of(0, 3);
            Page<ArticleDTO> matchingArticles = articleService.searchArticles(keyword, "title", null, null, articlePageable);
            suggestions.addAll(matchingArticles.getContent().stream()
                .map(ArticleDTO::getTitle)
                .collect(Collectors.toList()));
            
            // 4. 添加常见搜索模式
            if (suggestions.size() < 8) {
                suggestions.add(keyword + " 教程");
                suggestions.add(keyword + " 实战");
                suggestions.add(keyword + " 入门");
            }
            
            return Result.success(suggestions.stream()
                .limit(10)
                .collect(Collectors.toList()));
        } catch (Exception e) {
            // 如果出现异常，返回简单建议
            List<String> fallbackSuggestions = Arrays.asList(
                keyword + " 教程",
                keyword + " 实战",
                keyword + " 入门",
                keyword + " 进阶",
                keyword + " 最佳实践"
            );
            return Result.success(fallbackSuggestions);
        }
    }
}