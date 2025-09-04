package com.blog.service;

import com.blog.entity.OperationLog;
import com.blog.repository.OperationLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 操作日志服务类
 */
@Service
@Transactional
public class OperationLogService {
    
    private static final Logger logger = LoggerFactory.getLogger(OperationLogService.class);
    
    @Autowired
    private OperationLogRepository operationLogRepository;
    
    /**
     * 异步保存操作日志
     */
    @Async
    public void saveOperationLog(OperationLog operationLog) {
        try {
            operationLogRepository.save(operationLog);
        } catch (Exception e) {
            logger.error("保存操作日志失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 分页查询操作日志
     */
    @Transactional(readOnly = true)
    public Page<OperationLog> getOperationLogs(Pageable pageable) {
        return operationLogRepository.findAll(pageable);
    }
    
    /**
     * 根据用户ID分页查询操作日志
     */
    @Transactional(readOnly = true)
    public Page<OperationLog> getOperationLogsByUserId(Long userId, Pageable pageable) {
        return operationLogRepository.findByUserIdOrderByOperationTimeDesc(userId, pageable);
    }
    
    /**
     * 根据模块分页查询操作日志
     */
    @Transactional(readOnly = true)
    public Page<OperationLog> getOperationLogsByModule(String module, Pageable pageable) {
        return operationLogRepository.findByModuleOrderByOperationTimeDesc(module, pageable);
    }
    
    /**
     * 根据操作类型分页查询操作日志
     */
    @Transactional(readOnly = true)
    public Page<OperationLog> getOperationLogsByType(OperationLog.OperationType operationType, Pageable pageable) {
        return operationLogRepository.findByOperationTypeOrderByOperationTimeDesc(operationType, pageable);
    }
    
    /**
     * 根据操作状态分页查询操作日志
     */
    @Transactional(readOnly = true)
    public Page<OperationLog> getOperationLogsByStatus(OperationLog.OperationStatus status, Pageable pageable) {
        return operationLogRepository.findByStatusOrderByOperationTimeDesc(status, pageable);
    }
    
    /**
     * 根据时间范围分页查询操作日志
     */
    @Transactional(readOnly = true)
    public Page<OperationLog> getOperationLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return operationLogRepository.findByOperationTimeBetween(startTime, endTime, pageable);
    }
    
    /**
     * 多条件查询操作日志
     */
    @Transactional(readOnly = true)
    public Page<OperationLog> getOperationLogsByConditions(Long userId, String module, 
                                                          OperationLog.OperationType operationType,
                                                          OperationLog.OperationStatus status,
                                                          LocalDateTime startTime, LocalDateTime endTime,
                                                          Pageable pageable) {
        return operationLogRepository.findByMultipleConditions(userId, module, operationType, status, startTime, endTime, pageable);
    }
    
    /**
     * 根据IP地址分页查询操作日志
     */
    @Transactional(readOnly = true)
    public Page<OperationLog> getOperationLogsByIpAddress(String ipAddress, Pageable pageable) {
        return operationLogRepository.findByIpAddressOrderByOperationTimeDesc(ipAddress, pageable);
    }
    
    /**
     * 查询失败的操作日志
     */
    @Transactional(readOnly = true)
    public Page<OperationLog> getFailedOperationLogs(Pageable pageable) {
        return operationLogRepository.findFailedOperations(pageable);
    }
    
    /**
     * 查询慢操作日志
     */
    @Transactional(readOnly = true)
    public List<OperationLog> getSlowOperationLogs(Long threshold, Pageable pageable) {
        return operationLogRepository.findSlowOperations(threshold, pageable);
    }
    
    /**
     * 统计指定时间范围内的操作次数
     */
    @Transactional(readOnly = true)
    public Long countOperationsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return operationLogRepository.countByOperationTimeBetween(startTime, endTime);
    }
    
    /**
     * 统计指定用户在指定时间范围内的操作次数
     */
    @Transactional(readOnly = true)
    public Long countOperationsByUserAndTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return operationLogRepository.countByUserIdAndOperationTimeBetween(userId, startTime, endTime);
    }
    
    /**
     * 统计各模块的操作次数
     */
    @Transactional(readOnly = true)
    public Map<String, Long> countOperationsByModule(LocalDateTime startTime, LocalDateTime endTime) {
        List<Object[]> results = operationLogRepository.countByModuleAndOperationTimeBetween(startTime, endTime);
        return results.stream()
                .collect(Collectors.toMap(
                    result -> (String) result[0],
                    result -> (Long) result[1]
                ));
    }
    
    /**
     * 统计各操作类型的次数
     */
    @Transactional(readOnly = true)
    public Map<OperationLog.OperationType, Long> countOperationsByType(LocalDateTime startTime, LocalDateTime endTime) {
        List<Object[]> results = operationLogRepository.countByOperationTypeAndOperationTimeBetween(startTime, endTime);
        return results.stream()
                .collect(Collectors.toMap(
                    result -> (OperationLog.OperationType) result[0],
                    result -> (Long) result[1]
                ));
    }
    
    /**
     * 获取操作日志详情
     */
    @Transactional(readOnly = true)
    public OperationLog getOperationLogById(Long id) {
        return operationLogRepository.findById(id).orElse(null);
    }
    
    /**
     * 删除操作日志
     */
    public void deleteOperationLog(Long id) {
        operationLogRepository.deleteById(id);
    }
    
    /**
     * 批量删除操作日志
     */
    public void deleteOperationLogs(List<Long> ids) {
        operationLogRepository.deleteAllById(ids);
    }
    
    /**
     * 定时清理过期的操作日志
     * 每天凌晨2点执行，删除90天前的日志
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanExpiredLogs() {
        try {
            LocalDateTime expireTime = LocalDateTime.now().minusDays(90);
            operationLogRepository.deleteByOperationTimeBefore(expireTime);
            logger.info("清理过期操作日志完成，清理时间点: {}", expireTime);
        } catch (Exception e) {
            logger.error("清理过期操作日志失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取操作统计信息
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getOperationStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> statistics = new java.util.HashMap<>();
        
        // 总操作次数
        Long totalOperations = countOperationsByTimeRange(startTime, endTime);
        statistics.put("totalOperations", totalOperations);
        
        // 各模块操作次数
        Map<String, Long> moduleStats = countOperationsByModule(startTime, endTime);
        statistics.put("moduleStatistics", moduleStats);
        
        // 各操作类型次数
        Map<OperationLog.OperationType, Long> typeStats = countOperationsByType(startTime, endTime);
        statistics.put("typeStatistics", typeStats);
        
        // 成功率统计
        Page<OperationLog> allLogs = getOperationLogsByTimeRange(startTime, endTime, Pageable.unpaged());
        long successCount = allLogs.getContent().stream()
                .mapToLong(log -> log.getStatus() == OperationLog.OperationStatus.SUCCESS ? 1 : 0)
                .sum();
        double successRate = totalOperations > 0 ? (double) successCount / totalOperations * 100 : 0;
        statistics.put("successRate", successRate);
        
        return statistics;
    }
    
    /**
     * 获取操作统计信息（带更多参数）
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getOperationStats(LocalDateTime startTime, LocalDateTime endTime, 
                                               Long userId, String module, String operationType) {
        Map<String, Object> stats = new HashMap<>();
        
        // 基础统计
        Long totalCount = countOperationsByTimeRange(startTime, endTime);
        stats.put("totalCount", totalCount);
        
        // 如果指定了用户ID
        if (userId != null) {
            Long userCount = countOperationsByUserAndTimeRange(userId, startTime, endTime);
            stats.put("userOperationCount", userCount);
        }
        
        // 模块统计
        Map<String, Long> moduleStats = countOperationsByModule(startTime, endTime);
        stats.put("moduleStats", moduleStats);
        
        // 操作类型统计
        Map<OperationLog.OperationType, Long> typeStats = countOperationsByType(startTime, endTime);
        stats.put("typeStats", typeStats);
        
        return stats;
    }
    
    /**
     * 删除单个日志
     */
    public boolean deleteLog(Long id) {
        try {
            if (operationLogRepository.existsById(id)) {
                operationLogRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("删除操作日志失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 清理指定天数前的过期日志
     */
    public void cleanupExpiredLogs(int days) {
        try {
            LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
            operationLogRepository.deleteByOperationTimeBefore(expireTime);
            logger.info("清理{}天前的操作日志完成，清理时间点: {}", days, expireTime);
        } catch (Exception e) {
            logger.error("清理过期操作日志失败: {}", e.getMessage(), e);
        }
    }
}