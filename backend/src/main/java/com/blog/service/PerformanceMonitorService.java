package com.blog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控服务类
 */
@Service
public class PerformanceMonitorService {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitorService.class);
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE_LOGGER");
    
    // 性能指标缓存
    private final Map<String, Object> performanceMetrics = new ConcurrentHashMap<>();
    
    // 请求计数器
    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);
    
    // 响应时间统计
    private final Map<String, Long> responseTimes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> endpointCounts = new ConcurrentHashMap<>();
    
    /**
     * 记录请求
     */
    public void recordRequest() {
        requestCount.incrementAndGet();
    }
    
    /**
     * 记录错误
     */
    public void recordError() {
        errorCount.incrementAndGet();
    }
    
    /**
     * 记录接口响应时间
     */
    public void recordResponseTime(String endpoint, long responseTime) {
        responseTimes.merge(endpoint, responseTime, Long::sum);
        endpointCounts.computeIfAbsent(endpoint, k -> new AtomicLong(0)).incrementAndGet();
        
        // 记录慢接口
        if (responseTime > 3000) { // 超过3秒的接口
            performanceLogger.warn("慢接口检测: {} 耗时 {}ms", endpoint, responseTime);
        }
    }
    
    /**
     * 获取系统性能指标
     */
    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // JVM内存信息
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        metrics.put("heapMemoryUsed", memoryBean.getHeapMemoryUsage().getUsed());
        metrics.put("heapMemoryMax", memoryBean.getHeapMemoryUsage().getMax());
        metrics.put("nonHeapMemoryUsed", memoryBean.getNonHeapMemoryUsage().getUsed());
        
        // 系统信息
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        metrics.put("systemLoadAverage", osBean.getSystemLoadAverage());
        metrics.put("availableProcessors", osBean.getAvailableProcessors());
        
        // 线程信息
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        metrics.put("threadCount", threadBean.getThreadCount());
        metrics.put("peakThreadCount", threadBean.getPeakThreadCount());
        
        // 应用指标
        metrics.put("totalRequests", requestCount.get());
        metrics.put("totalErrors", errorCount.get());
        metrics.put("errorRate", calculateErrorRate());
        
        return metrics;
    }
    
    /**
     * 获取接口性能统计
     */
    public Map<String, Object> getEndpointMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        for (Map.Entry<String, Long> entry : responseTimes.entrySet()) {
            String endpoint = entry.getKey();
            Long totalTime = entry.getValue();
            Long count = endpointCounts.get(endpoint).get();
            
            Map<String, Object> endpointMetric = new HashMap<>();
            endpointMetric.put("totalTime", totalTime);
            endpointMetric.put("count", count);
            endpointMetric.put("averageTime", count > 0 ? totalTime / count : 0);
            
            metrics.put(endpoint, endpointMetric);
        }
        
        return metrics;
    }
    
    /**
     * 计算错误率
     */
    private double calculateErrorRate() {
        long total = requestCount.get();
        long errors = errorCount.get();
        return total > 0 ? (double) errors / total * 100 : 0.0;
    }
    
    /**
     * 获取内存使用率
     */
    public double getMemoryUsagePercentage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long used = memoryBean.getHeapMemoryUsage().getUsed();
        long max = memoryBean.getHeapMemoryUsage().getMax();
        return max > 0 ? (double) used / max * 100 : 0.0;
    }
    
    /**
     * 检查系统健康状态
     */
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        
        // 内存健康检查
        double memoryUsage = getMemoryUsagePercentage();
        health.put("memoryUsage", memoryUsage);
        health.put("memoryHealthy", memoryUsage < 85.0);
        
        // 错误率健康检查
        double errorRate = calculateErrorRate();
        health.put("errorRate", errorRate);
        health.put("errorRateHealthy", errorRate < 5.0);
        
        // 线程健康检查
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        int threadCount = threadBean.getThreadCount();
        health.put("threadCount", threadCount);
        health.put("threadHealthy", threadCount < 200);
        
        // 系统负载检查
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double systemLoad = osBean.getSystemLoadAverage();
        int processors = osBean.getAvailableProcessors();
        health.put("systemLoad", systemLoad);
        health.put("systemLoadHealthy", systemLoad < processors * 0.8);
        
        // 整体健康状态
        boolean overallHealthy = (boolean) health.get("memoryHealthy") &&
                                (boolean) health.get("errorRateHealthy") &&
                                (boolean) health.get("threadHealthy") &&
                                (boolean) health.get("systemLoadHealthy");
        health.put("overallHealthy", overallHealthy);
        
        return health;
    }
    
    /**
     * 定时监控系统性能
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000) // 5分钟
    public void monitorSystemPerformance() {
        try {
            Map<String, Object> metrics = getSystemMetrics();
            Map<String, Object> health = getHealthStatus();
            
            // 记录性能指标
            performanceLogger.info("系统性能监控 - 内存使用: {}%, 错误率: {}%, 线程数: {}, 系统负载: {}, 总请求数: {}",
                String.format("%.2f", getMemoryUsagePercentage()),
                String.format("%.2f", calculateErrorRate()),
                metrics.get("threadCount"),
                metrics.get("systemLoadAverage"),
                metrics.get("totalRequests"));
            
            // 检查是否需要告警
            checkAndAlert(health);
            
            // 更新性能指标缓存
            performanceMetrics.putAll(metrics);
            performanceMetrics.putAll(health);
            
        } catch (Exception e) {
            logger.error("系统性能监控异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 检查并发送告警
     */
    private void checkAndAlert(Map<String, Object> health) {
        // 内存使用率告警
        if (!(boolean) health.get("memoryHealthy")) {
            performanceLogger.error("内存使用率告警: {}%", health.get("memoryUsage"));
        }
        
        // 错误率告警
        if (!(boolean) health.get("errorRateHealthy")) {
            performanceLogger.error("错误率告警: {}%", health.get("errorRate"));
        }
        
        // 线程数告警
        if (!(boolean) health.get("threadHealthy")) {
            performanceLogger.error("线程数告警: {}", health.get("threadCount"));
        }
        
        // 系统负载告警
        if (!(boolean) health.get("systemLoadHealthy")) {
            performanceLogger.error("系统负载告警: {}", health.get("systemLoad"));
        }
    }
    
    /**
     * 重置统计数据
     */
    public void resetMetrics() {
        requestCount.set(0);
        errorCount.set(0);
        responseTimes.clear();
        endpointCounts.clear();
        performanceMetrics.clear();
        logger.info("性能监控统计数据已重置");
    }
    
    /**
     * 获取缓存的性能指标
     */
    public Map<String, Object> getCachedMetrics() {
        return new HashMap<>(performanceMetrics);
    }
    
    /**
     * 获取JVM内存指标
     */
    public Map<String, Object> getJvmMemoryMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        metrics.put("heapMemoryUsed", memoryBean.getHeapMemoryUsage().getUsed());
        metrics.put("heapMemoryMax", memoryBean.getHeapMemoryUsage().getMax());
        metrics.put("heapMemoryCommitted", memoryBean.getHeapMemoryUsage().getCommitted());
        metrics.put("nonHeapMemoryUsed", memoryBean.getNonHeapMemoryUsage().getUsed());
        metrics.put("nonHeapMemoryMax", memoryBean.getNonHeapMemoryUsage().getMax());
        metrics.put("memoryUsagePercentage", getMemoryUsagePercentage());
        
        return metrics;
    }
    
    /**
     * 获取线程指标
     */
    public Map<String, Object> getThreadMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        
        metrics.put("threadCount", threadBean.getThreadCount());
        metrics.put("peakThreadCount", threadBean.getPeakThreadCount());
        metrics.put("daemonThreadCount", threadBean.getDaemonThreadCount());
        metrics.put("totalStartedThreadCount", threadBean.getTotalStartedThreadCount());
        
        return metrics;
    }
    
    /**
     * 获取请求指标
     */
    public Map<String, Object> getRequestMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("totalRequests", requestCount.get());
        metrics.put("totalErrors", errorCount.get());
        metrics.put("errorRate", calculateErrorRate());
        metrics.put("successRate", 100.0 - calculateErrorRate());
        
        return metrics;
    }
    
    /**
     * 获取错误率
     */
    public double getErrorRate() {
        return calculateErrorRate();
    }
    
    /**
     * 获取响应时间统计
     */
    public Map<String, Object> getResponseTimeStats() {
        Map<String, Object> stats = new HashMap<>();
        
        if (responseTimes.isEmpty()) {
            stats.put("averageResponseTime", 0);
            stats.put("totalEndpoints", 0);
            stats.put("endpointStats", new HashMap<>());
            return stats;
        }
        
        // 计算平均响应时间
        long totalTime = responseTimes.values().stream().mapToLong(Long::longValue).sum();
        long totalCount = endpointCounts.values().stream().mapToLong(AtomicLong::get).sum();
        
        stats.put("averageResponseTime", totalCount > 0 ? totalTime / totalCount : 0);
        stats.put("totalEndpoints", responseTimes.size());
        stats.put("endpointStats", getEndpointMetrics());
        
        return stats;
    }
    
    /**
     * 清理指定天数前的数据
     */
    public void cleanupOldData(int days) {
        try {
            // 简单实现：清理所有数据（实际项目中可以根据时间戳清理）
            if (days > 0) {
                responseTimes.clear();
                endpointCounts.clear();
                performanceMetrics.clear();
                logger.info("清理{}天前的性能监控数据", days);
            }
        } catch (Exception e) {
            logger.error("清理历史数据异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取性能告警信息
     */
    public Map<String, Object> getPerformanceAlerts() {
        Map<String, Object> alerts = new HashMap<>();
        Map<String, Object> health = getHealthStatus();
        
        // 检查各项指标是否正常
        if (!(boolean) health.get("memoryHealthy")) {
            alerts.put("memoryAlert", "内存使用率过高: " + health.get("memoryUsage") + "%");
        }
        
        if (!(boolean) health.get("errorRateHealthy")) {
            alerts.put("errorRateAlert", "错误率过高: " + health.get("errorRate") + "%");
        }
        
        if (!(boolean) health.get("threadHealthy")) {
            alerts.put("threadAlert", "线程数过多: " + health.get("threadCount"));
        }
        
        if (!(boolean) health.get("systemLoadHealthy")) {
            alerts.put("systemLoadAlert", "系统负载过高: " + health.get("systemLoad"));
        }
        
        alerts.put("alertCount", alerts.size() - 1); // 减去alertCount本身
        alerts.put("overallHealthy", health.get("overallHealthy"));
        
        return alerts;
    }
    
    /**
     * 定时清理过期的响应时间数据
     * 每小时执行一次
     */
    @Scheduled(fixedRate = 3600000) // 1小时
    public void cleanupMetrics() {
        try {
            // 保留最近的数据，清理过多的历史数据
            if (responseTimes.size() > 1000) {
                responseTimes.clear();
                endpointCounts.clear();
                logger.info("清理过期的性能监控数据");
            }
        } catch (Exception e) {
            logger.error("清理性能监控数据异常: {}", e.getMessage(), e);
        }
    }
}