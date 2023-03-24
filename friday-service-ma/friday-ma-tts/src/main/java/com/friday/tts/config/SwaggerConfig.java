package com.friday.tts.config;

import com.friday.swagger.config.SwaggerInitConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;

/**
 * @Author chengxu.zheng
 * @Create 2022/7/13 10:30
 * @Description SWAGGER配置类
 */
@Configuration
public class SwaggerConfig {

    @Resource
    private SwaggerInitConfig swaggerInitConfig;

    @Bean
    public Docket docket() {
        return swaggerInitConfig.docket();
    }
}
