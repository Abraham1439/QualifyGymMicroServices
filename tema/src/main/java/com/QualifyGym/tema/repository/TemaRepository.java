package com.QualifyGym.tema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.QualifyGym.tema.model.Tema;

@Repository
public interface TemaRepository extends JpaRepository<Tema, Long> {
    
    // Buscar temas por estado
    List<Tema> findByEstadoId(Long estadoId);
    
    // Buscar tema por nombre
    Optional<Tema> findByNombreTema(String nombreTema);
    
    // Buscar temas por nombre (búsqueda parcial)
    @Query("SELECT t FROM Tema t WHERE t.nombreTema LIKE %:query%")
    List<Tema> buscarPorNombre(@Param("query") String query);
    
    // Verificar si existe un tema por nombre
    boolean existsByNombreTema(String nombreTema);
    
    // Contar temas por estado
    long countByEstadoId(Long estadoId);
    
    // Buscar por ID usando el nombre de columna
    @Query("SELECT t FROM Tema t WHERE t.idTema = :id")
    Optional<Tema> findByIdTema(@Param("id") Long id);
}
