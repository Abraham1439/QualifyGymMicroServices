package com.QualifyGym.tema.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.QualifyGym.tema.model.Tema;
import com.QualifyGym.tema.repository.TemaRepository;
import com.QualifyGym.tema.client.EstadoClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

/**
 * Tests unitarios para TemaService
 * 
 * Esta clase contiene tests que verifican la lógica de negocio del servicio de temas,
 * incluyendo creación, actualización, eliminación y validación de integridad con estados.
 * Utiliza mocks para aislar las pruebas de la capa de persistencia y del microservicio de estados.
 */
class TemaServiceTest {

    @Mock
    private TemaRepository temaRepository;

    @Mock
    private EstadoClient estadoClient;

    @InjectMocks
    private TemaService temaService;

    private Tema temaTest;

    /**
     * Configuración inicial antes de cada test
     * Crea objetos de prueba para temas
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        temaTest = new Tema();
        temaTest.setIdTema(1L);
        temaTest.setNombreTema("Rutinas de Fuerza");
        temaTest.setEstadoId(1L);
    }

    /**
     * Test: Crear tema exitosamente
     * Verifica que el servicio crea un tema cuando el nombre no existe y el estado existe
     */
    @Test
    void crearTema_debeRetornarTemaCreado() {
        // Arrange
        String nombreTema = "Rutinas de Fuerza";
        Long estadoId = 1L;
        
        // Configurar mock para validar que el estado existe
        when(estadoClient.existeEstado(estadoId)).thenReturn(true);
        
        Tema guardado = new Tema();
        guardado.setIdTema(1L);
        guardado.setNombreTema(nombreTema);
        guardado.setEstadoId(estadoId);

        when(temaRepository.existsByNombreTema(nombreTema)).thenReturn(false);
        when(temaRepository.save(any(Tema.class))).thenReturn(guardado);

        // Act
        Tema resultado = temaService.crearTema(nombreTema, estadoId);

        // Assert
        assertNotNull(resultado);
        assertEquals(nombreTema, resultado.getNombreTema());
        assertEquals(estadoId, resultado.getEstadoId());
        
        // Verificar que se validó el estado y se guardó
        verify(estadoClient, times(1)).existeEstado(estadoId);
        verify(temaRepository, times(1)).existsByNombreTema(nombreTema);
        verify(temaRepository, times(1)).save(any(Tema.class));
    }

    /**
     * Test: Crear tema con nombre existente
     * Verifica que el servicio lanza una excepción cuando el nombre ya existe
     */
    @Test
    void crearTema_conNombreExistente_debeLanzarExcepcion() {
        // Arrange
        String nombreTema = "Rutinas de Fuerza";
        when(temaRepository.existsByNombreTema(nombreTema)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            temaService.crearTema(nombreTema, 1L);
        });
        
        assertTrue(exception.getMessage().contains("Ya existe un tema con el nombre"));
        verify(temaRepository, times(1)).existsByNombreTema(nombreTema);
        verify(temaRepository, never()).save(any(Tema.class));
    }

    /**
     * Test: Crear tema con estado inexistente
     * Verifica que el servicio lanza una excepción cuando el estado no existe
     */
    @Test
    void crearTema_conEstadoInexistente_debeLanzarExcepcion() {
        // Arrange
        Long estadoIdInexistente = 999L;
        when(temaRepository.existsByNombreTema(anyString())).thenReturn(false);
        when(estadoClient.existeEstado(estadoIdInexistente)).thenReturn(false);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            temaService.crearTema("Nuevo Tema", estadoIdInexistente);
        });
        
        assertTrue(exception.getMessage().contains("El estado con ID 999 no existe"));
        verify(estadoClient, times(1)).existeEstado(estadoIdInexistente);
        verify(temaRepository, never()).save(any(Tema.class));
    }

    /**
     * Test: Actualizar tema exitosamente
     * Verifica que el servicio actualiza correctamente los campos del tema
     */
    @Test
    void actualizarTema_conDatosValidos_debeRetornarTemaActualizado() {
        // Arrange
        Long id = 1L;
        String nuevoNombre = "Rutinas de Fuerza Avanzadas";
        Long nuevoEstadoId = 2L;
        
        temaTest.setNombreTema(nuevoNombre);
        temaTest.setEstadoId(nuevoEstadoId);
        
        when(temaRepository.findById(id)).thenReturn(Optional.of(temaTest));
        when(estadoClient.existeEstado(nuevoEstadoId)).thenReturn(true);
        when(temaRepository.save(any(Tema.class))).thenReturn(temaTest);
        
        // Act
        Tema resultado = temaService.actualizarTema(id, nuevoNombre, nuevoEstadoId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(nuevoNombre, resultado.getNombreTema());
        assertEquals(nuevoEstadoId, resultado.getEstadoId());
        verify(temaRepository, times(1)).findById(id);
        verify(estadoClient, times(1)).existeEstado(nuevoEstadoId);
        verify(temaRepository, times(1)).save(any(Tema.class));
    }

    /**
     * Test: Obtener todos los temas
     * Verifica que el servicio retorna correctamente la lista de temas
     */
    @Test
    void obtenerTodosTemas_debeRetornarLista() {
        // Arrange
        List<Tema> temas = new ArrayList<>();
        temas.add(temaTest);
        
        when(temaRepository.findAll()).thenReturn(temas);
        
        // Act
        List<Tema> resultado = temaService.obtenerTodosTemas();
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Rutinas de Fuerza", resultado.get(0).getNombreTema());
        verify(temaRepository, times(1)).findAll();
    }

    /**
     * Test: Obtener tema por ID existente
     * Verifica que el servicio retorna el tema cuando existe
     */
    @Test
    void obtenerTemaPorId_conIdExistente_debeRetornarTema() {
        // Arrange
        Long id = 1L;
        when(temaRepository.findById(id)).thenReturn(Optional.of(temaTest));
        
        // Act
        Optional<Tema> resultado = temaService.obtenerTemaPorId(id);
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getIdTema());
        verify(temaRepository, times(1)).findById(id);
    }

    /**
     * Test: Eliminar tema
     * Verifica que el servicio elimina correctamente un tema
     */
    @Test
    void eliminarTema_debeEliminarTema() {
        // Arrange
        Long id = 1L;
        doNothing().when(temaRepository).deleteById(id);
        
        // Act
        temaService.eliminarTema(id);
        
        // Assert
        verify(temaRepository, times(1)).deleteById(id);
    }
}
