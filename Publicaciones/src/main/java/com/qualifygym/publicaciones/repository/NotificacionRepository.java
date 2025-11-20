package com.qualifygym.publicaciones.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qualifygym.publicaciones.model.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    // Buscar todas las notificaciones de un usuario, ordenadas por fecha descendente
    @Query("SELECT n FROM Notificacion n WHERE n.usuarioId = :usuarioId ORDER BY n.fechaCreacion DESC")
    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(@Param("usuarioId") Long usuarioId);
    
    // Buscar notificaciones no leídas de un usuario
    @Query("SELECT n FROM Notificacion n WHERE n.usuarioId = :usuarioId AND n.leida = false ORDER BY n.fechaCreacion DESC")
    List<Notificacion> findNoLeidasByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Contar notificaciones no leídas de un usuario
    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.usuarioId = :usuarioId AND n.leida = false")
    long countNoLeidasByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Buscar notificaciones por publicación
    @Query("SELECT n FROM Notificacion n WHERE n.publicacionId = :publicacionId ORDER BY n.fechaCreacion DESC")
    List<Notificacion> findByPublicacionId(@Param("publicacionId") Long publicacionId);
}

