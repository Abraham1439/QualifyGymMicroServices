package com.qualifygym.comentarios.model;

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
@Table(name = "comentarios", indexes = {
    @Index(name = "idx_usuario_id", columnList = "Usuarios_id_usuario"),
    @Index(name = "idx_publicacion_id", columnList = "Publicacion_id_publicacion")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comentario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario")
    private Long idComentario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_registro", nullable = false)
    private Long fechaRegistro; // Timestamp en milisegundos (como en la app)

    @Column(nullable = false)
    private Boolean oculto = false;

    @Column(name = "fecha_baneo")
    private Long fechaBaneo; // Timestamp en milisegundos (como en la app)

    @Column(name = "motivo_baneo", columnDefinition = "TEXT")
    private String motivoBaneo;

    @Column(name = "Usuarios_id_usuario", nullable = false)
    private Long usuarioId; // FK a Usuario (se conectará con el microservicio de usuarios)

    @Column(name = "Publicacion_id_publicacion", nullable = false)
    private Long publicacionId; // FK a Publicación (se conectará con el microservicio de publicaciones)

    //Formato para la fecha - se establece como timestamp en milisegundos
    @PrePersist
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = System.currentTimeMillis();
        }
    }
}

