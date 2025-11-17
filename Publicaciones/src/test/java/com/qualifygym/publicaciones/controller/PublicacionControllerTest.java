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
}
