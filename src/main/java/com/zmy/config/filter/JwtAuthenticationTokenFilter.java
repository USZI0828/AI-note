package com.zmy.config.filter;

import com.zmy.pojo.entity.User;
import com.zmy.service.SecretService;
import com.zmy.utils.JsonUtil;
import com.zmy.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter{

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SecretService service;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("Authentication");
        //解析token
        if(token == null) {
            doFilter(request,response,filterChain);
            return;
        }
        String key = JwtUtils.parseJwt(token,stringRedisTemplate,service);
        String userInfo = stringRedisTemplate.opsForValue().get(key);
        if (userInfo == null) {
            //抛出用户未登录异常
        }
        User user = JsonUtil.deserialize(userInfo, User.class);
        //将用户信息存入authenticationToken中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
        //设置上下文
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        doFilter(request,response,filterChain);
    }
}