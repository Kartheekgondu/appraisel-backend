package com.uxinterfacely.appraisel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI appraisalOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Appraisal Letter API")
                        .version("1.0")
                        .description(
                                "REST APIs for employee appraisal letters"
                        ));
    }
}