package com.QualifyGym.tema.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Table(name = "temas", indexes = {
    @Index(name = "idx_estado_id", columnList = "Estado_id_estado")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad Tema, representa los temas seleccionables dentro de la app para las publicaciones")
public class Tema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tema")
    @Schema(description = "ID único del tema", example = "1")
    private Long idTema;

    @Column(name = "nombre_tema", nullable = false, length = 200)
    @Schema(description = "Nombre del tema", example = "Nutrición")
    private String nombreTema;

    @Column(name = "Estado_id_estado", nullable = false)
    @Schema(description = "ID del estado asociado al tema", example = "1")
    private Long estadoId; // FK a Estado (se conectará con el microservicio de estados)

}
