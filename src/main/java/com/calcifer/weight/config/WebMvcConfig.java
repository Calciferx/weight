package com.calcifer.weight.config;

import com.calcifer.weight.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 设定规则：只有被 @RestController 注解的类，其路径才会自动加上 /api 前缀
        // 例如：原 @RequestMapping("/users") 变为 /api/users
        // 静态资源和普通 @Controller（如果用于返回视图）不受影响
        configurer.addPathPrefix("/api", c -> c.isAnnotationPresent(RestController.class));
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {

/* 单个枚举类转换器
        registry.addConverter(new Converter<String, RecordTypeEnum>() {
            @Override
            public RecordTypeEnum convert(String source) {
                int code = Integer.parseInt(source);
                return CodeEnumUtil.codeOf(RecordTypeEnum.class, code);
            }
        });
*/

        registry.addConverterFactory(new StringToBaseCodeEnumFactory());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new AuthInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login");
    }
}