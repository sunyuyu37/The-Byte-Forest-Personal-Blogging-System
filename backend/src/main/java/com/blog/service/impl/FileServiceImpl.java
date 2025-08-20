package com.blog.service.impl;

import com.blog.dto.FileUploadResponse;
import com.blog.entity.FileInfo;
import com.blog.repository.FileInfoRepository;
import com.blog.service.FileService;
import com.blog.service.UserService;
import com.blog.util.VideoMetadataExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FileServiceImpl implements FileService {
    
    @Autowired
    private FileInfoRepository fileInfoRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private VideoMetadataExtractor videoMetadataExtractor;
    
    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;
    
    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize;
    
    // 视频文件最大大小 (1GB)
    private static final long MAX_VIDEO_SIZE = 1073741824L;
    
    @Value("${server.servlet.context-path:/api}")
    private String contextPath;
    
    private static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };
    
    @Override
    public FileUploadResponse uploadFile(MultipartFile file, Long userId) {
        System.out.println("=== 开始文件上传处理 ===");
        validateFile(file);
        
        try {
            System.out.println("开始计算MD5哈希...");
            // 检查是否已存在相同文件
            String md5Hash = generateMd5Hash(file);
            System.out.println("MD5哈希计算完成: " + md5Hash);
            
            System.out.println("检查文件是否已存在...");
            Optional<FileInfo> existingFile = fileInfoRepository.findByMd5Hash(md5Hash);
            if (existingFile.isPresent()) {
                System.out.println("文件已存在，返回现有文件信息");
                return new FileUploadResponse(existingFile.get());
            }
            System.out.println("文件不存在，继续上传流程");
            
            // 生成文件名和路径
            String originalName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalName);
            String fileName = UUID.randomUUID().toString() + fileExtension;
            System.out.println("生成的文件名: " + fileName);
            
            // 按日期创建目录
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = "files/" + dateDir + "/" + fileName;
            String fullPath = uploadPath + relativePath;
            System.out.println("文件保存路径: " + fullPath);
            
            // 创建目录
            System.out.println("创建目录...");
            Path targetPath = Paths.get(fullPath);
            Files.createDirectories(targetPath.getParent());
            System.out.println("目录创建完成");
            
            // 保存文件
            System.out.println("开始保存文件到磁盘...");
            file.transferTo(targetPath.toFile());
            System.out.println("文件保存到磁盘完成");
            
            // 生成访问URL
            String fileUrl = contextPath + "/files/" + dateDir + "/" + fileName;
            System.out.println("生成的访问URL: " + fileUrl);
            
            // 保存文件信息到数据库
            System.out.println("开始保存文件信息到数据库...");
            FileInfo fileInfo = new FileInfo(
                originalName,
                fileName,
                fullPath,
                fileUrl,
                file.getSize(),
                getFileType(originalName),
                file.getContentType(),
                md5Hash,
                userId
            );
            
            // 如果是视频文件，提取元数据
            if (isVideoFile(file.getContentType())) {
                System.out.println("检测到视频文件，开始提取元数据...");
                try {
                    Map<String, Object> metadata = videoMetadataExtractor.extractMetadata(targetPath.toFile());
                    
                    // 设置视频元数据
                    if (metadata.containsKey("duration")) {
                        fileInfo.setVideoDuration((Integer) metadata.get("duration"));
                    }
                    if (metadata.containsKey("width")) {
                        fileInfo.setVideoWidth((Integer) metadata.get("width"));
                    }
                    if (metadata.containsKey("height")) {
                        fileInfo.setVideoHeight((Integer) metadata.get("height"));
                    }
                    if (metadata.containsKey("codec")) {
                        fileInfo.setVideoCodec((String) metadata.get("codec"));
                    }
                    if (metadata.containsKey("bitrate")) {
                        fileInfo.setVideoBitrate((Long) metadata.get("bitrate"));
                    }
                    if (metadata.containsKey("frameRate")) {
                        fileInfo.setVideoFrameRate((Double) metadata.get("frameRate"));
                    }
                    
                    System.out.println("视频元数据提取完成: " + metadata);
                } catch (Exception e) {
                    System.err.println("视频元数据提取失败: " + e.getMessage());
                    // 元数据提取失败不影响文件上传
                }
            }
            
            FileInfo savedFile = fileInfoRepository.save(fileInfo);
            System.out.println("文件信息保存到数据库完成，ID: " + savedFile.getId());
            System.out.println("=== 文件上传处理完成 ===");
            return new FileUploadResponse(savedFile);
            
        } catch (Exception e) {
            System.err.println("文件上传过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public FileUploadResponse uploadAvatar(MultipartFile file, Long userId) {
        // 验证是否为图片文件
        if (!isImageFile(file.getContentType())) {
            throw new RuntimeException("头像必须是图片文件");
        }
        
        // 上传文件
        FileUploadResponse response = uploadFile(file, userId);
        
        // 更新用户头像
        userService.updateAvatar(userId, response.getFileUrl());
        
        return response;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<FileInfo> getFileById(Long id) {
        return fileInfoRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<FileInfo> getFileByMd5(String md5Hash) {
        return fileInfoRepository.findByMd5Hash(md5Hash);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FileInfo> getUserFiles(Long userId, Pageable pageable) {
        return fileInfoRepository.findByUploadUserId(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FileInfo> getUserImageFiles(Long userId) {
        return fileInfoRepository.findImageFilesByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<FileInfo> searchFiles(String keyword, Pageable pageable) {
        return fileInfoRepository.searchByFileName(keyword, pageable);
    }
    
    @Override
    public void deleteFile(Long id, Long userId) {
        FileInfo fileInfo = fileInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文件不存在"));
        
        // 检查权限
        if (!fileInfo.getUploadUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此文件");
        }
        
        // 硬删除文件记录
        fileInfoRepository.delete(fileInfo);
        
        // 物理删除文件
        try {
            Files.deleteIfExists(Paths.get(fileInfo.getFilePath()));
        } catch (IOException e) {
            // 记录日志，但不抛出异常
            System.err.println("删除物理文件失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public FileStats getFileStats(Long userId) {
        long totalFiles = fileInfoRepository.countByUploadUserId(userId);
        long totalSize = fileInfoRepository.sumFileSizeByUploadUserId(userId);
        long imageFiles = fileInfoRepository.findImageFilesByUserId(userId).size();
        long videoFiles = fileInfoRepository.findVideoFilesByUserId(userId).size();
        
        return new FileStats(totalFiles, totalSize, imageFiles, videoFiles);
    }
    
    @Override
    public boolean isImageFile(String mimeType) {
        if (mimeType == null) return false;
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equals(mimeType.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查是否为视频文件
     */
    public boolean isVideoFile(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.toLowerCase().startsWith("video/");
    }
    
    @Override
    public boolean isValidFileSize(long fileSize, long maxSize) {
        return fileSize > 0 && fileSize <= maxSize;
    }
    
    @Override
    public String generateMd5Hash(MultipartFile file) {
        try {
            System.out.println("开始流式计算MD5哈希...");
            MessageDigest md = MessageDigest.getInstance("MD5");
            
            // 使用流式处理，避免一次性加载整个文件到内存
            try (var inputStream = file.getInputStream()) {
                byte[] buffer = new byte[8192]; // 8KB缓冲区
                int bytesRead;
                long totalBytes = 0;
                
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    md.update(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                    
                    // 每处理50MB输出一次进度
                    if (totalBytes % (50 * 1024 * 1024) == 0) {
                        System.out.println("MD5计算进度: " + (totalBytes / 1024 / 1024) + "MB");
                    }
                }
                
                byte[] hashBytes = md.digest();
                StringBuilder sb = new StringBuilder();
                for (byte b : hashBytes) {
                    sb.append(String.format("%02x", b));
                }
                
                String md5Hash = sb.toString();
                System.out.println("MD5计算完成: " + md5Hash);
                return md5Hash;
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            System.err.println("生成MD5哈希失败: " + e.getMessage());
            throw new RuntimeException("生成MD5哈希失败: " + e.getMessage());
        }
    }
    
    private void validateFile(MultipartFile file) {
        System.out.println("=== 开始文件验证 ===");
        
        if (file == null || file.isEmpty()) {
            System.err.println("文件验证失败: 文件为空");
            throw new RuntimeException("文件不能为空");
        }
        
        String originalName = file.getOriginalFilename();
        System.out.println("原始文件名: " + originalName);
        
        if (originalName == null || originalName.trim().isEmpty()) {
            System.err.println("文件验证失败: 文件名为空");
            throw new RuntimeException("文件名不能为空");
        }
        
        // 检查文件类型并应用相应的大小限制
        String fileType = getFileType(originalName);
        System.out.println("文件类型: " + fileType);
        System.out.println("文件大小: " + file.getSize() + " 字节");
        System.out.println("maxFileSize配置: " + maxFileSize + " 字节");
        System.out.println("MAX_VIDEO_SIZE: " + MAX_VIDEO_SIZE + " 字节");
        
        if ("video".equals(fileType)) {
            System.out.println("检查视频文件大小限制...");
            if (!isValidFileSize(file.getSize(), MAX_VIDEO_SIZE)) {
                System.err.println("视频文件大小验证失败: " + file.getSize() + " > " + MAX_VIDEO_SIZE);
                throw new RuntimeException("视频文件大小超过限制: " + (MAX_VIDEO_SIZE / 1024 / 1024) + "MB");
            }
            System.out.println("视频文件大小验证通过");
        } else {
            System.out.println("检查普通文件大小限制...");
            if (!isValidFileSize(file.getSize(), maxFileSize)) {
                System.err.println("文件大小验证失败: " + file.getSize() + " > " + maxFileSize);
                throw new RuntimeException("文件大小超过限制: " + (maxFileSize / 1024 / 1024) + "MB");
            }
            System.out.println("普通文件大小验证通过");
        }
        
        System.out.println("=== 文件验证完成 ===");
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
    
    private String getFileType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        switch (extension) {
            case ".jpg":
            case ".jpeg":
            case ".png":
            case ".gif":
            case ".webp":
                return "image";
            case ".pdf":
                return "document";
            case ".doc":
            case ".docx":
                return "document";
            case ".xls":
            case ".xlsx":
                return "spreadsheet";
            case ".mp4":
            case ".avi":
            case ".mov":
            case ".wmv":
            case ".webm":
            case ".flv":
            case ".mkv":
                return "video";
            case ".mp3":
            case ".wav":
                return "audio";
            default:
                return "other";
        }
    }
}