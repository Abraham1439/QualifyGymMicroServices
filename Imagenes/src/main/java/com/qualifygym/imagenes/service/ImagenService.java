package com.qualifygym.imagenes.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.qualifygym.imagenes.client.PublicacionClient;
import com.qualifygym.imagenes.client.UsuarioClient;
import com.qualifygym.imagenes.model.Imagen;
import com.qualifygym.imagenes.repository.ImagenRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ImagenService {

    @Autowired
    private ImagenRepository imagenRepository;

    @Autowired
    private UsuarioClient usuarioClient;

    @Autowired
    private PublicacionClient publicacionClient;

    // Límite de tamaño en bytes (10MB)
    private static final long MAX_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

    // Tipos MIME permitidos
    private static final String[] TIPOS_MIME_PERMITIDOS = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    };

    /**
     * Validar tamaño de imagen
     */
    private void validarTamaño(byte[] datosImagen) {
        if (datosImagen == null || datosImagen.length == 0) {
            throw new RuntimeException("La imagen no puede estar vacía");
        }
        if (datosImagen.length > MAX_SIZE_BYTES) {
            throw new RuntimeException("La imagen excede el tamaño máximo permitido de 10MB");
        }
    }

    /**
     * Validar tipo MIME
     */
    private void validarTipoMime(String tipoMime) {
        if (tipoMime == null || tipoMime.trim().isEmpty()) {
            throw new RuntimeException("El tipo MIME es requerido");
        }
        boolean esValido = false;
        for (String tipoPermitido : TIPOS_MIME_PERMITIDOS) {
            if (tipoMime.toLowerCase().equals(tipoPermitido.toLowerCase())) {
                esValido = true;
                break;
            }
        }
        if (!esValido) {
            throw new RuntimeException("Tipo de imagen no permitido. Solo se permiten: JPEG, PNG, GIF, WEBP");
        }
    }

    /**
     * Subir foto de perfil
     */
    public Imagen subirFotoPerfil(Long usuarioId, byte[] datosImagen, String tipoMime, String nombreArchivo) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new RuntimeException("El ID de usuario es inválido");
        }

        // Validar que el usuario existe
        if (!usuarioClient.existeUsuario(usuarioId)) {
            throw new RuntimeException("El usuario con ID " + usuarioId + " no existe");
        }

        validarTamaño(datosImagen);
        validarTipoMime(tipoMime);

        // Si ya existe una foto de perfil, eliminarla primero
        Optional<Imagen> fotoExistente = imagenRepository.findFotoPerfilByUsuarioId(usuarioId);
        if (fotoExistente.isPresent()) {
            imagenRepository.delete(fotoExistente.get());
        }

        Imagen nuevaImagen = new Imagen();
        nuevaImagen.setUsuarioId(usuarioId);
        nuevaImagen.setTipoImagen("PERFIL");
        nuevaImagen.setDatosImagen(datosImagen);
        nuevaImagen.setTipoMime(tipoMime);
        nuevaImagen.setNombreArchivo(nombreArchivo != null ? nombreArchivo : "foto_perfil_" + usuarioId);
        nuevaImagen.setTamaño((long) datosImagen.length);

        return imagenRepository.save(nuevaImagen);
    }

    /**
     * Subir foto de publicación
     */
    public Imagen subirFotoPublicacion(Long publicacionId, Long usuarioId, byte[] datosImagen, String tipoMime, String nombreArchivo) {
        if (publicacionId == null || publicacionId <= 0) {
            throw new RuntimeException("El ID de publicación es inválido");
        }
        if (usuarioId == null || usuarioId <= 0) {
            throw new RuntimeException("El ID de usuario es inválido");
        }

        // Validar que la publicación existe
        if (!publicacionClient.existePublicacion(publicacionId)) {
            throw new RuntimeException("La publicación con ID " + publicacionId + " no existe");
        }

        // Validar que el usuario existe
        if (!usuarioClient.existeUsuario(usuarioId)) {
            throw new RuntimeException("El usuario con ID " + usuarioId + " no existe");
        }

        validarTamaño(datosImagen);
        validarTipoMime(tipoMime);

        Imagen nuevaImagen = new Imagen();
        nuevaImagen.setPublicacionId(publicacionId);
        nuevaImagen.setUsuarioId(usuarioId);
        nuevaImagen.setTipoImagen("PUBLICACION");
        nuevaImagen.setDatosImagen(datosImagen);
        nuevaImagen.setTipoMime(tipoMime);
        nuevaImagen.setNombreArchivo(nombreArchivo != null ? nombreArchivo : "foto_publicacion_" + publicacionId);
        nuevaImagen.setTamaño((long) datosImagen.length);

        return imagenRepository.save(nuevaImagen);
    }

    /**
     * Obtener foto de perfil por usuario
     */
    public Optional<Imagen> obtenerFotoPerfil(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new RuntimeException("El ID de usuario es inválido");
        }
        return imagenRepository.findFotoPerfilByUsuarioId(usuarioId);
    }

    /**
     * Obtener imágenes de una publicación
     */
    public List<Imagen> obtenerImagenesPublicacion(Long publicacionId) {
        if (publicacionId == null || publicacionId <= 0) {
            throw new RuntimeException("El ID de publicación es inválido");
        }
        return imagenRepository.findImagenesByPublicacionId(publicacionId);
    }

    /**
     * Obtener imagen por ID
     */
    public Optional<Imagen> obtenerImagenPorId(Long idImagen) {
        return imagenRepository.findById(idImagen);
    }

    /**
     * Eliminar imagen
     */
    public void eliminarImagen(Long idImagen) {
        if (!imagenRepository.existsById(idImagen)) {
            throw new RuntimeException("Imagen no encontrada ID: " + idImagen);
        }
        imagenRepository.deleteById(idImagen);
    }

    /**
     * Eliminar foto de perfil de un usuario
     */
    public void eliminarFotoPerfil(Long usuarioId) {
        Optional<Imagen> fotoPerfil = imagenRepository.findFotoPerfilByUsuarioId(usuarioId);
        if (fotoPerfil.isPresent()) {
            imagenRepository.delete(fotoPerfil.get());
        } else {
            throw new RuntimeException("No se encontró foto de perfil para el usuario ID: " + usuarioId);
        }
    }

    /**
     * Eliminar todas las imágenes de una publicación
     */
    public void eliminarImagenesPublicacion(Long publicacionId) {
        List<Imagen> imagenes = imagenRepository.findImagenesByPublicacionId(publicacionId);
        imagenRepository.deleteAll(imagenes);
    }

    /**
     * Contar imágenes por usuario
     */
    public long contarImagenesPorUsuario(Long usuarioId) {
        return imagenRepository.countByUsuarioId(usuarioId);
    }

    /**
     * Contar imágenes por publicación
     */
    public long contarImagenesPorPublicacion(Long publicacionId) {
        return imagenRepository.countByPublicacionId(publicacionId);
    }
}

