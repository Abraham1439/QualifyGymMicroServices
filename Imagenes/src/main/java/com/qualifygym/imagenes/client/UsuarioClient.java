package com.qualifygym.imagenes.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(@Value("${usuario-service.url}") String usuarioServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(usuarioServiceUrl)
                .build();
    }

    public boolean existeUsuario(Long usuarioId) {
        try {
            webClient.get()
                    .uri("/usuarios/{id}/existe", usuarioId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            // Si el endpoint no existe, asumir que el usuario existe (para no bloquear)
            return true;
        }
    }
}

