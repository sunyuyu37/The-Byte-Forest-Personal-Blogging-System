package com.blog.service.impl;

import com.blog.service.CaptchaService;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 验证码服务实现类
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {
    
    private static final int CAPTCHA_WIDTH = 120;
    private static final int CAPTCHA_HEIGHT = 40;
    private static final int CAPTCHA_LENGTH = 4;
    private static final int EXPIRE_MINUTES = 5; // 验证码5分钟过期
    
    // 验证码字符集（去除容易混淆的字符）
    private static final String CAPTCHA_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    
    // 内存存储验证码（生产环境建议使用Redis）
    private final Map<String, CaptchaInfo> captchaStore = new ConcurrentHashMap<>();
    
    @Override
    public Map<String, String> generateCaptcha() {
        String captchaId = UUID.randomUUID().toString();
        String captchaCode = generateRandomCode();
        
        // 生成验证码图片
        String imageBase64 = generateCaptchaImage(captchaCode);
        
        // 存储验证码信息
        captchaStore.put(captchaId, new CaptchaInfo(captchaCode, LocalDateTime.now()));
        
        Map<String, String> result = new HashMap<>();
        result.put("captchaId", captchaId);
        result.put("captchaImage", "data:image/png;base64," + imageBase64);
        
        return result;
    }
    
    @Override
    public boolean verifyCaptcha(String captchaId, String captchaCode) {
        if (captchaId == null || captchaCode == null) {
            return false;
        }
        
        CaptchaInfo captchaInfo = captchaStore.get(captchaId);
        if (captchaInfo == null) {
            return false;
        }
        
        // 检查是否过期
        if (captchaInfo.getCreateTime().plusMinutes(EXPIRE_MINUTES).isBefore(LocalDateTime.now())) {
            captchaStore.remove(captchaId);
            return false;
        }
        
        // 忽略大小写比较
        boolean isValid = captchaInfo.getCode().equalsIgnoreCase(captchaCode);
        
        // 只有验证成功时才删除验证码（一次性使用）
        if (isValid) {
            captchaStore.remove(captchaId);
        }
        
        return isValid;
    }
    
    @Override
    public void clearCaptcha(String captchaId) {
        if (captchaId != null) {
            captchaStore.remove(captchaId);
        }
    }
    
    @Override
    @Scheduled(fixedRate = 300000) // 每5分钟清理一次过期验证码
    public void clearExpiredCaptchas() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(EXPIRE_MINUTES);
        captchaStore.entrySet().removeIf(entry -> 
            entry.getValue().getCreateTime().isBefore(expireTime)
        );
    }
    
    /**
     * 生成随机验证码
     */
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            code.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return code.toString();
    }
    
    /**
     * 生成验证码图片
     */
    private String generateCaptchaImage(String code) {
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 填充背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        
        // 绘制干扰线
        for (int i = 0; i < 5; i++) {
            g2d.setColor(getRandomColor(150, 200, random));
            int x1 = random.nextInt(CAPTCHA_WIDTH);
            int y1 = random.nextInt(CAPTCHA_HEIGHT);
            int x2 = random.nextInt(CAPTCHA_WIDTH);
            int y2 = random.nextInt(CAPTCHA_HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // 绘制验证码字符
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        for (int i = 0; i < code.length(); i++) {
            g2d.setColor(getRandomColor(50, 150, random));
            int x = 20 + i * 20 + random.nextInt(10);
            int y = 25 + random.nextInt(10);
            
            // 随机旋转字符
            double angle = (random.nextDouble() - 0.5) * 0.5;
            g2d.rotate(angle, x, y);
            g2d.drawString(String.valueOf(code.charAt(i)), x, y);
            g2d.rotate(-angle, x, y);
        }
        
        // 添加噪点
        for (int i = 0; i < 50; i++) {
            g2d.setColor(getRandomColor(100, 200, random));
            int x = random.nextInt(CAPTCHA_WIDTH);
            int y = random.nextInt(CAPTCHA_HEIGHT);
            g2d.fillOval(x, y, 1, 1);
        }
        
        g2d.dispose();
        
        // 转换为Base64
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }
    
    /**
     * 获取随机颜色
     */
    private Color getRandomColor(int min, int max, ThreadLocalRandom random) {
        int r = random.nextInt(max - min) + min;
        int g = random.nextInt(max - min) + min;
        int b = random.nextInt(max - min) + min;
        return new Color(r, g, b);
    }
    
    /**
     * 验证码信息内部类
     */
    private static class CaptchaInfo {
        private final String code;
        private final LocalDateTime createTime;
        
        public CaptchaInfo(String code, LocalDateTime createTime) {
            this.code = code;
            this.createTime = createTime;
        }
        
        public String getCode() {
            return code;
        }
        
        public LocalDateTime getCreateTime() {
            return createTime;
        }
    }
}