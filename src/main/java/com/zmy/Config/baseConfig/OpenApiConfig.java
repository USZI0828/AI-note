package com.zmy.Config.baseConfig;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    /**
     * swagger 接口文档默认地址：http://localhost:8080/swagger-ui.html#
     * Knife4j 接口文档默认地址：http://127.0.0.1:8080/doc.html
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                // 接口文档标题
                .info(new Info().title("API接口文档")
                        // 接口文档简介
                        .description("限时秒杀系统接口文档")
                        // 接口文档版本
                        .version("v1.0")
                        // 开发者联系方式
                        .contact(new Contact().name("Uzhi").email("3277512331@qq.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("跑腿系统")
                        .url("http://127.0.0.1:8088"));
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .pathsToMatch("/**")
                .group("auth-api")
                .build();
    }
}

