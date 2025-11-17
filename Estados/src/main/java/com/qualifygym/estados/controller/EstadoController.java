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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/estado")
@Tag(name = "Estados", description = "API para la gestión de estados del sistema QualifyGym")
public class EstadoController {

    @Autowired
    private EstadoService estadoService;

    @Operation(summary = "Obtener todos los estados", description = "Retorna una lista de todos los estados registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de estados obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay estados registrados")
    })
    @GetMapping("/estados")
    public ResponseEntity<List<Estado>> obtenerTodosEstados() {
        List<Estado> estados = estadoService.obtenerTodosEstados();
        return estados.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(estados);
    }

    @Operation(summary = "Obtener estado por ID", description = "Retorna la información de un estado específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Estado no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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

    @Operation(summary = "Obtener estado por nombre", description = "Retorna la información de un estado específico por su nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Estado no encontrado con el nombre especificado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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

    @Operation(summary = "Verificar existencia de estado", description = "Verifica si existe un estado con el nombre especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación realizada exitosamente (retorna true o false)")
    })
    @GetMapping("/estados/existe/{nombre}")
    public ResponseEntity<Boolean> existeEstadoPorNombre(@PathVariable String nombre) {
        boolean existe = estadoService.existePorNombre(nombre);
        return ResponseEntity.ok(existe);
    }

    @Operation(summary = "Crear nuevo estado", description = "Crea un nuevo estado en el sistema. El nombre debe ser único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Estado creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre ya existe")
    })
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

    @Operation(summary = "Obtener o crear estado", description = "Obtiene un estado existente por nombre, o lo crea si no existe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado obtenido o creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
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

    @Operation(summary = "Actualizar estado", description = "Actualiza el nombre de un estado existente. El nuevo nombre debe ser único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, estado no encontrado o nombre ya existe")
    })
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

    @Operation(summary = "Eliminar estado", description = "Elimina permanentemente un estado del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Estado eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estado no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al eliminar estado")
    })
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

