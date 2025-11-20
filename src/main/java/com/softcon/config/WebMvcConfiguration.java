package com.softcon.config;

import com.softcon.interceptor.JwtInterceptor;
import com.softcon.json.JacksonObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;


/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtInterceptor jwtInterceptor;

//    /**
//     * 解决跨域问题
//     *
//     * @param registry
//     */
//    @Override
//    protected void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**") // 所有接口
//                .allowCredentials(true) // 是否发送 Cookie
//                .allowedOriginPatterns("*") // 支持域
//                .allowedMethods(new String[]{"GET", "POST", "PUT", "DELETE"}) // 支持方法
//                .allowedHeaders("*")
//                .exposedHeaders("*");
//    }

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

    /**
     * 通过knife4j生成接口文档
     *
     * @return
     */
    @Bean
    public OpenAPI adminDocket() {
        log.info("准备生成接口文档...");
        // 创建接口文档对象
        return new OpenAPI()
                .info(
                        new Info()
                                .title("作业练习系统接口文档") // 标题
                                .version("1.0") // 版本
                                .description("作业练习系统接口文档")
                );
    }

    /**
     * 设置静态资源映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置静态资源映射");
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }

    /**
     * 扩展消息转换器
     *
     * @param converters
     */
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器");
        //创建一个消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //为消息转换器设置一个对象转换器，对象转换器可以将java对象序列化为json数据
        converter.setObjectMapper(new JacksonObjectMapper());
        //将消息转换器加入容器并设置优先级
        converters.add(6, converter);
    }

}
