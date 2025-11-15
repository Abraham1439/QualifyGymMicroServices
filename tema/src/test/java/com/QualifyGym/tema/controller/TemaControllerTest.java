package com.QualifyGym.tema.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.QualifyGym.tema.model.Tema;
import com.QualifyGym.tema.service.TemaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

@WebMvcTest(TemaController.class)
@AutoConfigureMockMvc(addFilters = false)
class TemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TemaService temaService;

    @Test
    void obtenerTodosTemas_deberiaRetornarListaYStatus200() throws Exception {
        Tema tema = new Tema();
        tema.setIdTema(1L);
        tema.setNombreTema("Rutinas de Fuerza");
        tema.setEstadoId(1L);
        List<Tema> listaTemas = List.of(tema);

        when(temaService.obtenerTodosTemas()).thenReturn(listaTemas);

        mockMvc.perform(get("/api/v1/tema/temas")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].nombreTema").value("Rutinas de Fuerza"))
               .andExpect(jsonPath("$[0].estadoId").value(1L));
    }
}

