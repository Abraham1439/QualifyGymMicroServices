package com.qualifygym.usuarios.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa un usuario en el sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario", example = "1")
    private Long id;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre de usuario", example = "Victor Isidro")
    private String username;

    @Column(nullable = false, length = 100)
    @Schema(description = "Email del usuario", example = "Victor@duocuc.cl")
    private String email;

    @Column(nullable = false, length = 20)
    @Schema(description = "Número de teléfono del usuario", example = "+56912345678")
    private String phone;

    @Column(nullable = false)
    @JsonIgnore
    @Schema(description = "Contraseña del usuario (almacenada de forma segura)", example = "hashed_password")
    private String password;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    @JsonIgnoreProperties("users")
    @Schema(description = "Rol asignado al usuario", example = "Administrador")
    private Rol rol;

    @Column(name = "photo_url", length = 255, nullable = true)
    @Schema(description = "ID de la imagen de perfil del microservicio de Imágenes", example = "123")
    private String photoUrl;
}

