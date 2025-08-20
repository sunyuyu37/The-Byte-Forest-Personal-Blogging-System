package com.blog.repository;

import com.blog.entity.Article;
import com.blog.entity.ArticleTag;
import com.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleTagRepository extends JpaRepository<ArticleTag, Long> {
    
    List<ArticleTag> findByArticle(Article article);
    
    List<ArticleTag> findByTag(Tag tag);
    
    boolean existsByArticleAndTag(Article article, Tag tag);
    
    void deleteByArticleAndTag(Article article, Tag tag);
    
    void deleteByArticle(Article article);
    
    @Query("SELECT at.tag FROM ArticleTag at WHERE at.article = :article")
    List<Tag> findTagsByArticle(@Param("article") Article article);
    
    @Query("SELECT at.article FROM ArticleTag at WHERE at.tag = :tag")
    List<Article> findArticlesByTag(@Param("tag") Tag tag);
    
    @Query("SELECT COUNT(at) FROM ArticleTag at WHERE at.tag = :tag AND at.article.status = :status")
    long countByTagAndArticleStatus(@Param("tag") Tag tag, @Param("status") Article.Status status);
}