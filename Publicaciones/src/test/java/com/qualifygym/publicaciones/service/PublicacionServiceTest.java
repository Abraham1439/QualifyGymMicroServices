package com.qualifygym.publicaciones.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.qualifygym.publicaciones.model.Publicacion;
import com.qualifygym.publicaciones.repository.PublicacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class PublicacionServiceTest {

    @Mock
    private PublicacionRepository publicacionRepository;

    @InjectMocks
    private PublicacionService publicacionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearPublicacion_debeRetornarPublicacionCreada() {
        String titulo = "Título de prueba";
        String descripcion = "Descripción de prueba";
        Long usuarioId = 1L;
        Long temaId = 1L;

        Publicacion guardada = new Publicacion();
        guardada.setIdPublicacion(1L);
        guardada.setTitulo(titulo);
        guardada.setDescripcion(descripcion);
        guardada.setUsuarioId(usuarioId);
        guardada.setTemaId(temaId);
        guardada.setFecha(System.currentTimeMillis());
        guardada.setOculta(false);

        when(publicacionRepository.save(any(Publicacion.class))).thenReturn(guardada);

        Publicacion resultado = publicacionService.crearPublicacion(titulo, descripcion, usuarioId, temaId, null);

        assertNotNull(resultado);
        assertEquals(titulo, resultado.getTitulo());
        assertEquals(descripcion, resultado.getDescripcion());
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(temaId, resultado.getTemaId());
        assertFalse(resultado.getOculta());
    }

    @Test
    void crearPublicacion_conTituloVacio_debeLanzarExcepcion() {
        assertThrows(RuntimeException.class, () -> {
            publicacionService.crearPublicacion("", "Descripción", 1L, 1L, null);
        });
    }
}

