package com.blog.repository;

import com.blog.entity.UserAnnouncementRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnnouncementReadRepository extends JpaRepository<UserAnnouncementRead, Long> {
    
    /**
     * 查找用户对特定公告的已读记录
     */
    Optional<UserAnnouncementRead> findByUserIdAndAnnouncementId(Long userId, Long announcementId);
    
    /**
     * 查找用户已读的所有公告ID
     */
    @Query("SELECT uar.announcementId FROM UserAnnouncementRead uar WHERE uar.userId = :userId")
    List<Long> findReadAnnouncementIdsByUserId(@Param("userId") Long userId);
    
    /**
     * 查找用户的所有已读记录
     */
    List<UserAnnouncementRead> findByUserIdOrderByReadAtDesc(Long userId);
    
    /**
     * 查找特定公告的所有已读记录
     */
    List<UserAnnouncementRead> findByAnnouncementIdOrderByReadAtDesc(Long announcementId);
    
    /**
     * 统计用户已读公告数量
     */
    Long countByUserId(Long userId);
    
    /**
     * 统计公告的已读用户数量
     */
    Long countByAnnouncementId(Long announcementId);
    
    /**
     * 检查用户是否已读特定公告
     */
    boolean existsByUserIdAndAnnouncementId(Long userId, Long announcementId);
    
    /**
     * 删除用户的所有已读记录
     */
    void deleteByUserId(Long userId);
    
    /**
     * 删除特定公告的所有已读记录
     */
    void deleteByAnnouncementId(Long announcementId);
}