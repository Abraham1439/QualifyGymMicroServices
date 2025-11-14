package com.qualifygym.estados.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qualifygym.estados.model.Estado;
import com.qualifygym.estados.service.EstadoService;

@RestController
@RequestMapping("/api/v1/estado")
public class EstadoController {

    @Autowired
    private EstadoService estadoService;

    // GET - Obtener todos los estados
    @GetMapping("/estados")
    public ResponseEntity<List<Estado>> obtenerTodosEstados() {
        List<Estado> estados = estadoService.obtenerTodosEstados();
        return estados.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(estados);
    }

    // GET - Obtener estado por ID
    @GetMapping("/estados/{id}")
    public ResponseEntity<?> obtenerEstadoPorId(@PathVariable Long id) {
        try {
            Optional<Estado> estado = estadoService.obtenerEstadoPorId(id);
            if (estado.isPresent()) {
                return ResponseEntity.ok(estado.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estado no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error interno: " + e.getMessage());
        }
    }

    // GET - Obtener estado por nombre
    @GetMapping("/estados/nombre/{nombre}")
    public ResponseEntity<?> obtenerEstadoPorNombre(@PathVariable String nombre) {
        try {
            Optional<Estado> estado = estadoService.obtenerEstadoPorNombre(nombre);
            if (estado.isPresent()) {
                return ResponseEntity.ok(estado.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Estado no encontrado con nombre: " + nombre);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error interno: " + e.getMessage());
        }
    }

    // GET - Verificar si existe estado por nombre
    @GetMapping("/estados/existe/{nombre}")
    public ResponseEntity<Boolean> existeEstadoPorNombre(@PathVariable String nombre) {
        boolean existe = estadoService.existePorNombre(nombre);
        return ResponseEntity.ok(existe);
    }

    // POST - Crear nuevo estado
    @PostMapping("/estados")
    public ResponseEntity<?> crearEstado(@RequestBody Map<String, Object> datos) {
        try {
            String nombre = (String) datos.get("nombre");

            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Falta el campo requerido: nombre");
            }

            Estado nuevo = estadoService.crearEstado(nombre);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST - Obtener o crear estado
    @PostMapping("/estados/obtener-o-crear")
    public ResponseEntity<?> obtenerOCrearEstado(@RequestBody Map<String, Object> datos) {
        try {
            String nombre = (String) datos.get("nombre");

            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Falta el campo requerido: nombre");
            }

            Estado estado = estadoService.obtenerOCrearEstado(nombre);
            return ResponseEntity.ok(estado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT - Actualizar estado
    @PutMapping("/estados/{id}")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            String nombre = (String) datos.get("nombre");

            if (nombre == null || nombre.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre no puede estar vacío");
            }

            Estado actualizado = estadoService.actualizarEstado(id, nombre);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE - Eliminar estado
    @DeleteMapping("/estados/{id}")
    public ResponseEntity<?> eliminarEstado(@PathVariable Long id) {
        try {
            estadoService.eliminarEstado(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al eliminar estado: " + e.getMessage());
        }
    }
}

