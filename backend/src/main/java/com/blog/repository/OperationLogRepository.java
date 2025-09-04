package com.blog.repository;

import com.blog.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志Repository接口
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    
    /**
     * 根据用户ID查询操作日志
     */
    Page<OperationLog> findByUserIdOrderByOperationTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 根据模块查询操作日志
     */
    Page<OperationLog> findByModuleOrderByOperationTimeDesc(String module, Pageable pageable);
    
    /**
     * 根据操作类型查询操作日志
     */
    Page<OperationLog> findByOperationTypeOrderByOperationTimeDesc(OperationLog.OperationType operationType, Pageable pageable);
    
    /**
     * 根据操作状态查询操作日志
     */
    Page<OperationLog> findByStatusOrderByOperationTimeDesc(OperationLog.OperationStatus status, Pageable pageable);
    
    /**
     * 根据时间范围查询操作日志
     */
    @Query("SELECT ol FROM OperationLog ol WHERE ol.operationTime BETWEEN :startTime AND :endTime ORDER BY ol.operationTime DESC")
    Page<OperationLog> findByOperationTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime, 
                                                  Pageable pageable);
    
    /**
     * 根据用户ID和时间范围查询操作日志
     */
    @Query("SELECT ol FROM OperationLog ol WHERE ol.userId = :userId AND ol.operationTime BETWEEN :startTime AND :endTime ORDER BY ol.operationTime DESC")
    Page<OperationLog> findByUserIdAndOperationTimeBetween(@Param("userId") Long userId,
                                                           @Param("startTime") LocalDateTime startTime,
                                                           @Param("endTime") LocalDateTime endTime,
                                                           Pageable pageable);
    
    /**
     * 根据IP地址查询操作日志
     */
    Page<OperationLog> findByIpAddressOrderByOperationTimeDesc(String ipAddress, Pageable pageable);
    
    /**
     * 多条件查询操作日志
     */
    @Query("SELECT ol FROM OperationLog ol WHERE " +
           "(:userId IS NULL OR ol.userId = :userId) AND " +
           "(:module IS NULL OR ol.module = :module) AND " +
           "(:operationType IS NULL OR ol.operationType = :operationType) AND " +
           "(:status IS NULL OR ol.status = :status) AND " +
           "(:startTime IS NULL OR ol.operationTime >= :startTime) AND " +
           "(:endTime IS NULL OR ol.operationTime <= :endTime) " +
           "ORDER BY ol.operationTime DESC")
    Page<OperationLog> findByMultipleConditions(@Param("userId") Long userId,
                                                @Param("module") String module,
                                                @Param("operationType") OperationLog.OperationType operationType,
                                                @Param("status") OperationLog.OperationStatus status,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime,
                                                Pageable pageable);
    
    /**
     * 统计指定时间范围内的操作次数
     */
    @Query("SELECT COUNT(ol) FROM OperationLog ol WHERE ol.operationTime BETWEEN :startTime AND :endTime")
    Long countByOperationTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                     @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计指定用户在指定时间范围内的操作次数
     */
    @Query("SELECT COUNT(ol) FROM OperationLog ol WHERE ol.userId = :userId AND ol.operationTime BETWEEN :startTime AND :endTime")
    Long countByUserIdAndOperationTimeBetween(@Param("userId") Long userId,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计各模块的操作次数
     */
    @Query("SELECT ol.module, COUNT(ol) FROM OperationLog ol WHERE ol.operationTime BETWEEN :startTime AND :endTime GROUP BY ol.module")
    List<Object[]> countByModuleAndOperationTimeBetween(@Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计各操作类型的次数
     */
    @Query("SELECT ol.operationType, COUNT(ol) FROM OperationLog ol WHERE ol.operationTime BETWEEN :startTime AND :endTime GROUP BY ol.operationType")
    List<Object[]> countByOperationTypeAndOperationTimeBetween(@Param("startTime") LocalDateTime startTime,
                                                               @Param("endTime") LocalDateTime endTime);
    
    /**
     * 删除指定时间之前的日志记录
     */
    void deleteByOperationTimeBefore(LocalDateTime beforeTime);
    
    /**
     * 查询执行时间超过指定阈值的操作日志
     */
    @Query("SELECT ol FROM OperationLog ol WHERE ol.executionTime > :threshold ORDER BY ol.executionTime DESC")
    List<OperationLog> findSlowOperations(@Param("threshold") Long threshold, Pageable pageable);
    
    /**
     * 查询失败的操作日志
     */
    @Query("SELECT ol FROM OperationLog ol WHERE ol.status IN ('FAILURE', 'ERROR') ORDER BY ol.operationTime DESC")
    Page<OperationLog> findFailedOperations(Pageable pageable);
}