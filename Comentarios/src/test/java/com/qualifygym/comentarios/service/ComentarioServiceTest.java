package com.qualifygym.comentarios.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.qualifygym.comentarios.model.Comentario;
import com.qualifygym.comentarios.repository.ComentarioRepository;
import com.qualifygym.comentarios.client.UsuarioClient;
import com.qualifygym.comentarios.client.PublicacionClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

/**
 * Tests unitarios para ComentarioService
 * 
 * Esta clase contiene tests que verifican la lógica de negocio del servicio de comentarios,
 * incluyendo creación, actualización, eliminación, moderación y validación de integridad
 * con usuarios y publicaciones. Utiliza mocks para aislar las pruebas de la capa de persistencia
 * y de los microservicios externos.
 */
class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private PublicacionClient publicacionClient;

    @InjectMocks
    private ComentarioService comentarioService;

    private Comentario comentarioTest;

    /**
     * Configuración inicial antes de cada test
     * Crea objetos de prueba para comentarios
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        comentarioTest = new Comentario();
        comentarioTest.setIdComentario(1L);
        comentarioTest.setComentario("Comentario de prueba");
        comentarioTest.setFechaRegistro(LocalDateTime.now());
        comentarioTest.setOculto(false);
        comentarioTest.setUsuarioId(1L);
        comentarioTest.setPublicacionId(1L);
    }

    /**
     * Test: Crear comentario exitosamente
     * Verifica que el servicio crea un comentario cuando el usuario y publicación existen
     */
    @Test
    void crearComentario_debeRetornarComentarioCreado() {
        // Arrange
        String comentario = "Comentario de prueba";
        Long usuarioId = 1L;
        Long publicacionId = 1L;

        // Configurar mocks para validar que usuario y publicación existen
        when(usuarioClient.existeUsuario(usuarioId)).thenReturn(true);
        when(publicacionClient.existePublicacion(publicacionId)).thenReturn(true);
        
        Comentario guardado = new Comentario();
        guardado.setIdComentario(1L);
        guardado.setComentario(comentario);
        guardado.setUsuarioId(usuarioId);
        guardado.setPublicacionId(publicacionId);
        guardado.setFechaRegistro(LocalDateTime.now());
        guardado.setOculto(false);

        when(comentarioRepository.save(any(Comentario.class))).thenReturn(guardado);

        // Act
        Comentario resultado = comentarioService.crearComentario(comentario, usuarioId, publicacionId);

        // Assert
        assertNotNull(resultado);
        assertEquals(comentario, resultado.getComentario());
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(publicacionId, resultado.getPublicacionId());
        assertFalse(resultado.getOculto());
        assertNotNull(resultado.getFechaRegistro());
        
        // Verificar que se validaron usuario y publicación
        verify(usuarioClient, times(1)).existeUsuario(usuarioId);
        verify(publicacionClient, times(1)).existePublicacion(publicacionId);
        verify(comentarioRepository, times(1)).save(any(Comentario.class));
    }

    /**
     * Test: Crear comentario con texto vacío
     * Verifica que el servicio lanza una excepción cuando el comentario está vacío
     */
    @Test
    void crearComentario_conComentarioVacio_debeLanzarExcepcion() {
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            comentarioService.crearComentario("", 1L, 1L);
        });
        
        // Verificar que no se guardó nada
        verify(comentarioRepository, never()).save(any(Comentario.class));
    }

    /**
     * Test: Crear comentario con usuario inexistente
     * Verifica que el servicio lanza una excepción cuando el usuario no existe
     */
    @Test
    void crearComentario_conUsuarioInexistente_debeLanzarExcepcion() {
        // Arrange
        Long usuarioIdInexistente = 999L;
        when(usuarioClient.existeUsuario(usuarioIdInexistente)).thenReturn(false);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            comentarioService.crearComentario("Comentario", usuarioIdInexistente, 1L);
        });
        
        assertTrue(exception.getMessage().contains("El usuario con ID 999 no existe"));
        verify(usuarioClient, times(1)).existeUsuario(usuarioIdInexistente);
        verify(publicacionClient, never()).existePublicacion(anyLong());
        verify(comentarioRepository, never()).save(any(Comentario.class));
    }

    /**
     * Test: Crear comentario con publicación inexistente
     * Verifica que el servicio lanza una excepción cuando la publicación no existe
     */
    @Test
    void crearComentario_conPublicacionInexistente_debeLanzarExcepcion() {
        // Arrange
        Long publicacionIdInexistente = 999L;
        when(usuarioClient.existeUsuario(1L)).thenReturn(true);
        when(publicacionClient.existePublicacion(publicacionIdInexistente)).thenReturn(false);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            comentarioService.crearComentario("Comentario", 1L, publicacionIdInexistente);
        });
        
        assertTrue(exception.getMessage().contains("La publicación con ID 999 no existe"));
        verify(usuarioClient, times(1)).existeUsuario(1L);
        verify(publicacionClient, times(1)).existePublicacion(publicacionIdInexistente);
        verify(comentarioRepository, never()).save(any(Comentario.class));
    }

    /**
     * Test: Actualizar comentario exitosamente
     * Verifica que el servicio actualiza correctamente el texto del comentario
     */
    @Test
    void actualizarComentario_conDatosValidos_debeRetornarComentarioActualizado() {
        // Arrange
        Long id = 1L;
        String nuevoComentario = "Comentario actualizado";
        
        comentarioTest.setComentario(nuevoComentario);
        when(comentarioRepository.findById(id)).thenReturn(Optional.of(comentarioTest));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentarioTest);
        
        // Act
        Comentario resultado = comentarioService.actualizarComentario(id, nuevoComentario);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(nuevoComentario, resultado.getComentario());
        verify(comentarioRepository, times(1)).findById(id);
        verify(comentarioRepository, times(1)).save(any(Comentario.class));
    }

    /**
     * Test: Actualizar comentario inexistente
     * Verifica que el servicio lanza una excepción cuando el comentario no existe
     */
    @Test
    void actualizarComentario_conIdInexistente_debeLanzarExcepcion() {
        // Arrange
        Long idInexistente = 999L;
        when(comentarioRepository.findById(idInexistente)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            comentarioService.actualizarComentario(idInexistente, "Nuevo comentario");
        });
        
        assertTrue(exception.getMessage().contains("Comentario no encontrado"));
        verify(comentarioRepository, times(1)).findById(idInexistente);
        verify(comentarioRepository, never()).save(any(Comentario.class));
    }

    /**
     * Test: Ocultar comentario exitosamente
     * Verifica que el servicio oculta un comentario y establece motivo y fecha de baneo
     */
    @Test
    void ocultarComentario_conMotivo_debeOcultarYEstablecerMotivo() {
        // Arrange
        Long id = 1L;
        String motivo = "Contenido inapropiado";
        
        comentarioTest.setOculto(true);
        comentarioTest.setMotivoBaneo(motivo);
        comentarioTest.setFechaBaneo(LocalDateTime.now());
        
        when(comentarioRepository.findById(id)).thenReturn(Optional.of(comentarioTest));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentarioTest);
        
        // Act
        Comentario resultado = comentarioService.ocultarComentario(id, motivo);
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getOculto());
        assertEquals(motivo, resultado.getMotivoBaneo());
        assertNotNull(resultado.getFechaBaneo());
        verify(comentarioRepository, times(1)).findById(id);
        verify(comentarioRepository, times(1)).save(any(Comentario.class));
    }

    /**
     * Test: Mostrar comentario oculto
     * Verifica que el servicio muestra un comentario previamente oculto
     */
    @Test
    void mostrarComentario_debeMostrarYLimpiarDatosDeBaneo() {
        // Arrange
        Long id = 1L;
        comentarioTest.setOculto(true);
        comentarioTest.setMotivoBaneo("Motivo anterior");
        comentarioTest.setFechaBaneo(LocalDateTime.now());
        
        when(comentarioRepository.findById(id)).thenReturn(Optional.of(comentarioTest));
        when(comentarioRepository.save(any(Comentario.class))).thenAnswer(invocation -> {
            Comentario c = invocation.getArgument(0);
            c.setOculto(false);
            c.setMotivoBaneo(null);
            c.setFechaBaneo(null);
            return c;
        });
        
        // Act
        Comentario resultado = comentarioService.mostrarComentario(id);
        
        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.getOculto());
        assertNull(resultado.getMotivoBaneo());
        assertNull(resultado.getFechaBaneo());
        verify(comentarioRepository, times(1)).findById(id);
        verify(comentarioRepository, times(1)).save(any(Comentario.class));
    }

    /**
     * Test: Eliminar comentario
     * Verifica que el servicio elimina correctamente un comentario
     */
    @Test
    void eliminarComentario_debeEliminarComentario() {
        // Arrange
        Long id = 1L;
        // El servicio verifica que El comentario existe antes de eliminar
        when(comentarioRepository.existsById(id)).thenReturn(true);
        doNothing().when(comentarioRepository).deleteById(id);
        
        // Act
        comentarioService.eliminarComentario(id);
        
        // Assert
        verify(comentarioRepository, times(1)).deleteById(id);
    }

    /**
     * Test: Obtener comentarios por publicación
     * Verifica que el servicio retorna correctamente los comentarios de una publicación
     */
    @Test
    void obtenerComentariosPorPublicacion_debeRetornarLista() {
        // Arrange
        Long publicacionId = 1L;
        List<Comentario> comentarios = new ArrayList<>();
        comentarios.add(comentarioTest);
        
        when(comentarioRepository.findByPublicacionIdOrderByFechaRegistroDesc(publicacionId))
                .thenReturn(comentarios);
        
        // Act
        List<Comentario> resultado = comentarioService.obtenerComentariosPorPublicacion(publicacionId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(publicacionId, resultado.get(0).getPublicacionId());
        verify(comentarioRepository, times(1))
                .findByPublicacionIdOrderByFechaRegistroDesc(publicacionId);
    }

    /**
     * Test: Obtener todos los comentarios
     * Verifica que el servicio retorna correctamente todos los comentarios
     */
    @Test
    void obtenerTodosComentarios_debeRetornarLista() {
        // Arrange
        List<Comentario> comentarios = new ArrayList<>();
        comentarios.add(comentarioTest);
        
        when(comentarioRepository.findAll()).thenReturn(comentarios);
        
        // Act
        List<Comentario> resultado = comentarioService.obtenerTodosComentarios();
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(comentarioRepository, times(1)).findAll();
    }

    /**
     * Test: Obtener comentarios visibles por publicación
     * Verifica que el servicio retorna solo comentarios no ocultos
     */
    @Test
    void obtenerComentariosVisiblesPorPublicacion_debeRetornarSoloVisibles() {
        // Arrange
        Long publicacionId = 1L;
        List<Comentario> comentarios = new ArrayList<>();
        comentarios.add(comentarioTest);
        
        when(comentarioRepository.findByPublicacionIdAndNotOculto(publicacionId))
                .thenReturn(comentarios);
        
        // Act
        List<Comentario> resultado = comentarioService.obtenerComentariosVisiblesPorPublicacion(publicacionId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(comentarioRepository, times(1)).findByPublicacionIdAndNotOculto(publicacionId);
    }

    /**
     * Test: Obtener comentarios por usuario
     * Verifica que el servicio retorna correctamente los comentarios de un usuario
     */
    @Test
    void obtenerComentariosPorUsuario_debeRetornarLista() {
        // Arrange
        Long usuarioId = 1L;
        List<Comentario> comentarios = new ArrayList<>();
        comentarios.add(comentarioTest);
        
        when(comentarioRepository.findByUsuarioIdOrderByFechaRegistroDesc(usuarioId))
                .thenReturn(comentarios);
        
        // Act
        List<Comentario> resultado = comentarioService.obtenerComentariosPorUsuario(usuarioId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(usuarioId, resultado.get(0).getUsuarioId());
        verify(comentarioRepository, times(1)).findByUsuarioIdOrderByFechaRegistroDesc(usuarioId);
    }

    /**
     * Test: Contar comentarios por publicación
     * Verifica que el servicio cuenta correctamente los comentarios de una publicación
     */
    @Test
    void contarComentariosPorPublicacion_debeRetornarCantidad() {
        // Arrange
        Long publicacionId = 1L;
        when(comentarioRepository.countByPublicacionId(publicacionId)).thenReturn(5L);
        
        // Act
        long resultado = comentarioService.contarComentariosPorPublicacion(publicacionId);
        
        // Assert
        assertEquals(5L, resultado);
        verify(comentarioRepository, times(1)).countByPublicacionId(publicacionId);
    }

    /**
     * Test: Contar comentarios por usuario
     * Verifica que el servicio cuenta correctamente los comentarios de un usuario
     */
    @Test
    void contarComentariosPorUsuario_debeRetornarCantidad() {
        // Arrange
        Long usuarioId = 1L;
        when(comentarioRepository.countByUsuarioId(usuarioId)).thenReturn(3L);
        
        // Act
        long resultado = comentarioService.contarComentariosPorUsuario(usuarioId);
        
        // Assert
        assertEquals(3L, resultado);
        verify(comentarioRepository, times(1)).countByUsuarioId(usuarioId);
    }
}
