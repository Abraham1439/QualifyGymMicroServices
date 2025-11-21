package com.qualifygym.imagenes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qualifygym.imagenes.model.Imagen;

@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    
    // Buscar imagen por usuario (foto de perfil)
    @Query("SELECT i FROM Imagen i WHERE i.usuarioId = :usuarioId AND i.tipoImagen = 'PERFIL' ORDER BY i.fechaSubida DESC")
    Optional<Imagen> findFotoPerfilByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Buscar imágenes por publicación
    @Query("SELECT i FROM Imagen i WHERE i.publicacionId = :publicacionId AND i.tipoImagen = 'PUBLICACION' ORDER BY i.fechaSubida DESC")
    List<Imagen> findImagenesByPublicacionId(@Param("publicacionId") Long publicacionId);
    
    // Buscar todas las imágenes de un usuario (perfil y publicaciones)
    @Query("SELECT i FROM Imagen i WHERE i.usuarioId = :usuarioId ORDER BY i.fechaSubida DESC")
    List<Imagen> findImagenesByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Contar imágenes por usuario
    @Query("SELECT COUNT(i) FROM Imagen i WHERE i.usuarioId = :usuarioId")
    long countByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Contar imágenes por publicación
    @Query("SELECT COUNT(i) FROM Imagen i WHERE i.publicacionId = :publicacionId")
    long countByPublicacionId(@Param("publicacionId") Long publicacionId);
    
    // Verificar si existe foto de perfil para un usuario
    @Query("SELECT COUNT(i) > 0 FROM Imagen i WHERE i.usuarioId = :usuarioId AND i.tipoImagen = 'PERFIL'")
    boolean existsFotoPerfilByUsuarioId(@Param("usuarioId") Long usuarioId);
}

