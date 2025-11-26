package com.zmy.service;

import com.zmy.common.Result;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {
    /**
     * 从图片中提取文字
     */
    Result<?> extractTextFromImage(MultipartFile file);
    
    /**
     * 从图片中提取文字并生成标签建议
     */
    Result<?> extractTextAndGenerateTags(MultipartFile file);
}

