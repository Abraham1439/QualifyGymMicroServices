package com.QualifyGym.tema.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Tema")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad Tema, representa los temas seleccionables dentro de la app para las publicaciones")
public class Tema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del tema", example = "1")
    private Long idTema;

    @Column(name = "nombre_tema", nullable = false)
    @Schema(description = "Nombre del tema", example = "Nutrición")
    private String nombreTema;

}
