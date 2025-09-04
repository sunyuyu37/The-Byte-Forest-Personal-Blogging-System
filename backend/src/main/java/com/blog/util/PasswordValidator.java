package com.blog.util;

import java.util.regex.Pattern;

/**
 * 密码验证工具类
 * 提供密码复杂度验证功能
 */
public class PasswordValidator {
    
    // 密码最小长度
    private static final int MIN_LENGTH = 8;
    
    // 密码最大长度
    private static final int MAX_LENGTH = 128;
    
    // 至少包含一个小写字母
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    
    // 至少包含一个大写字母
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    
    // 至少包含一个数字
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    
    // 至少包含一个特殊字符
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':,.<>?].*");
    
    // 不能包含连续的相同字符（超过2个）
    private static final Pattern CONSECUTIVE_CHARS_PATTERN = Pattern.compile(".*(.)\\1{2,}.*");
    
    // 常见弱密码列表
    private static final String[] WEAK_PASSWORDS = {
        "password", "123456", "123456789", "qwerty", "abc123", 
        "password123", "admin", "root", "user", "guest",
        "12345678", "qwerty123", "password1", "123123", "111111"
    };
    
    /**
     * 验证密码强度
     * @param password 待验证的密码
     * @return 验证结果
     */
    public static PasswordValidationResult validatePassword(String password) {
        PasswordValidationResult result = new PasswordValidationResult();
        
        if (password == null || password.trim().isEmpty()) {
            result.setValid(false);
            result.addError("密码不能为空");
            return result;
        }
        
        // 检查长度
        if (password.length() < MIN_LENGTH) {
            result.addError("密码长度至少" + MIN_LENGTH + "位");
        }
        
        if (password.length() > MAX_LENGTH) {
            result.addError("密码长度不能超过" + MAX_LENGTH + "位");
        }
        
        // 检查字符类型
        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            result.addError("密码必须包含至少一个小写字母");
        }
        
        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            result.addError("密码必须包含至少一个大写字母");
        }
        
        if (!DIGIT_PATTERN.matcher(password).matches()) {
            result.addError("密码必须包含至少一个数字");
        }
        
        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            result.addError("密码必须包含至少一个特殊字符 (!@#$%^&*()_+-=[]{};':,.<>?)");
        }
        
        // 检查连续相同字符
        if (CONSECUTIVE_CHARS_PATTERN.matcher(password).matches()) {
            result.addError("密码不能包含连续3个或以上相同字符");
        }
        
        // 检查是否为常见弱密码
        String lowerPassword = password.toLowerCase();
        for (String weakPassword : WEAK_PASSWORDS) {
            if (lowerPassword.contains(weakPassword)) {
                result.addError("密码不能包含常见弱密码模式");
                break;
            }
        }
        
        // 检查是否包含连续数字或字母
        if (containsSequentialChars(password)) {
            result.addError("密码不能包含连续的数字或字母序列（如123、abc）");
        }
        
        result.setValid(result.getErrors().isEmpty());
        return result;
    }
    
    /**
     * 检查密码强度等级
     * @param password 密码
     * @return 强度等级（WEAK, MEDIUM, STRONG）
     */
    public static PasswordStrength getPasswordStrength(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return PasswordStrength.WEAK;
        }
        
        int score = 0;
        
        // 长度评分
        if (password.length() >= 12) score += 2;
        else if (password.length() >= 10) score += 1;
        
        // 字符类型评分
        if (LOWERCASE_PATTERN.matcher(password).matches()) score += 1;
        if (UPPERCASE_PATTERN.matcher(password).matches()) score += 1;
        if (DIGIT_PATTERN.matcher(password).matches()) score += 1;
        if (SPECIAL_CHAR_PATTERN.matcher(password).matches()) score += 2;
        
        // 复杂度评分
        if (!CONSECUTIVE_CHARS_PATTERN.matcher(password).matches()) score += 1;
        if (!containsSequentialChars(password)) score += 1;
        
        if (score >= 7) return PasswordStrength.STRONG;
        if (score >= 4) return PasswordStrength.MEDIUM;
        return PasswordStrength.WEAK;
    }
    
    /**
     * 检查是否包含连续字符序列
     */
    private static boolean containsSequentialChars(String password) {
        String lowerPassword = password.toLowerCase();
        
        // 检查连续数字
        for (int i = 0; i < lowerPassword.length() - 2; i++) {
            char c1 = lowerPassword.charAt(i);
            char c2 = lowerPassword.charAt(i + 1);
            char c3 = lowerPassword.charAt(i + 2);
            
            if (Character.isDigit(c1) && Character.isDigit(c2) && Character.isDigit(c3)) {
                if (c2 == c1 + 1 && c3 == c2 + 1) {
                    return true;
                }
            }
            
            if (Character.isLetter(c1) && Character.isLetter(c2) && Character.isLetter(c3)) {
                if (c2 == c1 + 1 && c3 == c2 + 1) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * 密码强度枚举
     */
    public enum PasswordStrength {
        WEAK("弱"),
        MEDIUM("中等"),
        STRONG("强");
        
        private final String description;
        
        PasswordStrength(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}