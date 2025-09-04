package com.blog.service;

import com.blog.dto.ArticleDTO;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.Tag;
import com.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ArticleService {
    
    /**
     * 创建文章
     */
    ArticleDTO createArticle(ArticleDTO articleDTO, Long authorId);
    
    /**
     * 更新文章
     */
    ArticleDTO updateArticle(Long id, ArticleDTO articleDTO, Long authorId);
    
    /**
     * 根据ID查找文章
     */
    Optional<ArticleDTO> findById(Long id);
    
    /**
     * 根据slug查找文章
     */
    Optional<ArticleDTO> findBySlug(String slug);
    
    /**
     * 分页查询已发布文章
     */
    Page<ArticleDTO> findPublishedArticles(Pageable pageable);
    
    /**
     * 分页查询所有文章（管理员）
     */
    Page<ArticleDTO> findAllArticles(Pageable pageable);
    
    /**
     * 根据作者查询文章
     */
    Page<ArticleDTO> findArticlesByAuthor(User author, Pageable pageable);
    
    /**
     * 根据分类查询文章
     */
    Page<ArticleDTO> findArticlesByCategory(Category category, Pageable pageable);
    
    /**
     * 根据分类ID查询文章
     */
    Page<ArticleDTO> getArticlesByCategory(Long categoryId, Pageable pageable);
    
    /**
     * 根据标签查询文章
     */
    Page<ArticleDTO> findArticlesByTag(Tag tag, Pageable pageable);
    
    /**
     * 根据标签ID查询文章
     */
    Page<ArticleDTO> getArticlesByTag(Long tagId, Pageable pageable);
    
    /**
     * 搜索文章
     */
    Page<ArticleDTO> searchArticles(String keyword, Pageable pageable);
    
    Page<ArticleDTO> searchArticles(String keyword, String searchType, String category, String timeRange, Pageable pageable);
    
    // ========== 管理员专用方法 ==========
    
    /**
     * 获取所有文章（管理员）
     */
    Page<ArticleDTO> getAllArticles(Pageable pageable);
    
    /**
     * 管理员搜索文章
     */
    Page<ArticleDTO> searchArticlesForAdmin(String keyword, String status, String category, Pageable pageable);
    
    /**
     * 根据状态获取文章
     */
    Page<ArticleDTO> getArticlesByStatus(String status, Pageable pageable);
    
    /**
     * 根据分类名称获取文章
     */
    Page<ArticleDTO> getArticlesByCategoryName(String categoryName, Pageable pageable);
    
    /**
     * 批量删除文章
     */
    void batchDeleteArticles(List<Long> ids);
    
    /**
     * 批量发布文章
     */
    void batchPublishArticles(List<Long> ids);
    
    /**
     * 批量下线文章
     */
    void batchUnpublishArticles(List<Long> ids);
    
    /**
     * 置顶文章
     */
    ArticleDTO topArticle(Long id);
    
    /**
     * 取消置顶文章
     */
    ArticleDTO untopArticle(Long id);
    
    /**
     * 推荐文章
     */
    ArticleDTO featureArticle(Long id);
    
    /**
     * 取消推荐文章
     */
    ArticleDTO unfeatureArticle(Long id);
    
    /**
     * 获取置顶文章
     */
    List<ArticleDTO> getTopArticles();
    
    /**
     * 获取推荐文章
     */
    Page<ArticleDTO> getFeaturedArticles(Pageable pageable);
    
    /**
     * 获取最受欢迎文章
     */
    Page<ArticleDTO> getMostViewedArticles(Pageable pageable);
    
    /**
     * 获取最多点赞文章
     */
    Page<ArticleDTO> getMostLikedArticles(Pageable pageable);
    
    /**
     * 获取最新文章
     */
    Page<ArticleDTO> getRecentArticles(Pageable pageable);
    
    /**
     * 发布文章
     */
    ArticleDTO publishArticle(Long id);
    
    /**
     * 取消发布文章
     */
    ArticleDTO unpublishArticle(Long id);
    
    /**
     * 设置文章置顶
     */
    void setTopArticle(Long id, boolean isTop);
    
    /**
     * 设置文章推荐
     */
    void setFeaturedArticle(Long id, boolean isFeatured);
    
    /**
     * 删除文章
     */
    void deleteArticle(Long id);
    
    /**
     * 增加文章浏览量
     */
    void incrementViewCount(Long id);
    
    /**
     * 点赞文章
     */
    void likeArticle(Long articleId, Long userId);
    
    /**
     * 取消点赞文章
     */
    void unlikeArticle(Long articleId, Long userId);
    
    /**
     * 检查用户是否已点赞文章
     */
    boolean isArticleLikedByUser(Long articleId, Long userId);
    
    /**
     * 为文章添加标签
     */
    void addTagsToArticle(Long articleId, List<Long> tagIds);
    
    /**
     * 移除文章标签
     */
    void removeTagsFromArticle(Long articleId, List<Long> tagIds);
    
    /**
     * 获取文章统计信息
     */
    long countAllArticles();
    
    long countPublishedArticles();
    
    long countArticlesByAuthor(User author);
    
    /**
     * 获取总阅读量
     */
    long getTotalViewCount();
    
    /**
     * 获取最新文章（限制数量）
     */
    List<ArticleDTO> getRecentArticles(int limit);
    
    /**
     * 生成文章slug
     */
    String generateSlug(String title);
    
    /**
     * 计算文章字数和阅读时间
     */
    void calculateWordCountAndReadingTime(Article article);
    
    // ========== 图表数据方法 ==========
    
    /**
     * 获取文章发布趋势数据（按日期）
     */
    List<Object[]> getArticleTrendData(String period);
    
    /**
     * 获取文章分类统计数据
     */
    List<Object[]> getArticleCategoryStats();
    
    /**
     * 获取文章访问量趋势数据
     */
    List<Object[]> getArticleViewTrendData(String period);
    
    /**
     * 获取热门文章数据
     */
    List<Object[]> getPopularArticlesData(int limit);
    
    /**
     * 获取访问统计数据
     */
    List<Object[]> getVisitStats(String period);
}