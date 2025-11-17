package com.qualifygym.estados.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.qualifygym.estados.model.Estado;
import com.qualifygym.estados.repository.EstadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

/**
 * Tests unitarios para EstadoService
 * 
 * Esta clase contiene tests que verifican la lógica de negocio del servicio de estados,
 * incluyendo creación, actualización, eliminación y búsqueda de estados.
 * Utiliza mocks para aislar las pruebas de la capa de persistencia.
 */
class EstadoServiceTest {

    @Mock
    private EstadoRepository estadoRepository;

    @InjectMocks
    private EstadoService estadoService;

    private Estado estadoTest;

    /**
     * Configuración inicial antes de cada test
     * Crea objetos de prueba para estados
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        estadoTest = new Estado();
        estadoTest.setIdEstado(1L);
        estadoTest.setNombre("Activo");
    }

    /**
     * Test: Crear estado exitosamente
     * Verifica que el servicio crea un estado cuando el nombre no existe
     */
    @Test
    void crearEstado_debeRetornarEstadoCreado() {
        // Arrange
        String nombre = "Activo";
        
        Estado guardado = new Estado();
        guardado.setIdEstado(1L);
        guardado.setNombre(nombre);

        when(estadoRepository.existsByNombre(nombre)).thenReturn(false);
        when(estadoRepository.save(any(Estado.class))).thenReturn(guardado);

        // Act
        Estado resultado = estadoService.crearEstado(nombre);

        // Assert
        assertNotNull(resultado);
        assertEquals(nombre, resultado.getNombre());
        assertEquals(1L, resultado.getIdEstado());
        
        // Verificar que se validó la existencia y se guardó
        verify(estadoRepository, times(1)).existsByNombre(nombre);
        verify(estadoRepository, times(1)).save(any(Estado.class));
    }

    /**
     * Test: Crear estado con nombre existente
     * Verifica que el servicio lanza una excepción cuando el nombre ya existe
     */
    @Test
    void crearEstado_conNombreExistente_debeLanzarExcepcion() {
        // Arrange
        String nombre = "Activo";
        when(estadoRepository.existsByNombre(nombre)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            estadoService.crearEstado(nombre);
        });
        
        assertTrue(exception.getMessage().contains("Ya existe un estado con el nombre"));
        verify(estadoRepository, times(1)).existsByNombre(nombre);
        verify(estadoRepository, never()).save(any(Estado.class));
    }

    /**
     * Test: Obtener o crear estado cuando existe
     * Verifica que el servicio retorna el estado existente sin crear uno nuevo
     */
    @Test
    void obtenerOCrearEstado_conEstadoExistente_debeRetornarExistente() {
        // Arrange
        String nombre = "Activo";
        Estado existente = new Estado(1L, nombre);
        
        when(estadoRepository.findByNombre(nombre)).thenReturn(Optional.of(existente));

        // Act
        Estado resultado = estadoService.obtenerOCrearEstado(nombre);

        // Assert
        assertEquals(existente, resultado);
        verify(estadoRepository, times(1)).findByNombre(nombre);
        verify(estadoRepository, never()).save(any(Estado.class));
    }

    /**
     * Test: Obtener o crear estado cuando no existe
     * Verifica que el servicio crea un nuevo estado cuando no existe
     */
    @Test
    void obtenerOCrearEstado_conEstadoInexistente_debeCrearNuevo() {
        // Arrange
        String nombre = "Nuevo Estado";
        Estado nuevo = new Estado();
        nuevo.setIdEstado(2L);
        nuevo.setNombre(nombre);
        
        when(estadoRepository.findByNombre(nombre)).thenReturn(Optional.empty());
        when(estadoRepository.existsByNombre(nombre)).thenReturn(false);
        when(estadoRepository.save(any(Estado.class))).thenReturn(nuevo);

        // Act
        Estado resultado = estadoService.obtenerOCrearEstado(nombre);

        // Assert
        assertNotNull(resultado);
        assertEquals(nombre, resultado.getNombre());
        verify(estadoRepository, times(1)).findByNombre(nombre);
        verify(estadoRepository, times(1)).save(any(Estado.class));
    }

    /**
     * Test: Obtener todos los estados
     * Verifica que el servicio retorna correctamente la lista de estados
     */
    @Test
    void obtenerTodosEstados_debeRetornarLista() {
        // Arrange
        List<Estado> estados = new ArrayList<>();
        estados.add(estadoTest);
        
        when(estadoRepository.findAll()).thenReturn(estados);

        // Act
        List<Estado> resultado = estadoService.obtenerTodosEstados();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Activo", resultado.get(0).getNombre());
        verify(estadoRepository, times(1)).findAll();
    }

    /**
     * Test: Obtener estado por ID existente
     * Verifica que el servicio retorna el estado cuando existe
     */
    @Test
    void obtenerEstadoPorId_conIdExistente_debeRetornarEstado() {
        // Arrange
        Long id = 1L;
        when(estadoRepository.findById(id)).thenReturn(Optional.of(estadoTest));

        // Act
        Optional<Estado> resultado = estadoService.obtenerEstadoPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getIdEstado());
        verify(estadoRepository, times(1)).findById(id);
    }

    /**
     * Test: Obtener estado por ID inexistente
     * Verifica que el servicio retorna Optional vacío cuando el estado no existe
     */
    @Test
    void obtenerEstadoPorId_conIdInexistente_debeRetornarOptionalVacio() {
        // Arrange
        Long id = 999L;
        when(estadoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Estado> resultado = estadoService.obtenerEstadoPorId(id);

        // Assert
        assertFalse(resultado.isPresent());
        verify(estadoRepository, times(1)).findById(id);
    }

    /**
     * Test: Actualizar estado exitosamente
     * Verifica que el servicio actualiza correctamente el nombre del estado
     */
    @Test
    void actualizarEstado_conDatosValidos_debeRetornarEstadoActualizado() {
        // Arrange
        Long id = 1L;
        String nuevoNombre = "Estado Actualizado";
        
        estadoTest.setNombre(nuevoNombre);
        when(estadoRepository.findById(id)).thenReturn(Optional.of(estadoTest));
        when(estadoRepository.existsByNombre(nuevoNombre)).thenReturn(false);
        when(estadoRepository.save(any(Estado.class))).thenReturn(estadoTest);

        // Act
        Estado resultado = estadoService.actualizarEstado(id, nuevoNombre);

        // Assert
        assertNotNull(resultado);
        assertEquals(nuevoNombre, resultado.getNombre());
        verify(estadoRepository, times(1)).findById(id);
        verify(estadoRepository, times(1)).existsByNombre(nuevoNombre);
        verify(estadoRepository, times(1)).save(any(Estado.class));
    }

    /**
     * Test: Eliminar estado
     * Verifica que el servicio elimina correctamente un estado
     */
    @Test
    void eliminarEstado_debeEliminarEstado() {
        // Arrange
        Long id = 1L;
        doNothing().when(estadoRepository).deleteById(id);

        // Act
        estadoService.eliminarEstado(id);

        // Assert
        verify(estadoRepository, times(1)).deleteById(id);
    }
}
