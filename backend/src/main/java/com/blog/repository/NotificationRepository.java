package com.blog.repository;

import com.blog.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 通知Repository接口
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * 根据用户ID获取通知列表（分页）
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和类型获取通知列表（分页）
     */
    Page<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, String type, Pageable pageable);
    
    /**
     * 根据用户ID获取未读通知列表（分页）
     */
    Page<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和关键词搜索通知
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND (n.title LIKE %:keyword% OR n.content LIKE %:keyword%) ORDER BY n.createdAt DESC")
    Page<Notification> findByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 统计用户未读通知数量
     */
    long countByUserIdAndIsReadFalse(Long userId);
    
    /**
     * 获取用户最新的几条通知
     */
    List<Notification> findTop5ByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 标记用户所有通知为已读
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.updatedAt = CURRENT_TIMESTAMP WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsReadByUserId(@Param("userId") Long userId);
    
    /**
     * 删除用户所有通知
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
    
    /**
     * 根据相关实体删除通知
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.relatedType = :relatedType AND n.relatedId = :relatedId")
    int deleteByRelatedTypeAndRelatedId(@Param("relatedType") String relatedType, @Param("relatedId") Long relatedId);
}