package com.QualifyGym.tema.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.QualifyGym.tema.model.Tema;
import com.QualifyGym.tema.repository.TemaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TemaService {
    
    @Autowired
    private TemaRepository temaRepository;

    // Obtener todos los temas
    public List<Tema> obtenerTodosTemas() {
        return temaRepository.findAll();
    }

    // Obtener tema por ID
    public Optional<Tema> obtenerTemaPorId(Long id) {
        return temaRepository.findById(id);
    }

    // Obtener temas por estado
    public List<Tema> obtenerTemasPorEstado(Long estadoId) {
        return temaRepository.findByEstadoId(estadoId);
    }

    // Buscar temas por nombre
    public List<Tema> buscarTemas(String query) {
        if (query == null || query.trim().isEmpty()) {
            return temaRepository.findAll();
        }
        return temaRepository.buscarPorNombre(query.trim());
    }

    // Obtener tema por nombre exacto
    public Optional<Tema> obtenerTemaPorNombre(String nombre) {
        return temaRepository.findByNombreTema(nombre);
    }

    // Verificar si existe un tema por nombre
    public boolean existePorNombre(String nombre) {
        return temaRepository.existsByNombreTema(nombre);
    }

    // Crear nuevo tema
    public Tema crearTema(String nombreTema, Long estadoId) {
        if (nombreTema == null || nombreTema.trim().isEmpty()) {
            throw new RuntimeException("El nombre del tema no puede estar vacío");
        }
        if (estadoId == null || estadoId <= 0) {
            throw new RuntimeException("El ID de estado es inválido");
        }
        
        // Verificar si ya existe un tema con el mismo nombre
        if (temaRepository.existsByNombreTema(nombreTema.trim())) {
            throw new RuntimeException("Ya existe un tema con el nombre: " + nombreTema);
        }

        Tema nuevo = new Tema();
        nuevo.setNombreTema(nombreTema.trim());
        nuevo.setEstadoId(estadoId);
        return temaRepository.save(nuevo);
    }

    // Actualizar tema
    public Tema actualizarTema(Long id, String nombreTema, Long estadoId) {
        Tema existente = temaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tema no encontrado ID: " + id));

        if (nombreTema != null && !nombreTema.trim().isEmpty()) {
            // Verificar si el nuevo nombre ya existe (excepto el mismo tema)
            Optional<Tema> temaConMismoNombre = temaRepository.findByNombreTema(nombreTema.trim());
            if (temaConMismoNombre.isPresent() && !temaConMismoNombre.get().getIdTema().equals(id)) {
                throw new RuntimeException("Ya existe un tema con el nombre: " + nombreTema);
            }
            existente.setNombreTema(nombreTema.trim());
        }
        
        if (estadoId != null && estadoId > 0) {
            existente.setEstadoId(estadoId);
        }

        return temaRepository.save(existente);
    }

    // Eliminar tema
    public void eliminarTema(Long id) {
        if (!temaRepository.existsById(id)) {
            throw new RuntimeException("Tema no encontrado ID: " + id);
        }
        temaRepository.deleteById(id);
    }

    // Contar temas por estado
    public long contarTemasPorEstado(Long estadoId) {
        return temaRepository.countByEstadoId(estadoId);
    }
}
