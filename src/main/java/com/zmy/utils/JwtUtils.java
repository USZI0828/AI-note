package com.zmy.utils;

import com.zmy.service.SecretService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    public static String createJwt(String key, StringRedisTemplate redisTemplate, SecretService service) {
        String secret = getSecret(redisTemplate,service);
        //设置加密算法
        Map<String,Object> header = new HashMap<>();
        header.put("alg","HS256");
        header.put("typ","JWT");
        JWTCreator.Builder builder = JWT.create();
        return builder.withHeader(header).withClaim("key",key).sign(Algorithm.HMAC256(secret));
    }

    public static String parseJwt(String token, StringRedisTemplate redisTemplate, SecretService service){
        String secret = getSecret(redisTemplate,service);
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        System.out.println(decodedJWT.getClaim("key"));
        return decodedJWT.getClaim("key").asString();
    }

    private static String getSecret(StringRedisTemplate redisTemplate, SecretService service) {
        String secret = redisTemplate.opsForValue().get("key_secret");
        if (secret == null) {
            secret = service.getById(1).getSecret();
            redisTemplate.opsForValue().set("key_secret",secret);
        }
        return secret;
    }
}
