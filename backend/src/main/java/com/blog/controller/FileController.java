package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.FileUploadResponse;
import com.blog.entity.FileInfo;
import com.blog.service.FileService;
import com.blog.service.UserService;
import com.blog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/files")
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;
    
    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public Result<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {
        System.out.println("=== FileController.uploadFile 方法被调用 ===");
        System.out.println("文件名: " + (file != null ? file.getOriginalFilename() : "null"));
        System.out.println("文件大小: " + (file != null ? file.getSize() : "null"));
        System.out.println("Token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        try {
            System.out.println("=== 文件上传开始 ===");
            System.out.println("文件名: " + file.getOriginalFilename());
            System.out.println("文件大小: " + file.getSize());
            System.out.println("文件类型: " + file.getContentType());
            
            Long userId = getUserIdFromToken(token);
            System.out.println("用户ID: " + userId);
            
            FileUploadResponse response = fileService.uploadFile(file, userId);
            System.out.println("文件上传成功: " + response.getFileUrl());
            return Result.success(response);
        } catch (Exception e) {
            System.err.println("文件上传失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传头像
     */
    @PostMapping("/upload/avatar")
    public Result<FileUploadResponse> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long userId = getUserIdFromToken(token);
            FileUploadResponse response = fileService.uploadAvatar(file, userId);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取文件列表
     */
    @GetMapping("/list")
    public Result<Page<FileInfo>> getFileList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long userId = getUserIdFromToken(token);
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<FileInfo> files = fileService.getUserFiles(userId, pageable);
            return Result.success(files);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的图片文件
     */
    @GetMapping("/images")
    public Result<List<FileInfo>> getUserImages(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            List<FileInfo> images = fileService.getUserImageFiles(userId);
            return Result.success(images);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 搜索文件
     */
    @GetMapping("/search")
    public Result<Page<FileInfo>> searchFiles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String token) {
        
        try {
            getUserIdFromToken(token); // 验证token
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<FileInfo> files = fileService.searchFiles(keyword, pageable);
            return Result.success(files);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除文件
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteFile(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long userId = getUserIdFromToken(token);
            fileService.deleteFile(id, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取文件统计信息
     */
    @GetMapping("/stats")
    public Result<FileService.FileStats> getFileStats(@RequestHeader("Authorization") String token) {
        try {
            Long userId = getUserIdFromToken(token);
            FileService.FileStats stats = fileService.getFileStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 下载/访问文件
     */
    @GetMapping("/{year}/{month}/{day}/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String year,
            @PathVariable String month,
            @PathVariable String day,
            @PathVariable String filename) {
        
        try {
            // 构建文件路径
            String filePath = uploadPath + "files/" + year + "/" + month + "/" + day + "/" + filename;
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            // 确定内容类型
            String contentType = determineContentType(filename);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
                    
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取文件信息
     */
    @GetMapping("/info/{id}")
    public Result<FileInfo> getFileInfo(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        try {
            getUserIdFromToken(token); // 验证token
            Optional<FileInfo> fileInfo = fileService.getFileById(id);
            if (fileInfo.isPresent()) {
                return Result.success(fileInfo.get());
            } else {
                return Result.error("文件不存在");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    private Long getUserIdFromToken(String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                throw new RuntimeException("无效的token");
            }
            
            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            
            if (username == null || !jwtUtil.validateToken(jwt, username)) {
                throw new RuntimeException("token已过期或无效");
            }
            
            Long userId = userService.getUserIdByUsername(username);
            if (userId == null) {
                throw new RuntimeException("用户不存在");
            }
            
            return userId;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "mp4":
                return "video/mp4";
            case "avi":
                return "video/x-msvideo";
            case "mov":
                return "video/quicktime";
            case "wmv":
                return "video/x-ms-wmv";
            case "webm":
                return "video/webm";
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            default:
                return "application/octet-stream";
        }
    }
}