package com.qualifygym.imagenes.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.qualifygym.imagenes.model.Imagen;
import com.qualifygym.imagenes.service.ImagenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/imagen")
@Tag(name = "Imágenes", description = "API para la gestión de imágenes del sistema QualifyGym")
public class ImagenController {

    @Autowired
    private ImagenService imagenService;

    @Operation(summary = "Subir foto de perfil", 
               description = "Sube una foto de perfil para un usuario. Si ya existe una foto de perfil, se reemplaza.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Foto de perfil subida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, imagen muy grande o tipo no permitido")
    })
    @PostMapping("/perfil/{usuarioId}")
    public ResponseEntity<?> subirFotoPerfil(
            @PathVariable Long usuarioId,
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            if (archivo.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo no puede estar vacío");
            }

            byte[] datosImagen = archivo.getBytes();
            String tipoMime = archivo.getContentType();
            String nombreArchivo = archivo.getOriginalFilename();

            Imagen imagen = imagenService.subirFotoPerfil(usuarioId, datosImagen, tipoMime, nombreArchivo);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "idImagen", imagen.getIdImagen(),
                "usuarioId", imagen.getUsuarioId(),
                "tipoImagen", imagen.getTipoImagen(),
                "tipoMime", imagen.getTipoMime(),
                "nombreArchivo", imagen.getNombreArchivo(),
                "tamaño", imagen.getTamaño(),
                "fechaSubida", imagen.getFechaSubida()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir foto de perfil: " + e.getMessage());
        }
    }

    @Operation(summary = "Subir foto de publicación", 
               description = "Sube una foto asociada a una publicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Foto de publicación subida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, imagen muy grande o tipo no permitido")
    })
    @PostMapping("/publicacion/{publicacionId}")
    public ResponseEntity<?> subirFotoPublicacion(
            @PathVariable Long publicacionId,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            if (archivo.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo no puede estar vacío");
            }

            byte[] datosImagen = archivo.getBytes();
            String tipoMime = archivo.getContentType();
            String nombreArchivo = archivo.getOriginalFilename();

            Imagen imagen = imagenService.subirFotoPublicacion(publicacionId, usuarioId, datosImagen, tipoMime, nombreArchivo);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "idImagen", imagen.getIdImagen(),
                "publicacionId", imagen.getPublicacionId(),
                "usuarioId", imagen.getUsuarioId(),
                "tipoImagen", imagen.getTipoImagen(),
                "tipoMime", imagen.getTipoMime(),
                "nombreArchivo", imagen.getNombreArchivo(),
                "tamaño", imagen.getTamaño(),
                "fechaSubida", imagen.getFechaSubida()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir foto de publicación: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener foto de perfil", 
               description = "Obtiene la foto de perfil de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Foto de perfil obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró foto de perfil para el usuario")
    })
    @GetMapping("/perfil/{usuarioId}")
    public ResponseEntity<?> obtenerFotoPerfil(@PathVariable Long usuarioId) {
        try {
            Optional<Imagen> imagen = imagenService.obtenerFotoPerfil(usuarioId);
            if (imagen.isPresent()) {
                Imagen img = imagen.get();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(img.getTipoMime()));
                headers.setContentLength(img.getDatosImagen().length);
                headers.setContentDispositionFormData("inline", img.getNombreArchivo());
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(img.getDatosImagen());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró foto de perfil para el usuario ID: " + usuarioId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener foto de perfil: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener imagen por ID", 
               description = "Obtiene una imagen por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagen obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Imagen no encontrada")
    })
    @GetMapping("/{idImagen}")
    public ResponseEntity<?> obtenerImagenPorId(@PathVariable Long idImagen) {
        try {
            Optional<Imagen> imagen = imagenService.obtenerImagenPorId(idImagen);
            if (imagen.isPresent()) {
                Imagen img = imagen.get();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(img.getTipoMime()));
                headers.setContentLength(img.getDatosImagen().length);
                headers.setContentDispositionFormData("inline", img.getNombreArchivo());
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(img.getDatosImagen());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Imagen no encontrada ID: " + idImagen);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener imagen: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener imágenes de una publicación", 
               description = "Obtiene todas las imágenes asociadas a una publicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de imágenes obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay imágenes para esta publicación")
    })
    @GetMapping("/publicacion/{publicacionId}")
    public ResponseEntity<?> obtenerImagenesPublicacion(@PathVariable Long publicacionId) {
        try {
            List<Imagen> imagenes = imagenService.obtenerImagenesPublicacion(publicacionId);
            if (imagenes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            // Retornar solo metadatos, no los datos de la imagen
            return ResponseEntity.ok(imagenes.stream().map(img -> Map.of(
                "idImagen", img.getIdImagen(),
                "publicacionId", img.getPublicacionId(),
                "usuarioId", img.getUsuarioId(),
                "tipoImagen", img.getTipoImagen(),
                "tipoMime", img.getTipoMime(),
                "nombreArchivo", img.getNombreArchivo(),
                "tamaño", img.getTamaño(),
                "fechaSubida", img.getFechaSubida()
            )).toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener imágenes: " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar imagen", 
               description = "Elimina una imagen por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Imagen eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Imagen no encontrada")
    })
    @DeleteMapping("/{idImagen}")
    public ResponseEntity<?> eliminarImagen(@PathVariable Long idImagen) {
        try {
            imagenService.eliminarImagen(idImagen);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar imagen: " + e.getMessage());
        }
    }

    @Operation(summary = "Eliminar foto de perfil", 
               description = "Elimina la foto de perfil de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Foto de perfil eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró foto de perfil")
    })
    @DeleteMapping("/perfil/{usuarioId}")
    public ResponseEntity<?> eliminarFotoPerfil(@PathVariable Long usuarioId) {
        try {
            imagenService.eliminarFotoPerfil(usuarioId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar foto de perfil: " + e.getMessage());
        }
    }

    @Operation(summary = "Contar imágenes por usuario", 
               description = "Retorna el número de imágenes de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo realizado exitosamente")
    })
    @GetMapping("/usuario/{usuarioId}/count")
    public ResponseEntity<Long> contarImagenesPorUsuario(@PathVariable Long usuarioId) {
        long count = imagenService.contarImagenesPorUsuario(usuarioId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Contar imágenes por publicación", 
               description = "Retorna el número de imágenes de una publicación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo realizado exitosamente")
    })
    @GetMapping("/publicacion/{publicacionId}/count")
    public ResponseEntity<Long> contarImagenesPorPublicacion(@PathVariable Long publicacionId) {
        long count = imagenService.contarImagenesPorPublicacion(publicacionId);
        return ResponseEntity.ok(count);
    }
}

