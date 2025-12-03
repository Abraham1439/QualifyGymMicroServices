package com.qualifygym.publicaciones.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.qualifygym.publicaciones.model.Publicacion;
import com.qualifygym.publicaciones.service.PublicacionService;
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
 * Tests de integración para PublicacionController
 * 
 * Esta clase contiene tests que verifican los endpoints REST del controlador de publicaciones.
 * Utiliza MockMvc para simular peticiones HTTP y verificar las respuestas.
 * Los tests cubren todos los endpoints: GET, POST, PUT, DELETE y operaciones de moderación.
 */
@WebMvcTest(PublicacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PublicacionService publicacionService;

    private Publicacion publicacionTest;

    /**
     * Configuración inicial antes de cada test
     * Crea objetos de prueba para publicaciones
     */
    @BeforeEach
    void setUp() {
        publicacionTest = new Publicacion();
        publicacionTest.setIdPublicacion(1L);
        publicacionTest.setTitulo("Título de prueba");
        publicacionTest.setDescripcion("Descripción de prueba");
        publicacionTest.setFecha(LocalDateTime.now());
        publicacionTest.setOculta(false);
        publicacionTest.setUsuarioId(1L);
        publicacionTest.setTemaId(1L);
    }

    /**
     * Test: GET /publicaciones/tema/{id} - Obtener publicaciones por tema
     * Verifica que el endpoint retorna publicaciones visibles de un tema
     */
    @Test
    void obtenerPublicacionesPorTema_deberiaRetornarListaYStatus200() throws Exception {
        // Arrange
        Long temaId = 1L;
        List<Publicacion> publicaciones = List.of(publicacionTest);

        when(publicacionService.obtenerPublicacionesVisiblesPorTema(temaId))
                .thenReturn(publicaciones);

        // Act & Assert
        mockMvc.perform(get("/api/v1/publicacion/publicaciones/tema/" + temaId)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].titulo").value("Título de prueba"))
               .andExpect(jsonPath("$[0].temaId").value(temaId));

        verify(publicacionService, times(1)).obtenerPublicacionesVisiblesPorTema(temaId);
    }

    /**
     * Test: POST /publicaciones - Crear publicación exitosamente
     * Verifica que el endpoint crea una publicación y retorna status 201
     */
    @Test
    void crearPublicacion_conDatosValidos_deberiaRetornarStatus201() throws Exception {
        // Arrange
        String requestBody = """
            {
                "titulo": "Nueva publicación",
                "descripcion": "Descripción de la publicación",
                "usuarioId": 1,
                "temaId": 1
            }
            """;

        Publicacion nuevaPublicacion = new Publicacion();
        nuevaPublicacion.setIdPublicacion(2L);
        nuevaPublicacion.setTitulo("Nueva publicación");
        nuevaPublicacion.setDescripcion("Descripción de la publicación");
        nuevaPublicacion.setUsuarioId(1L);
        nuevaPublicacion.setTemaId(1L);
        nuevaPublicacion.setFecha(LocalDateTime.now());
        nuevaPublicacion.setOculta(false);

        when(publicacionService.crearPublicacion("Nueva publicación", "Descripción de la publicación", 1L, 1L, null))
                .thenReturn(nuevaPublicacion);

        // Act & Assert
        mockMvc.perform(post("/api/v1/publicacion/publicaciones")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.titulo").value("Nueva publicación"))
               .andExpect(jsonPath("$.usuarioId").value(1L))
               .andExpect(jsonPath("$.temaId").value(1L));

        verify(publicacionService, times(1))
                .crearPublicacion("Nueva publicación", "Descripción de la publicación", 1L, 1L, null);
    }

    /**
     * Test: PUT /publicaciones/{id} - Actualizar publicación
     * Verifica que el endpoint actualiza una publicación y retorna status 200
     */
    @Test
    void actualizarPublicacion_conDatosValidos_deberiaRetornarStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        String requestBody = """
            {
                "titulo": "Título actualizado",
                "descripcion": "Descripción actualizada"
            }
            """;

        publicacionTest.setTitulo("Título actualizado");
        publicacionTest.setDescripcion("Descripción actualizada");

        when(publicacionService.actualizarPublicacion(id, "Título actualizado", "Descripción actualizada"))
                .thenReturn(publicacionTest);

        // Act & Assert
        mockMvc.perform(put("/api/v1/publicacion/publicaciones/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.titulo").value("Título actualizado"))
               .andExpect(jsonPath("$.descripcion").value("Descripción actualizada"));

        verify(publicacionService, times(1))
                .actualizarPublicacion(id, "Título actualizado", "Descripción actualizada");
    }

    /**
     * Test: PUT /publicaciones/{id}/ocultar - Ocultar publicación
     * Verifica que el endpoint oculta una publicación y retorna status 200
     */
    @Test
    void ocultarPublicacion_deberiaRetornarStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        String requestBody = """
            {
                "motivoBaneo": "Contenido inapropiado"
            }
            """;

        publicacionTest.setOculta(true);
        publicacionTest.setMotivoBaneo("Contenido inapropiado");
        publicacionTest.setFechaBaneo(LocalDateTime.now());

        when(publicacionService.ocultarPublicacion(id, "Contenido inapropiado"))
                .thenReturn(publicacionTest);

        // Act & Assert
        mockMvc.perform(put("/api/v1/publicacion/publicaciones/{id}/ocultar", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.oculta").value(true))
               .andExpect(jsonPath("$.motivoBaneo").value("Contenido inapropiado"));

        verify(publicacionService, times(1)).ocultarPublicacion(id, "Contenido inapropiado");
    }

    /**
     * Test: DELETE /publicaciones/{id} - Eliminar publicación
     * Verifica que el endpoint elimina una publicación y retorna status 204
     */
    @Test
    void eliminarPublicacion_deberiaRetornarStatus204() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(publicacionService).eliminarPublicacion(id);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/publicacion/publicaciones/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());

        verify(publicacionService, times(1)).eliminarPublicacion(id);
    }

    /**
     * Test: GET /publicaciones - Obtener todas las publicaciones
     * Verifica que el endpoint retorna una lista de publicaciones con status 200
     */
    @Test
    void obtenerTodasPublicaciones_deberiaRetornarListaYStatus200() throws Exception {
        // Arrange
        List<Publicacion> publicaciones = List.of(publicacionTest);
        // El controller usa obtenerPublicacionesVisibles() por defecto (incluirOcultas=false)
        when(publicacionService.obtenerPublicacionesVisibles()).thenReturn(publicaciones);

        // Act & Assert
        mockMvc.perform(get("/api/v1/publicacion/publicaciones")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].titulo").value("Título de prueba"));

        verify(publicacionService, times(1)).obtenerPublicacionesVisibles();
    }

    /**
     * Test: GET /publicaciones/{id} - Obtener publicación por ID
     * Verifica que el endpoint retorna la publicación con status 200
     */
    @Test
    void obtenerPublicacionPorId_conIdExistente_deberiaRetornarPublicacionYStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        when(publicacionService.obtenerPublicacionPorId(id)).thenReturn(Optional.of(publicacionTest));

        // Act & Assert
        mockMvc.perform(get("/api/v1/publicacion/publicaciones/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.idPublicacion").value(1L))
               .andExpect(jsonPath("$.titulo").value("Título de prueba"));

        verify(publicacionService, times(1)).obtenerPublicacionPorId(id);
    }

    /**
     * Test: GET /publicaciones/usuario/{id} - Obtener publicaciones por usuario
     * Verifica que el endpoint retorna publicaciones de un usuario
     */
    @Test
    void obtenerPublicacionesPorUsuario_deberiaRetornarListaYStatus200() throws Exception {
        // Arrange
        Long usuarioId = 1L;
        List<Publicacion> publicaciones = List.of(publicacionTest);
        when(publicacionService.obtenerPublicacionesVisiblesPorUsuario(usuarioId)).thenReturn(publicaciones);

        // Act & Assert
        mockMvc.perform(get("/api/v1/publicacion/publicaciones/usuario/{usuarioId}", usuarioId)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].usuarioId").value(usuarioId));

        verify(publicacionService, times(1)).obtenerPublicacionesVisiblesPorUsuario(usuarioId);
    }

    /**
     * Test: GET /publicaciones/buscar - Buscar publicaciones
     * Verifica que el endpoint busca publicaciones por texto
     */
    @Test
    void buscarPublicaciones_deberiaRetornarListaYStatus200() throws Exception {
        // Arrange
        String query = "test";
        List<Publicacion> publicaciones = List.of(publicacionTest);
        // El servicio usa trim() en el query
        when(publicacionService.buscarPublicaciones(query)).thenReturn(publicaciones);

        // Act & Assert
        mockMvc.perform(get("/api/v1/publicacion/publicaciones/buscar")
               .param("query", query)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].titulo").value("Título de prueba"));

        verify(publicacionService, times(1)).buscarPublicaciones(query);
    }

    /**
     * Test: GET /publicaciones/tema/{id}/count - Contar publicaciones por tema
     * Verifica que el endpoint retorna el conteo de publicaciones
     */
    @Test
    void contarPublicacionesPorTema_deberiaRetornarCantidadYStatus200() throws Exception {
        // Arrange
        Long temaId = 1L;
        when(publicacionService.contarPublicacionesPorTema(temaId)).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/publicacion/publicaciones/tema/{temaId}/count", temaId)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().string("5"));

        verify(publicacionService, times(1)).contarPublicacionesPorTema(temaId);
    }

    /**
     * Test: GET /publicaciones/usuario/{id}/count - Contar publicaciones por usuario
     * Verifica que el endpoint retorna el conteo de publicaciones
     */
    @Test
    void contarPublicacionesPorUsuario_deberiaRetornarCantidadYStatus200() throws Exception {
        // Arrange
        Long usuarioId = 1L;
        when(publicacionService.contarPublicacionesPorUsuario(usuarioId)).thenReturn(3L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/publicacion/publicaciones/usuario/{usuarioId}/count", usuarioId)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().string("3"));

        verify(publicacionService, times(1)).contarPublicacionesPorUsuario(usuarioId);
    }

    /**
     * Test: PUT /publicaciones/{id}/imagen - Actualizar imagen de publicación
     * Verifica que el endpoint actualiza la imagen y retorna status 200
     */
    @Test
    void actualizarImagenPublicacion_deberiaRetornarStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        String requestBody = """
            {
                "imageUrl": "https://example.com/image.jpg"
            }
            """;

        publicacionTest.setImageUrl("https://example.com/image.jpg");
        when(publicacionService.actualizarImagenPublicacion(id, "https://example.com/image.jpg"))
                .thenReturn(publicacionTest);

        // Act & Assert
        mockMvc.perform(put("/api/v1/publicacion/publicaciones/{id}/imagen", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.imageUrl").value("https://example.com/image.jpg"));

        verify(publicacionService, times(1)).actualizarImagenPublicacion(id, "https://example.com/image.jpg");
    }

    /**
     * Test: PUT /publicaciones/{id}/mostrar - Mostrar publicación oculta
     * Verifica que el endpoint muestra una publicación previamente oculta
     */
    @Test
    void mostrarPublicacion_deberiaRetornarStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        publicacionTest.setOculta(false);
        publicacionTest.setFechaBaneo(null);
        publicacionTest.setMotivoBaneo(null);

        when(publicacionService.mostrarPublicacion(id)).thenReturn(publicacionTest);

        // Act & Assert
        mockMvc.perform(put("/api/v1/publicacion/publicaciones/{id}/mostrar", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.oculta").value(false));

        verify(publicacionService, times(1)).mostrarPublicacion(id);
    }

    /**
     * Test: GET /existe/{id} - Verificar existencia de publicación
     * Verifica que el endpoint retorna true cuando la publicación existe
     */
    @Test
    void existePublicacion_conIdExistente_deberiaRetornarTrue() throws Exception {
        // Arrange
        Long id = 1L;
        when(publicacionService.obtenerPublicacionPorId(id)).thenReturn(Optional.of(publicacionTest));

        // Act & Assert
        mockMvc.perform(get("/api/v1/publicacion/existe/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().string("true"));

        verify(publicacionService, times(1)).obtenerPublicacionPorId(id);
    }
}
