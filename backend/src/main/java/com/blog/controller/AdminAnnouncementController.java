package com.blog.controller;

import com.blog.dto.AnnouncementDTO;
import com.blog.service.AnnouncementService;
import com.blog.service.UserService;
import com.blog.common.Result;
import com.blog.annotation.OperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import com.blog.entity.User;

/**
 * 管理员公告管理控制器
 */
@RestController
@RequestMapping("/admin/announcements")
public class AdminAnnouncementController {
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 创建公告
     */
    @PostMapping
    @OperationLog(module = "公告管理", type = com.blog.entity.OperationLog.OperationType.CREATE, description = "创建公告")
    public Result<AnnouncementDTO> createAnnouncement(
            @Valid @RequestBody AnnouncementDTO announcementDTO,
            HttpServletRequest request) {
        try {
            Long userId = getCurrentUserId();
            AnnouncementDTO result = announcementService.createAnnouncement(announcementDTO, userId);
            return Result.success("公告创建成功", result);
        } catch (Exception e) {
            return Result.error("创建公告失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    @OperationLog(module = "公告管理", type = com.blog.entity.OperationLog.OperationType.UPDATE, description = "更新公告")
    public Result<AnnouncementDTO> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementDTO announcementDTO) {
        try {
            AnnouncementDTO result = announcementService.updateAnnouncement(id, announcementDTO);
            return Result.success("公告更新成功", result);
        } catch (Exception e) {
            return Result.error("更新公告失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    @OperationLog(module = "公告管理", type = com.blog.entity.OperationLog.OperationType.DELETE, description = "删除公告")
    public Result<Void> deleteAnnouncement(@PathVariable Long id) {
        try {
            announcementService.deleteAnnouncement(id);
            return Result.success("公告删除成功", null);
        } catch (Exception e) {
            return Result.error("删除公告失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取公告详情
     */
    @GetMapping("/{id}")
    public Result<AnnouncementDTO> getAnnouncement(@PathVariable Long id) {
        try {
            AnnouncementDTO result = announcementService.getAnnouncementById(id);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取公告失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页获取所有公告
     */
    @GetMapping
    public Result<Page<AnnouncementDTO>> getAllAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<AnnouncementDTO> result = announcementService.getAllAnnouncements(pageable);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取公告列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索公告
     */
    @GetMapping("/search")
    public Result<Page<AnnouncementDTO>> searchAnnouncements(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<AnnouncementDTO> result = announcementService.searchAnnouncements(keyword, pageable);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("搜索公告失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据类型获取公告
     */
    @GetMapping("/type/{type}")
    public Result<List<AnnouncementDTO>> getAnnouncementsByType(@PathVariable String type) {
        try {
            List<AnnouncementDTO> result = announcementService.getAnnouncementsByType(type);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取公告失败: " + e.getMessage());
        }
    }
    
    /**
     * 启用/禁用公告
     */
    @PatchMapping("/{id}/status")
    public Result<Void> toggleAnnouncementStatus(
            @PathVariable Long id,
            @RequestParam Boolean enabled) {
        try {
            announcementService.toggleAnnouncementStatus(id, enabled);
            String message = enabled ? "公告已启用" : "公告已禁用";
            return Result.success(message, null);
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 置顶/取消置顶公告
     */
    @PatchMapping("/{id}/pin")
    public Result<Void> toggleAnnouncementPin(
            @PathVariable Long id,
            @RequestParam Boolean pinned) {
        System.out.println("=== toggleAnnouncementPin 方法被调用 ===");
        System.out.println("公告ID: " + id + ", 置顶状态: " + pinned);
        try {
            // 验证管理员权限
            verifyAdminPermission();
            
            announcementService.toggleAnnouncementPin(id, pinned);
            String message = pinned ? "公告已置顶" : "公告已取消置顶";
            System.out.println("置顶操作成功: " + message);
            return Result.success(message, null);
        } catch (Exception e) {
            System.out.println("置顶操作失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error("操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取公告统计信息
     */
    @GetMapping("/stats")
    public Result<AnnouncementStatsDTO> getAnnouncementStats() {
        try {
            Long activeCount = announcementService.countActiveAnnouncements();
            AnnouncementStatsDTO stats = new AnnouncementStatsDTO();
            stats.setActiveCount(activeCount);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取公告已读统计
     */
    @GetMapping("/{id}/read-stats")
    public Result<Long> getAnnouncementReadStats(@PathVariable Long id) {
        try {
            Long readCount = announcementService.getAnnouncementReadCount(id);
            return Result.success(readCount);
        } catch (Exception e) {
            return Result.error("获取已读统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("=== 获取当前用户ID ===");
        System.out.println("认证对象: " + (authentication != null ? "存在" : "不存在"));
        
        if (authentication != null) {
            System.out.println("是否已认证: " + authentication.isAuthenticated());
            System.out.println("Principal: " + authentication.getPrincipal());
            System.out.println("用户名: " + authentication.getName());
        }
        
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            System.out.println("从用户名获取ID: " + username);
            Long userId = userService.getUserIdByUsername(username);
            System.out.println("获取到的用户ID: " + userId);
            return userId;
        }
        
        System.out.println("未获取到有效的用户认证信息");
        return null;
    }
    
    /**
     * 验证管理员权限
     */
    private void verifyAdminPermission() {
        try {
            Long userId = getCurrentUserId();
            System.out.println("=== 验证管理员权限 ===");
            System.out.println("当前用户ID: " + userId);
            
            if (userId == null) {
                System.out.println("错误: 用户未登录");
                throw new RuntimeException("用户未登录");
            }
            
            Optional<User> user = userService.findById(userId);
            System.out.println("用户查询结果: " + (user.isPresent() ? "找到用户" : "用户不存在"));
            
            if (user.isPresent()) {
                System.out.println("用户角色: " + user.get().getRole());
                System.out.println("用户名: " + user.get().getUsername());
            }
            
            if (user.isEmpty() || user.get().getRole() != User.Role.ADMIN) {
                System.out.println("错误: 无管理员权限");
                throw new RuntimeException("无管理员权限");
            }
            
            System.out.println("权限验证通过");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * 公告统计DTO
     */
    public static class AnnouncementStatsDTO {
        private Long activeCount;
        
        public Long getActiveCount() {
            return activeCount;
        }
        
        public void setActiveCount(Long activeCount) {
            this.activeCount = activeCount;
        }
    }
}