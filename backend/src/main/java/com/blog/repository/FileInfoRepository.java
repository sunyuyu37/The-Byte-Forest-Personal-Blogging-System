package com.blog.repository;

import com.blog.entity.FileInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    
    /**
     * 根据MD5哈希查找文件
     */
    Optional<FileInfo> findByMd5Hash(String md5Hash);
    
    /**
     * 根据上传用户ID查找文件
     */
    @Query("SELECT f FROM FileInfo f WHERE f.uploadUserId = :userId")
    Page<FileInfo> findByUploadUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 根据文件类型查找文件
     */
    @Query("SELECT f FROM FileInfo f WHERE f.fileType = :fileType")
    Page<FileInfo> findByFileType(@Param("fileType") String fileType, Pageable pageable);
    
    /**
     * 查找所有文件
     */
    @Query("SELECT f FROM FileInfo f")
    Page<FileInfo> findAllActive(Pageable pageable);
    
    /**
     * 根据文件名模糊搜索
     */
    @Query("SELECT f FROM FileInfo f WHERE f.originalName LIKE %:keyword%")
    Page<FileInfo> searchByFileName(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 统计用户上传的文件数量
     */
    @Query("SELECT COUNT(f) FROM FileInfo f WHERE f.uploadUserId = :userId")
    long countByUploadUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户上传的文件总大小
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileInfo f WHERE f.uploadUserId = :userId")
    long sumFileSizeByUploadUserId(@Param("userId") Long userId);
    
    /**
     * 查找图片文件
     */
    @Query("SELECT f FROM FileInfo f WHERE f.mimeType LIKE 'image/%'")
    Page<FileInfo> findImageFiles(Pageable pageable);
    
    /**
     * 根据用户ID查找图片文件
     */
    @Query("SELECT f FROM FileInfo f WHERE f.uploadUserId = :userId AND f.mimeType LIKE 'image/%'")
    List<FileInfo> findImageFilesByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID查找视频文件
     */
    @Query("SELECT f FROM FileInfo f WHERE f.uploadUserId = :userId AND f.mimeType LIKE 'video/%'")
    List<FileInfo> findVideoFilesByUserId(@Param("userId") Long userId);
}