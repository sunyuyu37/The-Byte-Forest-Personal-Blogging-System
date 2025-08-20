package com.blog.repository;

import com.blog.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findBySlug(String slug);
    
    boolean existsByName(String name);
    
    boolean existsBySlug(String slug);
    
    @Query("SELECT c FROM Category c WHERE c.status = true ORDER BY c.sortOrder ASC, c.name ASC")
    List<Category> findActiveCategories();
    
    @Query("SELECT c FROM Category c ORDER BY c.sortOrder ASC, c.name ASC")
    Page<Category> findAllCategories(Pageable pageable);
    
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:keyword%")
    Page<Category> searchCategories(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Category c")
    long countActiveCategories();
}