package com.softcon.interceptor;

import com.softcon.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 拦截器，用于验证学生端的 Token
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的 Authorization
        String token = request.getHeader("Authorization");
        
        // 如果是学生端的 API 路径，需要验证 Token
        if (request.getRequestURI().startsWith("/student/") && 
            !request.getRequestURI().equals("/student/login")) {
            
            // 检查 Token 是否存在
            if (token == null || token.isEmpty()) {
                sendError(response, "未提供认证 Token");
                return false;
            }
            
            // 检查 Token 格式
            if (!token.startsWith("Bearer ")) {
                sendError(response, "Token 格式错误");
                return false;
            }
            
            // 提取 Token
            token = token.substring(7);
            
            // 验证 Token
            if (!jwtUtil.validateToken(token)) {
                sendError(response, "Token 无效或已过期");
                return false;
            }
            
            // 将学生信息存入请求属性中，供后续使用
            String studentId = jwtUtil.getStudentIdFromToken(token);
            String studentName = jwtUtil.getStudentNameFromToken(token);
            request.setAttribute("studentId", studentId);
            request.setAttribute("studentName", studentName);
        }
        
        return true;
    }
    
    /**
     * 发送错误响应
     */
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\",\"data\":null}");
    }
}