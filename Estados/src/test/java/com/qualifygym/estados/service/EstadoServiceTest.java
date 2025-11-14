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

class EstadoServiceTest {

    @Mock
    private EstadoRepository estadoRepository;

    @InjectMocks
    private EstadoService estadoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearEstado_debeRetornarEstadoCreado() {
        String nombre = "Activo";
        
        Estado guardado = new Estado();
        guardado.setIdEstado(1L);
        guardado.setNombre(nombre);

        when(estadoRepository.existsByNombre(nombre)).thenReturn(false);
        when(estadoRepository.save(any(Estado.class))).thenReturn(guardado);

        Estado resultado = estadoService.crearEstado(nombre);

        assertNotNull(resultado);
        assertEquals(nombre, resultado.getNombre());
        assertEquals(1L, resultado.getIdEstado());
    }

    @Test
    void crearEstado_conNombreExistente_debeLanzarExcepcion() {
        String nombre = "Activo";
        when(estadoRepository.existsByNombre(nombre)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            estadoService.crearEstado(nombre);
        });
    }

    @Test
    void obtenerOCrearEstado_conEstadoExistente_debeRetornarExistente() {
        String nombre = "Activo";
        Estado existente = new Estado(1L, nombre);
        
        when(estadoRepository.findByNombre(nombre)).thenReturn(Optional.of(existente));

        Estado resultado = estadoService.obtenerOCrearEstado(nombre);

        assertEquals(existente, resultado);
        verify(estadoRepository, never()).save(any(Estado.class));
    }
}

