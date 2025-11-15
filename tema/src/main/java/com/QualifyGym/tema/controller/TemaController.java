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

@RestController
@RequestMapping("/api/v1/tema")
public class TemaController {

    @Autowired
    private TemaService temaService;

    // GET - Obtener todos los temas
    @GetMapping("/temas")
    public ResponseEntity<List<Tema>> obtenerTodosTemas() {
        List<Tema> temas = temaService.obtenerTodosTemas();
        return temas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(temas);
    }

    // GET - Obtener tema por ID
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

    // GET - Obtener temas por estado
    @GetMapping("/temas/estado/{estadoId}")
    public ResponseEntity<List<Tema>> obtenerTemasPorEstado(@PathVariable Long estadoId) {
        List<Tema> temas = temaService.obtenerTemasPorEstado(estadoId);
        return temas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(temas);
    }

    // GET - Buscar temas por nombre
    @GetMapping("/temas/buscar")
    public ResponseEntity<List<Tema>> buscarTemas(@RequestParam String query) {
        List<Tema> temas = temaService.buscarTemas(query);
        return temas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(temas);
    }

    // GET - Obtener tema por nombre exacto
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

    // GET - Verificar si existe tema por nombre
    @GetMapping("/temas/existe/{nombre}")
    public ResponseEntity<Boolean> existeTemaPorNombre(@PathVariable String nombre) {
        boolean existe = temaService.existePorNombre(nombre);
        return ResponseEntity.ok(existe);
    }

    // GET - Contar temas por estado
    @GetMapping("/temas/estado/{estadoId}/count")
    public ResponseEntity<Long> contarTemasPorEstado(@PathVariable Long estadoId) {
        long count = temaService.contarTemasPorEstado(estadoId);
        return ResponseEntity.ok(count);
    }

    // POST - Crear nuevo tema
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

    // PUT - Actualizar tema
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

    // DELETE - Eliminar tema
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

