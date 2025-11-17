package com.qualifygym.publicaciones.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.qualifygym.publicaciones.model.Publicacion;
import com.qualifygym.publicaciones.service.PublicacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(PublicacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicacionService publicacionService;

    @Test
    void obtenerPublicacionesPorTema_deberiaRetornarListaYStatus200() throws Exception {
        Long temaId = 1L;
        Publicacion publicacion = new Publicacion();
        publicacion.setIdPublicacion(1L);
        publicacion.setTitulo("Título de prueba");
        publicacion.setDescripcion("Descripción de prueba");
        publicacion.setTemaId(temaId);
        publicacion.setUsuarioId(1L);
        publicacion.setFecha(LocalDateTime.now());
        publicacion.setOculta(false);
        
        List<Publicacion> listaPublicaciones = List.of(publicacion);

        when(publicacionService.obtenerPublicacionesVisiblesPorTema(temaId))
                .thenReturn(listaPublicaciones);

        mockMvc.perform(get("/api/v1/publicacion/publicaciones/tema/" + temaId)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].titulo").value("Título de prueba"))
               .andExpect(jsonPath("$[0].temaId").value(temaId));
    }
}

