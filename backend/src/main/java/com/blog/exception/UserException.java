package com.blog.exception;

/**
 * 用户相关业务异常
 */
public class UserException extends BusinessException {
    
    public UserException(String message) {
        super(message);
    }
    
    public UserException(Integer code, String message) {
        super(code, message);
    }
    
    // 用户不存在
    public static class UserNotFoundException extends UserException {
        public UserNotFoundException() {
            super(404, "用户不存在");
        }
        
        public UserNotFoundException(String message) {
            super(404, message);
        }
    }
    
    // 用户已存在
    public static class UserAlreadyExistsException extends UserException {
        public UserAlreadyExistsException() {
            super(409, "用户已存在");
        }
        
        public UserAlreadyExistsException(String message) {
            super(409, message);
        }
    }
    
    // 密码错误
    public static class InvalidPasswordException extends UserException {
        public InvalidPasswordException() {
            super(401, "密码错误");
        }
        
        public InvalidPasswordException(String message) {
            super(401, message);
        }
    }
    
    // 用户被禁用
    public static class UserDisabledException extends UserException {
        public UserDisabledException() {
            super(403, "用户已被禁用");
        }
        
        public UserDisabledException(String message) {
            super(403, message);
        }
    }
    
    // 权限不足
    public static class InsufficientPermissionException extends UserException {
        public InsufficientPermissionException() {
            super(403, "权限不足");
        }
        
        public InsufficientPermissionException(String message) {
            super(403, message);
        }
    }
}