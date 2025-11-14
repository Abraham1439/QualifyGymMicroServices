package com.qualifygym.comentarios.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qualifygym.comentarios.model.Comentario;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    
    // Buscar comentarios por ID de publicación, ordenados por fecha descendente
    List<Comentario> findByPublicacionIdOrderByFechaRegistroDesc(Long publicacionId);
    
    // Buscar comentarios por ID de usuario, ordenados por fecha descendente
    List<Comentario> findByUsuarioIdOrderByFechaRegistroDesc(Long usuarioId);
    
    // Buscar comentarios por ID de publicación que no estén ocultos
    @Query("SELECT c FROM Comentario c WHERE c.publicacionId = :publicacionId AND c.oculto = false ORDER BY c.fechaRegistro DESC")
    List<Comentario> findByPublicacionIdAndNotOculto(@Param("publicacionId") Long publicacionId);
    
    // Buscar comentarios por ID de usuario que no estén ocultos
    @Query("SELECT c FROM Comentario c WHERE c.usuarioId = :usuarioId AND c.oculto = false ORDER BY c.fechaRegistro DESC")
    List<Comentario> findByUsuarioIdAndNotOculto(@Param("usuarioId") Long usuarioId);
    
    // Contar comentarios por publicación
    long countByPublicacionId(Long publicacionId);
    
    // Contar comentarios por usuario
    long countByUsuarioId(Long usuarioId);
    
    // Buscar por ID (usando el nombre de columna)
    @Query("SELECT c FROM Comentario c WHERE c.idComentario = :id")
    Optional<Comentario> findByIdComentario(@Param("id") Long id);
}

