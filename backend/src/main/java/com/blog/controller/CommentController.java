package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.CommentDTO;
import com.blog.entity.Comment;
import com.blog.service.CommentService;
import com.blog.service.impl.CommentServiceImpl;
import com.blog.annotation.OperationLog;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
     * 获取最近评论（公开接口，用于首页展示）
     */
    @GetMapping("/recent")
    public Result<Object> getRecentCommentsPublic(@RequestParam(defaultValue = "5") int limit) {
        try {
            return Result.success(commentService.getRecentComments(limit));
        } catch (Exception e) {
            return Result.error("获取最近评论失败: " + e.getMessage());
        }
    }

    /**
     * 创建评论（公开）
     */
    @PostMapping
    @OperationLog(module = "评论管理", type = com.blog.entity.OperationLog.OperationType.CREATE, description = "创建评论")
    public Result<CommentDTO> createComment(@Valid @RequestBody CommentDTO commentDTO) {
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
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "false") Boolean messageOnly) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<CommentDTO> comments = commentServiceImpl.searchCommentsForAdmin(
                content, userNickname, articleTitle, status, messageOnly, pageable);

            Map<String, Object> data = new HashMap<>();
            data.put("records", comments.getContent());
            data.put("total", comments.getTotalElements());
            data.put("pages", comments.getTotalPages());
            data.put("current", page);
            data.put("size", size);
            data.put("hasNext", comments.hasNext());
            data.put("hasPrevious", comments.hasPrevious());

            String successMessage = messageOnly ? "获取留言列表成功" : "获取评论列表成功";
            return Result.success(successMessage, data);
        } catch (Exception e) {
            String errorMessage = messageOnly ? "获取留言列表失败: " : "获取评论列表失败: ";
            return Result.error(errorMessage + e.getMessage());
        }
    }

    /**
     * 获取留言列表（支持高级筛选）
     */
    @GetMapping("/messages")
    public ResponseEntity<Map<String, Object>> getMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String userNickname,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "true") boolean messageOnly,
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String contentLength,
            @RequestParam(defaultValue = "createdAt_desc") String sortBy,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        Page<CommentDTO> messages = commentService.getMessages(page - 1, size, content, userNickname, status, messageOnly, ip, userEmail, contentLength, sortBy, startTime, endTime);
        
        Map<String, Object> response = new HashMap<>();
        response.put("messages", messages.getContent());
        response.put("total", messages.getTotalElements());
        response.put("totalPages", messages.getTotalPages());
        response.put("currentPage", page);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 审核评论
     */
    @PutMapping("/{id}/approve")
    @OperationLog(module = "评论管理", type = com.blog.entity.OperationLog.OperationType.UPDATE, description = "审核通过评论")
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
    @OperationLog(module = "评论管理", type = com.blog.entity.OperationLog.OperationType.UPDATE, description = "审核拒绝评论")
    public Result<String> rejectComment(@PathVariable Long id) {
        try {
            commentService.rejectComment(id);
            return Result.success("评论已拒绝");
        } catch (Exception e) {
            return Result.error("拒绝评论失败: " + e.getMessage());
        }
    }

    /**
     * 更新评论
     */
    @PutMapping("/{id}")
    @OperationLog(module = "评论管理", type = com.blog.entity.OperationLog.OperationType.UPDATE, description = "更新评论")
    public Result<String> updateComment(@PathVariable Long id, @Valid @RequestBody CommentDTO commentDTO) {
        try {
            commentService.updateComment(id, commentDTO);
            return Result.success("评论更新成功");
        } catch (Exception e) {
            return Result.error("更新评论失败: " + e.getMessage());
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    @OperationLog(module = "评论管理", type = com.blog.entity.OperationLog.OperationType.DELETE, description = "删除评论")
    public Result<String> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return Result.success("评论已删除");
        } catch (Exception e) {
            return Result.error("删除评论失败: " + e.getMessage());
        }
    }

    /**
     * 批量审核通过评论
     */
    @PutMapping("/batch/approve")
    @OperationLog(module = "评论管理", type = com.blog.entity.OperationLog.OperationType.UPDATE, description = "批量审核通过评论")
    public Result<String> batchApproveComments(@Valid @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            java.util.List<Long> ids = (java.util.List<Long>) request.get("ids");
            if (ids == null || ids.isEmpty()) {
                return Result.badRequest("参数错误：ids 不能为空");
            }
            for (Long id : ids) {
                commentService.approveComment(id);
            }
            return Result.success("批量审核通过成功");
        } catch (Exception e) {
            return Result.error("批量审核通过失败: " + e.getMessage());
        }
    }

    /**
     * 批量拒绝评论
     */
    @PutMapping("/batch/reject")
    @OperationLog(module = "评论管理", type = com.blog.entity.OperationLog.OperationType.UPDATE, description = "批量拒绝评论")
    public Result<String> batchRejectComments(@Valid @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            java.util.List<Long> ids = (java.util.List<Long>) request.get("ids");
            if (ids == null || ids.isEmpty()) {
                return Result.badRequest("参数错误：ids 不能为空");
            }
            for (Long id : ids) {
                commentService.rejectComment(id);
            }
            return Result.success("批量拒绝成功");
        } catch (Exception e) {
            return Result.error("批量拒绝失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除评论
     */
    @DeleteMapping("/batch")
    public Result<String> batchDeleteComments(@Valid @RequestBody Map<String, Object> request) {
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
    
    /**
     * 获取留言统计信息
     */
    @GetMapping("/messages/stats")
    public Result<Map<String, Object>> getMessageStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalMessages", commentService.countAllMessages());
            stats.put("approvedMessages", commentService.countApprovedMessages());
            stats.put("pendingMessages", commentService.countPendingMessages());
            stats.put("rejectedMessages", commentService.countRejectedMessages());
            return Result.success("获取留言统计信息成功", stats);
        } catch (Exception e) {
            return Result.error("获取留言统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取留言趋势统计（按日期）
     */
    @GetMapping("/messages/trend")
    public Result<Map<String, Object>> getMessageTrend(
            @RequestParam(defaultValue = "7") int days) {
        try {
            Map<String, Object> trend = commentService.getMessageTrend(days);
            return Result.success("获取留言趋势成功", trend);
        } catch (Exception e) {
            return Result.error("获取留言趋势失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取最新留言
     */
    @GetMapping("/messages/recent")
    public Result<Object> getRecentMessages(@RequestParam(defaultValue = "5") int limit) {
        try {
            return Result.success("获取最新留言成功", commentService.getRecentMessages(limit));
        } catch (Exception e) {
            return Result.error("获取最新留言失败: " + e.getMessage());
        }
    }

    /**
     * 获取留言详情
     */
    @GetMapping("/messages/{id}/detail")
    public Result<Object> getMessageDetail(@PathVariable Long id) {
        try {
            CommentDTO messageDetail = commentService.getMessageDetail(id);
            if (messageDetail == null) {
                return Result.notFound("留言不存在");
            }
            return Result.success("获取留言详情成功", messageDetail);
        } catch (Exception e) {
            return Result.error("获取留言详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 导出留言
     */
    @GetMapping("/messages/export")
    public ResponseEntity<byte[]> exportMessages(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userNickname,
            @RequestParam(required = false) String content) {
        
        byte[] excelData = commentService.exportMessages(status, userNickname, content);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", 
            "messages_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
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