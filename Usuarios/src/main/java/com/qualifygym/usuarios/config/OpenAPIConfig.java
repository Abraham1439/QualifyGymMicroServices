package com.qualifygym.usuarios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuración de OpenAPI/Swagger para el microservicio de Usuarios
 * 
 * Esta clase configura la documentación automática de la API REST usando Swagger/OpenAPI.
 * Define información sobre el microservicio, contactos, licencias y servidores disponibles.
 */
@Configuration
public class OpenAPIConfig {
@Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info().title("Usuarios QuialifyGym").version("1.0").description("Usuarios que se encuentras registrados en  en QuialifyGym"));
    }
}

