package com.qualifygym.comentarios.client;

import java.util.Map;

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

    /**
     * Verifica si una publicación existe mediante su ID
     * @param id ID de la publicación
     * @return true si la publicación existe, false en caso contrario
     */
    public boolean existePublicacion(Long id) {
        try {
            Map<String, Object> publicacion = webClient.get()
                    .uri("/publicaciones/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return publicacion != null;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar publicación: " + e.getMessage());
        }
    }

    /**
     * Obtiene una publicación por su ID
     * @param id ID de la publicación
     * @return Map con los datos de la publicación o null si no existe
     */
    public Map<String, Object> obtenerPublicacionPorId(Long id) {
        try {
            return webClient.get()
                    .uri("/publicaciones/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener publicación: " + e.getMessage());
        }
    }
}

