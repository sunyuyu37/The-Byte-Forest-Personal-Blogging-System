package com.blog.service;

import com.blog.dto.AnnouncementDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 公告服务接口
 */
public interface AnnouncementService {
    
    /**
     * 创建公告
     */
    AnnouncementDTO createAnnouncement(AnnouncementDTO announcementDTO, Long publisherId);
    
    /**
     * 更新公告
     */
    AnnouncementDTO updateAnnouncement(Long id, AnnouncementDTO announcementDTO);
    
    /**
     * 删除公告
     */
    void deleteAnnouncement(Long id);
    
    /**
     * 根据ID获取公告
     */
    AnnouncementDTO getAnnouncementById(Long id);
    
    /**
     * 根据ID获取公告（包含用户已读状态）
     */
    AnnouncementDTO getAnnouncementById(Long id, Long userId);
    
    /**
     * 分页获取所有公告（管理员）
     */
    Page<AnnouncementDTO> getAllAnnouncements(Pageable pageable);
    
    /**
     * 获取活跃的公告列表
     */
    List<AnnouncementDTO> getActiveAnnouncements();
    
    /**
     * 获取用户未读的公告
     */
    List<AnnouncementDTO> getUnreadAnnouncementsForUser(Long userId);
    
    /**
     * 标记公告为已读
     */
    void markAnnouncementAsRead(Long userId, Long announcementId);
    
    /**
     * 批量标记公告为已读
     */
    void markAnnouncementsAsRead(Long userId, List<Long> announcementIds);
    
    /**
     * 搜索公告
     */
    Page<AnnouncementDTO> searchAnnouncements(String keyword, Pageable pageable);
    
    /**
     * 根据类型获取公告
     */
    List<AnnouncementDTO> getAnnouncementsByType(String type);
    
    /**
     * 根据类型获取公告（包含用户已读状态）
     */
    List<AnnouncementDTO> getAnnouncementsByType(String type, Long userId);
    
    /**
     * 启用/禁用公告
     */
    void toggleAnnouncementStatus(Long id, Boolean enabled);
    
    /**
     * 置顶/取消置顶公告
     */
    void toggleAnnouncementPin(Long id, Boolean pinned);
    
    /**
     * 统计活跃公告数量
     */
    Long countActiveAnnouncements();
    
    /**
     * 统计用户未读公告数量
     */
    Long countUnreadAnnouncementsForUser(Long userId);
    
    /**
     * 获取公告的已读统计
     */
    Long getAnnouncementReadCount(Long announcementId);
}