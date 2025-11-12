package com.zmy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zmy.mapper.SecretMapper;
import com.zmy.pojo.entity.Secret;
import com.zmy.service.SecretService;
import org.springframework.stereotype.Service;

@Service
public class SecretServiceImpl extends ServiceImpl<SecretMapper, Secret>
        implements SecretService {

}