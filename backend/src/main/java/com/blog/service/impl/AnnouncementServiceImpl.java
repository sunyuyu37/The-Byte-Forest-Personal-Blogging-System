package com.blog.service.impl;

import com.blog.dto.AnnouncementDTO;
import com.blog.entity.Announcement;
import com.blog.entity.User;
import com.blog.entity.UserAnnouncementRead;
import com.blog.repository.AnnouncementRepository;
import com.blog.repository.UserAnnouncementReadRepository;
import com.blog.repository.UserRepository;
import com.blog.service.AnnouncementService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {
    
    @Autowired
    private AnnouncementRepository announcementRepository;
    
    @Autowired
    private UserAnnouncementReadRepository userAnnouncementReadRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public AnnouncementDTO createAnnouncement(AnnouncementDTO announcementDTO, Long publisherId) {
        // 验证发布者是否存在且为管理员
        User publisher = userRepository.findById(publisherId)
                .orElseThrow(() -> new RuntimeException("发布者不存在"));
        
        if (!User.Role.ADMIN.equals(publisher.getRole())) {
            throw new RuntimeException("只有管理员可以发布公告");
        }
        
        // 创建公告实体
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementDTO, announcement);
        announcement.setPublisherId(publisherId);
        
        // 保存公告
        announcement = announcementRepository.save(announcement);
        
        // 转换为DTO并返回
        return convertToDTO(announcement);
    }
    
    @Override
    public AnnouncementDTO updateAnnouncement(Long id, AnnouncementDTO announcementDTO) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        // 更新字段
        if (announcementDTO.getTitle() != null) {
            announcement.setTitle(announcementDTO.getTitle());
        }
        if (announcementDTO.getContent() != null) {
            announcement.setContent(announcementDTO.getContent());
        }
        if (announcementDTO.getType() != null) {
            announcement.setType(announcementDTO.getType());
        }
        if (announcementDTO.getPriority() != null) {
            announcement.setPriority(announcementDTO.getPriority());
        }
        if (announcementDTO.getEnabled() != null) {
            announcement.setEnabled(announcementDTO.getEnabled());
        }
        if (announcementDTO.getPinned() != null) {
            announcement.setPinned(announcementDTO.getPinned());
        }
        if (announcementDTO.getStartTime() != null) {
            announcement.setStartTime(announcementDTO.getStartTime());
        }
        if (announcementDTO.getEndTime() != null) {
            announcement.setEndTime(announcementDTO.getEndTime());
        }
        
        announcement = announcementRepository.save(announcement);
        return convertToDTO(announcement);
    }
    
    @Override
    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        announcement.setDeleted(true);
        announcementRepository.save(announcement);
        
        // 删除相关的已读记录
        userAnnouncementReadRepository.deleteByAnnouncementId(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AnnouncementDTO getAnnouncementById(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        if (announcement.getDeleted()) {
            throw new RuntimeException("公告已删除");
        }
        
        return convertToDTO(announcement);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AnnouncementDTO getAnnouncementById(Long id, Long userId) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        if (announcement.getDeleted()) {
            throw new RuntimeException("公告已删除");
        }
        
        return convertToDTO(announcement, userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO> getAllAnnouncements(Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.findByDeletedFalse(pageable);
        return announcements.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getActiveAnnouncements() {
        List<Announcement> announcements = announcementRepository.findActiveAnnouncements(LocalDateTime.now());
        return announcements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getUnreadAnnouncementsForUser(Long userId) {
        List<Announcement> announcements = announcementRepository.findUnreadAnnouncementsForUser(userId, LocalDateTime.now());
        return announcements.stream()
                .map(announcement -> {
                    AnnouncementDTO dto = convertToDTO(announcement);
                    dto.setIsRead(false);
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public void markAnnouncementAsRead(Long userId, Long announcementId) {
        // 检查是否已经标记为已读
        if (!userAnnouncementReadRepository.existsByUserIdAndAnnouncementId(userId, announcementId)) {
            UserAnnouncementRead readRecord = new UserAnnouncementRead(userId, announcementId);
            userAnnouncementReadRepository.save(readRecord);
        }
    }
    
    @Override
    public void markAnnouncementsAsRead(Long userId, List<Long> announcementIds) {
        for (Long announcementId : announcementIds) {
            markAnnouncementAsRead(userId, announcementId);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AnnouncementDTO> searchAnnouncements(String keyword, Pageable pageable) {
        Page<Announcement> announcements = announcementRepository.searchAnnouncements(keyword, pageable);
        return announcements.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByType(String type) {
        List<Announcement> announcements = announcementRepository.findByDeletedFalseAndTypeOrderByCreatedAtDesc(type);
        return announcements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementDTO> getAnnouncementsByType(String type, Long userId) {
        List<Announcement> announcements = announcementRepository.findByTypeAndDeletedFalse(type);
        return announcements.stream()
                .map(announcement -> convertToDTO(announcement, userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public void toggleAnnouncementStatus(Long id, Boolean enabled) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        announcement.setEnabled(enabled);
        announcementRepository.save(announcement);
    }
    
    @Override
    public void toggleAnnouncementPin(Long id, Boolean pinned) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        announcement.setPinned(pinned);
        announcementRepository.save(announcement);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long countActiveAnnouncements() {
        return announcementRepository.countActiveAnnouncements(LocalDateTime.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long countUnreadAnnouncementsForUser(Long userId) {
        return announcementRepository.countUnreadAnnouncementsForUser(userId, LocalDateTime.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getAnnouncementReadCount(Long announcementId) {
        return userAnnouncementReadRepository.countByAnnouncementId(announcementId);
    }
    
    /**
     * 将实体转换为DTO
     */
    private AnnouncementDTO convertToDTO(Announcement announcement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        BeanUtils.copyProperties(announcement, dto);
        
        // 获取发布者信息
        userRepository.findById(announcement.getPublisherId())
                .ifPresent(publisher -> dto.setPublisherName(publisher.getNickname()));
        
        // 获取已读数量
        dto.setReadCount(getAnnouncementReadCount(announcement.getId()));
        
        return dto;
    }
    
    /**
     * 将实体转换为DTO（包含用户已读状态）
     */
    private AnnouncementDTO convertToDTO(Announcement announcement, Long userId) {
        AnnouncementDTO dto = convertToDTO(announcement);
        
        // 检查用户是否已读
        boolean isRead = userAnnouncementReadRepository
                .existsByUserIdAndAnnouncementId(userId, announcement.getId());
        dto.setIsRead(isRead);
        
        return dto;
    }
}