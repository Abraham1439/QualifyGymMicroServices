package com.qualifygym.estados.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.qualifygym.estados.model.Estado;
import com.qualifygym.estados.service.EstadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

@WebMvcTest(EstadoController.class)
@AutoConfigureMockMvc(addFilters = false)
class EstadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EstadoService estadoService;

    @Test
    void obtenerTodosEstados_deberiaRetornarListaYStatus200() throws Exception {
        Estado estado = new Estado();
        estado.setIdEstado(1L);
        estado.setNombre("Activo");
        List<Estado> listaEstados = List.of(estado);

        when(estadoService.obtenerTodosEstados()).thenReturn(listaEstados);

        mockMvc.perform(get("/api/v1/estado/estados")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].nombre").value("Activo"))
               .andExpect(jsonPath("$[0].idEstado").value(1L));
    }
}

