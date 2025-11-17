package com.qualifygym.comentarios.controller;

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

import com.qualifygym.comentarios.model.Comentario;
import com.qualifygym.comentarios.service.ComentarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/comentario")
@Tag(name = "Comentarios", description = "API para la gestión de comentarios del sistema QualifyGym")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @Operation(summary = "Obtener todos los comentarios", description = "Retorna una lista de todos los comentarios registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comentarios obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay comentarios registrados")
    })
    @GetMapping("/comentarios")
    public ResponseEntity<List<Comentario>> obtenerTodosComentarios() {
        List<Comentario> comentarios = comentarioService.obtenerTodosComentarios();
        return comentarios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(comentarios);
    }

    @Operation(summary = "Obtener comentario por ID", description = "Retorna la información de un comentario específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Comentario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/comentarios/{id}")
    public ResponseEntity<?> obtenerComentarioPorId(@PathVariable Long id) {
        try {
            Optional<Comentario> comentario = comentarioService.obtenerComentarioPorId(id);
            if (comentario.isPresent()) {
                return ResponseEntity.ok(comentario.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comentario no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener comentarios por publicación", description = "Retorna todos los comentarios asociados a una publicación específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comentarios obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay comentarios para esta publicación")
    })
    @GetMapping("/comentarios/publicacion/{publicacionId}")
    public ResponseEntity<List<Comentario>> obtenerComentariosPorPublicacion(
            @PathVariable Long publicacionId,
            @RequestParam(defaultValue = "false") boolean incluirOcultos) {
        List<Comentario> comentarios;
        if (incluirOcultos) {
            comentarios = comentarioService.obtenerComentariosPorPublicacion(publicacionId);
        } else {
            comentarios = comentarioService.obtenerComentariosVisiblesPorPublicacion(publicacionId);
        }
        return comentarios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(comentarios);
    }

    @Operation(summary = "Obtener comentarios por usuario", description = "Retorna todos los comentarios realizados por un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de comentarios obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "El usuario no tiene comentarios")
    })
    @GetMapping("/comentarios/usuario/{usuarioId}")
    public ResponseEntity<List<Comentario>> obtenerComentariosPorUsuario(@PathVariable Long usuarioId) {
        List<Comentario> comentarios = comentarioService.obtenerComentariosPorUsuario(usuarioId);
        return comentarios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(comentarios);
    }

    @Operation(summary = "Contar comentarios por publicación", description = "Retorna el número total de comentarios asociados a una publicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo realizado exitosamente")
    })
    @GetMapping("/comentarios/publicacion/{publicacionId}/count")
    public ResponseEntity<Long> contarComentariosPorPublicacion(@PathVariable Long publicacionId) {
        long count = comentarioService.contarComentariosPorPublicacion(publicacionId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Contar comentarios por usuario", description = "Retorna el número total de comentarios realizados por un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo realizado exitosamente")
    })
    @GetMapping("/comentarios/usuario/{usuarioId}/count")
    public ResponseEntity<Long> contarComentariosPorUsuario(@PathVariable Long usuarioId) {
        long count = comentarioService.contarComentariosPorUsuario(usuarioId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Crear nuevo comentario", description = "Crea un nuevo comentario asociado a una publicación. Valida que el usuario y la publicación existan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comentario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, faltantes o usuario/publicación no existe")
    })
    @PostMapping("/comentarios")
    public ResponseEntity<?> crearComentario(@RequestBody Map<String, Object> datos) {
        try {
            String comentario = (String) datos.get("comentario");
            Long usuarioId = datos.get("usuarioId") != null 
                    ? Long.valueOf(datos.get("usuarioId").toString()) 
                    : null;
            Long publicacionId = datos.get("publicacionId") != null 
                    ? Long.valueOf(datos.get("publicacionId").toString()) 
                    : null;

            if (comentario == null || usuarioId == null || publicacionId == null) {
                return ResponseEntity.badRequest()
                        .body("Faltan campos requeridos: comentario, usuarioId, publicacionId");
            }

            Comentario nuevo = comentarioService.crearComentario(comentario, usuarioId, publicacionId);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar comentario", description = "Actualiza el texto de un comentario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentario actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o comentario no encontrado")
    })
    @PutMapping("/comentarios/{id}")
    public ResponseEntity<?> actualizarComentario(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            String comentario = (String) datos.get("comentario");

            if (comentario == null || comentario.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El comentario no puede estar vacío");
            }

            Comentario actualizado = comentarioService.actualizarComentario(id, comentario);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Ocultar comentario", description = "Oculta un comentario (moderación). El comentario no será visible para los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentario ocultado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Comentario no encontrado")
    })
    @PutMapping("/comentarios/{id}/ocultar")
    public ResponseEntity<?> ocultarComentario(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            String motivoBaneo = (String) datos.get("motivoBaneo");
            Comentario oculto = comentarioService.ocultarComentario(id, motivoBaneo);
            return ResponseEntity.ok(oculto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Mostrar comentario", description = "Muestra un comentario previamente oculto (desocultar)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comentario mostrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Comentario no encontrado")
    })
    @PutMapping("/comentarios/{id}/mostrar")
    public ResponseEntity<?> mostrarComentario(@PathVariable Long id) {
        try {
            Comentario mostrado = comentarioService.mostrarComentario(id);
            return ResponseEntity.ok(mostrado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar comentario", description = "Elimina permanentemente un comentario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comentario eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Comentario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al eliminar comentario")
    })
    @DeleteMapping("/comentarios/{id}")
    public ResponseEntity<?> eliminarComentario(@PathVariable Long id) {
        try {
            comentarioService.eliminarComentario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al eliminar comentario: " + e.getMessage());
        }
    }
}

