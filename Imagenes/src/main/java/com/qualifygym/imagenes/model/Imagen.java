package com.qualifygym.imagenes.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "imagenes", indexes = {
    @Index(name = "idx_usuario_id", columnList = "usuario_id"),
    @Index(name = "idx_publicacion_id", columnList = "publicacion_id"),
    @Index(name = "idx_tipo_imagen", columnList = "tipo_imagen")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Imagen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen")
    private Long idImagen;

    @Column(name = "usuario_id")
    private Long usuarioId; // Para foto de perfil (opcional)

    @Column(name = "publicacion_id")
    private Long publicacionId; // Para foto de publicación (opcional)

    @Column(name = "tipo_imagen", nullable = false, length = 50)
    private String tipoImagen; // "PERFIL" o "PUBLICACION"

    @Lob
    @Column(name = "datos_imagen", nullable = false, columnDefinition = "LONGBLOB")
    @JsonIgnore // No serializar el BLOB en JSON por defecto
    private byte[] datosImagen;

    @Column(name = "tipo_mime", nullable = false, length = 100)
    private String tipoMime; // "image/jpeg", "image/png", etc.

    @Column(name = "nombre_archivo", length = 255)
    private String nombreArchivo;

    @Column(name = "tamaño", nullable = false)
    private Long tamaño; // Tamaño en bytes

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm", timezone = "America/Santiago")
    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    @PrePersist
    public void prePersist() {
        if (this.fechaSubida == null) {
            this.fechaSubida = LocalDateTime.now();
        }
    }
}

