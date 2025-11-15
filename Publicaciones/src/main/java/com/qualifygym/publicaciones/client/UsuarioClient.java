package com.qualifygym.publicaciones.client;

import java.util.Map;

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

    /**
     * Verifica si un usuario existe mediante su ID
     * @param id ID del usuario
     * @return true si el usuario existe, false en caso contrario
     */
    public boolean existeUsuario(Long id) {
        try {
            Map<String, Object> usuario = webClient.get()
                    .uri("/users/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return usuario != null;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar usuario: " + e.getMessage());
        }
    }

    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return Map con los datos del usuario o null si no existe
     */
    public Map<String, Object> obtenerUsuarioPorId(Long id) {
        try {
            return webClient.get()
                    .uri("/users/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener usuario: " + e.getMessage());
        }
    }
}

