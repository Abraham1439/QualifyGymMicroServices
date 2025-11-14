package com.qualifygym.publicaciones.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qualifygym.publicaciones.model.Publicacion;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    
    // Buscar publicaciones por ID de usuario, ordenadas por fecha descendente
    List<Publicacion> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
    
    // Buscar publicaciones por ID de tema, ordenadas por fecha descendente
    List<Publicacion> findByTemaIdOrderByFechaDesc(Long temaId);
    
    // Buscar publicaciones por tema que no estén ocultas
    @Query("SELECT p FROM Publicacion p WHERE p.temaId = :temaId AND p.oculta = false ORDER BY p.fecha DESC")
    List<Publicacion> findByTemaIdAndNotOculta(@Param("temaId") Long temaId);
    
    // Buscar publicaciones por usuario que no estén ocultas
    @Query("SELECT p FROM Publicacion p WHERE p.usuarioId = :usuarioId AND p.oculta = false ORDER BY p.fecha DESC")
    List<Publicacion> findByUsuarioIdAndNotOculta(@Param("usuarioId") Long usuarioId);
    
    // Buscar publicaciones visibles (no ocultas), ordenadas por fecha descendente
    @Query("SELECT p FROM Publicacion p WHERE p.oculta = false ORDER BY p.fecha DESC")
    List<Publicacion> findAllNotOculta();
    
    // Buscar publicaciones por título o descripción (búsqueda)
    @Query("SELECT p FROM Publicacion p WHERE (p.titulo LIKE %:query% OR p.descripcion LIKE %:query%) AND p.oculta = false ORDER BY p.fecha DESC")
    List<Publicacion> searchPublicaciones(@Param("query") String query);
    
    // Contar publicaciones por tema
    long countByTemaId(Long temaId);
    
    // Contar publicaciones por usuario
    long countByUsuarioId(Long usuarioId);
    
    // Buscar por ID usando el nombre de columna
    @Query("SELECT p FROM Publicacion p WHERE p.idPublicacion = :id")
    Optional<Publicacion> findByIdPublicacion(@Param("id") Long id);
}

