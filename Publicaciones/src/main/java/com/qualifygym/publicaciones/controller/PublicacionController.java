package com.qualifygym.publicaciones.controller;

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

import com.qualifygym.publicaciones.model.Publicacion;
import com.qualifygym.publicaciones.service.PublicacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/publicacion")
@Tag(name = "Publicaciones", description = "API para la gestión de publicaciones del sistema QualifyGym")
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    @Operation(summary = "Obtener todas las publicaciones", description = "Retorna una lista de todas las publicaciones registradas en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de publicaciones obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay publicaciones registradas")
    })
    @GetMapping("/publicaciones")
    public ResponseEntity<List<Publicacion>> obtenerTodasPublicaciones(
            @RequestParam(defaultValue = "false") boolean incluirOcultas) {
        List<Publicacion> publicaciones;
        if (incluirOcultas) {
            publicaciones = publicacionService.obtenerTodasPublicaciones();
        } else {
            publicaciones = publicacionService.obtenerPublicacionesVisibles();
        }
        return publicaciones.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(publicaciones);
    }

    @Operation(summary = "Obtener publicación por ID", description = "Retorna la información de una publicación específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicación encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Publicación no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/publicaciones/{id}")
    public ResponseEntity<?> obtenerPublicacionPorId(@PathVariable Long id) {
        try {
            Optional<Publicacion> publicacion = publicacionService.obtenerPublicacionPorId(id);
            if (publicacion.isPresent()) {
                return ResponseEntity.ok(publicacion.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publicación no encontrada");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener publicaciones por tema", description = "Retorna todas las publicaciones asociadas a un tema específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de publicaciones obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay publicaciones para este tema")
    })
    @GetMapping("/publicaciones/tema/{temaId}")
    public ResponseEntity<List<Publicacion>> obtenerPublicacionesPorTema(
            @PathVariable Long temaId,
            @RequestParam(defaultValue = "false") boolean incluirOcultas) {
        List<Publicacion> publicaciones;
        if (incluirOcultas) {
            publicaciones = publicacionService.obtenerPublicacionesPorTema(temaId);
        } else {
            publicaciones = publicacionService.obtenerPublicacionesVisiblesPorTema(temaId);
        }
        return publicaciones.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(publicaciones);
    }

    @Operation(summary = "Obtener publicaciones por usuario", description = "Retorna todas las publicaciones creadas por un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de publicaciones obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "El usuario no tiene publicaciones")
    })
    @GetMapping("/publicaciones/usuario/{usuarioId}")
    public ResponseEntity<List<Publicacion>> obtenerPublicacionesPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "false") boolean incluirOcultas) {
        List<Publicacion> publicaciones;
        if (incluirOcultas) {
            publicaciones = publicacionService.obtenerPublicacionesPorUsuario(usuarioId);
        } else {
            publicaciones = publicacionService.obtenerPublicacionesVisiblesPorUsuario(usuarioId);
        }
        return publicaciones.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(publicaciones);
    }

    @Operation(summary = "Buscar publicaciones", description = "Busca publicaciones por texto en título o descripción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
            @ApiResponse(responseCode = "204", description = "No se encontraron publicaciones")
    })
    @GetMapping("/publicaciones/buscar")
    public ResponseEntity<List<Publicacion>> buscarPublicaciones(@RequestParam String query) {
        List<Publicacion> publicaciones = publicacionService.buscarPublicaciones(query);
        return publicaciones.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(publicaciones);
    }

    @Operation(summary = "Contar publicaciones por tema", description = "Retorna el número total de publicaciones asociadas a un tema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo realizado exitosamente")
    })
    @GetMapping("/publicaciones/tema/{temaId}/count")
    public ResponseEntity<Long> contarPublicacionesPorTema(@PathVariable Long temaId) {
        long count = publicacionService.contarPublicacionesPorTema(temaId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Contar publicaciones por usuario", description = "Retorna el número total de publicaciones creadas por un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo realizado exitosamente")
    })
    @GetMapping("/publicaciones/usuario/{usuarioId}/count")
    public ResponseEntity<Long> contarPublicacionesPorUsuario(@PathVariable Long usuarioId) {
        long count = publicacionService.contarPublicacionesPorUsuario(usuarioId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Crear nueva publicación", description = "Crea una nueva publicación. Valida que el usuario y el tema existan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Publicación creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, faltantes o usuario/tema no existe")
    })
    @PostMapping("/publicaciones")
    public ResponseEntity<?> crearPublicacion(@RequestBody Map<String, Object> datos) {
        try {
            String titulo = (String) datos.get("titulo");
            String descripcion = (String) datos.get("descripcion");
            Long usuarioId = datos.get("usuarioId") != null 
                    ? Long.valueOf(datos.get("usuarioId").toString()) 
                    : null;
            Long temaId = datos.get("temaId") != null 
                    ? Long.valueOf(datos.get("temaId").toString()) 
                    : null;
            String imageUrl = (String) datos.get("imageUrl");

            if (titulo == null || descripcion == null || usuarioId == null || temaId == null) {
                return ResponseEntity.badRequest()
                        .body("Faltan campos requeridos: titulo, descripcion, usuarioId, temaId");
            }

            Publicacion nueva = publicacionService.crearPublicacion(titulo, descripcion, usuarioId, temaId, imageUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar publicación", description = "Actualiza el título y/o descripción de una publicación existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicación actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o publicación no encontrada")
    })
    @PutMapping("/publicaciones/{id}")
    public ResponseEntity<?> actualizarPublicacion(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            String titulo = (String) datos.get("titulo");
            String descripcion = (String) datos.get("descripcion");

            Publicacion actualizada = publicacionService.actualizarPublicacion(id, titulo, descripcion);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar imagen de publicación", description = "Actualiza la URL de la imagen asociada a una publicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Publicación no encontrada")
    })
    @PutMapping("/publicaciones/{id}/imagen")
    public ResponseEntity<?> actualizarImagenPublicacion(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            String imageUrl = (String) datos.get("imageUrl");
            Publicacion actualizada = publicacionService.actualizarImagenPublicacion(id, imageUrl);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Ocultar publicación", description = "Oculta una publicación (moderación). La publicación no será visible para los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicación ocultada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Publicación no encontrada")
    })
    @PutMapping("/publicaciones/{id}/ocultar")
    public ResponseEntity<?> ocultarPublicacion(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            String motivoBaneo = (String) datos.get("motivoBaneo");
            Publicacion oculta = publicacionService.ocultarPublicacion(id, motivoBaneo);
            return ResponseEntity.ok(oculta);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Mostrar publicación", description = "Muestra una publicación previamente oculta (desocultar)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Publicación mostrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Publicación no encontrada")
    })
    @PutMapping("/publicaciones/{id}/mostrar")
    public ResponseEntity<?> mostrarPublicacion(@PathVariable Long id) {
        try {
            Publicacion mostrada = publicacionService.mostrarPublicacion(id);
            return ResponseEntity.ok(mostrada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar publicación", description = "Elimina permanentemente una publicación del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Publicación eliminada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Publicación no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error al eliminar publicación")
    })
    @DeleteMapping("/publicaciones/{id}")
    public ResponseEntity<?> eliminarPublicacion(@PathVariable Long id) {
        try {
            publicacionService.eliminarPublicacion(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al eliminar publicación: " + e.getMessage());
        }
    }
}

