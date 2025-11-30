package com.zmy.service.impl;

import com.zmy.common.Result;
import com.zmy.config.baseConfig.MinioConfig;
import com.zmy.service.MinioService;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class MinioServiceImpl implements MinioService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    // 支持的图片格式
    private static final String[] ALLOWED_IMAGE_TYPES = {
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp"
    };

    @Override
    public Result<?> uploadImage(MultipartFile file) {
        try {
            // 验证文件是否为空
            if (file == null || file.isEmpty()) {
                return Result.fail(400, "文件不能为空", null);
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !isImageType(contentType)) {
                return Result.fail(400, "不支持的文件类型，仅支持图片格式（jpg、png、gif、bmp、webp）", null);
            }

            // 验证文件大小（限制为10MB）
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxSize) {
                return Result.fail(400, "文件大小不能超过10MB", null);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String objectName = generateObjectName(extension);

            // 上传文件
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );

            // 生成访问URL
            String url = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + objectName;

            // 返回结果
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("url", url);
            result.put("objectName", objectName);
            result.put("originalFilename", originalFilename);
            result.put("size", file.getSize());

            log.info("图片上传成功: {}", url);
            return Result.success(result);

        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("图片上传失败", e);
            return Result.fail(500, "图片上传失败: " + e.getMessage(), null);
        }
    }

    @Override
    public Result<?> deleteImage(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(objectName)
                            .build()
            );
            log.info("图片删除成功: {}", objectName);
            return Result.success("图片删除成功");
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("图片删除失败", e);
            return Result.fail(500, "图片删除失败: " + e.getMessage(), null);
        }
    }

    /**
     * 检查是否为图片类型
     */
    private boolean isImageType(String contentType) {
        for (String allowedType : ALLOWED_IMAGE_TYPES) {
            if (allowedType.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 生成对象名称（文件路径）
     * 格式: images/yyyy/MM/dd/uuid.extension
     */
    private String generateObjectName(String extension) {
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "images/" + datePath + "/" + uuid + extension;
    }
}

