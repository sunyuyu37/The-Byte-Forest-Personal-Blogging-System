package com.blog.dto;

import com.blog.entity.FileInfo;

public class FileUploadResponse {
    private Long id;
    private String originalName;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileType;
    private String mimeType;
    
    public FileUploadResponse() {}
    
    public FileUploadResponse(FileInfo fileInfo) {
        this.id = fileInfo.getId();
        this.originalName = fileInfo.getOriginalName();
        this.fileName = fileInfo.getFileName();
        this.fileUrl = fileInfo.getFileUrl();
        this.fileSize = fileInfo.getFileSize();
        this.fileType = fileInfo.getFileType();
        this.mimeType = fileInfo.getMimeType();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
}