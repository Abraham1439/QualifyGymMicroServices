package com.qualifygym.comentarios.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qualifygym.comentarios.model.Notificacion;
import com.qualifygym.comentarios.repository.NotificacionRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    /**
     * Crear una nueva notificación
     * @param usuarioId ID del usuario que recibe la notificación
     * @param comentarioId ID del comentario relacionado
     * @param mensaje Mensaje personalizado del admin/moderador
     * @return Notificación creada
     */
    public Notificacion crearNotificacion(Long usuarioId, Long comentarioId, String mensaje) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new RuntimeException("El ID de usuario es inválido");
        }
        if (comentarioId == null || comentarioId <= 0) {
            throw new RuntimeException("El ID de comentario es inválido");
        }
        if (mensaje == null || mensaje.trim().isEmpty()) {
            throw new RuntimeException("El mensaje no puede estar vacío");
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(usuarioId);
        notificacion.setComentarioId(comentarioId);
        notificacion.setMensaje(mensaje.trim());
        notificacion.setFechaCreacion(LocalDateTime.now());
        notificacion.setLeida(false);

        return notificacionRepository.save(notificacion);
    }

    /**
     * Obtener todas las notificaciones de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de notificaciones ordenadas por fecha descendente
     */
    public List<Notificacion> obtenerNotificacionesPorUsuario(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new RuntimeException("El ID de usuario es inválido");
        }
        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    /**
     * Obtener notificaciones no leídas de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de notificaciones no leídas
     */
    public List<Notificacion> obtenerNotificacionesNoLeidasPorUsuario(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new RuntimeException("El ID de usuario es inválido");
        }
        return notificacionRepository.findNoLeidasByUsuarioId(usuarioId);
    }

    /**
     * Marcar una notificación como leída
     * @param notificacionId ID de la notificación
     * @return Notificación actualizada
     */
    public Notificacion marcarComoLeida(Long notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada ID: " + notificacionId));
        
        notificacion.setLeida(true);
        return notificacionRepository.save(notificacion);
    }

    /**
     * Marcar todas las notificaciones de un usuario como leídas
     * @param usuarioId ID del usuario
     */
    public void marcarTodasComoLeidas(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new RuntimeException("El ID de usuario es inválido");
        }
        
        List<Notificacion> notificaciones = notificacionRepository.findNoLeidasByUsuarioId(usuarioId);
        for (Notificacion notificacion : notificaciones) {
            notificacion.setLeida(true);
        }
        notificacionRepository.saveAll(notificaciones);
    }

    /**
     * Contar notificaciones no leídas de un usuario
     * @param usuarioId ID del usuario
     * @return Número de notificaciones no leídas
     */
    public long contarNotificacionesNoLeidas(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new RuntimeException("El ID de usuario es inválido");
        }
        return notificacionRepository.countNoLeidasByUsuarioId(usuarioId);
    }

    /**
     * Eliminar una notificación
     * @param notificacionId ID de la notificación
     */
    public void eliminarNotificacion(Long notificacionId) {
        if (!notificacionRepository.existsById(notificacionId)) {
            throw new RuntimeException("Notificación no encontrada ID: " + notificacionId);
        }
        notificacionRepository.deleteById(notificacionId);
    }
}

