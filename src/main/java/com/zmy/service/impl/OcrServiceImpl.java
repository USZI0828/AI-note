package com.zmy.service.impl;

import com.zmy.common.Result;
import com.zmy.service.OcrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
public class OcrServiceImpl implements OcrService {

    @Value("${ocr.service.url:http://139.9.179.161:5000}")
    private String ocrServiceUrl;

    private final RestTemplate restTemplate;

    public OcrServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Result<?> extractTextFromImage(MultipartFile file) {
        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                return Result.fail(400, "文件不能为空", null);
            }

            // 调用Python服务
            String url = ocrServiceUrl + "/ocr/extract";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>) 
                    (ResponseEntity<?>) restTemplate.postForEntity(url, requestEntity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Integer code = (Integer) responseBody.get("code");
                if (code == 200) {
                    return Result.success(responseBody.get("data"));
                } else {
                    return Result.fail(code, (String) responseBody.get("msg"), null);
                }
            }
            
            return Result.fail(500, "OCR服务调用失败", null);

        } catch (Exception e) {
            log.error("OCR文字提取失败", e);
            return Result.fail(500, "OCR文字提取失败: " + e.getMessage(), null);
        }
    }

    @Override
    public Result<?> extractTextAndGenerateTags(MultipartFile file) {
        try {
            // 验证文件
            if (file == null || file.isEmpty()) {
                return Result.fail(400, "文件不能为空", null);
            }

            // 调用Python服务的一站式接口
            String url = ocrServiceUrl + "/ocr/extract_and_tag";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = (ResponseEntity<Map<String, Object>>) 
                    (ResponseEntity<?>) restTemplate.postForEntity(url, requestEntity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Integer code = (Integer) responseBody.get("code");
                if (code == 200) {
                    return Result.success(responseBody.get("data"));
                } else {
                    return Result.fail(code, (String) responseBody.get("msg"), null);
                }
            }
            
            return Result.fail(500, "OCR服务调用失败", null);

        } catch (Exception e) {
            log.error("OCR文字提取和标签生成失败", e);
            return Result.fail(500, "OCR文字提取和标签生成失败: " + e.getMessage(), null);
        }
    }
}

