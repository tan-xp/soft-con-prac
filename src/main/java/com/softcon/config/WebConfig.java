package com.softcon.config;

import com.softcon.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类，用于注册拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private JwtInterceptor jwtInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 JWT 拦截器，拦截所有请求
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/student/**")
                // 排除登录接口
                .excludePathPatterns("/student/login","/student/submission/detail/**","/student/list","/student/add","/student/edit/**","/student/update","/student/delete/**","/student/search","/student/resetPassword")
                // 排除静态资源
                .excludePathPatterns("/static/**", "/templates/**");
    }
}