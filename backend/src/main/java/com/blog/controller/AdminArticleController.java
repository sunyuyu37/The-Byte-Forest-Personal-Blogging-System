package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.ArticleDTO;
import com.blog.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/articles")
public class AdminArticleController {

    private static final Logger log = LoggerFactory.getLogger(AdminArticleController.class);

    @Autowired
    private ArticleService articleService;

    /**
     * 获取所有文章（管理员）
     */
    @GetMapping
    public Result<Page<ArticleDTO>> getAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // URL解码关键词
            if (keyword != null && !keyword.trim().isEmpty()) {
                keyword = java.net.URLDecoder.decode(keyword, "UTF-8");
                log.info("搜索关键词: {}", keyword);
            }
            
            // 转换status字符串为枚举类型
            String statusEnum = null;
            if (status != null && !status.trim().isEmpty()) {
                try {
                    // 验证状态值是否有效
                    if ("DRAFT".equalsIgnoreCase(status) || "PUBLISHED".equalsIgnoreCase(status) || "ARCHIVED".equalsIgnoreCase(status)) {
                        statusEnum = status.toUpperCase();
                        log.info("状态参数: {} -> {}", status, statusEnum);
                    } else {
                        log.warn("无效的状态参数: {}", status);
                    }
                } catch (Exception e) {
                    log.warn("状态参数处理异常: {}", status);
                }
            }
            
            Page<ArticleDTO> articles;
            
            // 如果有搜索条件，使用搜索方法
            if (keyword != null && !keyword.trim().isEmpty()) {
                articles = articleService.searchArticlesForAdmin(keyword, statusEnum, category, pageable);
            } else if (statusEnum != null) {
                articles = articleService.getArticlesByStatus(statusEnum, pageable);
            } else if (category != null && !category.trim().isEmpty()) {
                articles = articleService.getArticlesByCategoryName(category, pageable);
            } else {
                articles = articleService.getAllArticles(pageable);
            }
            
            return Result.success(articles);
        } catch (Exception e) {
            log.error("获取文章列表失败", e);
            return Result.error("获取文章列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取文章详情（管理员）
     */
    @GetMapping("/{id}")
    public Result<ArticleDTO> getArticleById(@PathVariable Long id) {
        Optional<ArticleDTO> article = articleService.findById(id);
        if (article.isEmpty()) {
            return Result.error("文章不存在");
        }
        return Result.success(article.get());
    }

    /**
     * 创建文章
     */
    @PostMapping
    public Result<ArticleDTO> createArticle(@Valid @RequestBody ArticleDTO articleDTO) {
        try {
            // TODO: 从JWT token中获取当前用户ID
            Long authorId = 1L; // 临时使用固定值
            ArticleDTO createdArticle = articleService.createArticle(articleDTO, authorId);
            return Result.success(createdArticle);
        } catch (Exception e) {
            return Result.error("创建文章失败：" + e.getMessage());
        }
    }

    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    public Result<ArticleDTO> updateArticle(@PathVariable Long id, @Valid @RequestBody ArticleDTO articleDTO) {
        try {
            // TODO: 从JWT token中获取当前用户ID
            Long authorId = 1L; // 临时使用固定值
            ArticleDTO updatedArticle = articleService.updateArticle(id, articleDTO, authorId);
            return Result.success(updatedArticle);
        } catch (Exception e) {
            return Result.error("更新文章失败：" + e.getMessage());
        }
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        try {
            // 验证文章ID
            if (id == null || id <= 0) {
                return Result.error("无效的文章ID");
            }
            
            // 检查文章是否存在
            Optional<ArticleDTO> existingArticle = articleService.findById(id);
            if (existingArticle.isEmpty()) {
                return Result.error("文章不存在");
            }
            
            // 执行软删除
            articleService.deleteArticle(id);
            log.info("文章删除成功，ID: {}", id);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除文章失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return Result.error("删除文章失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除文章
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteArticles(@RequestBody List<Long> ids) {
        try {
            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的文章");
            }
            
            // 验证ID有效性
            for (Long id : ids) {
                if (id == null || id <= 0) {
                    return Result.error("包含无效的文章ID");
                }
            }
            
            // 执行批量软删除
            articleService.batchDeleteArticles(ids);
            log.info("批量删除文章成功，数量: {}, IDs: {}", ids.size(), ids);
            return Result.success(null);
        } catch (Exception e) {
            log.error("批量删除文章失败，IDs: {}, 错误: {}", ids, e.getMessage(), e);
            return Result.error("批量删除文章失败：" + e.getMessage());
        }
    }

    /**
     * 发布文章
     */
    @PostMapping("/{id}/publish")
    public Result<ArticleDTO> publishArticle(@PathVariable Long id) {
        try {
            ArticleDTO article = articleService.publishArticle(id);
            return Result.success(article);
        } catch (Exception e) {
            return Result.error("发布文章失败：" + e.getMessage());
        }
    }

    /**
     * 下线文章
     */
    @PostMapping("/{id}/unpublish")
    public Result<ArticleDTO> unpublishArticle(@PathVariable Long id) {
        try {
            // 验证文章ID
            if (id == null || id <= 0) {
                return Result.error("无效的文章ID");
            }
            
            // 检查文章是否存在
            Optional<ArticleDTO> existingArticle = articleService.findById(id);
            if (existingArticle.isEmpty()) {
                return Result.error("文章不存在");
            }
            
            // 检查文章状态
            if ("DRAFT".equals(existingArticle.get().getStatus())) {
                return Result.error("文章已经是草稿状态");
            }
            
            // 执行下线操作
            ArticleDTO article = articleService.unpublishArticle(id);
            log.info("文章下线成功，ID: {}, 标题: {}", id, article.getTitle());
            return Result.success(article);
        } catch (Exception e) {
            log.error("下线文章失败，ID: {}, 错误: {}", id, e.getMessage(), e);
            return Result.error("下线文章失败：" + e.getMessage());
        }
    }

    /**
     * 批量发布文章
     */
    @PostMapping("/batch/publish")
    public Result<Void> batchPublishArticles(@RequestBody List<Long> ids) {
        try {
            articleService.batchPublishArticles(ids);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("批量发布文章失败：" + e.getMessage());
        }
    }

    /**
     * 批量下线文章
     */
    @PostMapping("/batch/unpublish")
    public Result<Void> batchUnpublishArticles(@RequestBody List<Long> ids) {
        try {
            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要下线的文章");
            }
            
            // 验证ID有效性
            for (Long id : ids) {
                if (id == null || id <= 0) {
                    return Result.error("包含无效的文章ID");
                }
            }
            
            // 执行批量下线
            articleService.batchUnpublishArticles(ids);
            log.info("批量下线文章成功，数量: {}, IDs: {}", ids.size(), ids);
            return Result.success(null);
        } catch (Exception e) {
            log.error("批量下线文章失败，IDs: {}, 错误: {}", ids, e.getMessage(), e);
            return Result.error("批量下线文章失败：" + e.getMessage());
        }
    }

    /**
     * 置顶文章
     */
    @PostMapping("/{id}/top")
    public Result<ArticleDTO> topArticle(@PathVariable Long id) {
        try {
            ArticleDTO article = articleService.topArticle(id);
            return Result.success(article);
        } catch (Exception e) {
            return Result.error("置顶文章失败：" + e.getMessage());
        }
    }

    /**
     * 取消置顶文章
     */
    @PostMapping("/{id}/untop")
    public Result<ArticleDTO> untopArticle(@PathVariable Long id) {
        try {
            ArticleDTO article = articleService.untopArticle(id);
            return Result.success(article);
        } catch (Exception e) {
            return Result.error("取消置顶失败：" + e.getMessage());
        }
    }

    /**
     * 推荐文章
     */
    @PostMapping("/{id}/feature")
    public Result<ArticleDTO> featureArticle(@PathVariable Long id) {
        try {
            ArticleDTO article = articleService.featureArticle(id);
            return Result.success(article);
        } catch (Exception e) {
            return Result.error("推荐文章失败：" + e.getMessage());
        }
    }

    /**
     * 取消推荐文章
     */
    @PostMapping("/{id}/unfeature")
    public Result<ArticleDTO> unfeatureArticle(@PathVariable Long id) {
        try {
            ArticleDTO article = articleService.unfeatureArticle(id);
            return Result.success(article);
        } catch (Exception e) {
            return Result.error("取消推荐失败：" + e.getMessage());
        }
    }
}