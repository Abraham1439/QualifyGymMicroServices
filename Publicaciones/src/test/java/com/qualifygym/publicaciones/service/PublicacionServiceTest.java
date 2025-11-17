package com.qualifygym.publicaciones.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.qualifygym.publicaciones.model.Publicacion;
import com.qualifygym.publicaciones.repository.PublicacionRepository;
import com.qualifygym.publicaciones.client.UsuarioClient;
import com.qualifygym.publicaciones.client.TemaClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
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
        
        // Act
        Publicacion resultado = publicacionService.ocultarPublicacion(id, motivo);
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.getOculta());
        assertEquals(motivo, resultado.getMotivoBaneo());
        assertNotNull(resultado.getFechaBaneo());
        verify(publicacionRepository, times(1)).findById(id);
        verify(publicacionRepository, times(1)).save(any(Publicacion.class));
    }

    /**
     * Test: Eliminar publicación
     * Verifica que el servicio elimina correctamente una publicación
     */
    @Test
    void eliminarPublicacion_debeEliminarPublicacion() {
        // Arrange
        Long id = 1L;
        doNothing().when(publicacionRepository).deleteById(id);
        
        // Act
        publicacionService.eliminarPublicacion(id);
        
        // Assert
        verify(publicacionRepository, times(1)).deleteById(id);
    }
}
