package com.blog.controller;

import com.blog.annotation.OperationLog;
import com.blog.common.Result;
import com.blog.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作日志控制器
 * 提供操作日志的查询和管理功能
 */
@RestController
@RequestMapping("/admin/operation-logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     */
    @GetMapping
    public Result<Page<com.blog.entity.OperationLog>> getOperationLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) String ipAddress) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
            
            LocalDateTime start = null;
            LocalDateTime end = null;
            if (startTime != null && !startTime.isEmpty()) {
                start = LocalDateTime.parse(startTime);
            }
            if (endTime != null && !endTime.isEmpty()) {
                end = LocalDateTime.parse(endTime);
            }
            
            // 转换操作类型和状态
            com.blog.entity.OperationLog.OperationType opType = null;
            if (operationType != null && !operationType.isEmpty()) {
                try {
                    opType = com.blog.entity.OperationLog.OperationType.valueOf(operationType.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // 忽略无效的操作类型
                }
            }
            
            com.blog.entity.OperationLog.OperationStatus opStatus = null;
            if (status != null && !status.isEmpty()) {
                try {
                    opStatus = com.blog.entity.OperationLog.OperationStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // 忽略无效的状态
                }
            }
            
            Page<com.blog.entity.OperationLog> logs = operationLogService.getOperationLogsByConditions(
                    userId, module, opType, opStatus, start, end, pageable);
            return Result.success(logs);
        } catch (Exception e) {
            return Result.error("查询操作日志失败: " + e.getMessage());
        }
    }

    /**
     * 获取操作日志详情
     */
    @GetMapping("/{id}")
    public Result<com.blog.entity.OperationLog> getOperationLogDetail(@PathVariable Long id) {
        try {
            com.blog.entity.OperationLog log = operationLogService.getOperationLogById(id);
            if (log == null) {
                return Result.error("操作日志不存在");
            }
            return Result.success(log);
        } catch (Exception e) {
            return Result.error("获取操作日志详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取失败操作日志
     */
    @GetMapping("/failed")
    public Result<Page<com.blog.entity.OperationLog>> getFailedLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<com.blog.entity.OperationLog> logs = operationLogService.getFailedOperationLogs(pageable);
            return Result.success(logs);
        } catch (Exception e) {
            return Result.error("查询失败日志失败: " + e.getMessage());
        }
    }

    /**
     * 获取慢操作日志
     */
    @GetMapping("/slow")
    public Result<Page<com.blog.entity.OperationLog>> getSlowLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "3000") long minExecutionTime) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "executionTime"));
            List<com.blog.entity.OperationLog> slowLogs = operationLogService.getSlowOperationLogs(minExecutionTime, pageable);
            // 将List转换为Page（简单实现）
            Page<com.blog.entity.OperationLog> logs = new PageImpl<>(slowLogs, pageable, slowLogs.size());
            return Result.success(logs);
        } catch (Exception e) {
            return Result.error("查询慢操作日志失败: " + e.getMessage());
        }
    }

    /**
     * 获取操作统计信息
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getOperationStats(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operationType) {
        try {
            LocalDateTime start = null;
            LocalDateTime end = null;
            if (startTime != null && !startTime.isEmpty()) {
                start = LocalDateTime.parse(startTime);
            }
            if (endTime != null && !endTime.isEmpty()) {
                end = LocalDateTime.parse(endTime);
            }
            
            Map<String, Object> stats = operationLogService.getOperationStats(
                    start, end, userId, module, operationType);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取操作统计失败: " + e.getMessage());
        }
    }

    /**
     * 删除操作日志
     */
    @DeleteMapping("/{id}")
    @OperationLog(module = "日志管理", type = com.blog.entity.OperationLog.OperationType.DELETE, description = "删除操作日志")
    public Result<String> deleteOperationLog(@PathVariable Long id) {
        try {
            operationLogService.deleteLog(id);
            return Result.success("删除操作日志成功");
        } catch (Exception e) {
            return Result.error("删除操作日志失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除操作日志
     */
    @DeleteMapping("/batch")
    @OperationLog(module = "日志管理", type = com.blog.entity.OperationLog.OperationType.DELETE, description = "批量删除操作日志")
    public Result<String> batchDeleteOperationLogs(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            java.util.List<Long> ids = (java.util.List<Long>) request.get("ids");
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的日志");
            }
            
            for (Long id : ids) {
                operationLogService.deleteLog(id);
            }
            return Result.success("批量删除操作日志成功");
        } catch (Exception e) {
            return Result.error("批量删除操作日志失败: " + e.getMessage());
        }
    }

    /**
     * 清理过期日志
     */
    @DeleteMapping("/cleanup")
    @OperationLog(module = "日志管理", type = com.blog.entity.OperationLog.OperationType.DELETE, description = "清理过期日志")
    public Result<String> cleanupExpiredLogs(@RequestParam(defaultValue = "30") int days) {
        try {
            operationLogService.cleanupExpiredLogs(days);
            return Result.success("清理过期日志成功");
        } catch (Exception e) {
            return Result.error("清理过期日志失败: " + e.getMessage());
        }
    }
}