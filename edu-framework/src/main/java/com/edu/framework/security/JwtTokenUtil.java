package com.edu.framework.security;

import cn.hutool.core.util.IdUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT Token 工具类
 */
@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret:edu-admin-secret-key-must-be-at-least-256-bits-long}")
    private String secret;

    @Value("${jwt.expiration:86400}")
    private Long expiration; // 秒

    @Value("${jwt.header:Authorization}")
    private String header;

    @Value("${jwt.prefix:Bearer }")
    private String prefix;

    private SecretKey secretKey;

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_CACHE_PREFIX = "auth:token:";

    public JwtTokenUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 创建 Token
     */
    public String createToken(LoginUser loginUser) {
        String uuid = IdUtil.fastSimpleUUID();
        loginUser.setToken(uuid);
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(System.currentTimeMillis() + expiration * 1000);

        // 存入 Redis
        String cacheKey = TOKEN_CACHE_PREFIX + uuid;
        redisTemplate.opsForValue().set(cacheKey, loginUser, expiration, TimeUnit.SECONDS);

        // 生成 JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("uuid", uuid);
        claims.put("userId", loginUser.getUserId());
        claims.put("username", loginUser.getUsername());

        return Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 从请求中获取 Token
     */
    public String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(header);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(prefix)) {
            return bearerToken.substring(prefix.length());
        }
        return null;
    }

    /**
     * 解析 Token 获取登录用户
     */
    public LoginUser getLoginUser(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String uuid = (String) claims.get("uuid");
            String cacheKey = TOKEN_CACHE_PREFIX + uuid;
            return (LoginUser) redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.error("Token 解析失败", e);
            return null;
        }
    }

    /**
     * 刷新 Token 有效期
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setExpireTime(System.currentTimeMillis() + expiration * 1000);
        String cacheKey = TOKEN_CACHE_PREFIX + loginUser.getToken();
        redisTemplate.opsForValue().set(cacheKey, loginUser, expiration, TimeUnit.SECONDS);
    }

    /**
     * 删除 Token
     */
    public void deleteToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String uuid = (String) claims.get("uuid");
            String cacheKey = TOKEN_CACHE_PREFIX + uuid;
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.error("删除 Token 失败", e);
        }
    }

    /**
     * 验证 Token 是否有效
     */
    public boolean validateToken(String token) {
        LoginUser loginUser = getLoginUser(token);
        return loginUser != null && loginUser.getExpireTime() > System.currentTimeMillis();
    }
}
