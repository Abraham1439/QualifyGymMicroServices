package com.qualifygym.publicaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "publicaciones", indexes = {
    @Index(name = "idx_usuario_id", columnList = "Usuarios_id_usuario"),
    @Index(name = "idx_tema_id", columnList = "Tema_id_tema")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Publicacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicacion")
    private Long idPublicacion;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(name = "fecha", nullable = false)
    private Long fecha; // Timestamp en milisegundos

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Boolean oculta = false;

    @Column(name = "fecha_baneo")
    private Long fechaBaneo; // Timestamp en milisegundos

    @Column(name = "motivo_baneo", columnDefinition = "TEXT")
    private String motivoBaneo;

    @Column(name = "Usuarios_id_usuario", nullable = false)
    private Long usuarioId; // FK a Usuario (se conectará con el microservicio de usuarios)

    @Column(name = "Tema_id_tema", nullable = false)
    private Long temaId; // FK a Tema (se conectará con el microservicio de temas)

    @Column(name = "imageUrl", length = 500)
    private String imageUrl; // URL o path de la imagen de la publicación
}

