package com.qualifygym.imagenes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.qualifygym.imagenes.model.Imagen;
import com.qualifygym.imagenes.repository.ImagenRepository;
import com.qualifygym.imagenes.client.UsuarioClient;
import com.qualifygym.imagenes.client.PublicacionClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Tests unitarios para ImagenService
 * 
 * Esta clase contiene tests que verifican la lógica de negocio del servicio de imágenes,
 * incluyendo subida, recuperación y eliminación de imágenes de perfil y publicaciones.
 * Utiliza mocks para aislar las pruebas de la capa de persistencia.
 */
class ImagenServiceTest {

    @Mock
    private ImagenRepository imagenRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private PublicacionClient publicacionClient;

    @InjectMocks
    private ImagenService imagenService;

    private Imagen imagenTest;
    private byte[] datosImagenValidos;

    /**
     * Configuración inicial antes de cada test
     * Crea objetos de prueba para imágenes
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Crear datos de imagen de prueba (1KB)
        datosImagenValidos = new byte[1024];
        for (int i = 0; i < datosImagenValidos.length; i++) {
            datosImagenValidos[i] = (byte) i;
        }
        
        imagenTest = new Imagen();
        imagenTest.setIdImagen(1L);
        imagenTest.setUsuarioId(1L);
        imagenTest.setTipoImagen("PERFIL");
        imagenTest.setDatosImagen(datosImagenValidos);
        imagenTest.setTipoMime("image/jpeg");
        imagenTest.setNombreArchivo("foto_perfil_1");
        imagenTest.setTamaño(1024L);
        imagenTest.setFechaSubida(LocalDateTime.now());
    }

    /**
     * Test: Subir foto de perfil exitosamente
     * Verifica que el servicio sube una foto de perfil cuando el usuario existe
     */
    @Test
    void subirFotoPerfil_conDatosValidos_debeRetornarImagenCreada() {
        // Arrange
        Long usuarioId = 1L;
        String tipoMime = "image/jpeg";
        String nombreArchivo = "foto_perfil.jpg";
        
        when(usuarioClient.existeUsuario(usuarioId)).thenReturn(true);
        when(imagenRepository.findFotoPerfilByUsuarioId(usuarioId)).thenReturn(Optional.empty());
        when(imagenRepository.save(any(Imagen.class))).thenReturn(imagenTest);
        
        // Act
        Imagen resultado = imagenService.subirFotoPerfil(usuarioId, datosImagenValidos, tipoMime, nombreArchivo);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals("PERFIL", resultado.getTipoImagen());
        assertEquals(tipoMime, resultado.getTipoMime());
        verify(usuarioClient, times(1)).existeUsuario(usuarioId);
        verify(imagenRepository, times(1)).findFotoPerfilByUsuarioId(usuarioId);
        verify(imagenRepository, times(1)).save(any(Imagen.class));
    }

    /**
     * Test: Subir foto de perfil con usuario inexistente
     * Verifica que el servicio lanza una excepción cuando el usuario no existe
     */
    @Test
    void subirFotoPerfil_conUsuarioInexistente_debeLanzarExcepcion() {
        // Arrange
        Long usuarioIdInexistente = 999L;
        when(usuarioClient.existeUsuario(usuarioIdInexistente)).thenReturn(false);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imagenService.subirFotoPerfil(usuarioIdInexistente, datosImagenValidos, "image/jpeg", "foto.jpg");
        });
        
        assertTrue(exception.getMessage().contains("El usuario con ID 999 no existe"));
        verify(usuarioClient, times(1)).existeUsuario(usuarioIdInexistente);
        verify(imagenRepository, never()).save(any(Imagen.class));
    }

    /**
     * Test: Subir foto de perfil con imagen vacía
     * Verifica que el servicio lanza una excepción cuando la imagen está vacía
     */
    @Test
    void subirFotoPerfil_conImagenVacia_debeLanzarExcepcion() {
        // Arrange
        Long usuarioId = 1L;
        byte[] datosVacios = new byte[0];
        when(usuarioClient.existeUsuario(usuarioId)).thenReturn(true);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imagenService.subirFotoPerfil(usuarioId, datosVacios, "image/jpeg", "foto.jpg");
        });
        
        assertTrue(exception.getMessage().contains("La imagen no puede estar vacía"));
        verify(imagenRepository, never()).save(any(Imagen.class));
    }

    /**
     * Test: Subir foto de perfil con tipo MIME no permitido
     * Verifica que el servicio lanza una excepción cuando el tipo MIME no es válido
     */
    @Test
    void subirFotoPerfil_conTipoMimeInvalido_debeLanzarExcepcion() {
        // Arrange
        Long usuarioId = 1L;
        String tipoMimeInvalido = "image/bmp";
        when(usuarioClient.existeUsuario(usuarioId)).thenReturn(true);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imagenService.subirFotoPerfil(usuarioId, datosImagenValidos, tipoMimeInvalido, "foto.bmp");
        });
        
        assertTrue(exception.getMessage().contains("Tipo de imagen no permitido"));
        verify(imagenRepository, never()).save(any(Imagen.class));
    }

    /**
     * Test: Subir foto de perfil reemplazando una existente
     * Verifica que el servicio elimina la foto anterior antes de guardar la nueva
     */
    @Test
    void subirFotoPerfil_conFotoExistente_debeReemplazarla() {
        // Arrange
        Long usuarioId = 1L;
        Imagen fotoAnterior = new Imagen();
        fotoAnterior.setIdImagen(10L);
        
        when(usuarioClient.existeUsuario(usuarioId)).thenReturn(true);
        when(imagenRepository.findFotoPerfilByUsuarioId(usuarioId)).thenReturn(Optional.of(fotoAnterior));
        doNothing().when(imagenRepository).delete(fotoAnterior);
        when(imagenRepository.save(any(Imagen.class))).thenReturn(imagenTest);
        
        // Act
        Imagen resultado = imagenService.subirFotoPerfil(usuarioId, datosImagenValidos, "image/jpeg", "nueva_foto.jpg");
        
        // Assert
        assertNotNull(resultado);
        verify(imagenRepository, times(1)).delete(fotoAnterior);
        verify(imagenRepository, times(1)).save(any(Imagen.class));
    }

    /**
     * Test: Subir foto de publicación exitosamente
     * Verifica que el servicio sube una foto de publicación cuando la publicación y usuario existen
     */
    @Test
    void subirFotoPublicacion_conDatosValidos_debeRetornarImagenCreada() {
        // Arrange
        Long publicacionId = 1L;
        Long usuarioId = 1L;
        String tipoMime = "image/png";
        String nombreArchivo = "foto_publicacion.png";
        
        Imagen imagenPublicacion = new Imagen();
        imagenPublicacion.setIdImagen(2L);
        imagenPublicacion.setPublicacionId(publicacionId);
        imagenPublicacion.setUsuarioId(usuarioId);
        imagenPublicacion.setTipoImagen("PUBLICACION");
        imagenPublicacion.setTipoMime(tipoMime);
        
        when(publicacionClient.existePublicacion(publicacionId)).thenReturn(true);
        when(usuarioClient.existeUsuario(usuarioId)).thenReturn(true);
        when(imagenRepository.save(any(Imagen.class))).thenReturn(imagenPublicacion);
        
        // Act
        Imagen resultado = imagenService.subirFotoPublicacion(publicacionId, usuarioId, datosImagenValidos, tipoMime, nombreArchivo);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(publicacionId, resultado.getPublicacionId());
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals("PUBLICACION", resultado.getTipoImagen());
        verify(publicacionClient, times(1)).existePublicacion(publicacionId);
        verify(usuarioClient, times(1)).existeUsuario(usuarioId);
        verify(imagenRepository, times(1)).save(any(Imagen.class));
    }

    /**
     * Test: Subir foto de publicación con publicación inexistente
     * Verifica que el servicio lanza una excepción cuando la publicación no existe
     */
    @Test
    void subirFotoPublicacion_conPublicacionInexistente_debeLanzarExcepcion() {
        // Arrange
        Long publicacionIdInexistente = 999L;
        Long usuarioId = 1L;
        when(publicacionClient.existePublicacion(publicacionIdInexistente)).thenReturn(false);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imagenService.subirFotoPublicacion(publicacionIdInexistente, usuarioId, datosImagenValidos, "image/jpeg", "foto.jpg");
        });
        
        assertTrue(exception.getMessage().contains("La publicación con ID 999 no existe"));
        verify(publicacionClient, times(1)).existePublicacion(publicacionIdInexistente);
        verify(imagenRepository, never()).save(any(Imagen.class));
    }

    /**
     * Test: Obtener foto de perfil existente
     * Verifica que el servicio retorna la foto de perfil cuando existe
     */
    @Test
    void obtenerFotoPerfil_conUsuarioConFoto_debeRetornarImagen() {
        // Arrange
        Long usuarioId = 1L;
        when(imagenRepository.findFotoPerfilByUsuarioId(usuarioId)).thenReturn(Optional.of(imagenTest));
        
        // Act
        Optional<Imagen> resultado = imagenService.obtenerFotoPerfil(usuarioId);
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuarioId, resultado.get().getUsuarioId());
        assertEquals("PERFIL", resultado.get().getTipoImagen());
        verify(imagenRepository, times(1)).findFotoPerfilByUsuarioId(usuarioId);
    }

    /**
     * Test: Obtener foto de perfil inexistente
     * Verifica que el servicio retorna Optional vacío cuando no hay foto
     */
    @Test
    void obtenerFotoPerfil_conUsuarioSinFoto_debeRetornarOptionalVacio() {
        // Arrange
        Long usuarioId = 2L;
        when(imagenRepository.findFotoPerfilByUsuarioId(usuarioId)).thenReturn(Optional.empty());
        
        // Act
        Optional<Imagen> resultado = imagenService.obtenerFotoPerfil(usuarioId);
        
        // Assert
        assertFalse(resultado.isPresent());
        verify(imagenRepository, times(1)).findFotoPerfilByUsuarioId(usuarioId);
    }

    /**
     * Test: Obtener imágenes de publicación
     * Verifica que el servicio retorna la lista de imágenes de una publicación
     */
    @Test
    void obtenerImagenesPublicacion_debeRetornarLista() {
        // Arrange
        Long publicacionId = 1L;
        List<Imagen> imagenes = new ArrayList<>();
        imagenes.add(imagenTest);
        when(imagenRepository.findImagenesByPublicacionId(publicacionId)).thenReturn(imagenes);
        
        // Act
        List<Imagen> resultado = imagenService.obtenerImagenesPublicacion(publicacionId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(imagenRepository, times(1)).findImagenesByPublicacionId(publicacionId);
    }

    /**
     * Test: Obtener imagen por ID existente
     * Verifica que el servicio retorna la imagen cuando existe
     */
    @Test
    void obtenerImagenPorId_conIdExistente_debeRetornarImagen() {
        // Arrange
        Long id = 1L;
        when(imagenRepository.findById(id)).thenReturn(Optional.of(imagenTest));
        
        // Act
        Optional<Imagen> resultado = imagenService.obtenerImagenPorId(id);
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getIdImagen());
        verify(imagenRepository, times(1)).findById(id);
    }

    /**
     * Test: Eliminar imagen exitosamente
     * Verifica que el servicio elimina correctamente una imagen
     */
    @Test
    void eliminarImagen_conIdExistente_debeEliminarImagen() {
        // Arrange
        Long id = 1L;
        when(imagenRepository.existsById(id)).thenReturn(true);
        doNothing().when(imagenRepository).deleteById(id);
        
        // Act
        imagenService.eliminarImagen(id);
        
        // Assert
        verify(imagenRepository, times(1)).existsById(id);
        verify(imagenRepository, times(1)).deleteById(id);
    }

    /**
     * Test: Eliminar imagen inexistente
     * Verifica que el servicio lanza una excepción cuando la imagen no existe
     */
    @Test
    void eliminarImagen_conIdInexistente_debeLanzarExcepcion() {
        // Arrange
        Long idInexistente = 999L;
        when(imagenRepository.existsById(idInexistente)).thenReturn(false);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imagenService.eliminarImagen(idInexistente);
        });
        
        assertTrue(exception.getMessage().contains("Imagen no encontrada ID: " + idInexistente));
        verify(imagenRepository, times(1)).existsById(idInexistente);
        verify(imagenRepository, never()).deleteById(anyLong());
    }

    /**
     * Test: Eliminar foto de perfil exitosamente
     * Verifica que el servicio elimina la foto de perfil de un usuario
     */
    @Test
    void eliminarFotoPerfil_conFotoExistente_debeEliminarFoto() {
        // Arrange
        Long usuarioId = 1L;
        when(imagenRepository.findFotoPerfilByUsuarioId(usuarioId)).thenReturn(Optional.of(imagenTest));
        doNothing().when(imagenRepository).delete(imagenTest);
        
        // Act
        imagenService.eliminarFotoPerfil(usuarioId);
        
        // Assert
        verify(imagenRepository, times(1)).findFotoPerfilByUsuarioId(usuarioId);
        verify(imagenRepository, times(1)).delete(imagenTest);
    }

    /**
     * Test: Eliminar foto de perfil inexistente
     * Verifica que el servicio lanza una excepción cuando no hay foto de perfil
     */
    @Test
    void eliminarFotoPerfil_conFotoInexistente_debeLanzarExcepcion() {
        // Arrange
        Long usuarioId = 2L;
        when(imagenRepository.findFotoPerfilByUsuarioId(usuarioId)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imagenService.eliminarFotoPerfil(usuarioId);
        });
        
        assertTrue(exception.getMessage().contains("No se encontró foto de perfil para el usuario ID: " + usuarioId));
        verify(imagenRepository, times(1)).findFotoPerfilByUsuarioId(usuarioId);
        verify(imagenRepository, never()).delete(any(Imagen.class));
    }

    /**
     * Test: Eliminar todas las imágenes de una publicación
     * Verifica que el servicio elimina todas las imágenes asociadas a una publicación
     */
    @Test
    void eliminarImagenesPublicacion_debeEliminarTodasLasImagenes() {
        // Arrange
        Long publicacionId = 1L;
        List<Imagen> imagenes = new ArrayList<>();
        imagenes.add(imagenTest);
        when(imagenRepository.findImagenesByPublicacionId(publicacionId)).thenReturn(imagenes);
        doNothing().when(imagenRepository).deleteAll(imagenes);
        
        // Act
        imagenService.eliminarImagenesPublicacion(publicacionId);
        
        // Assert
        verify(imagenRepository, times(1)).findImagenesByPublicacionId(publicacionId);
        verify(imagenRepository, times(1)).deleteAll(imagenes);
    }

    /**
     * Test: Contar imágenes por usuario
     * Verifica que el servicio cuenta correctamente las imágenes de un usuario
     */
    @Test
    void contarImagenesPorUsuario_debeRetornarCantidad() {
        // Arrange
        Long usuarioId = 1L;
        when(imagenRepository.countByUsuarioId(usuarioId)).thenReturn(3L);
        
        // Act
        long resultado = imagenService.contarImagenesPorUsuario(usuarioId);
        
        // Assert
        assertEquals(3L, resultado);
        verify(imagenRepository, times(1)).countByUsuarioId(usuarioId);
    }

    /**
     * Test: Contar imágenes por publicación
     * Verifica que el servicio cuenta correctamente las imágenes de una publicación
     */
    @Test
    void contarImagenesPorPublicacion_debeRetornarCantidad() {
        // Arrange
        Long publicacionId = 1L;
        when(imagenRepository.countByPublicacionId(publicacionId)).thenReturn(2L);
        
        // Act
        long resultado = imagenService.contarImagenesPorPublicacion(publicacionId);
        
        // Assert
        assertEquals(2L, resultado);
        verify(imagenRepository, times(1)).countByPublicacionId(publicacionId);
    }

    /**
     * Test: Subir imagen muy grande (más de 10MB)
     * Verifica que el servicio rechaza imágenes que exceden el límite
     */
    @Test
    void subirFotoPerfil_conImagenMuyGrande_debeLanzarExcepcion() {
        // Arrange
        Long usuarioId = 1L;
        byte[] imagenGrande = new byte[11 * 1024 * 1024]; // 11MB
        when(usuarioClient.existeUsuario(usuarioId)).thenReturn(true);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imagenService.subirFotoPerfil(usuarioId, imagenGrande, "image/jpeg", "foto_grande.jpg");
        });
        
        assertTrue(exception.getMessage().contains("La imagen excede el tamaño máximo permitido de 10MB"));
        verify(imagenRepository, never()).save(any(Imagen.class));
    }

    /**
     * Test: Obtener foto de perfil con ID de usuario inválido
     * Verifica que el servicio lanza una excepción cuando el ID de usuario es inválido
     */
    @Test
    void obtenerFotoPerfil_conIdInvalido_debeLanzarExcepcion() {
        // Arrange
        Long usuarioIdInvalido = 0L;
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imagenService.obtenerFotoPerfil(usuarioIdInvalido);
        });
        
        assertTrue(exception.getMessage().contains("El ID de usuario es inválido"));
        verify(imagenRepository, never()).findFotoPerfilByUsuarioId(anyLong());
    }

    /**
     * Test: Obtener imágenes de publicación con ID inválido
     * Verifica que el servicio lanza una excepción cuando el ID de publicación es inválido
     */
    @Test
    void obtenerImagenesPublicacion_conIdInvalido_debeLanzarExcepcion() {
        // Arrange
        Long publicacionIdInvalida = -1L;
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imagenService.obtenerImagenesPublicacion(publicacionIdInvalida);
        });
        
        assertTrue(exception.getMessage().contains("El ID de publicación es inválido"));
        verify(imagenRepository, never()).findImagenesByPublicacionId(anyLong());
    }
}

