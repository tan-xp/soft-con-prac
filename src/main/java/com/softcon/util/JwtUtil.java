package com.softcon.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类，用于生成和验证 Token
 */
@Component
public class JwtUtil {
    
    // 密钥，实际项目中应该从配置文件读取
    private static final String SECRET_KEY = "softcon_practice_secret_key_2024";
    
    // Token 过期时间（7天）
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000;
    
    // 获取签名密钥
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    
    /**
     * 生成 Token
     * @param studentId 学生学号
     * @param studentName 学生姓名
     * @return Token 字符串
     */
    public String generateToken(String studentId, String studentName) {
        // 设置 Token 中的自定义声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("studentId", studentId);
        claims.put("studentName", studentName);
        
        // 生成 Token
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 解析 Token
     * @param token Token 字符串
     * @return Claims 对象
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 验证 Token 是否有效
     * @param token Token 字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 从 Token 中获取学生学号
     * @param token Token 字符串
     * @return 学生学号
     */
    public String getStudentIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("studentId", String.class);
    }
    
    /**
     * 从 Token 中获取学生姓名
     * @param token Token 字符串
     * @return 学生姓名
     */
    public String getStudentNameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("studentName", String.class);
    }
}