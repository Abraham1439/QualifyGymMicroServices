package com.QualifyGym.tema.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class EstadoClient {

    private final WebClient webClient;

    public EstadoClient(@Value("${estado-service.url}") String estadoServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(estadoServiceUrl)
                .build();
    }

    /**
     * Verifica si un estado existe mediante su ID
     * @param id ID del estado
     * @return true si el estado existe, false en caso contrario
     */
    public boolean existeEstado(Long id) {
        try {
            Map<String, Object> estado = webClient.get()
                    .uri("/estados/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return estado != null;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar estado: " + e.getMessage());
        }
    }

    /**
     * Obtiene un estado por su ID
     * @param id ID del estado
     * @return Map con los datos del estado o null si no existe
     */
    public Map<String, Object> obtenerEstadoPorId(Long id) {
        try {
            return webClient.get()
                    .uri("/estados/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estado: " + e.getMessage());
        }
    }
}

