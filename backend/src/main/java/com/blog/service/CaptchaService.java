package com.blog.service;

import java.util.Map;

/**
 * 验证码服务接口
 */
public interface CaptchaService {
    
    /**
     * 生成验证码
     * @return 包含验证码ID和图片Base64编码的Map
     */
    Map<String, String> generateCaptcha();
    
    /**
     * 验证验证码
     * @param captchaId 验证码ID
     * @param captchaCode 用户输入的验证码
     * @return 验证是否成功
     */
    boolean verifyCaptcha(String captchaId, String captchaCode);
    
    /**
     * 清除指定的验证码
     * @param captchaId 验证码ID
     */
    void clearCaptcha(String captchaId);
    
    /**
     * 清除过期的验证码
     */
    void clearExpiredCaptchas();
}