package com.blog.repository;

import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    Optional<Article> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.publishedAt DESC")
    Page<Article> findPublishedArticles(Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND a.isTop = true ORDER BY a.publishedAt DESC")
    List<Article> findTopArticles();
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND a.isFeatured = true ORDER BY a.publishedAt DESC")
    Page<Article> findFeaturedArticles(Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.author = :author")
    Page<Article> findByAuthor(@Param("author") User author, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.category = :category AND a.status = 'PUBLISHED'")
    Page<Article> findByCategory(@Param("category") Category category, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> searchArticles(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND " +
           "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Article> searchArticlesByTitle(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND " +
           "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Article> searchArticlesByContent(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND " +
           "a.category.slug = :categorySlug AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> searchArticlesByCategory(@Param("keyword") String keyword, @Param("categorySlug") String categorySlug, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND " +
           "a.publishedAt >= :startDate AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> searchArticlesByTimeRange(@Param("keyword") String keyword, @Param("startDate") LocalDateTime startDate, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' AND " +
           "a.category.slug = :categorySlug AND a.publishedAt >= :startDate AND " +
           "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Article> searchArticlesByCategoryAndTimeRange(@Param("keyword") String keyword, @Param("categorySlug") String categorySlug, @Param("startDate") LocalDateTime startDate, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' " +
           "ORDER BY a.viewCount DESC")
    Page<Article> findMostViewedArticles(Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' " +
           "ORDER BY a.likeCount DESC")
    Page<Article> findMostLikedArticles(Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' " +
           "AND a.publishedAt >= :startDate ORDER BY a.publishedAt DESC")
    Page<Article> findRecentArticles(@Param("startDate") LocalDateTime startDate, Pageable pageable);
    
    @Modifying
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    void incrementViewCount(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE Article a SET a.likeCount = a.likeCount + 1 WHERE a.id = :id")
    void incrementLikeCount(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE Article a SET a.likeCount = a.likeCount - 1 WHERE a.id = :id AND a.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE Article a SET a.commentCount = a.commentCount + 1 WHERE a.id = :id")
    void incrementCommentCount(@Param("id") Long id);
    
    @Modifying
    @Query("UPDATE Article a SET a.commentCount = a.commentCount - 1 WHERE a.id = :id AND a.commentCount > 0")
    void decrementCommentCount(@Param("id") Long id);
    
    @Query("SELECT COUNT(a) FROM Article a")
    long countAllArticles();
    
    @Query("SELECT COUNT(a) FROM Article a WHERE a.status = 'PUBLISHED'")
    long countPublishedArticles();
    
    @Query("SELECT COUNT(a) FROM Article a WHERE a.author = :author")
    long countByAuthor(@Param("author") User author);
    
    @Query("SELECT COALESCE(SUM(a.viewCount), 0) FROM Article a WHERE a.status = 'PUBLISHED'")
    long getTotalViewCount();
    
    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.publishedAt DESC")
    List<Article> findRecentArticlesLimit(Pageable pageable);
    

    
    // ========== 管理员专用查询方法 ==========
    
    /**
     * 获取所有文章（包括未发布的）
     */
    @Query("SELECT a FROM Article a ORDER BY a.createdAt DESC")
    Page<Article> findAllArticles(Pageable pageable);
    
    /**
     * 根据状态查询文章
     */
    @Query("SELECT a FROM Article a WHERE CAST(a.status AS string) = :status")
    Page<Article> findByStatus(@Param("status") String status, Pageable pageable);
    
    /**
     * 根据分类名称查询文章
     */
    @Query("SELECT a FROM Article a WHERE a.category.name = :categoryName")
    Page<Article> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);
    
    /**
     * 管理员搜索文章（支持状态和分类筛选）
     */
    @Query("SELECT a FROM Article a WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR CAST(a.status AS string) = :status) AND " +
           "(:category IS NULL OR :category = '' OR a.category.name = :category) " +
           "ORDER BY a.createdAt DESC")
    Page<Article> searchArticlesForAdmin(@Param("keyword") String keyword, 
                                       @Param("status") String status, 
                                       @Param("category") String category, 
                                       Pageable pageable);
    
    /**
     * 批量更新文章状态
     */
    @Modifying
    @Query("UPDATE Article a SET a.status = :status WHERE a.id IN :ids")
    void batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);
    
    /**
     * 更新文章置顶状态
     */
    @Modifying
    @Query("UPDATE Article a SET a.isTop = :isTop WHERE a.id = :id")
    void updateTopStatus(@Param("id") Long id, @Param("isTop") boolean isTop);
    
    /**
     * 更新文章推荐状态
     */
    @Modifying
    @Query("UPDATE Article a SET a.isFeatured = :isFeatured WHERE a.id = :id")
    void updateFeaturedStatus(@Param("id") Long id, @Param("isFeatured") boolean isFeatured);
    
    // ========== 图表数据查询方法 ==========
    
    /**
     * 按日期统计文章发布数量（最近30天）
     */
    @Query(value = "SELECT DATE(published_at) as date, COUNT(*) as count " +
           "FROM article " +
           "WHERE status = 'PUBLISHED' " +
           "AND published_at >= :startDate " +
           "GROUP BY DATE(published_at) " +
           "ORDER BY DATE(published_at)", nativeQuery = true)
    List<Object[]> getArticleCountByDate(@Param("startDate") LocalDateTime startDate);
    
    /**
     * 按分类统计文章数量
     */
    @Query(value = "SELECT c.name as categoryName, COUNT(*) as count " +
           "FROM article a JOIN category c ON a.category_id = c.id " +
           "WHERE a.status = 'PUBLISHED' " +
           "GROUP BY c.id, c.name " +
           "ORDER BY COUNT(*) DESC", nativeQuery = true)
    List<Object[]> getArticleCountByCategory();
    
    /**
     * 按日期统计文章访问量（最近30天）
     */
    @Query(value = "SELECT DATE(published_at) as date, COALESCE(SUM(view_count), 0) as totalViews " +
           "FROM article " +
           "WHERE status = 'PUBLISHED' " +
           "AND published_at >= :startDate " +
           "GROUP BY DATE(published_at) " +
           "ORDER BY DATE(published_at)", nativeQuery = true)
    List<Object[]> getViewCountByDate(@Param("startDate") LocalDateTime startDate);
    
    /**
     * 获取热门文章（按访问量排序）
     */
    @Query(value = "SELECT title, view_count, published_at " +
           "FROM article " +
           "WHERE status = 'PUBLISHED' " +
           "ORDER BY view_count DESC", nativeQuery = true)
    List<Object[]> getPopularArticlesData(Pageable pageable);
}