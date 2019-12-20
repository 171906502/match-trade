package com.flying.cattle.exchange.plugins.swagger;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName: DataUtil
 * @Description: TODO(静态资源映射)
 * @author flying-cattle
 * @date 2019年12月19日
 */
@Configuration
public class WebSecurityConfiguration implements WebMvcConfigurer {

    /**
     * swagger资源路径映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html","/**/*.js","/**/*.css")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
