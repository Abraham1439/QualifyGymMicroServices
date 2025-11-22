package com.qualifygym.imagenes.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class PublicacionClient {

    private final WebClient webClient;

    public PublicacionClient(@Value("${publicacion-service.url}") String publicacionServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(publicacionServiceUrl)
                .build();
    }

    public boolean existePublicacion(Long publicacionId) {
        try {
            Boolean existe = webClient.get()
                    .uri("/existe/{id}", publicacionId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            return existe != null && existe;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            // Si hay un error, loguear y retornar false para evitar subir imágenes a publicaciones inexistentes
            System.err.println("Error al verificar existencia de publicación ID " + publicacionId + ": " + e.getMessage());
            return false;
        }
    }
}

