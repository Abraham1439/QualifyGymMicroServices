package com.qualifygym.publicaciones.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.qualifygym.publicaciones.model.Publicacion;
import com.qualifygym.publicaciones.repository.PublicacionRepository;
import com.qualifygym.publicaciones.client.UsuarioClient;
import com.qualifygym.publicaciones.client.TemaClient;
import com.qualifygym.publicaciones.service.NotificacionService;
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
 * Tests unitarios para PublicacionService
 * 
 * Esta clase contiene tests que verifican la lógica de negocio del servicio de publicaciones,
 * incluyendo creación, actualización, eliminación, moderación y validación de integridad
 * con usuarios y temas. Utiliza mocks para aislar las pruebas.
 */
class PublicacionServiceTest {

    @Mock
    private PublicacionRepository publicacionRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private TemaClient temaClient;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private PublicacionService publicacionService;

    private Publicacion publicacionTest;

    /**
     * Configuración inicial antes de cada test
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
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
     * Test: Crear publicación exitosamente
     * Verifica que el servicio crea una publicación cuando el usuario y tema existen
     */
    @Test
    void crearPublicacion_debeRetornarPublicacionCreada() {
        // Arrange
        String titulo = "Título de prueba";
        String descripcion = "Descripción de prueba";
        Long usuarioId = 1L;
        Long temaId = 1L;

        // Configurar mocks para validar que usuario y tema existen
        when(usuarioClient.existeUsuario(usuarioId)).thenReturn(true);
        when(temaClient.existeTema(temaId)).thenReturn(true);
        
        Publicacion guardada = new Publicacion();
        guardada.setIdPublicacion(1L);
        guardada.setTitulo(titulo);
        guardada.setDescripcion(descripcion);
        guardada.setUsuarioId(usuarioId);
        guardada.setTemaId(temaId);
        guardada.setFecha(LocalDateTime.now());
        guardada.setOculta(false);

        when(publicacionRepository.save(any(Publicacion.class))).thenReturn(guardada);

        // Act
        Publicacion resultado = publicacionService.crearPublicacion(titulo, descripcion, usuarioId, temaId, null);

        // Assert
        assertNotNull(resultado);
        assertEquals(titulo, resultado.getTitulo());
        assertEquals(descripcion, resultado.getDescripcion());
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(temaId, resultado.getTemaId());
        assertFalse(resultado.getOculta());
        assertNotNull(resultado.getFecha());
        
        // Verificar que se validaron usuario y tema
        verify(usuarioClient, times(1)).existeUsuario(usuarioId);
        verify(temaClient, times(1)).existeTema(temaId);
        verify(publicacionRepository, times(1)).save(any(Publicacion.class));
    }

    /**
     * Test: Crear publicación con título vacío
     * Verifica que el servicio lanza una excepción cuando el título está vacío
     */
    @Test
    void crearPublicacion_conTituloVacio_debeLanzarExcepcion() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            publicacionService.crearPublicacion("", "Descripción", 1L, 1L, null);
        });
        
        // Verificar que no se guardó nada
        verify(publicacionRepository, never()).save(any(Publicacion.class));
    }

    /**
     * Test: Crear publicación con usuario inexistente
     * Verifica que el servicio lanza una excepción cuando el usuario no existe
     */
    @Test
    void crearPublicacion_conUsuarioInexistente_debeLanzarExcepcion() {
        // Arrange
        Long usuarioIdInexistente = 999L;
        when(usuarioClient.existeUsuario(usuarioIdInexistente)).thenReturn(false);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            publicacionService.crearPublicacion("Título", "Descripción", usuarioIdInexistente, 1L, null);
        });
        
        assertTrue(exception.getMessage().contains("El usuario con ID 999 no existe"));
        verify(usuarioClient, times(1)).existeUsuario(usuarioIdInexistente);
        verify(temaClient, never()).existeTema(anyLong());
        verify(publicacionRepository, never()).save(any(Publicacion.class));
    }

    /**
     * Test: Crear publicación con tema inexistente
     * Verifica que el servicio lanza una excepción cuando el tema no existe
     */
    @Test
    void crearPublicacion_conTemaInexistente_debeLanzarExcepcion() {
        // Arrange
        Long temaIdInexistente = 999L;
        when(usuarioClient.existeUsuario(1L)).thenReturn(true);
        when(temaClient.existeTema(temaIdInexistente)).thenReturn(false);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            publicacionService.crearPublicacion("Título", "Descripción", 1L, temaIdInexistente, null);
        });
        
        assertTrue(exception.getMessage().contains("El tema con ID 999 no existe"));
        verify(usuarioClient, times(1)).existeUsuario(1L);
        verify(temaClient, times(1)).existeTema(temaIdInexistente);
        verify(publicacionRepository, never()).save(any(Publicacion.class));
    }

    /**
     * Test: Actualizar publicación exitosamente
     * Verifica que el servicio actualiza correctamente los campos de la publicación
     */
    @Test
    void actualizarPublicacion_conDatosValidos_debeRetornarPublicacionActualizada() {
        // Arrange
        Long id = 1L;
        String nuevoTitulo = "Título actualizado";
        String nuevaDescripcion = "Descripción actualizada";
        
        publicacionTest.setTitulo(nuevoTitulo);
        publicacionTest.setDescripcion(nuevaDescripcion);
        
        when(publicacionRepository.findById(id)).thenReturn(Optional.of(publicacionTest));
        when(publicacionRepository.save(any(Publicacion.class))).thenReturn(publicacionTest);
        
        // Act
        Publicacion resultado = publicacionService.actualizarPublicacion(id, nuevoTitulo, nuevaDescripcion);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(nuevoTitulo, resultado.getTitulo());
        assertEquals(nuevaDescripcion, resultado.getDescripcion());
        verify(publicacionRepository, times(1)).findById(id);
        verify(publicacionRepository, times(1)).save(any(Publicacion.class));
    }

    /**
     * Test: Ocultar publicación exitosamente
     * Verifica que el servicio oculta una publicación y establece motivo y fecha de baneo
     */
    @Test
    void ocultarPublicacion_conMotivo_debeOcultarYEstablecerMotivo() {
        // Arrange
        Long id = 1L;
        String motivo = "Contenido inapropiado";
        
        publicacionTest.setOculta(true);
        publicacionTest.setMotivoBaneo(motivo);
        publicacionTest.setFechaBaneo(LocalDateTime.now());
        
        when(publicacionRepository.findById(id)).thenReturn(Optional.of(publicacionTest));
        when(publicacionRepository.save(any(Publicacion.class))).thenReturn(publicacionTest);
        // Mock del servicio de notificaciones (el servicio lo llama cuando hay motivo)
        // El método crearNotificacion retorna Notificacion, no void
        com.qualifygym.publicaciones.model.Notificacion notificacionMock = 
            new com.qualifygym.publicaciones.model.Notificacion();
        when(notificacionService.crearNotificacion(anyLong(), anyLong(), anyString()))
            .thenReturn(notificacionMock);
        
        // Act
        Publicacion resultado = publicacionService.ocultarPublicacion(id, motivo);
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getOculta());
        assertEquals(motivo, resultado.getMotivoBaneo());
        assertNotNull(resultado.getFechaBaneo());
        verify(publicacionRepository, times(1)).findById(id);
        verify(publicacionRepository, times(1)).save(any(Publicacion.class));
        verify(notificacionService, times(1)).crearNotificacion(
            publicacionTest.getUsuarioId(),
            publicacionTest.getIdPublicacion(),
            motivo.trim()
        );
    }

    /**
     * Test: Eliminar publicación
     * Verifica que el servicio elimina correctamente una publicación
     */
    @Test
    void eliminarPublicacion_debeEliminarPublicacion() {
        // Arrange
        Long id = 1L;
        // El servicio verifica que la publicación existe antes de eliminar
        when(publicacionRepository.existsById(id)).thenReturn(true);
        doNothing().when(publicacionRepository).deleteById(id);
        
        // Act
        publicacionService.eliminarPublicacion(id);
        
        // Assert
        verify(publicacionRepository, times(1)).existsById(id);
        verify(publicacionRepository, times(1)).deleteById(id);
    }

    /**
     * Test: Obtener todas las publicaciones
     * Verifica que el servicio retorna correctamente todas las publicaciones
     */
    @Test
    void obtenerTodasPublicaciones_debeRetornarLista() {
        // Arrange
        List<Publicacion> publicaciones = new ArrayList<>();
        publicaciones.add(publicacionTest);
        when(publicacionRepository.findAll()).thenReturn(publicaciones);
        
        // Act
        List<Publicacion> resultado = publicacionService.obtenerTodasPublicaciones();
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(publicacionRepository, times(1)).findAll();
    }

    /**
     * Test: Obtener publicaciones visibles
     * Verifica que el servicio retorna solo publicaciones no ocultas
     */
    @Test
    void obtenerPublicacionesVisibles_debeRetornarSoloVisibles() {
        // Arrange
        List<Publicacion> publicaciones = new ArrayList<>();
        publicaciones.add(publicacionTest);
        when(publicacionRepository.findAllNotOculta()).thenReturn(publicaciones);
        
        // Act
        List<Publicacion> resultado = publicacionService.obtenerPublicacionesVisibles();
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(publicacionRepository, times(1)).findAllNotOculta();
    }

    /**
     * Test: Obtener publicaciones por tema
     * Verifica que el servicio retorna correctamente las publicaciones de un tema
     */
    @Test
    void obtenerPublicacionesPorTema_debeRetornarLista() {
        // Arrange
        Long temaId = 1L;
        List<Publicacion> publicaciones = new ArrayList<>();
        publicaciones.add(publicacionTest);
        when(publicacionRepository.findByTemaIdOrderByFechaDesc(temaId)).thenReturn(publicaciones);
        
        // Act
        List<Publicacion> resultado = publicacionService.obtenerPublicacionesPorTema(temaId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(temaId, resultado.get(0).getTemaId());
        verify(publicacionRepository, times(1)).findByTemaIdOrderByFechaDesc(temaId);
    }

    /**
     * Test: Obtener publicaciones visibles por tema
     * Verifica que el servicio retorna solo publicaciones visibles de un tema
     */
    @Test
    void obtenerPublicacionesVisiblesPorTema_debeRetornarSoloVisibles() {
        // Arrange
        Long temaId = 1L;
        List<Publicacion> publicaciones = new ArrayList<>();
        publicaciones.add(publicacionTest);
        when(publicacionRepository.findByTemaIdAndNotOculta(temaId)).thenReturn(publicaciones);
        
        // Act
        List<Publicacion> resultado = publicacionService.obtenerPublicacionesVisiblesPorTema(temaId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(publicacionRepository, times(1)).findByTemaIdAndNotOculta(temaId);
    }

    /**
     * Test: Obtener publicaciones por usuario
     * Verifica que el servicio retorna correctamente las publicaciones de un usuario
     */
    @Test
    void obtenerPublicacionesPorUsuario_debeRetornarLista() {
        // Arrange
        Long usuarioId = 1L;
        List<Publicacion> publicaciones = new ArrayList<>();
        publicaciones.add(publicacionTest);
        when(publicacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId)).thenReturn(publicaciones);
        
        // Act
        List<Publicacion> resultado = publicacionService.obtenerPublicacionesPorUsuario(usuarioId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(usuarioId, resultado.get(0).getUsuarioId());
        verify(publicacionRepository, times(1)).findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    /**
     * Test: Obtener publicaciones visibles por usuario
     * Verifica que el servicio retorna solo publicaciones visibles de un usuario
     */
    @Test
    void obtenerPublicacionesVisiblesPorUsuario_debeRetornarSoloVisibles() {
        // Arrange
        Long usuarioId = 1L;
        List<Publicacion> publicaciones = new ArrayList<>();
        publicaciones.add(publicacionTest);
        when(publicacionRepository.findByUsuarioIdAndNotOculta(usuarioId)).thenReturn(publicaciones);
        
        // Act
        List<Publicacion> resultado = publicacionService.obtenerPublicacionesVisiblesPorUsuario(usuarioId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(publicacionRepository, times(1)).findByUsuarioIdAndNotOculta(usuarioId);
    }

    /**
     * Test: Buscar publicaciones
     * Verifica que el servicio busca correctamente publicaciones por texto
     */
    @Test
    void buscarPublicaciones_debeRetornarLista() {
        // Arrange
        String query = "test";
        List<Publicacion> publicaciones = new ArrayList<>();
        publicaciones.add(publicacionTest);
        // El servicio usa trim() en el query
        when(publicacionRepository.searchPublicaciones(query.trim())).thenReturn(publicaciones);
        
        // Act
        List<Publicacion> resultado = publicacionService.buscarPublicaciones(query);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(publicacionRepository, times(1)).searchPublicaciones(query.trim());
    }

    /**
     * Test: Buscar publicaciones con query vacío
     * Verifica que el servicio retorna todas las publicaciones visibles cuando el query está vacío
     */
    @Test
    void buscarPublicaciones_conQueryVacio_debeRetornarTodasVisibles() {
        // Arrange
        String queryVacio = "";
        List<Publicacion> publicaciones = new ArrayList<>();
        publicaciones.add(publicacionTest);
        // Cuando el query está vacío, el servicio retorna findAllNotOculta()
        when(publicacionRepository.findAllNotOculta()).thenReturn(publicaciones);
        
        // Act
        List<Publicacion> resultado = publicacionService.buscarPublicaciones(queryVacio);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(publicacionRepository, times(1)).findAllNotOculta();
        verify(publicacionRepository, never()).searchPublicaciones(anyString());
    }

    /**
     * Test: Actualizar imagen de publicación
     * Verifica que el servicio actualiza correctamente la imagen de una publicación
     */
    @Test
    void actualizarImagenPublicacion_debeRetornarPublicacionActualizada() {
        // Arrange
        Long id = 1L;
        String imageUrl = "https://example.com/image.jpg";
        publicacionTest.setImageUrl(imageUrl);
        
        when(publicacionRepository.findById(id)).thenReturn(Optional.of(publicacionTest));
        when(publicacionRepository.save(any(Publicacion.class))).thenReturn(publicacionTest);
        
        // Act
        Publicacion resultado = publicacionService.actualizarImagenPublicacion(id, imageUrl);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(imageUrl, resultado.getImageUrl());
        verify(publicacionRepository, times(1)).findById(id);
        verify(publicacionRepository, times(1)).save(any(Publicacion.class));
    }

    /**
     * Test: Mostrar publicación oculta
     * Verifica que el servicio muestra una publicación previamente oculta
     */
    @Test
    void mostrarPublicacion_debeMostrarYLimpiarDatosDeBaneo() {
        // Arrange
        Long id = 1L;
        publicacionTest.setOculta(true);
        publicacionTest.setMotivoBaneo("Motivo anterior");
        publicacionTest.setFechaBaneo(LocalDateTime.now());
        
        when(publicacionRepository.findById(id)).thenReturn(Optional.of(publicacionTest));
        when(publicacionRepository.save(any(Publicacion.class))).thenAnswer(invocation -> {
            Publicacion p = invocation.getArgument(0);
            p.setOculta(false);
            p.setMotivoBaneo(null);
            p.setFechaBaneo(null);
            return p;
        });
        
        // Act
        Publicacion resultado = publicacionService.mostrarPublicacion(id);
        
        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.getOculta());
        assertNull(resultado.getMotivoBaneo());
        assertNull(resultado.getFechaBaneo());
        verify(publicacionRepository, times(1)).findById(id);
        verify(publicacionRepository, times(1)).save(any(Publicacion.class));
    }

    /**
     * Test: Contar publicaciones por tema
     * Verifica que el servicio cuenta correctamente las publicaciones de un tema
     */
    @Test
    void contarPublicacionesPorTema_debeRetornarCantidad() {
        // Arrange
        Long temaId = 1L;
        when(publicacionRepository.countByTemaId(temaId)).thenReturn(5L);
        
        // Act
        long resultado = publicacionService.contarPublicacionesPorTema(temaId);
        
        // Assert
        assertEquals(5L, resultado);
        verify(publicacionRepository, times(1)).countByTemaId(temaId);
    }

    /**
     * Test: Contar publicaciones por usuario
     * Verifica que el servicio cuenta correctamente las publicaciones de un usuario
     */
    @Test
    void contarPublicacionesPorUsuario_debeRetornarCantidad() {
        // Arrange
        Long usuarioId = 1L;
        when(publicacionRepository.countByUsuarioId(usuarioId)).thenReturn(3L);
        
        // Act
        long resultado = publicacionService.contarPublicacionesPorUsuario(usuarioId);
        
        // Assert
        assertEquals(3L, resultado);
        verify(publicacionRepository, times(1)).countByUsuarioId(usuarioId);
    }

    /**
     * Test: Eliminar publicación inexistente
     * Verifica que el servicio lanza una excepción cuando la publicación no existe
     */
    @Test
    void eliminarPublicacion_conIdInexistente_debeLanzarExcepcion() {
        // Arrange
        Long idInexistente = 999L;
        when(publicacionRepository.existsById(idInexistente)).thenReturn(false);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            publicacionService.eliminarPublicacion(idInexistente);
        });
        
        assertTrue(exception.getMessage().contains("Publicación no encontrada ID: " + idInexistente));
        verify(publicacionRepository, times(1)).existsById(idInexistente);
        verify(publicacionRepository, never()).deleteById(anyLong());
    }

    /**
     * Test: Actualizar publicación inexistente
     * Verifica que el servicio lanza una excepción cuando la publicación no existe
     */
    @Test
    void actualizarPublicacion_conIdInexistente_debeLanzarExcepcion() {
        // Arrange
        Long idInexistente = 999L;
        when(publicacionRepository.findById(idInexistente)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            publicacionService.actualizarPublicacion(idInexistente, "Nuevo título", "Nueva descripción");
        });
        
        assertTrue(exception.getMessage().contains("Publicación no encontrada ID: " + idInexistente));
        verify(publicacionRepository, times(1)).findById(idInexistente);
        verify(publicacionRepository, never()).save(any(Publicacion.class));
    }

    /**
     * Test: Actualizar imagen de publicación inexistente
     * Verifica que el servicio lanza una excepción cuando la publicación no existe
     */
    @Test
    void actualizarImagenPublicacion_conIdInexistente_debeLanzarExcepcion() {
        // Arrange
        Long idInexistente = 999L;
        when(publicacionRepository.findById(idInexistente)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            publicacionService.actualizarImagenPublicacion(idInexistente, "https://example.com/image.jpg");
        });
        
        assertTrue(exception.getMessage().contains("Publicación no encontrada ID: " + idInexistente));
        verify(publicacionRepository, times(1)).findById(idInexistente);
        verify(publicacionRepository, never()).save(any(Publicacion.class));
    }

    /**
     * Test: Ocultar publicación inexistente
     * Verifica que el servicio lanza una excepción cuando la publicación no existe
     */
    @Test
    void ocultarPublicacion_conIdInexistente_debeLanzarExcepcion() {
        // Arrange
        Long idInexistente = 999L;
        when(publicacionRepository.findById(idInexistente)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            publicacionService.ocultarPublicacion(idInexistente, "Motivo de baneo");
        });
        
        assertTrue(exception.getMessage().contains("Publicación no encontrada ID: " + idInexistente));
        verify(publicacionRepository, times(1)).findById(idInexistente);
        verify(publicacionRepository, never()).save(any(Publicacion.class));
    }

    /**
     * Test: Mostrar publicación inexistente
     * Verifica que el servicio lanza una excepción cuando la publicación no existe
     */
    @Test
    void mostrarPublicacion_conIdInexistente_debeLanzarExcepcion() {
        // Arrange
        Long idInexistente = 999L;
        when(publicacionRepository.findById(idInexistente)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            publicacionService.mostrarPublicacion(idInexistente);
        });
        
        assertTrue(exception.getMessage().contains("Publicación no encontrada ID: " + idInexistente));
        verify(publicacionRepository, times(1)).findById(idInexistente);
        verify(publicacionRepository, never()).save(any(Publicacion.class));
    }
}
