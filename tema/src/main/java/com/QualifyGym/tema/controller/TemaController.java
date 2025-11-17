package com.QualifyGym.tema.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.QualifyGym.tema.model.Tema;
import com.QualifyGym.tema.service.TemaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/tema")
@Tag(name = "Temas", description = "API para la gestión de temas del sistema QualifyGym")
public class TemaController {

    @Autowired
    private TemaService temaService;

    @Operation(summary = "Obtener todos los temas", description = "Retorna una lista de todos los temas registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de temas obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay temas registrados")
    })
    @GetMapping("/temas")
    public ResponseEntity<List<Tema>> obtenerTodosTemas() {
        List<Tema> temas = temaService.obtenerTodosTemas();
        return temas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(temas);
    }

    @Operation(summary = "Obtener tema por ID", description = "Retorna la información de un tema específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tema no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/temas/{id}")
    public ResponseEntity<?> obtenerTemaPorId(@PathVariable Long id) {
        try {
            Optional<Tema> tema = temaService.obtenerTemaPorId(id);
            if (tema.isPresent()) {
                return ResponseEntity.ok(tema.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tema no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener temas por estado", description = "Retorna todos los temas asociados a un estado específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de temas obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay temas para este estado")
    })
    @GetMapping("/temas/estado/{estadoId}")
    public ResponseEntity<List<Tema>> obtenerTemasPorEstado(@PathVariable Long estadoId) {
        List<Tema> temas = temaService.obtenerTemasPorEstado(estadoId);
        return temas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(temas);
    }

    @Operation(summary = "Buscar temas por nombre", description = "Busca temas cuyo nombre contenga el texto especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
            @ApiResponse(responseCode = "204", description = "No se encontraron temas")
    })
    @GetMapping("/temas/buscar")
    public ResponseEntity<List<Tema>> buscarTemas(@RequestParam String query) {
        List<Tema> temas = temaService.buscarTemas(query);
        return temas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(temas);
    }

    @Operation(summary = "Obtener tema por nombre exacto", description = "Retorna la información de un tema específico por su nombre exacto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tema no encontrado con el nombre especificado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/temas/nombre/{nombre}")
    public ResponseEntity<?> obtenerTemaPorNombre(@PathVariable String nombre) {
        try {
            Optional<Tema> tema = temaService.obtenerTemaPorNombre(nombre);
            if (tema.isPresent()) {
                return ResponseEntity.ok(tema.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tema no encontrado con nombre: " + nombre);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Verificar existencia de tema", description = "Verifica si existe un tema con el nombre especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación realizada exitosamente (retorna true o false)")
    })
    @GetMapping("/temas/existe/{nombre}")
    public ResponseEntity<Boolean> existeTemaPorNombre(@PathVariable String nombre) {
        boolean existe = temaService.existePorNombre(nombre);
        return ResponseEntity.ok(existe);
    }

    @Operation(summary = "Contar temas por estado", description = "Retorna el número total de temas asociados a un estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo realizado exitosamente")
    })
    @GetMapping("/temas/estado/{estadoId}/count")
    public ResponseEntity<Long> contarTemasPorEstado(@PathVariable Long estadoId) {
        long count = temaService.contarTemasPorEstado(estadoId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Crear nuevo tema", description = "Crea un nuevo tema en el sistema. Valida que el estado exista. El nombre debe ser único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tema creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, faltantes, estado no existe o nombre ya existe")
    })
    @PostMapping("/temas")
    public ResponseEntity<?> crearTema(@RequestBody Map<String, Object> datos) {
        try {
            String nombreTema = (String) datos.get("nombreTema");
            Long estadoId = datos.get("estadoId") != null 
                    ? Long.valueOf(datos.get("estadoId").toString()) 
                    : null;

            if (nombreTema == null || estadoId == null) {
                return ResponseEntity.badRequest()
                        .body("Faltan campos requeridos: nombreTema, estadoId");
            }

            Tema nuevo = temaService.crearTema(nombreTema, estadoId);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar tema", description = "Actualiza el nombre y/o estado de un tema existente. Valida que el nuevo estado exista si se proporciona")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, tema no encontrado o estado no existe")
    })
    @PutMapping("/temas/{id}")
    public ResponseEntity<?> actualizarTema(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            String nombreTema = (String) datos.get("nombreTema");
            Long estadoId = datos.get("estadoId") != null 
                    ? Long.valueOf(datos.get("estadoId").toString()) 
                    : null;

            Tema actualizado = temaService.actualizarTema(id, nombreTema, estadoId);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar tema", description = "Elimina permanentemente un tema del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tema eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Tema no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al eliminar tema")
    })
    @DeleteMapping("/temas/{id}")
    public ResponseEntity<?> eliminarTema(@PathVariable Long id) {
        try {
            temaService.eliminarTema(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al eliminar tema: " + e.getMessage());
        }
    }
}

