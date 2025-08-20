package com.blog.repository;

import com.blog.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    /**
     * 查找所有未删除的公告
     */
    List<Announcement> findByDeletedFalseOrderByPinnedDescCreatedAtDesc();
    
    /**
     * 分页查询未删除的公告
     */
    Page<Announcement> findByDeletedFalse(Pageable pageable);
    
    /**
     * 查找启用的公告
     */
    @Query("SELECT a FROM Announcement a WHERE a.deleted = false AND a.enabled = true " +
           "AND (a.startTime IS NULL OR a.startTime <= :now) " +
           "AND (a.endTime IS NULL OR a.endTime >= :now) " +
           "ORDER BY a.pinned DESC, a.priority DESC, a.createdAt DESC")
    List<Announcement> findActiveAnnouncements(@Param("now") LocalDateTime now);
    
    /**
     * 查找用户未读的公告
     */
    @Query("SELECT a FROM Announcement a WHERE a.deleted = false AND a.enabled = true " +
           "AND (a.startTime IS NULL OR a.startTime <= :now) " +
           "AND (a.endTime IS NULL OR a.endTime >= :now) " +
           "AND a.id NOT IN (SELECT uar.announcementId FROM UserAnnouncementRead uar WHERE uar.userId = :userId) " +
           "ORDER BY a.pinned DESC, a.priority DESC, a.createdAt DESC")
    List<Announcement> findUnreadAnnouncementsForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    /**
     * 根据类型查找公告
     */
    List<Announcement> findByDeletedFalseAndTypeOrderByCreatedAtDesc(String type);
    
    /**
     * 根据类型查找公告（简化版本）
     */
    List<Announcement> findByTypeAndDeletedFalse(String type);
    
    /**
     * 根据发布者查找公告
     */
    Page<Announcement> findByDeletedFalseAndPublisherId(Long publisherId, Pageable pageable);
    
    /**
     * 搜索公告
     */
    @Query("SELECT a FROM Announcement a WHERE a.deleted = false " +
           "AND (a.title LIKE %:keyword% OR a.content LIKE %:keyword%) " +
           "ORDER BY a.createdAt DESC")
    Page<Announcement> searchAnnouncements(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 统计启用的公告数量
     */
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.deleted = false AND a.enabled = true " +
           "AND (a.startTime IS NULL OR a.startTime <= :now) " +
           "AND (a.endTime IS NULL OR a.endTime >= :now)")
    Long countActiveAnnouncements(@Param("now") LocalDateTime now);
    
    /**
     * 统计用户未读公告数量
     */
    @Query("SELECT COUNT(a) FROM Announcement a WHERE a.deleted = false AND a.enabled = true " +
           "AND (a.startTime IS NULL OR a.startTime <= :now) " +
           "AND (a.endTime IS NULL OR a.endTime >= :now) " +
           "AND a.id NOT IN (SELECT uar.announcementId FROM UserAnnouncementRead uar WHERE uar.userId = :userId)")
    Long countUnreadAnnouncementsForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}