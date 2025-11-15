package com.qualifygym.estados.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qualifygym.estados.model.Estado;
import com.qualifygym.estados.repository.EstadoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EstadoService {

    @Autowired
    private EstadoRepository estadoRepository;

    // Obtener todos los estados
    public List<Estado> obtenerTodosEstados() {
        return estadoRepository.findAll();
    }

    // Obtener estado por ID
    public Optional<Estado> obtenerEstadoPorId(Long id) {
        return estadoRepository.findById(id);
    }

    // Obtener estado por nombre
    public Optional<Estado> obtenerEstadoPorNombre(String nombre) {
        return estadoRepository.findByNombre(nombre);
    }

    // Verificar si existe un estado por nombre
    public boolean existePorNombre(String nombre) {
        return estadoRepository.existsByNombre(nombre);
    }

    // Crear nuevo estado
    public Estado crearEstado(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new RuntimeException("El nombre del estado no puede estar vacío");
        }
        
        // Verificar si ya existe
        if (estadoRepository.existsByNombre(nombre.trim())) {
            throw new RuntimeException("Ya existe un estado con el nombre: " + nombre);
        }

        Estado nuevo = new Estado();
        nuevo.setNombre(nombre.trim());
        return estadoRepository.save(nuevo);
    }

    // Actualizar estado
    public Estado actualizarEstado(Long id, String nombre) {
        Estado existente = estadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado ID: " + id));

        if (nombre != null && !nombre.trim().isEmpty()) {
            // Verificar si el nuevo nombre ya existe (excepto el mismo estado)
            Optional<Estado> estadoConMismoNombre = estadoRepository.findByNombre(nombre.trim());
            if (estadoConMismoNombre.isPresent() && !estadoConMismoNombre.get().getIdEstado().equals(id)) {
                throw new RuntimeException("Ya existe un estado con el nombre: " + nombre);
            }
            existente.setNombre(nombre.trim());
        }

        return estadoRepository.save(existente);
    }

    // Eliminar estado
    public void eliminarEstado(Long id) {
        if (!estadoRepository.existsById(id)) {
            throw new RuntimeException("Estado no encontrado ID: " + id);
        }
        estadoRepository.deleteById(id);
    }

    // Obtener o crear estado (útil para evitar duplicados)
    public Estado obtenerOCrearEstado(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new RuntimeException("El nombre del estado no puede estar vacío");
        }

        Optional<Estado> existente = estadoRepository.findByNombre(nombre.trim());
        if (existente.isPresent()) {
            return existente.get();
        }

        // Crear nuevo si no existe
        Estado nuevo = new Estado();
        nuevo.setNombre(nombre.trim());
        return estadoRepository.save(nuevo);
    }
}

