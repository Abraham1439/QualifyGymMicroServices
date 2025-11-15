package com.QualifyGym.tema.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.QualifyGym.tema.model.Tema;
import com.QualifyGym.tema.repository.TemaRepository;

@Configuration
public class LoadDataBase {
    
    @Bean
    CommandLineRunner initDataBase(TemaRepository temaRepo) {
        return args -> {
            // Si no hay registros en las tablas inserto los de defecto
            if (temaRepo.count() == 0) {
                // Cargar los Temas iniciales
                // Nota: Se asume que el estado con ID 1 (Activo) existe en el microservicio de estados
                temaRepo.save(new Tema(null, "Rutinas de Fuerza", 1L));
                temaRepo.save(new Tema(null, "Cardio y Resistencia", 1L));
                temaRepo.save(new Tema(null, "Nutrición", 1L));
                temaRepo.save(new Tema(null, "Suplementos", 1L));
                temaRepo.save(new Tema(null, "Recuperación", 1L));
                temaRepo.save(new Tema(null, "Motivación", 1L));

                System.out.println("Temas iniciales creados");
            } else {
                System.out.println("Datos ya existen. No se cargaron nuevos datos.");
            }
        };
    }
}
