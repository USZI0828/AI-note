package com.zmy.service;

import com.zmy.common.Result;
import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    Result<?> uploadImage(MultipartFile file);
    
    Result<?> deleteImage(String objectName);
}

