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

@RestController
@RequestMapping("/api/v1/publicacion")
public class PublicacionController {

    @Autowired
    private PublicacionService publicacionService;

    // GET - Obtener todas las publicaciones
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

    // GET - Obtener publicación por ID
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

    // GET - Obtener publicaciones por tema
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

    // GET - Obtener publicaciones por usuario
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

    // GET - Buscar publicaciones
    @GetMapping("/publicaciones/buscar")
    public ResponseEntity<List<Publicacion>> buscarPublicaciones(@RequestParam String query) {
        List<Publicacion> publicaciones = publicacionService.buscarPublicaciones(query);
        return publicaciones.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(publicaciones);
    }

    // GET - Contar publicaciones por tema
    @GetMapping("/publicaciones/tema/{temaId}/count")
    public ResponseEntity<Long> contarPublicacionesPorTema(@PathVariable Long temaId) {
        long count = publicacionService.contarPublicacionesPorTema(temaId);
        return ResponseEntity.ok(count);
    }

    // GET - Contar publicaciones por usuario
    @GetMapping("/publicaciones/usuario/{usuarioId}/count")
    public ResponseEntity<Long> contarPublicacionesPorUsuario(@PathVariable Long usuarioId) {
        long count = publicacionService.contarPublicacionesPorUsuario(usuarioId);
        return ResponseEntity.ok(count);
    }

    // POST - Crear nueva publicación
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

    // PUT - Actualizar publicación
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

    // PUT - Actualizar imagen de publicación
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

    // PUT - Ocultar publicación
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

    // PUT - Mostrar publicación (desocultar)
    @PutMapping("/publicaciones/{id}/mostrar")
    public ResponseEntity<?> mostrarPublicacion(@PathVariable Long id) {
        try {
            Publicacion mostrada = publicacionService.mostrarPublicacion(id);
            return ResponseEntity.ok(mostrada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE - Eliminar publicación
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

