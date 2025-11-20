package com.qualifygym.publicaciones.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qualifygym.publicaciones.model.Notificacion;
import com.qualifygym.publicaciones.service.NotificacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/publicacion")
@Tag(name = "Notificaciones", description = "API para la gestión de notificaciones del sistema QualifyGym")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @Operation(summary = "Obtener todas las notificaciones de un usuario", 
               description = "Retorna todas las notificaciones de un usuario, ordenadas por fecha descendente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "El usuario no tiene notificaciones"),
            @ApiResponse(responseCode = "400", description = "ID de usuario inválido")
    })
    @GetMapping("/notificaciones/usuario/{usuarioId}")
    public ResponseEntity<List<Notificacion>> obtenerNotificacionesPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesPorUsuario(usuarioId);
            return notificaciones.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(notificaciones);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Obtener notificaciones no leídas de un usuario", 
               description = "Retorna solo las notificaciones no leídas de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones no leídas obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "El usuario no tiene notificaciones no leídas"),
            @ApiResponse(responseCode = "400", description = "ID de usuario inválido")
    })
    @GetMapping("/notificaciones/usuario/{usuarioId}/no-leidas")
    public ResponseEntity<List<Notificacion>> obtenerNotificacionesNoLeidasPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesNoLeidasPorUsuario(usuarioId);
            return notificaciones.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(notificaciones);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Contar notificaciones no leídas de un usuario", 
               description = "Retorna el número de notificaciones no leídas de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo realizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "ID de usuario inválido")
    })
    @GetMapping("/notificaciones/usuario/{usuarioId}/no-leidas/count")
    public ResponseEntity<Long> contarNotificacionesNoLeidas(@PathVariable Long usuarioId) {
        try {
            long count = notificacionService.contarNotificacionesNoLeidas(usuarioId);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Marcar notificación como leída", 
               description = "Marca una notificación específica como leída")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación marcada como leída exitosamente"),
            @ApiResponse(responseCode = "400", description = "Notificación no encontrada")
    })
    @PutMapping("/notificaciones/{id}/marcar-leida")
    public ResponseEntity<?> marcarNotificacionComoLeida(@PathVariable Long id) {
        try {
            Notificacion notificacion = notificacionService.marcarComoLeida(id);
            return ResponseEntity.ok(notificacion);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Marcar todas las notificaciones de un usuario como leídas", 
               description = "Marca todas las notificaciones no leídas de un usuario como leídas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Todas las notificaciones marcadas como leídas exitosamente"),
            @ApiResponse(responseCode = "400", description = "ID de usuario inválido")
    })
    @PutMapping("/notificaciones/usuario/{usuarioId}/marcar-todas-leidas")
    public ResponseEntity<?> marcarTodasComoLeidas(@PathVariable Long usuarioId) {
        try {
            notificacionService.marcarTodasComoLeidas(usuarioId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar notificación", 
               description = "Elimina permanentemente una notificación del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Notificación no encontrada")
    })
    @DeleteMapping("/notificaciones/{id}")
    public ResponseEntity<?> eliminarNotificacion(@PathVariable Long id) {
        try {
            notificacionService.eliminarNotificacion(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

