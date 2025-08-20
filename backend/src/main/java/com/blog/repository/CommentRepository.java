package com.blog.repository;

import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    @Query("SELECT c FROM Comment c WHERE c.article = :article AND c.parent IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findByArticleAndParentIsNull(@Param("article") Article article, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.parent = :parent ORDER BY c.createdAt ASC")
    List<Comment> findByParent(@Param("parent") Comment parent);
    
    @Query("SELECT c FROM Comment c WHERE c.user = :user ORDER BY c.createdAt DESC")
    Page<Comment> findByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.status = :status ORDER BY c.createdAt DESC")
    Page<Comment> findByStatus(@Param("status") Comment.Status status, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.content LIKE %:keyword% ORDER BY c.createdAt DESC")
    Page<Comment> searchComments(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT c FROM Comment c " +
           "WHERE (:content IS NULL OR :content = '' OR c.content LIKE %:content%) " +
           "AND (:userNickname IS NULL OR :userNickname = '' OR " +
           "     EXISTS (SELECT 1 FROM User u WHERE u.id = c.user.id AND u.nickname LIKE %:userNickname%)) " +
           "AND (:articleTitle IS NULL OR :articleTitle = '' OR " +
           "     EXISTS (SELECT 1 FROM Article a WHERE a.id = c.article.id AND a.title LIKE %:articleTitle%)) " +
           "AND (:statusEnum IS NULL OR c.status = :statusEnum) " +
           "ORDER BY c.createdAt DESC")
    Page<Comment> searchCommentsForAdmin(@Param("content") String content,
                                        @Param("userNickname") String userNickname,
                                        @Param("articleTitle") String articleTitle,
                                        @Param("statusEnum") Comment.Status statusEnum,
                                        Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.article = :article")
    long countByArticle(@Param("article") Article article);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parent = :parent")
    long countByParent(@Param("parent") Comment parent);
    
    @Query("SELECT COUNT(c) FROM Comment c")
    long countAllComments();
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.status = 'APPROVED'")
    long countApprovedComments();
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.status = 'PENDING'")
    long countPendingComments();
    
    @Query("SELECT c FROM Comment c WHERE c.status = 'APPROVED' ORDER BY c.createdAt DESC")
    List<Comment> findRecentCommentsLimit(Pageable pageable);
    
    // ========== 图表数据查询方法 ==========
    
    /**
     * 按日期统计评论数量（最近30天）
     */
    @Query("SELECT DATE(c.createdAt) as date, COUNT(c) as count " +
           "FROM Comment c " +
           "WHERE c.status = 'APPROVED' " +
           "AND c.createdAt >= :startDate " +
           "GROUP BY DATE(c.createdAt) " +
           "ORDER BY DATE(c.createdAt)")
    List<Object[]> getCommentCountByDate(@Param("startDate") LocalDateTime startDate);
    
    /**
     * 获取评论内容用于词云分析
     */
    @Query(value = "SELECT c.content FROM comment c WHERE c.status = 'APPROVED' AND LENGTH(c.content) > 2 ORDER BY c.created_at DESC LIMIT 1000", nativeQuery = true)
    List<String> getCommentWordCloudData();
}