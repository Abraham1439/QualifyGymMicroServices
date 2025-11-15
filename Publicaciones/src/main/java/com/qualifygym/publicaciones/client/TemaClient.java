package com.qualifygym.publicaciones.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class TemaClient {

    private final WebClient webClient;

    public TemaClient(@Value("${tema-service.url}") String temaServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(temaServiceUrl)
                .build();
    }

    /**
     * Verifica si un tema existe mediante su ID
     * @param id ID del tema
     * @return true si el tema existe, false en caso contrario
     */
    public boolean existeTema(Long id) {
        try {
            Map<String, Object> tema = webClient.get()
                    .uri("/temas/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return tema != null;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar tema: " + e.getMessage());
        }
    }

    /**
     * Obtiene un tema por su ID
     * @param id ID del tema
     * @return Map con los datos del tema o null si no existe
     */
    public Map<String, Object> obtenerTemaPorId(Long id) {
        try {
            return webClient.get()
                    .uri("/temas/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tema: " + e.getMessage());
        }
    }
}

