package com.qualifygym.comentarios.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.qualifygym.comentarios.model.Comentario;
import com.qualifygym.comentarios.service.ComentarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(ComentarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComentarioService comentarioService;

    @Test
    void obtenerComentariosPorPublicacion_deberiaRetornarListaYStatus200() throws Exception {
        Long publicacionId = 1L;
        Comentario comentario = new Comentario();
        comentario.setIdComentario(1L);
        comentario.setComentario("Comentario de prueba");
        comentario.setPublicacionId(publicacionId);
        comentario.setUsuarioId(1L);
        comentario.setFechaRegistro(LocalDateTime.now());
        comentario.setOculto(false);
        
        List<Comentario> listaComentarios = List.of(comentario);

        when(comentarioService.obtenerComentariosVisiblesPorPublicacion(publicacionId))
                .thenReturn(listaComentarios);

        mockMvc.perform(get("/api/v1/comentario/comentarios/publicacion/" + publicacionId)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].comentario").value("Comentario de prueba"))
               .andExpect(jsonPath("$[0].publicacionId").value(publicacionId));
    }
}

