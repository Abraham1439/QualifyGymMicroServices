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

@RestController
@RequestMapping("/api/v1/comentario")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    // GET - Obtener todos los comentarios
    @GetMapping("/comentarios")
    public ResponseEntity<List<Comentario>> obtenerTodosComentarios() {
        List<Comentario> comentarios = comentarioService.obtenerTodosComentarios();
        return comentarios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(comentarios);
    }

    // GET - Obtener comentario por ID
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

    // GET - Obtener comentarios por publicación
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

    // GET - Obtener comentarios por usuario
    @GetMapping("/comentarios/usuario/{usuarioId}")
    public ResponseEntity<List<Comentario>> obtenerComentariosPorUsuario(@PathVariable Long usuarioId) {
        List<Comentario> comentarios = comentarioService.obtenerComentariosPorUsuario(usuarioId);
        return comentarios.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(comentarios);
    }

    // GET - Contar comentarios por publicación
    @GetMapping("/comentarios/publicacion/{publicacionId}/count")
    public ResponseEntity<Long> contarComentariosPorPublicacion(@PathVariable Long publicacionId) {
        long count = comentarioService.contarComentariosPorPublicacion(publicacionId);
        return ResponseEntity.ok(count);
    }

    // GET - Contar comentarios por usuario
    @GetMapping("/comentarios/usuario/{usuarioId}/count")
    public ResponseEntity<Long> contarComentariosPorUsuario(@PathVariable Long usuarioId) {
        long count = comentarioService.contarComentariosPorUsuario(usuarioId);
        return ResponseEntity.ok(count);
    }

    // POST - Crear nuevo comentario
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

    // PUT - Actualizar comentario
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

    // PUT - Ocultar comentario
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

    // PUT - Mostrar comentario (desocultar)
    @PutMapping("/comentarios/{id}/mostrar")
    public ResponseEntity<?> mostrarComentario(@PathVariable Long id) {
        try {
            Comentario mostrado = comentarioService.mostrarComentario(id);
            return ResponseEntity.ok(mostrado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE - Eliminar comentario
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

