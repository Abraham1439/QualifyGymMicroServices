package com.QualifyGym.tema.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.QualifyGym.tema.model.Tema;
import com.QualifyGym.tema.repository.TemaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class TemaServiceTest {

    @Mock
    private TemaRepository temaRepository;

    @InjectMocks
    private TemaService temaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearTema_debeRetornarTemaCreado() {
        String nombreTema = "Rutinas de Fuerza";
        Long estadoId = 1L;
        
        Tema guardado = new Tema();
        guardado.setIdTema(1L);
        guardado.setNombreTema(nombreTema);
        guardado.setEstadoId(estadoId);

        when(temaRepository.existsByNombreTema(nombreTema)).thenReturn(false);
        when(temaRepository.save(any(Tema.class))).thenReturn(guardado);

        Tema resultado = temaService.crearTema(nombreTema, estadoId);

        assertNotNull(resultado);
        assertEquals(nombreTema, resultado.getNombreTema());
        assertEquals(estadoId, resultado.getEstadoId());
    }

    @Test
    void crearTema_conNombreExistente_debeLanzarExcepcion() {
        String nombreTema = "Rutinas de Fuerza";
        when(temaRepository.existsByNombreTema(nombreTema)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            temaService.crearTema(nombreTema, 1L);
        });
    }
}

