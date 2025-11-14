package com.qualifygym.comentarios.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.qualifygym.comentarios.model.Comentario;
import com.qualifygym.comentarios.repository.ComentarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @InjectMocks
    private ComentarioService comentarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearComentario_debeRetornarComentarioCreado() {
        String comentario = "Comentario de prueba";
        Long usuarioId = 1L;
        Long publicacionId = 1L;

        Comentario guardado = new Comentario();
        guardado.setIdComentario(1L);
        guardado.setComentario(comentario);
        guardado.setUsuarioId(usuarioId);
        guardado.setPublicacionId(publicacionId);
        guardado.setFechaRegistro(java.time.LocalDateTime.now());
        guardado.setOculto(false);

        when(comentarioRepository.save(any(Comentario.class))).thenReturn(guardado);

        Comentario resultado = comentarioService.crearComentario(comentario, usuarioId, publicacionId);

        assertNotNull(resultado);
        assertEquals(comentario, resultado.getComentario());
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(publicacionId, resultado.getPublicacionId());
        assertFalse(resultado.getOculto());
    }

    @Test
    void crearComentario_conComentarioVacio_debeLanzarExcepcion() {
        assertThrows(RuntimeException.class, () -> {
            comentarioService.crearComentario("", 1L, 1L);
        });
    }
}

