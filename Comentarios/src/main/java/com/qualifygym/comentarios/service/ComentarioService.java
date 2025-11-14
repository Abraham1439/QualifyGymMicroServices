package com.qualifygym.comentarios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qualifygym.comentarios.model.Comentario;
import com.qualifygym.comentarios.repository.ComentarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    // Obtener todos los comentarios
    public List<Comentario> obtenerTodosComentarios() {
        return comentarioRepository.findAll();
    }

    // Obtener comentario por ID
    public Optional<Comentario> obtenerComentarioPorId(Long id) {
        return comentarioRepository.findById(id);
    }

    // Obtener comentarios por publicación
    public List<Comentario> obtenerComentariosPorPublicacion(Long publicacionId) {
        return comentarioRepository.findByPublicacionIdOrderByFechaRegistroDesc(publicacionId);
    }

    // Obtener comentarios visibles por publicación (no ocultos)
    public List<Comentario> obtenerComentariosVisiblesPorPublicacion(Long publicacionId) {
        return comentarioRepository.findByPublicacionIdAndNotOculto(publicacionId);
    }

    // Obtener comentarios por usuario
    public List<Comentario> obtenerComentariosPorUsuario(Long usuarioId) {
        return comentarioRepository.findByUsuarioIdOrderByFechaRegistroDesc(usuarioId);
    }

    // Crear nuevo comentario
    public Comentario crearComentario(String comentario, Long usuarioId, Long publicacionId) {
        if (comentario == null || comentario.trim().isEmpty()) {
            throw new RuntimeException("El comentario no puede estar vacío");
        }
        if (usuarioId == null || usuarioId <= 0) {
            throw new RuntimeException("El ID de usuario es inválido");
        }
        if (publicacionId == null || publicacionId <= 0) {
            throw new RuntimeException("El ID de publicación es inválido");
        }

        Comentario nuevo = new Comentario();
        nuevo.setComentario(comentario.trim());
        nuevo.setFechaRegistro(System.currentTimeMillis());
        nuevo.setOculto(false);
        nuevo.setUsuarioId(usuarioId);
        nuevo.setPublicacionId(publicacionId);

        return comentarioRepository.save(nuevo);
    }

    // Actualizar comentario
    public Comentario actualizarComentario(Long id, String comentario) {
        Comentario existente = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado ID: " + id));

        if (comentario != null && !comentario.trim().isEmpty()) {
            existente.setComentario(comentario.trim());
        }

        return comentarioRepository.save(existente);
    }

    // Ocultar comentario
    public Comentario ocultarComentario(Long id, String motivoBaneo) {
        Comentario existente = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado ID: " + id));

        existente.setOculto(true);
        existente.setFechaBaneo(System.currentTimeMillis());
        if (motivoBaneo != null && !motivoBaneo.trim().isEmpty()) {
            existente.setMotivoBaneo(motivoBaneo.trim());
        }

        return comentarioRepository.save(existente);
    }

    // Mostrar comentario (desocultar)
    public Comentario mostrarComentario(Long id) {
        Comentario existente = comentarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado ID: " + id));

        existente.setOculto(false);
        existente.setFechaBaneo(null);
        existente.setMotivoBaneo(null);

        return comentarioRepository.save(existente);
    }

    // Eliminar comentario
    public void eliminarComentario(Long id) {
        if (!comentarioRepository.existsById(id)) {
            throw new RuntimeException("Comentario no encontrado ID: " + id);
        }
        comentarioRepository.deleteById(id);
    }

    // Contar comentarios por publicación
    public long contarComentariosPorPublicacion(Long publicacionId) {
        return comentarioRepository.countByPublicacionId(publicacionId);
    }

    // Contar comentarios por usuario
    public long contarComentariosPorUsuario(Long usuarioId) {
        return comentarioRepository.countByUsuarioId(usuarioId);
    }
}

