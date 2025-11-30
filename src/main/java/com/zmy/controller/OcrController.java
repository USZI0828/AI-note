package com.zmy.controller;

import com.zmy.common.Result;
import com.zmy.service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ocr")
@Tag(name = "OCR文字识别控制器")
public class OcrController {

    @Autowired
    private OcrService ocrService;

    @Operation(summary = "从图片中提取文字")
    @PostMapping("/extractText")
    public Result<?> extractText(@RequestParam("file") MultipartFile file) {
        return ocrService.extractTextFromImage(file);
    }

    @Operation(summary = "从图片中提取文字并生成标签建议")
    @PostMapping("/extractTextAndTags")
    public Result<?> extractTextAndTags(@RequestParam("file") MultipartFile file) {
        return ocrService.extractTextAndGenerateTags(file);
    }
}

