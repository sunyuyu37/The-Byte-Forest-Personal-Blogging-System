package com.blog.exception;

/**
 * 文件相关业务异常
 */
public class FileException extends BusinessException {
    
    public FileException(String message) {
        super(message);
    }
    
    public FileException(Integer code, String message) {
        super(code, message);
    }
    
    // 文件不存在
    public static class FileNotFoundException extends FileException {
        public FileNotFoundException() {
            super(404, "文件不存在");
        }
        
        public FileNotFoundException(String message) {
            super(404, message);
        }
    }
    
    // 文件上传失败
    public static class FileUploadException extends FileException {
        public FileUploadException() {
            super(500, "文件上传失败");
        }
        
        public FileUploadException(String message) {
            super(500, message);
        }
    }
    
    // 文件类型不支持
    public static class UnsupportedFileTypeException extends FileException {
        public UnsupportedFileTypeException() {
            super(400, "不支持的文件类型");
        }
        
        public UnsupportedFileTypeException(String message) {
            super(400, message);
        }
    }
    
    // 文件大小超限
    public static class FileSizeExceededException extends FileException {
        public FileSizeExceededException() {
            super(413, "文件大小超出限制");
        }
        
        public FileSizeExceededException(String message) {
            super(413, message);
        }
    }
    
    // 文件删除失败
    public static class FileDeleteException extends FileException {
        public FileDeleteException() {
            super(500, "文件删除失败");
        }
        
        public FileDeleteException(String message) {
            super(500, message);
        }
    }
}