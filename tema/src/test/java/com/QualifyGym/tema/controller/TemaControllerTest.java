package com.QualifyGym.tema.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.QualifyGym.tema.model.Tema;
import com.QualifyGym.tema.service.TemaService;
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
 * Tests de integración para TemaController
 * 
 * Esta clase contiene tests que verifican los endpoints REST del controlador de temas.
 * Utiliza MockMvc para simular peticiones HTTP y verificar las respuestas.
 * Los tests cubren todos los endpoints: GET, POST, PUT, DELETE.
 */
@WebMvcTest(TemaController.class)
@AutoConfigureMockMvc(addFilters = false)
class TemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TemaService temaService;

    private Tema temaTest;

    /**
     * Configuración inicial antes de cada test
     */
    @BeforeEach
    void setUp() {
        temaTest = new Tema();
        temaTest.setIdTema(1L);
        temaTest.setNombreTema("Rutinas de Fuerza");
        temaTest.setEstadoId(1L);
    }

    /**
     * Test: GET /temas - Listar todos los temas
     * Verifica que el endpoint retorna una lista de temas con status 200
     */
    @Test
    void obtenerTodosTemas_deberiaRetornarListaYStatus200() throws Exception {
        // Arrange
        List<Tema> temas = List.of(temaTest);
        when(temaService.obtenerTodosTemas()).thenReturn(temas);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tema/temas")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].nombreTema").value("Rutinas de Fuerza"))
               .andExpect(jsonPath("$[0].estadoId").value(1L));

        verify(temaService, times(1)).obtenerTodosTemas();
    }

    /**
     * Test: GET /temas/{id} - Obtener tema por ID existente
     * Verifica que el endpoint retorna el tema con status 200
     */
    @Test
    void obtenerTemaPorId_conIdExistente_deberiaRetornarTemaYStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        when(temaService.obtenerTemaPorId(id)).thenReturn(Optional.of(temaTest));

        // Act & Assert
        mockMvc.perform(get("/api/v1/tema/temas/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.idTema").value(1L))
               .andExpect(jsonPath("$.nombreTema").value("Rutinas de Fuerza"));

        verify(temaService, times(1)).obtenerTemaPorId(id);
    }

    /**
     * Test: GET /temas/{id} - Tema no encontrado
     * Verifica que el endpoint retorna status 404 cuando el tema no existe
     */
    @Test
    void obtenerTemaPorId_conIdInexistente_deberiaRetornarStatus404() throws Exception {
        // Arrange
        Long id = 999L;
        when(temaService.obtenerTemaPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/tema/temas/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(content().string(org.hamcrest.Matchers.containsString("Tema no encontrado")));

        verify(temaService, times(1)).obtenerTemaPorId(id);
    }

    /**
     * Test: POST /temas - Crear tema exitosamente
     * Verifica que el endpoint crea un tema y retorna status 201
     */
    @Test
    void crearTema_conDatosValidos_deberiaRetornarStatus201() throws Exception {
        // Arrange
        String requestBody = """
            {
                "nombreTema": "Yoga y Flexibilidad",
                "estadoId": 1
            }
            """;

        Tema nuevoTema = new Tema();
        nuevoTema.setIdTema(2L);
        nuevoTema.setNombreTema("Yoga y Flexibilidad");
        nuevoTema.setEstadoId(1L);

        when(temaService.crearTema("Yoga y Flexibilidad", 1L)).thenReturn(nuevoTema);

        // Act & Assert
        mockMvc.perform(post("/api/v1/tema/temas")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.nombreTema").value("Yoga y Flexibilidad"))
               .andExpect(jsonPath("$.estadoId").value(1L));

        verify(temaService, times(1)).crearTema("Yoga y Flexibilidad", 1L);
    }

    /**
     * Test: POST /temas - Crear tema con nombre duplicado
     * Verifica que el endpoint retorna status 400 cuando el nombre ya existe
     */
    @Test
    void crearTema_conNombreDuplicado_deberiaRetornarStatus400() throws Exception {
        // Arrange
        String requestBody = """
            {
                "nombreTema": "Rutinas de Fuerza",
                "estadoId": 1
            }
            """;

        when(temaService.crearTema("Rutinas de Fuerza", 1L))
                .thenThrow(new RuntimeException("Ya existe un tema con el nombre: Rutinas de Fuerza"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/tema/temas")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(org.hamcrest.Matchers.containsString("Ya existe un tema")));
    }

    /**
     * Test: PUT /temas/{id} - Actualizar tema exitosamente
     * Verifica que el endpoint actualiza un tema y retorna status 200
     */
    @Test
    void actualizarTema_conDatosValidos_deberiaRetornarStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        String requestBody = """
            {
                "nombreTema": "Rutinas de Fuerza Avanzadas",
                "estadoId": 1
            }
            """;

        temaTest.setNombreTema("Rutinas de Fuerza Avanzadas");
        when(temaService.actualizarTema(id, "Rutinas de Fuerza Avanzadas", 1L)).thenReturn(temaTest);

        // Act & Assert
        mockMvc.perform(put("/api/v1/tema/temas/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nombreTema").value("Rutinas de Fuerza Avanzadas"));

        verify(temaService, times(1)).actualizarTema(id, "Rutinas de Fuerza Avanzadas", 1L);
    }

    /**
     * Test: DELETE /temas/{id} - Eliminar tema
     * Verifica que el endpoint elimina un tema y retorna status 204
     */
    @Test
    void eliminarTema_deberiaRetornarStatus204() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(temaService).eliminarTema(id);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/tema/temas/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());

        verify(temaService, times(1)).eliminarTema(id);
    }

    /**
     * Test: GET /temas/estado/{id} - Obtener temas por estado
     * Verifica que el endpoint retorna temas de un estado
     */
    @Test
    void obtenerTemasPorEstado_deberiaRetornarListaYStatus200() throws Exception {
        // Arrange
        Long estadoId = 1L;
        List<Tema> temas = List.of(temaTest);
        when(temaService.obtenerTemasPorEstado(estadoId)).thenReturn(temas);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tema/temas/estado/{estadoId}", estadoId)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].estadoId").value(estadoId));

        verify(temaService, times(1)).obtenerTemasPorEstado(estadoId);
    }

    /**
     * Test: GET /temas/buscar - Buscar temas
     * Verifica que el endpoint busca temas por texto
     */
    @Test
    void buscarTemas_deberiaRetornarListaYStatus200() throws Exception {
        // Arrange
        String query = "Fuerza";
        List<Tema> temas = List.of(temaTest);
        when(temaService.buscarTemas(query)).thenReturn(temas);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tema/temas/buscar")
               .param("query", query)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].nombreTema").value("Rutinas de Fuerza"));

        verify(temaService, times(1)).buscarTemas(query);
    }

    /**
     * Test: GET /temas/nombre/{nombre} - Obtener tema por nombre
     * Verifica que el endpoint retorna el tema con status 200
     */
    @Test
    void obtenerTemaPorNombre_conNombreExistente_deberiaRetornarTemaYStatus200() throws Exception {
        // Arrange
        String nombre = "Rutinas de Fuerza";
        when(temaService.obtenerTemaPorNombre(nombre)).thenReturn(Optional.of(temaTest));

        // Act & Assert
        mockMvc.perform(get("/api/v1/tema/temas/nombre/{nombre}", nombre)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.nombreTema").value("Rutinas de Fuerza"));

        verify(temaService, times(1)).obtenerTemaPorNombre(nombre);
    }

    /**
     * Test: GET /temas/existe/{nombre} - Verificar existencia de tema
     * Verifica que el endpoint retorna true cuando el tema existe
     */
    @Test
    void existeTemaPorNombre_conNombreExistente_deberiaRetornarTrue() throws Exception {
        // Arrange
        String nombre = "Rutinas de Fuerza";
        when(temaService.existePorNombre(nombre)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tema/temas/existe/{nombre}", nombre)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().string("true"));

        verify(temaService, times(1)).existePorNombre(nombre);
    }

    /**
     * Test: GET /temas/estado/{id}/count - Contar temas por estado
     * Verifica que el endpoint retorna el conteo de temas
     */
    @Test
    void contarTemasPorEstado_deberiaRetornarCantidadYStatus200() throws Exception {
        // Arrange
        Long estadoId = 1L;
        when(temaService.contarTemasPorEstado(estadoId)).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tema/temas/estado/{estadoId}/count", estadoId)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().string("5"));

        verify(temaService, times(1)).contarTemasPorEstado(estadoId);
    }
}
