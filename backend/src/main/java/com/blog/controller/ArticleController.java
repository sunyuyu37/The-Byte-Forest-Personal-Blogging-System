package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.ArticleDTO;
import com.blog.dto.CategoryDTO;
import com.blog.dto.CommentDTO;
import com.blog.entity.Category;
import com.blog.entity.User;
import com.blog.service.ArticleService;
import com.blog.service.CategoryService;
import com.blog.service.CommentService;
import com.blog.service.UserService;
import com.blog.service.TagService;
import com.blog.repository.ArticleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.blog.entity.Article;
import com.blog.annotation.OperationLog;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private ArticleRepository articleRepository;

    /**
     * 创建文章
     */
    @PostMapping
    @OperationLog(module = "文章管理", type = com.blog.entity.OperationLog.OperationType.CREATE, description = "创建文章")
    public Result<ArticleDTO> createArticle(@Valid @RequestBody ArticleDTO articleDTO, Authentication authentication) {
        try {
            // 获取当前用户
            String username = authentication.getName();
            User user = userService.findByUsernameOrEmail(username);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 创建文章
            ArticleDTO createdArticle = articleService.createArticle(articleDTO, user.getId());
            return Result.success(createdArticle);
        } catch (Exception e) {
            return Result.error("创建文章失败：" + e.getMessage());
        }
    }

    /**
     * 更新文章
     */
    @PutMapping("/{id}")
    @OperationLog(module = "文章管理", type = com.blog.entity.OperationLog.OperationType.UPDATE, description = "更新文章")
    public Result<ArticleDTO> updateArticle(@PathVariable Long id, @Valid @RequestBody ArticleDTO articleDTO, Authentication authentication) {
        try {
            // 获取当前用户
            String username = authentication.getName();
            User user = userService.findByUsernameOrEmail(username);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 检查文章是否存在
            Optional<ArticleDTO> existingArticle = articleService.findById(id);
            if (existingArticle.isEmpty()) {
                return Result.error("文章不存在");
            }
            
            // 检查权限（只有作者可以修改）
            if (!existingArticle.get().getAuthorId().equals(user.getId())) {
                return Result.error("无权限修改此文章");
            }
            
            // 更新文章
            ArticleDTO updatedArticle = articleService.updateArticle(id, articleDTO, user.getId());
            return Result.success(updatedArticle);
        } catch (Exception e) {
            return Result.error("更新文章失败：" + e.getMessage());
        }
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    @OperationLog(module = "文章管理", type = com.blog.entity.OperationLog.OperationType.DELETE, description = "删除文章")
    public Result<Void> deleteArticle(@PathVariable Long id, Authentication authentication) {
        try {
            // 获取当前用户
            String username = authentication.getName();
            User user = userService.findByUsernameOrEmail(username);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            // 检查文章是否存在
            Optional<ArticleDTO> existingArticle = articleService.findById(id);
            if (existingArticle.isEmpty()) {
                return Result.error("文章不存在");
            }
            
            // 检查权限（只有作者可以删除）
            if (!existingArticle.get().getAuthorId().equals(user.getId())) {
                return Result.error("无权限删除此文章");
            }
            
            // 删除文章
            articleService.deleteArticle(id);
            return Result.success(null);
        } catch (Exception e) {
            return Result.error("删除文章失败：" + e.getMessage());
        }
    }

    /**
     * 获取已发布的文章列表
     */
    @GetMapping("/published")
    public Result<Page<ArticleDTO>> getPublishedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publishedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ArticleDTO> articles = articleService.findPublishedArticles(pageable);
        return Result.success(articles);
    }

    /**
     * 根据分类获取文章
     */
    @GetMapping("/category/{categorySlug}")
    public Result<Page<ArticleDTO>> getArticlesByCategory(
            @PathVariable String categorySlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        CategoryDTO category = categoryService.getCategoryBySlug(categorySlug);
        if (category == null) {
            return Result.error("分类不存在");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleDTO> articles = articleService.getArticlesByCategory(category.getId(), pageable);
        return Result.success(articles);
    }

    /**
     * 获取置顶文章
     */
    @GetMapping("/top")
    public Result<List<ArticleDTO>> getTopArticles() {
        List<ArticleDTO> articles = articleService.getTopArticles();
        return Result.success(articles);
    }

    /**
     * 获取推荐文章
     */
    @GetMapping("/featured")
    public Result<Page<ArticleDTO>> getFeaturedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleDTO> articles = articleService.getFeaturedArticles(pageable);
        return Result.success(articles);
    }

    /**
     * 获取最受欢迎的文章
     */
    @GetMapping("/popular")
    public Result<Page<ArticleDTO>> getMostLikedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleDTO> articles = articleService.getMostLikedArticles(pageable);
        return Result.success(articles);
    }

    /**
     * 根据ID获取文章详情
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
     * 根据slug获取文章详情
     */
    @GetMapping("/slug/{slug}")
    public Result<ArticleDTO> getArticleBySlug(@PathVariable String slug) {
        Optional<ArticleDTO> article = articleService.findBySlug(slug);
        if (article.isEmpty()) {
            return Result.error("文章不存在");
        }
        return Result.success(article.get());
    }

    /**
     * 搜索文章
     */
    @GetMapping("/search")
    public Result<Page<ArticleDTO>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "all") String searchType,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "relevance") String sortBy,
            @RequestParam(required = false) String timeRange) {
        
        // 根据排序方式设置排序
        Sort sort;
        switch (sortBy) {
            case "latest":
                sort = Sort.by("publishedAt").descending();
                break;
            case "views":
                sort = Sort.by("viewCount").descending();
                break;
            case "likes":
                sort = Sort.by("likeCount").descending();
                break;
            default: // relevance
                sort = Sort.by("publishedAt").descending();
                break;
        }
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ArticleDTO> articles = articleService.searchArticles(keyword, searchType, category, timeRange, pageable);
        return Result.success(articles);
    }

    /**
     * 根据标签获取文章
     */
    @GetMapping("/tag/{tagSlug}")
    public Result<Page<ArticleDTO>> getArticlesByTag(
            @PathVariable String tagSlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // 获取标签
        com.blog.dto.TagDTO tag = tagService.getTagBySlug(tagSlug);
        if (tag == null) {
            return Result.error("标签不存在");
        }
        
        // 根据标签查询文章
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleDTO> articles = articleService.getArticlesByTag(tag.getId(), pageable);
        return Result.success(articles);
    }

    /**
     * 根据文章ID获取评论
     */
    @GetMapping("/{id}/comments")
    public Result<Page<CommentDTO>> getArticleComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 获取文章
            Optional<ArticleDTO> articleDTO = articleService.findById(id);
            if (articleDTO.isEmpty()) {
                return Result.error("文章不存在");
            }
            
            // 查找文章实体（CommentService需要Article实体）
            Optional<Article> articleOpt = articleRepository.findById(id);
            if (articleOpt.isEmpty()) {
                return Result.error("文章不存在");
            }
            
            // 获取评论
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<CommentDTO> comments = commentService.findCommentsByArticle(articleOpt.get(), pageable);
            
            return Result.success(comments);
        } catch (Exception e) {
            return Result.error("获取文章评论失败: " + e.getMessage());
        }
    }

    /**
     * 根据文章slug获取评论
     */
    @GetMapping("/slug/{slug}/comments")
    public Result<Page<CommentDTO>> getArticleCommentsBySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // 获取文章
            Optional<ArticleDTO> articleDTO = articleService.findBySlug(slug);
            if (articleDTO.isEmpty()) {
                return Result.error("文章不存在");
            }
            
            // 查找文章实体（CommentService需要Article实体）
            Optional<Article> articleOpt = articleRepository.findBySlug(slug);
            if (articleOpt.isEmpty()) {
                return Result.error("文章不存在");
            }
            
            // 获取评论
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<CommentDTO> comments = commentService.findCommentsByArticle(articleOpt.get(), pageable);
            
            return Result.success(comments);
        } catch (Exception e) {
            return Result.error("获取文章评论失败: " + e.getMessage());
        }
    }

    /**
     * 点赞文章
     */
    @PostMapping("/{id}/like")
    public Result<String> likeArticle(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }
            articleService.likeArticle(id, userId);
            return Result.success("点赞成功");
        } catch (Exception e) {
            return Result.error("点赞失败: " + e.getMessage());
        }
    }

    /**
     * 取消点赞文章
     */
    @DeleteMapping("/{id}/like")
    public Result<String> unlikeArticle(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }
            articleService.unlikeArticle(id, userId);
            return Result.success("已取消点赞");
        } catch (Exception e) {
            return Result.error("取消点赞失败: " + e.getMessage());
        }
    }

    /**
     * 检查当前用户是否已点赞该文章
     */
    @GetMapping("/{id}/liked")
    public Result<Boolean> isArticleLiked(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.success(false);
            }
            boolean liked = articleService.isArticleLikedByUser(id, userId);
            return Result.success(liked);
        } catch (Exception e) {
            return Result.error("查询点赞状态失败: " + e.getMessage());
        }
    }

    // ========== 工具方法 ==========
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() != null &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            return userService.getUserIdByUsername(username);
        }
        return null;
    }
}