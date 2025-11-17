package com.qualifygym.estados.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.qualifygym.estados.model.Estado;
import com.qualifygym.estados.service.EstadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

/**
 * Tests de integración para EstadoController
 * 
 * Esta clase contiene tests que verifican los endpoints REST del controlador de estados.
 * Utiliza MockMvc para simular peticiones HTTP y verificar las respuestas.
 * Los tests cubren todos los endpoints: GET, POST, PUT, DELETE.
 */
@WebMvcTest(EstadoController.class)
@AutoConfigureMockMvc(addFilters = false)
class EstadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EstadoService estadoService;

    private Estado estadoTest;

    /**
     * Configuración inicial antes de cada test
     */
    @BeforeEach
    void setUp() {
        estadoTest = new Estado();
        estadoTest.setIdEstado(1L);
        estadoTest.setNombre("Activo");
    }

    /**
     * Test: GET /estados - Listar todos los estados
     * Verifica que el endpoint retorna una lista de estados con status 200
     */
    @Test
    void obtenerTodosEstados_deberiaRetornarListaYStatus200() throws Exception {
        // Arrange
        List<Estado> estados = List.of(estadoTest);
        when(estadoService.obtenerTodosEstados()).thenReturn(estados);

        // Act & Assert
        mockMvc.perform(get("/api/v1/estado/estados")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].nombre").value("Activo"))
               .andExpect(jsonPath("$[0].idEstado").value(1L));

        verify(estadoService, times(1)).obtenerTodosEstados();
    }

    /**
     * Test: GET /estados/{id} - Obtener estado por ID existente
     * Verifica que el endpoint retorna el estado con status 200
     */
    @Test
    void obtenerEstadoPorId_conIdExistente_deberiaRetornarEstadoYStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        when(estadoService.obtenerEstadoPorId(id)).thenReturn(Optional.of(estadoTest));

        // Act & Assert
        mockMvc.perform(get("/api/v1/estado/estados/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.idEstado").value(1L))
               .andExpect(jsonPath("$.nombre").value("Activo"));

        verify(estadoService, times(1)).obtenerEstadoPorId(id);
    }

    /**
     * Test: GET /estados/{id} - Estado no encontrado
     * Verifica que el endpoint retorna status 404 cuando el estado no existe
     */
    @Test
    void obtenerEstadoPorId_conIdInexistente_deberiaRetornarStatus404() throws Exception {
        // Arrange
        Long id = 999L;
        when(estadoService.obtenerEstadoPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/estado/estados/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(content().string(org.hamcrest.Matchers.containsString("Estado no encontrado")));

        verify(estadoService, times(1)).obtenerEstadoPorId(id);
    }

    /**
     * Test: POST /estados - Crear estado exitosamente
     * Verifica que el endpoint crea un estado y retorna status 201
     */
    @Test
    void crearEstado_conDatosValidos_deberiaRetornarStatus201() throws Exception {
        // Arrange
        String requestBody = """
            {
                "nombre": "Nuevo Estado"
            }
            """;

        Estado nuevoEstado = new Estado();
        nuevoEstado.setIdEstado(2L);
        nuevoEstado.setNombre("Nuevo Estado");

        when(estadoService.crearEstado("Nuevo Estado")).thenReturn(nuevoEstado);

        // Act & Assert
        mockMvc.perform(post("/api/v1/estado/estados")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.nombre").value("Nuevo Estado"))
               .andExpect(jsonPath("$.idEstado").value(2L));

        verify(estadoService, times(1)).crearEstado("Nuevo Estado");
    }

    /**
     * Test: POST /estados - Crear estado con nombre duplicado
     * Verifica que el endpoint retorna status 400 cuando el nombre ya existe
     */
    @Test
    void crearEstado_conNombreDuplicado_deberiaRetornarStatus400() throws Exception {
        // Arrange
        String requestBody = """
            {
                "nombre": "Activo"
            }
            """;

        when(estadoService.crearEstado("Activo"))
                .thenThrow(new RuntimeException("Ya existe un estado con el nombre: Activo"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/estado/estados")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(org.hamcrest.Matchers.containsString("Ya existe un estado")));
    }

    /**
     * Test: PUT /estados/{id} - Actualizar estado exitosamente
     * Verifica que el endpoint actualiza un estado y retorna status 200
     */
    @Test
    void actualizarEstado_conDatosValidos_deberiaRetornarStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        String requestBody = """
            {
                "nombre": "Estado Actualizado"
            }
            """;

        estadoTest.setNombre("Estado Actualizado");
        when(estadoService.actualizarEstado(id, "Estado Actualizado")).thenReturn(estadoTest);

        // Act & Assert
        mockMvc.perform(put("/api/v1/estado/estados/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nombre").value("Estado Actualizado"));

        verify(estadoService, times(1)).actualizarEstado(id, "Estado Actualizado");
    }

    /**
     * Test: DELETE /estados/{id} - Eliminar estado
     * Verifica que el endpoint elimina un estado y retorna status 204
     */
    @Test
    void eliminarEstado_deberiaRetornarStatus204() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(estadoService).eliminarEstado(id);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/estado/estados/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());

        verify(estadoService, times(1)).eliminarEstado(id);
    }
}
