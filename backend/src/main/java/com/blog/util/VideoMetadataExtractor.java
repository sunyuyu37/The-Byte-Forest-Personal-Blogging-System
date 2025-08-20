package com.blog.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 视频元数据提取工具类
 * 使用FFprobe命令行工具提取视频文件的元数据信息
 */
@Component
public class VideoMetadataExtractor {
    
    private static final Logger logger = LoggerFactory.getLogger(VideoMetadataExtractor.class);
    
    // FFprobe命令路径（需要系统安装FFmpeg）
    private static final String FFPROBE_COMMAND = "ffprobe";
    
    /**
     * 提取视频元数据
     * @param videoFile 视频文件
     * @return 包含视频元数据的Map
     */
    public Map<String, Object> extractMetadata(File videoFile) {
        Map<String, Object> metadata = new HashMap<>();
        
        try {
            // 构建FFprobe命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                FFPROBE_COMMAND,
                "-v", "quiet",
                "-print_format", "json",
                "-show_format",
                "-show_streams",
                videoFile.getAbsolutePath()
            );
            
            Process process = processBuilder.start();
            
            // 读取命令输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // 解析JSON输出
                metadata = parseFFprobeOutput(output.toString());
            } else {
                logger.warn("FFprobe命令执行失败，退出码: {}", exitCode);
                // 如果FFprobe不可用，使用备用方法
                metadata = extractBasicMetadata(videoFile);
            }
            
        } catch (Exception e) {
            logger.warn("提取视频元数据失败，使用备用方法: {}", e.getMessage());
            // 使用备用方法
            metadata = extractBasicMetadata(videoFile);
        }
        
        return metadata;
    }
    
    /**
     * 解析FFprobe的JSON输出
     */
    private Map<String, Object> parseFFprobeOutput(String jsonOutput) {
        Map<String, Object> metadata = new HashMap<>();
        
        try {
            // 简单的正则表达式解析（在生产环境中建议使用JSON库）
            
            // 提取时长
            Pattern durationPattern = Pattern.compile("\"duration\"\s*:\s*\"([0-9.]+)\"");
            Matcher durationMatcher = durationPattern.matcher(jsonOutput);
            if (durationMatcher.find()) {
                double duration = Double.parseDouble(durationMatcher.group(1));
                metadata.put("duration", (int) Math.round(duration));
            }
            
            // 提取视频流信息
            Pattern widthPattern = Pattern.compile("\"width\"\s*:\s*([0-9]+)");
            Pattern heightPattern = Pattern.compile("\"height\"\s*:\s*([0-9]+)");
            Pattern codecPattern = Pattern.compile("\"codec_name\"\s*:\s*\"([^\"]+)\"");
            Pattern bitratePattern = Pattern.compile("\"bit_rate\"\s*:\s*\"([0-9]+)\"");
            Pattern frameRatePattern = Pattern.compile("\"r_frame_rate\"\s*:\s*\"([0-9]+)/([0-9]+)\"");
            
            Matcher widthMatcher = widthPattern.matcher(jsonOutput);
            if (widthMatcher.find()) {
                metadata.put("width", Integer.parseInt(widthMatcher.group(1)));
            }
            
            Matcher heightMatcher = heightPattern.matcher(jsonOutput);
            if (heightMatcher.find()) {
                metadata.put("height", Integer.parseInt(heightMatcher.group(1)));
            }
            
            Matcher codecMatcher = codecPattern.matcher(jsonOutput);
            if (codecMatcher.find()) {
                metadata.put("codec", codecMatcher.group(1));
            }
            
            Matcher bitrateMatcher = bitratePattern.matcher(jsonOutput);
            if (bitrateMatcher.find()) {
                metadata.put("bitrate", Long.parseLong(bitrateMatcher.group(1)));
            }
            
            Matcher frameRateMatcher = frameRatePattern.matcher(jsonOutput);
            if (frameRateMatcher.find()) {
                int numerator = Integer.parseInt(frameRateMatcher.group(1));
                int denominator = Integer.parseInt(frameRateMatcher.group(2));
                if (denominator != 0) {
                    metadata.put("frameRate", (double) numerator / denominator);
                }
            }
            
        } catch (Exception e) {
            logger.warn("解析FFprobe输出失败: {}", e.getMessage());
        }
        
        return metadata;
    }
    
    /**
     * 备用的基本元数据提取方法
     * 当FFprobe不可用时使用
     */
    private Map<String, Object> extractBasicMetadata(File videoFile) {
        Map<String, Object> metadata = new HashMap<>();
        
        try {
            // 尝试使用Java的基本方法获取一些信息
            // 注意：这种方法获取的信息有限
            
            // 根据文件扩展名推断编码格式
            String fileName = videoFile.getName().toLowerCase();
            if (fileName.endsWith(".mp4")) {
                metadata.put("codec", "h264");
            } else if (fileName.endsWith(".avi")) {
                metadata.put("codec", "xvid");
            } else if (fileName.endsWith(".mov")) {
                metadata.put("codec", "h264");
            } else if (fileName.endsWith(".webm")) {
                metadata.put("codec", "vp8");
            }
            
            // 设置默认值
            metadata.put("duration", 0);
            metadata.put("width", 0);
            metadata.put("height", 0);
            metadata.put("bitrate", 0L);
            metadata.put("frameRate", 0.0);
            
            logger.info("使用备用方法提取视频元数据，信息有限");
            
        } catch (Exception e) {
            logger.error("备用元数据提取方法也失败: {}", e.getMessage());
        }
        
        return metadata;
    }
    
    /**
     * 检查FFprobe是否可用
     */
    public boolean isFFprobeAvailable() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(FFPROBE_COMMAND, "-version");
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 格式化时长为可读字符串
     */
    public String formatDuration(int seconds) {
        if (seconds <= 0) {
            return "未知";
        }
        
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%d:%02d", minutes, secs);
        }
    }
    
    /**
     * 格式化分辨率为可读字符串
     */
    public String formatResolution(Integer width, Integer height) {
        if (width == null || height == null || width <= 0 || height <= 0) {
            return "未知";
        }
        return width + "x" + height;
    }
    
    /**
     * 格式化比特率为可读字符串
     */
    public String formatBitrate(Long bitrate) {
        if (bitrate == null || bitrate <= 0) {
            return "未知";
        }
        
        if (bitrate >= 1000000) {
            return String.format("%.1f Mbps", bitrate / 1000000.0);
        } else if (bitrate >= 1000) {
            return String.format("%.1f Kbps", bitrate / 1000.0);
        } else {
            return bitrate + " bps";
        }
    }
}