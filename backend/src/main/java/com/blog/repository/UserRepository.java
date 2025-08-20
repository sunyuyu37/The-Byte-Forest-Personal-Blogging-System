package com.blog.repository;

import com.blog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u")
    Page<User> findAllActive(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.role = :role")
    Page<User> findByRole(@Param("role") User.Role role, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "(u.username LIKE %:keyword% OR u.nickname LIKE %:keyword% OR u.email LIKE %:keyword%)")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM User u")
    long countActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = 'ADMIN'")
    long countAdminUsers();
    
    // ========== 图表数据查询方法 ==========
    
    /**
     * 按日期统计用户注册数量（最近30天）
     */
    @Query("SELECT DATE(u.createdAt) as date, COUNT(u) as count " +
           "FROM User u " +
           "WHERE u.createdAt >= :startDate " +
           "GROUP BY DATE(u.createdAt) " +
           "ORDER BY DATE(u.createdAt)")
    List<Object[]> getUserRegistrationByDate(@Param("startDate") LocalDateTime startDate);
}