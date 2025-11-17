package com.qualifygym.comentarios.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.qualifygym.comentarios.model.Comentario;
import com.qualifygym.comentarios.service.ComentarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Tests de integración para ComentarioController
 * 
 * Esta clase contiene tests que verifican los endpoints REST del controlador de comentarios.
 * Utiliza MockMvc para simular peticiones HTTP y verificar las respuestas.
 * Los tests cubren todos los endpoints: GET, POST, PUT, DELETE y operaciones de moderación.
 */
@WebMvcTest(ComentarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ComentarioService comentarioService;

    private Comentario comentarioTest;

    /**
     * Configuración inicial antes de cada test
     * Crea objetos de prueba para comentarios
     */
    @BeforeEach
    void setUp() {
        comentarioTest = new Comentario();
        comentarioTest.setIdComentario(1L);
        comentarioTest.setComentario("Comentario de prueba");
        comentarioTest.setFechaRegistro(LocalDateTime.now());
        comentarioTest.setOculto(false);
        comentarioTest.setUsuarioId(1L);
        comentarioTest.setPublicacionId(1L);
    }

    /**
     * Test: GET /comentarios - Listar todos los comentarios
     * Verifica que el endpoint retorna una lista de comentarios con status 200
     */
    @Test
    void obtenerTodosComentarios_deberiaRetornarListaYStatus200() throws Exception {
        // Arrange
        List<Comentario> comentarios = List.of(comentarioTest);
        when(comentarioService.obtenerTodosComentarios()).thenReturn(comentarios);

        // Act & Assert
        mockMvc.perform(get("/api/v1/comentario/comentarios")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].comentario").value("Comentario de prueba"))
               .andExpect(jsonPath("$[0].publicacionId").value(1L));

        verify(comentarioService, times(1)).obtenerTodosComentarios();
    }

    /**
     * Test: GET /comentarios/{id} - Obtener comentario por ID existente
     * Verifica que el endpoint retorna el comentario con status 200
     */
    @Test
    void obtenerComentarioPorId_conIdExistente_deberiaRetornarComentarioYStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        when(comentarioService.obtenerComentarioPorId(id)).thenReturn(Optional.of(comentarioTest));

        // Act & Assert
        mockMvc.perform(get("/api/v1/comentario/comentarios/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.idComentario").value(1L))
               .andExpect(jsonPath("$.comentario").value("Comentario de prueba"));

        verify(comentarioService, times(1)).obtenerComentarioPorId(id);
    }

    /**
     * Test: GET /comentarios/{id} - Comentario no encontrado
     * Verifica que el endpoint retorna status 404 cuando el comentario no existe
     */
    @Test
    void obtenerComentarioPorId_conIdInexistente_deberiaRetornarStatus404() throws Exception {
        // Arrange
        Long id = 999L;
        when(comentarioService.obtenerComentarioPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/comentario/comentarios/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(content().string("Comentario no encontrado"));

        verify(comentarioService, times(1)).obtenerComentarioPorId(id);
    }

    /**
     * Test: GET /comentarios/publicacion/{id} - Obtener comentarios por publicación
     * Verifica que el endpoint retorna comentarios visibles de una publicación
     */
    @Test
    void obtenerComentariosPorPublicacion_deberiaRetornarListaYStatus200() throws Exception {
        // Arrange
        Long publicacionId = 1L;
        List<Comentario> comentarios = List.of(comentarioTest);

        when(comentarioService.obtenerComentariosVisiblesPorPublicacion(publicacionId))
                .thenReturn(comentarios);

        // Act & Assert
        mockMvc.perform(get("/api/v1/comentario/comentarios/publicacion/{publicacionId}", publicacionId)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].comentario").value("Comentario de prueba"))
               .andExpect(jsonPath("$[0].publicacionId").value(publicacionId));

        verify(comentarioService, times(1)).obtenerComentariosVisiblesPorPublicacion(publicacionId);
    }

    /**
     * Test: POST /comentarios - Crear comentario exitosamente
     * Verifica que el endpoint crea un comentario y retorna status 201
     */
    @Test
    void crearComentario_conDatosValidos_deberiaRetornarStatus201() throws Exception {
        // Arrange
        String requestBody = """
            {
                "comentario": "Nuevo comentario",
                "usuarioId": 1,
                "publicacionId": 1
            }
            """;

        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setIdComentario(2L);
        nuevoComentario.setComentario("Nuevo comentario");
        nuevoComentario.setUsuarioId(1L);
        nuevoComentario.setPublicacionId(1L);
        nuevoComentario.setFechaRegistro(LocalDateTime.now());
        nuevoComentario.setOculto(false);

        when(comentarioService.crearComentario("Nuevo comentario", 1L, 1L))
                .thenReturn(nuevoComentario);

        // Act & Assert
        mockMvc.perform(post("/api/v1/comentario/comentarios")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.comentario").value("Nuevo comentario"))
               .andExpect(jsonPath("$.usuarioId").value(1L))
               .andExpect(jsonPath("$.publicacionId").value(1L));

        verify(comentarioService, times(1)).crearComentario("Nuevo comentario", 1L, 1L);
    }

    /**
     * Test: POST /comentarios - Crear comentario con usuario inexistente
     * Verifica que el endpoint retorna status 400 cuando el usuario no existe
     */
    @Test
    void crearComentario_conUsuarioInexistente_deberiaRetornarStatus400() throws Exception {
        // Arrange
        String requestBody = """
            {
                "comentario": "Comentario",
                "usuarioId": 999,
                "publicacionId": 1
            }
            """;

        when(comentarioService.crearComentario(anyString(), eq(999L), eq(1L)))
                .thenThrow(new RuntimeException("El usuario con ID 999 no existe"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/comentario/comentarios")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(org.hamcrest.Matchers.containsString("El usuario con ID 999 no existe")));
    }

    /**
     * Test: PUT /comentarios/{id} - Actualizar comentario exitosamente
     * Verifica que el endpoint actualiza un comentario y retorna status 200
     */
    @Test
    void actualizarComentario_conDatosValidos_deberiaRetornarStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        String requestBody = """
            {
                "comentario": "Comentario actualizado"
            }
            """;

        comentarioTest.setComentario("Comentario actualizado");
        when(comentarioService.actualizarComentario(id, "Comentario actualizado"))
                .thenReturn(comentarioTest);

        // Act & Assert
        mockMvc.perform(put("/api/v1/comentario/comentarios/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.comentario").value("Comentario actualizado"));

        verify(comentarioService, times(1)).actualizarComentario(id, "Comentario actualizado");
    }

    /**
     * Test: PUT /comentarios/{id}/ocultar - Ocultar comentario
     * Verifica que el endpoint oculta un comentario y retorna status 200
     */
    @Test
    void ocultarComentario_deberiaRetornarStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        String requestBody = """
            {
                "motivoBaneo": "Contenido inapropiado"
            }
            """;

        comentarioTest.setOculto(true);
        comentarioTest.setMotivoBaneo("Contenido inapropiado");
        comentarioTest.setFechaBaneo(LocalDateTime.now());

        when(comentarioService.ocultarComentario(id, "Contenido inapropiado"))
                .thenReturn(comentarioTest);

        // Act & Assert
        mockMvc.perform(put("/api/v1/comentario/comentarios/{id}/ocultar", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.oculto").value(true))
               .andExpect(jsonPath("$.motivoBaneo").value("Contenido inapropiado"));

        verify(comentarioService, times(1)).ocultarComentario(id, "Contenido inapropiado");
    }

    /**
     * Test: PUT /comentarios/{id}/mostrar - Mostrar comentario oculto
     * Verifica que el endpoint muestra un comentario previamente oculto
     */
    @Test
    void mostrarComentario_deberiaRetornarStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        comentarioTest.setOculto(false);
        comentarioTest.setFechaBaneo(null);
        comentarioTest.setMotivoBaneo(null);

        when(comentarioService.mostrarComentario(id)).thenReturn(comentarioTest);

        // Act & Assert
        mockMvc.perform(put("/api/v1/comentario/comentarios/{id}/mostrar", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.oculto").value(false));

        verify(comentarioService, times(1)).mostrarComentario(id);
    }

    /**
     * Test: DELETE /comentarios/{id} - Eliminar comentario
     * Verifica que el endpoint elimina un comentario y retorna status 204
     */
    @Test
    void eliminarComentario_deberiaRetornarStatus204() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(comentarioService).eliminarComentario(id);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/comentario/comentarios/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());

        verify(comentarioService, times(1)).eliminarComentario(id);
    }
}
