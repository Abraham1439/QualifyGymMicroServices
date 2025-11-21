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
            webClient.get()
                    .uri("/publicaciones/{id}", publicacionId)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            // Si el endpoint no existe, asumir que la publicación existe (para no bloquear)
            return true;
        }
    }
}

