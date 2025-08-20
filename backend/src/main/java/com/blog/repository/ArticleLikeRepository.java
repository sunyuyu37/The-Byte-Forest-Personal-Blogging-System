package com.blog.repository;

import com.blog.entity.Article;
import com.blog.entity.ArticleLike;
import com.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    
    boolean existsByArticleAndUser(Article article, User user);
    
    void deleteByArticleAndUser(Article article, User user);
    
    @Query("SELECT COUNT(al) FROM ArticleLike al WHERE al.article = :article")
    long countByArticle(@Param("article") Article article);
    
    @Query("SELECT COUNT(al) FROM ArticleLike al WHERE al.user = :user")
    long countByUser(@Param("user") User user);

    // 新增：根据文章和用户查找点赞记录
    Optional<ArticleLike> findByArticleAndUser(Article article, User user);
}