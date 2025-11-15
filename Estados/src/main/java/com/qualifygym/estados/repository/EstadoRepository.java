package com.qualifygym.estados.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qualifygym.estados.model.Estado;

@Repository
public interface EstadoRepository extends JpaRepository<Estado, Long> {
    
    // Buscar estado por nombre
    Optional<Estado> findByNombre(String nombre);
    
    // Buscar estado por nombre (case insensitive)
    @Query("SELECT e FROM Estado e WHERE LOWER(e.nombre) = LOWER(:nombre)")
    Optional<Estado> findByNombreIgnoreCase(@Param("nombre") String nombre);
    
    // Verificar si existe un estado por nombre
    boolean existsByNombre(String nombre);
    
    // Buscar por ID usando el nombre de columna
    @Query("SELECT e FROM Estado e WHERE e.idEstado = :id")
    Optional<Estado> findByIdEstado(@Param("id") Long id);
}

