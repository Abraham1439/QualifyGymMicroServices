package com.qualifygym.imagenes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("QualifyGym Imagen Microservice API")
                        .version("1.0.0")
                        .description("API para la gestión de imágenes del sistema QualifyGym")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}

