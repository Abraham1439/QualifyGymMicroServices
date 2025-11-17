package com.qualifygym.comentarios.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para el microservicio de Comentarios
 * 
 * Esta clase configura la documentación automática de la API REST usando Swagger/OpenAPI.
 * Define información sobre el microservicio, contactos, licencias y servidores disponibles.
 */
@Configuration
public class OpenAPIConfig {

    /**
     * Configura la información de la API para Swagger
     * 
     * @return OpenAPI con toda la información de la API documentada
     */
    @Bean
    public OpenAPI apiInfo() {
        Server server = new Server();
        server.setUrl("http://localhost:8082");
        server.setDescription("Servidor de desarrollo - Microservicio de Comentarios");

        Contact contact = new Contact();
        contact.setName("QualifyGym Team");
        contact.setEmail("support@qualifygym.com");

        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("https://www.apache.org/licenses/LICENSE-2.0.html");

        Info info = new Info()
                .title("QualifyGym - Microservicio de Comentarios")
                .version("1.0.0")
                .description("API REST para la gestión de comentarios del sistema QualifyGym. " +
                        "Incluye operaciones CRUD, moderación (ocultar/mostrar), y validación " +
                        "de integridad con usuarios y publicaciones.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}

