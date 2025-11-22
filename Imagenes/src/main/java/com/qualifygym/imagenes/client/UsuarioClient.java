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
            Boolean existe = webClient.get()
                    .uri("/users/{id}/existe", usuarioId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            return existe != null && existe;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            // Si hay un error, loguear y retornar false para evitar subir imágenes a usuarios inexistentes
            System.err.println("Error al verificar existencia de usuario ID " + usuarioId + ": " + e.getMessage());
            return false;
        }
    }
}

