package com.valuego.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class SwaggerConfig {

    private Info apiInfo() {
        return new Info()
                .title("Valuego Swagger API")
                .description("Valuego Swagger API입니다.")
                .version("2.0");
    }

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .openapi("3.1.0")
                .info(apiInfo());
    }
}
