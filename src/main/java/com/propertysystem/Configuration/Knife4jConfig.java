package com.propertysystem.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class Knife4jConfig {
    @Bean
    public OpenAPI springShopOpenAPI() {
        log.info("初始化knife4j");
        return new OpenAPI()
                // 接口文档标题
                .info(new Info().title("API接口文档")
                        // 接口文档简介
                        .description("平台接口文档")
                        // 接口文档版本
                        .version("1.0")
                        );

    }

}
