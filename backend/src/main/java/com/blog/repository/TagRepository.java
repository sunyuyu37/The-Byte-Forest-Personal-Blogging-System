package com.blog.repository;

import com.blog.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    Optional<Tag> findByName(String name);
    
    Optional<Tag> findBySlug(String slug);
    
    boolean existsByName(String name);
    
    boolean existsBySlug(String slug);
    
    @Query("SELECT t FROM Tag t ORDER BY t.articleCount DESC, t.name ASC")
    List<Tag> findAllActive();
    
    @Query("SELECT t FROM Tag t")
    Page<Tag> findAllTags(Pageable pageable);
    
    @Query("SELECT t FROM Tag t WHERE t.name LIKE %:keyword%")
    Page<Tag> searchTags(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT t FROM Tag t ORDER BY t.articleCount DESC")
    List<Tag> findPopularTags(Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM Tag t")
    long countActiveTags();
}