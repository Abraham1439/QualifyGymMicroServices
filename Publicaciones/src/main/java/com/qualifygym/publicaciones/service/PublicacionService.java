package com.qualifygym.publicaciones.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qualifygym.publicaciones.client.TemaClient;
import com.qualifygym.publicaciones.client.UsuarioClient;
import com.qualifygym.publicaciones.model.Publicacion;
import com.qualifygym.publicaciones.repository.PublicacionRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PublicacionService {

    @Autowired
    private PublicacionRepository publicacionRepository;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private TemaClient temaClient;

    // Obtener todas las publicaciones
    public List<Publicacion> obtenerTodasPublicaciones() {
        return publicacionRepository.findAll();
    }

    // Obtener todas las publicaciones visibles (no ocultas)
    public List<Publicacion> obtenerPublicacionesVisibles() {
        return publicacionRepository.findAllNotOculta();
    }

    // Obtener publicación por ID
    public Optional<Publicacion> obtenerPublicacionPorId(Long id) {
        return publicacionRepository.findById(id);
    }

    // Obtener publicaciones por tema
    public List<Publicacion> obtenerPublicacionesPorTema(Long temaId) {
        return publicacionRepository.findByTemaIdOrderByFechaDesc(temaId);
    }

    // Obtener publicaciones visibles por tema
    public List<Publicacion> obtenerPublicacionesVisiblesPorTema(Long temaId) {
        return publicacionRepository.findByTemaIdAndNotOculta(temaId);
    }

    // Obtener publicaciones por usuario
    public List<Publicacion> obtenerPublicacionesPorUsuario(Long usuarioId) {
        return publicacionRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
    }

    // Obtener publicaciones visibles por usuario
    public List<Publicacion> obtenerPublicacionesVisiblesPorUsuario(Long usuarioId) {
        return publicacionRepository.findByUsuarioIdAndNotOculta(usuarioId);
    }

    // Buscar publicaciones por título o descripción
    public List<Publicacion> buscarPublicaciones(String query) {
        if (query == null || query.trim().isEmpty()) {
            return publicacionRepository.findAllNotOculta();
        }
        return publicacionRepository.searchPublicaciones(query.trim());
    }

    // Crear nueva publicación
    public Publicacion crearPublicacion(String titulo, String descripcion, Long usuarioId, Long temaId, String imageUrl) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new RuntimeException("El título no puede estar vacío");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new RuntimeException("La descripción no puede estar vacía");
        }
        if (usuarioId == null || usuarioId <= 0) {
            throw new RuntimeException("El ID de usuario es inválido");
        }
        if (temaId == null || temaId <= 0) {
            throw new RuntimeException("El ID de tema es inválido");
        }

        // Validar que el usuario existe
        if (!usuarioClient.existeUsuario(usuarioId)) {
            throw new RuntimeException("El usuario con ID " + usuarioId + " no existe");
        }

        // Validar que el tema existe
        if (!temaClient.existeTema(temaId)) {
            throw new RuntimeException("El tema con ID " + temaId + " no existe");
        }

        Publicacion nueva = new Publicacion();
        nueva.setTitulo(titulo.trim());
        nueva.setDescripcion(descripcion.trim());
        nueva.setFecha(System.currentTimeMillis());
        nueva.setOculta(false);
        nueva.setUsuarioId(usuarioId);
        nueva.setTemaId(temaId);
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            nueva.setImageUrl(imageUrl.trim());
        }

        return publicacionRepository.save(nueva);
    }

    // Actualizar publicación
    public Publicacion actualizarPublicacion(Long id, String titulo, String descripcion) {
        Publicacion existente = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada ID: " + id));

        if (titulo != null && !titulo.trim().isEmpty()) {
            existente.setTitulo(titulo.trim());
        }
        if (descripcion != null && !descripcion.trim().isEmpty()) {
            existente.setDescripcion(descripcion.trim());
        }

        return publicacionRepository.save(existente);
    }

    // Actualizar imagen de publicación
    public Publicacion actualizarImagenPublicacion(Long id, String imageUrl) {
        Publicacion existente = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada ID: " + id));

        existente.setImageUrl(imageUrl != null ? imageUrl.trim() : null);

        return publicacionRepository.save(existente);
    }

    // Ocultar publicación
    public Publicacion ocultarPublicacion(Long id, String motivoBaneo) {
        Publicacion existente = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada ID: " + id));

        existente.setOculta(true);
        existente.setFechaBaneo(System.currentTimeMillis());
        if (motivoBaneo != null && !motivoBaneo.trim().isEmpty()) {
            existente.setMotivoBaneo(motivoBaneo.trim());
        }

        return publicacionRepository.save(existente);
    }

    // Mostrar publicación (desocultar)
    public Publicacion mostrarPublicacion(Long id) {
        Publicacion existente = publicacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publicación no encontrada ID: " + id));

        existente.setOculta(false);
        existente.setFechaBaneo(null);
        existente.setMotivoBaneo(null);

        return publicacionRepository.save(existente);
    }

    // Eliminar publicación
    public void eliminarPublicacion(Long id) {
        if (!publicacionRepository.existsById(id)) {
            throw new RuntimeException("Publicación no encontrada ID: " + id);
        }
        publicacionRepository.deleteById(id);
    }

    // Contar publicaciones por tema
    public long contarPublicacionesPorTema(Long temaId) {
        return publicacionRepository.countByTemaId(temaId);
    }

    // Contar publicaciones por usuario
    public long contarPublicacionesPorUsuario(Long usuarioId) {
        return publicacionRepository.countByUsuarioId(usuarioId);
    }
}

