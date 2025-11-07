package com.QualifyGym.tema.webpublicacion;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PublicacionCat {

    //variable para la comunicacion 
    private final WebClient webclient;

    //metodo constructor de la clase 
    public PublicacionCat(@Value("${publicacion-service.url}") String publicacionServiceUrl) {
        this.webclient = WebClient.builder().baseUrl(publicacionServiceUrl).build();
    }

    //metodo para comunicarnos con el microservicio de una categoria y buscar  si una categoria existe mediante su id 
    public Map<String, Object> obtenerPublicacionPorId(Long id) {
        return this.webclient.get().uri("/{id}", id).retrieve().onStatus(status -> status.is4xxClientError() , response -> response.bodyToMono(String.class).map(body -> new RuntimeException("Publicacion no encontrada"))).bodyToMono(Map.class).block();
    }

}
