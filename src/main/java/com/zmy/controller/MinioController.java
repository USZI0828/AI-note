package com.zmy.controller;

import com.zmy.common.Result;
import com.zmy.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/minio")
@Tag(name = "MinIO文件上传控制器")
public class MinioController {

    @Autowired
    private MinioService minioService;

    @Operation(summary = "上传图片")
    @PostMapping("/upload")
    public Result<?> uploadImage(@RequestParam("file") MultipartFile file) {
        return minioService.uploadImage(file);
    }

    @Operation(summary = "删除图片")
    @DeleteMapping("/delete")
    public Result<?> deleteImage(@RequestParam("objectName") String objectName) {
        return minioService.deleteImage(objectName);
    }
}

