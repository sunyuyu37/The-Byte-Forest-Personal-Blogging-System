package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.CommentDTO;
import com.blog.entity.Comment;
import com.blog.service.CommentService;
import com.blog.service.impl.CommentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// 新增导入
import com.blog.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private CommentServiceImpl commentServiceImpl;
    
    // 注入 UserService 以获取当前用户ID
    @Autowired
    private UserService userService;

    // ========== 公开接口 ==========

    /**
     * 创建评论（公开）
     */
    @PostMapping
    public Result<CommentDTO> createComment(@RequestBody CommentDTO commentDTO) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }
            CommentDTO created = commentService.createComment(commentDTO, userId);
            return Result.success("评论创建成功", created);
        } catch (Exception e) {
            return Result.error("创建评论失败: " + e.getMessage());
        }
    }

    /**
     * 点赞评论（公开）
     */
    @PostMapping("/{id}/like")
    public Result<String> likeComment(@PathVariable("id") Long id) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }
            commentService.likeComment(id, userId);
            return Result.success("点赞成功");
        } catch (Exception e) {
            return Result.error("点赞失败: " + e.getMessage());
        }
    }

    /**
     * 取消点赞评论（公开）
     */
    @DeleteMapping("/{id}/like")
    public Result<String> unlikeComment(@PathVariable("id") Long id) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.unauthorized("请先登录");
            }
            commentService.unlikeComment(id, userId);
            return Result.success("已取消点赞");
        } catch (Exception e) {
            return Result.error("取消点赞失败: " + e.getMessage());
        }
    }

    /**
     * 判断当前用户是否已点赞该评论（公开）
     */
    @GetMapping("/{id}/liked")
    public Result<Boolean> isLiked(@PathVariable("id") Long id) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.success(false);
            }
            boolean liked = commentService.isCommentLikedByUser(id, userId);
            return Result.success(liked);
        } catch (Exception e) {
            return Result.error("查询点赞状态失败: " + e.getMessage());
        }
    }

    // ========== 管理端接口 ==========

    /**
     * 获取评论列表（管理员）
     */
    @GetMapping("/admin")
    public Result<Map<String, Object>> getCommentsForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String userNickname,
            @RequestParam(required = false) String articleTitle,
            @RequestParam(required = false) String status) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<CommentDTO> comments = commentServiceImpl.searchCommentsForAdmin(
                content, userNickname, articleTitle, status, pageable);

            Map<String, Object> data = new HashMap<>();
            data.put("records", comments.getContent());
            data.put("total", comments.getTotalElements());
            data.put("pages", comments.getTotalPages());
            data.put("current", page);
            data.put("size", size);
            data.put("hasNext", comments.hasNext());
            data.put("hasPrevious", comments.hasPrevious());

            return Result.success("获取评论列表成功", data);
        } catch (Exception e) {
            return Result.error("获取评论列表失败: " + e.getMessage());
        }
    }

    /**
     * 审核评论
     */
    @PutMapping("/{id}/approve")
    public Result<String> approveComment(@PathVariable Long id) {
        try {
            commentService.approveComment(id);
            return Result.success("评论审核通过");
        } catch (Exception e) {
            return Result.error("审核评论失败: " + e.getMessage());
        }
    }

    /**
     * 拒绝评论
     */
    @PutMapping("/{id}/reject")
    public Result<String> rejectComment(@PathVariable Long id) {
        try {
            commentService.rejectComment(id);
            return Result.success("评论已拒绝");
        } catch (Exception e) {
            return Result.error("拒绝评论失败: " + e.getMessage());
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return Result.success("评论已删除");
        } catch (Exception e) {
            return Result.error("删除评论失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除评论
     */
    @DeleteMapping("/batch")
    public Result<String> batchDeleteComments(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            java.util.List<Long> ids = (java.util.List<Long>) request.get("ids");
            if (ids == null || ids.isEmpty()) {
                return Result.badRequest("参数错误：ids 不能为空");
            }
            for (Long id : ids) {
                commentService.deleteComment(id);
            }
            return Result.success("批量删除成功");
        } catch (Exception e) {
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取评论统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getCommentStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalComments", commentService.countAllComments());
            stats.put("approvedComments", commentService.countApprovedComments());
            stats.put("pendingComments", commentService.countPendingComments());
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }

    // ========== 工具方法 ==========
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() != null &&
                !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            return userService.getUserIdByUsername(username);
        }
        return null;
    }
}