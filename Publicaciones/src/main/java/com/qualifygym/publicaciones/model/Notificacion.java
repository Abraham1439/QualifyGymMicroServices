package com.qualifygym.publicaciones.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notificaciones", indexes = {
    @Index(name = "idx_usuario_id", columnList = "usuario_id"),
    @Index(name = "idx_publicacion_id", columnList = "publicacion_id")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notificacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId; // Usuario que recibe la notificación

    @Column(name = "publicacion_id", nullable = false)
    private Long publicacionId; // Publicación relacionada

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje; // Mensaje personalizado del admin/moderador

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm", timezone = "America/Santiago")
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private Boolean leida = false; // Indica si la notificación ha sido leída

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.leida == null) {
            this.leida = false;
        }
    }
}

