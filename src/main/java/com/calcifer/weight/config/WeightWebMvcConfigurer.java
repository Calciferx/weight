package com.calcifer.weight.config;

import com.calcifer.weight.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WeightWebMvcConfigurer implements WebMvcConfigurer {
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
                .addPathPatterns("/adminx/**")
                .excludePathPatterns("/adminx/login/**");
    }
}
