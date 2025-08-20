package com.blog.service;

import com.blog.dto.FileUploadResponse;
import com.blog.entity.FileInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface FileService {
    
    /**
     * 上传文件
     */
    FileUploadResponse uploadFile(MultipartFile file, Long userId);
    
    /**
     * 上传头像
     */
    FileUploadResponse uploadAvatar(MultipartFile file, Long userId);
    
    /**
     * 根据ID获取文件信息
     */
    Optional<FileInfo> getFileById(Long id);
    
    /**
     * 根据MD5获取文件信息
     */
    Optional<FileInfo> getFileByMd5(String md5Hash);
    
    /**
     * 获取用户的文件列表
     */
    Page<FileInfo> getUserFiles(Long userId, Pageable pageable);
    
    /**
     * 获取用户的图片文件
     */
    List<FileInfo> getUserImageFiles(Long userId);
    
    /**
     * 搜索文件
     */
    Page<FileInfo> searchFiles(String keyword, Pageable pageable);
    
    /**
     * 删除文件
     */
    void deleteFile(Long id, Long userId);
    
    /**
     * 获取文件统计信息
     */
    FileStats getFileStats(Long userId);
    
    /**
     * 验证文件类型是否为图片
     */
    boolean isImageFile(String mimeType);
    
    /**
     * 验证文件大小
     */
    boolean isValidFileSize(long fileSize, long maxSize);
    
    /**
     * 生成文件的MD5哈希
     */
    String generateMd5Hash(MultipartFile file);
    
    /**
     * 文件统计信息
     */
    class FileStats {
        private long totalFiles;
        private long totalSize;
        private long imageFiles;
        private long videoFiles;
        
        public FileStats(long totalFiles, long totalSize, long imageFiles, long videoFiles) {
            this.totalFiles = totalFiles;
            this.totalSize = totalSize;
            this.imageFiles = imageFiles;
            this.videoFiles = videoFiles;
        }
        
        // Getters
        public long getTotalFiles() { return totalFiles; }
        public long getTotalSize() { return totalSize; }
        public long getImageFiles() { return imageFiles; }
        public long getVideoFiles() { return videoFiles; }
    }
}