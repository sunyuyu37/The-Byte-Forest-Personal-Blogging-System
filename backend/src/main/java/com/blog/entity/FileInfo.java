package com.blog.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_info")
@EntityListeners(AuditingEntityListener.class)
public class FileInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;
    
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;
    
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;
    
    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;
    
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "file_type", nullable = false, length = 100)
    private String fileType;
    
    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;
    
    @Column(name = "md5_hash", nullable = false, length = 32)
    private String md5Hash;
    
    @Column(name = "upload_user_id")
    private Long uploadUserId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false, length = 20)
    private StorageType storageType = StorageType.LOCAL;
    
    @Column(name = "bucket_name", length = 100)
    private String bucketName;
    
    // 视频元数据字段
    @Column(name = "video_duration") // 视频时长（秒）
    private Integer videoDuration;
    
    @Column(name = "video_width") // 视频宽度
    private Integer videoWidth;
    
    @Column(name = "video_height") // 视频高度
    private Integer videoHeight;
    
    @Column(name = "video_codec", length = 50) // 视频编码格式
    private String videoCodec;
    
    @Column(name = "video_bitrate") // 视频比特率
    private Long videoBitrate;
    
    @Column(name = "video_frame_rate") // 帧率
    private Double videoFrameRate;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    
    public enum StorageType {
        LOCAL, MINIO, OSS
    }
    
    // Constructors
    public FileInfo() {}
    
    public FileInfo(String originalName, String fileName, String filePath, String fileUrl,
                   Long fileSize, String fileType, String mimeType, String md5Hash, Long uploadUserId) {
        this.originalName = originalName;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.mimeType = mimeType;
        this.md5Hash = md5Hash;
        this.uploadUserId = uploadUserId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public String getMd5Hash() { return md5Hash; }
    public void setMd5Hash(String md5Hash) { this.md5Hash = md5Hash; }
    
    public Long getUploadUserId() { return uploadUserId; }
    public void setUploadUserId(Long uploadUserId) { this.uploadUserId = uploadUserId; }
    
    public StorageType getStorageType() { return storageType; }
    public void setStorageType(StorageType storageType) { this.storageType = storageType; }
    
    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }
    
    // 视频元数据字段的getter和setter
    public Integer getVideoDuration() { return videoDuration; }
    public void setVideoDuration(Integer videoDuration) { this.videoDuration = videoDuration; }
    
    public Integer getVideoWidth() { return videoWidth; }
    public void setVideoWidth(Integer videoWidth) { this.videoWidth = videoWidth; }
    
    public Integer getVideoHeight() { return videoHeight; }
    public void setVideoHeight(Integer videoHeight) { this.videoHeight = videoHeight; }
    
    public String getVideoCodec() { return videoCodec; }
    public void setVideoCodec(String videoCodec) { this.videoCodec = videoCodec; }
    
    public Long getVideoBitrate() { return videoBitrate; }
    public void setVideoBitrate(Long videoBitrate) { this.videoBitrate = videoBitrate; }
    
    public Double getVideoFrameRate() { return videoFrameRate; }
    public void setVideoFrameRate(Double videoFrameRate) { this.videoFrameRate = videoFrameRate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}