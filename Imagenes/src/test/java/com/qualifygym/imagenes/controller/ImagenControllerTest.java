package com.qualifygym.imagenes.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.qualifygym.imagenes.model.Imagen;
import com.qualifygym.imagenes.service.ImagenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Tests de integración para ImagenController
 * 
 * Esta clase contiene tests que verifican los endpoints REST del controlador de imágenes.
 * Utiliza MockMvc para simular peticiones HTTP y verificar las respuestas.
 * Los tests cubren todos los endpoints: POST (subir), GET (obtener), DELETE (eliminar).
 */
@WebMvcTest(ImagenController.class)
@AutoConfigureMockMvc(addFilters = false)
class ImagenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImagenService imagenService;

    private Imagen imagenTest;
    private byte[] datosImagenTest;

    /**
     * Configuración inicial antes de cada test
     * Crea objetos de prueba para imágenes
     */
    @BeforeEach
    void setUp() {
        datosImagenTest = new byte[1024];
        for (int i = 0; i < datosImagenTest.length; i++) {
            datosImagenTest[i] = (byte) i;
        }
        
        imagenTest = new Imagen();
        imagenTest.setIdImagen(1L);
        imagenTest.setUsuarioId(1L);
        imagenTest.setTipoImagen("PERFIL");
        imagenTest.setDatosImagen(datosImagenTest);
        imagenTest.setTipoMime("image/jpeg");
        imagenTest.setNombreArchivo("foto_perfil_1.jpg");
        imagenTest.setTamaño(1024L);
        imagenTest.setFechaSubida(LocalDateTime.now());
    }

    /**
     * Test: POST /perfil/{usuarioId} - Subir foto de perfil exitosamente
     * Verifica que el endpoint sube una foto de perfil y retorna status 201
     */
    @Test
    void subirFotoPerfil_conDatosValidos_deberiaRetornarStatus201() throws Exception {
        // Arrange
        Long usuarioId = 1L;
        MockMultipartFile archivo = new MockMultipartFile(
            "archivo",
            "foto_perfil.jpg",
            "image/jpeg",
            datosImagenTest
        );
        
        when(imagenService.subirFotoPerfil(
            eq(usuarioId), 
            any(byte[].class), 
            eq("image/jpeg"), 
            eq("foto_perfil.jpg")
        )).thenReturn(imagenTest);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/imagen/perfil/" + usuarioId)
               .file(archivo))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.idImagen").value(1L))
               .andExpect(jsonPath("$.usuarioId").value(usuarioId))
               .andExpect(jsonPath("$.tipoImagen").value("PERFIL"));

        verify(imagenService, times(1)).subirFotoPerfil(
            eq(usuarioId), 
            any(byte[].class), 
            eq("image/jpeg"), 
            eq("foto_perfil.jpg")
        );
    }

    /**
     * Test: POST /publicacion/{publicacionId} - Subir foto de publicación exitosamente
     * Verifica que el endpoint sube una foto de publicación y retorna status 201
     */
    @Test
    void subirFotoPublicacion_conDatosValidos_deberiaRetornarStatus201() throws Exception {
        // Arrange
        Long publicacionId = 1L;
        Long usuarioId = 1L;
        
        Imagen imagenPublicacion = new Imagen();
        imagenPublicacion.setIdImagen(2L);
        imagenPublicacion.setPublicacionId(publicacionId);
        imagenPublicacion.setUsuarioId(usuarioId);
        imagenPublicacion.setTipoImagen("PUBLICACION");
        imagenPublicacion.setDatosImagen(datosImagenTest);
        imagenPublicacion.setTipoMime("image/png");
        imagenPublicacion.setNombreArchivo("foto_publicacion.png");
        imagenPublicacion.setTamaño(1024L);
        imagenPublicacion.setFechaSubida(LocalDateTime.now());
        
        MockMultipartFile archivo = new MockMultipartFile(
            "archivo",
            "foto_publicacion.png",
            "image/png",
            datosImagenTest
        );
        
        when(imagenService.subirFotoPublicacion(
            eq(publicacionId), 
            eq(usuarioId), 
            any(byte[].class), 
            eq("image/png"), 
            eq("foto_publicacion.png")
        )).thenReturn(imagenPublicacion);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/imagen/publicacion/" + publicacionId)
               .file(archivo)
               .param("usuarioId", usuarioId.toString()))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.idImagen").value(2L))
               .andExpect(jsonPath("$.publicacionId").value(publicacionId))
               .andExpect(jsonPath("$.tipoImagen").value("PUBLICACION"));

        verify(imagenService, times(1)).subirFotoPublicacion(
            eq(publicacionId), 
            eq(usuarioId), 
            any(byte[].class), 
            eq("image/png"), 
            eq("foto_publicacion.png")
        );
    }

    /**
     * Test: GET /perfil/{usuarioId} - Obtener foto de perfil existente
     * Verifica que el endpoint retorna la foto de perfil con status 200
     */
    @Test
    void obtenerFotoPerfil_conFotoExistente_deberiaRetornarImagenYStatus200() throws Exception {
        // Arrange
        Long usuarioId = 1L;
        when(imagenService.obtenerFotoPerfil(usuarioId)).thenReturn(Optional.of(imagenTest));

        // Act & Assert
        mockMvc.perform(get("/api/v1/imagen/perfil/" + usuarioId))
               .andExpect(status().isOk())
               .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "image/jpeg"))
               .andExpect(content().bytes(datosImagenTest));

        verify(imagenService, times(1)).obtenerFotoPerfil(usuarioId);
    }

    /**
     * Test: GET /perfil/{usuarioId} - Obtener foto de perfil inexistente
     * Verifica que el endpoint retorna status 404 cuando no hay foto
     */
    @Test
    void obtenerFotoPerfil_conFotoInexistente_deberiaRetornarStatus404() throws Exception {
        // Arrange
        Long usuarioId = 2L;
        when(imagenService.obtenerFotoPerfil(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/imagen/perfil/" + usuarioId))
               .andExpect(status().isNotFound());

        verify(imagenService, times(1)).obtenerFotoPerfil(usuarioId);
    }

    /**
     * Test: GET /{idImagen} - Obtener imagen por ID existente
     * Verifica que el endpoint retorna la imagen con status 200
     */
    @Test
    void obtenerImagenPorId_conIdExistente_deberiaRetornarImagenYStatus200() throws Exception {
        // Arrange
        Long idImagen = 1L;
        when(imagenService.obtenerImagenPorId(idImagen)).thenReturn(Optional.of(imagenTest));

        // Act & Assert
        mockMvc.perform(get("/api/v1/imagen/" + idImagen))
               .andExpect(status().isOk())
               .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "image/jpeg"))
               .andExpect(content().bytes(datosImagenTest));

        verify(imagenService, times(1)).obtenerImagenPorId(idImagen);
    }

    /**
     * Test: GET /{idImagen} - Obtener imagen por ID inexistente
     * Verifica que el endpoint retorna status 404 cuando la imagen no existe
     */
    @Test
    void obtenerImagenPorId_conIdInexistente_deberiaRetornarStatus404() throws Exception {
        // Arrange
        Long idInexistente = 999L;
        when(imagenService.obtenerImagenPorId(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/imagen/" + idInexistente))
               .andExpect(status().isNotFound());

        verify(imagenService, times(1)).obtenerImagenPorId(idInexistente);
    }

    /**
     * Test: GET /publicacion/{publicacionId} - Obtener imágenes de publicación
     * Verifica que el endpoint retorna la lista de imágenes de una publicación
     */
    @Test
    void obtenerImagenesPublicacion_deberiaRetornarListaYStatus200() throws Exception {
        // Arrange
        Long publicacionId = 1L;
        
        // Crear una imagen de publicación válida (con publicacionId establecido)
        Imagen imagenPublicacion = new Imagen();
        imagenPublicacion.setIdImagen(2L);
        imagenPublicacion.setPublicacionId(publicacionId);
        imagenPublicacion.setUsuarioId(1L);
        imagenPublicacion.setTipoImagen("PUBLICACION");
        imagenPublicacion.setDatosImagen(datosImagenTest);
        imagenPublicacion.setTipoMime("image/png");
        imagenPublicacion.setNombreArchivo("foto_publicacion.png");
        imagenPublicacion.setTamaño(1024L);
        imagenPublicacion.setFechaSubida(LocalDateTime.now());
        
        List<Imagen> imagenes = List.of(imagenPublicacion);
        when(imagenService.obtenerImagenesPublicacion(publicacionId)).thenReturn(imagenes);

        // Act & Assert
        mockMvc.perform(get("/api/v1/imagen/publicacion/" + publicacionId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].idImagen").value(2L))
               .andExpect(jsonPath("$[0].publicacionId").value(publicacionId))
               .andExpect(jsonPath("$[0].tipoImagen").value("PUBLICACION"));

        verify(imagenService, times(1)).obtenerImagenesPublicacion(publicacionId);
    }

    /**
     * Test: DELETE /{idImagen} - Eliminar imagen exitosamente
     * Verifica que el endpoint elimina una imagen y retorna status 204
     */
    @Test
    void eliminarImagen_conIdExistente_deberiaRetornarStatus204() throws Exception {
        // Arrange
        Long idImagen = 1L;
        doNothing().when(imagenService).eliminarImagen(idImagen);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/imagen/" + idImagen))
               .andExpect(status().isNoContent());

        verify(imagenService, times(1)).eliminarImagen(idImagen);
    }

    /**
     * Test: DELETE /perfil/{usuarioId} - Eliminar foto de perfil exitosamente
     * Verifica que el endpoint elimina la foto de perfil y retorna status 204
     */
    @Test
    void eliminarFotoPerfil_conFotoExistente_deberiaRetornarStatus204() throws Exception {
        // Arrange
        Long usuarioId = 1L;
        doNothing().when(imagenService).eliminarFotoPerfil(usuarioId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/imagen/perfil/" + usuarioId))
               .andExpect(status().isNoContent());

        verify(imagenService, times(1)).eliminarFotoPerfil(usuarioId);
    }

    /**
     * Test: GET /usuario/{usuarioId}/count - Contar imágenes por usuario
     * Verifica que el endpoint retorna el conteo de imágenes
     */
    @Test
    void contarImagenesPorUsuario_deberiaRetornarCantidadYStatus200() throws Exception {
        // Arrange
        Long usuarioId = 1L;
        when(imagenService.contarImagenesPorUsuario(usuarioId)).thenReturn(3L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/imagen/usuario/" + usuarioId + "/count"))
               .andExpect(status().isOk())
               .andExpect(content().string("3"));

        verify(imagenService, times(1)).contarImagenesPorUsuario(usuarioId);
    }

    /**
     * Test: GET /publicacion/{publicacionId}/count - Contar imágenes por publicación
     * Verifica que el endpoint retorna el conteo de imágenes
     */
    @Test
    void contarImagenesPorPublicacion_deberiaRetornarCantidadYStatus200() throws Exception {
        // Arrange
        Long publicacionId = 1L;
        when(imagenService.contarImagenesPorPublicacion(publicacionId)).thenReturn(2L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/imagen/publicacion/" + publicacionId + "/count"))
               .andExpect(status().isOk())
               .andExpect(content().string("2"));

        verify(imagenService, times(1)).contarImagenesPorPublicacion(publicacionId);
    }

    /**
     * Test: POST /perfil/{usuarioId} - Subir archivo vacío
     * Verifica que el endpoint rechaza archivos vacíos con status 400
     */
    @Test
    void subirFotoPerfil_conArchivoVacio_deberiaRetornarStatus400() throws Exception {
        // Arrange
        Long usuarioId = 1L;
        MockMultipartFile archivoVacio = new MockMultipartFile(
            "archivo",
            "vacio.jpg",
            "image/jpeg",
            new byte[0]
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/imagen/perfil/" + usuarioId)
               .file(archivoVacio))
               .andExpect(status().isBadRequest())
               .andExpect(content().string("El archivo no puede estar vacío"));

        verify(imagenService, never()).subirFotoPerfil(anyLong(), any(byte[].class), anyString(), anyString());
    }

    /**
     * Test: DELETE /{idImagen} - Eliminar imagen inexistente
     * Verifica que el endpoint retorna status 404 cuando la imagen no existe
     */
    @Test
    void eliminarImagen_conIdInexistente_deberiaRetornarStatus404() throws Exception {
        // Arrange
        Long idInexistente = 999L;
        doThrow(new RuntimeException("Imagen no encontrada ID: " + idInexistente))
            .when(imagenService).eliminarImagen(idInexistente);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/imagen/" + idInexistente))
               .andExpect(status().isNotFound());

        verify(imagenService, times(1)).eliminarImagen(idInexistente);
    }

    /**
     * Test: DELETE /perfil/{usuarioId} - Eliminar foto de perfil inexistente
     * Verifica que el endpoint retorna status 404 cuando no hay foto de perfil
     */
    @Test
    void eliminarFotoPerfil_conFotoInexistente_deberiaRetornarStatus404() throws Exception {
        // Arrange
        Long usuarioId = 2L;
        doThrow(new RuntimeException("No se encontró foto de perfil para el usuario ID: " + usuarioId))
            .when(imagenService).eliminarFotoPerfil(usuarioId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/imagen/perfil/" + usuarioId))
               .andExpect(status().isNotFound());

        verify(imagenService, times(1)).eliminarFotoPerfil(usuarioId);
    }
}

