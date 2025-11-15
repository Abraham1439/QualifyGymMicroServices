package com.qualifygym.estados.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.qualifygym.estados.model.Estado;
import com.qualifygym.estados.repository.EstadoRepository;

@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(EstadoRepository estadoRepo) {
        return args -> {
            if (estadoRepo.count() == 0) {
                // Crear estados iniciales comunes
                estadoRepo.save(new Estado(null, "Activo"));
                estadoRepo.save(new Estado(null, "Inactivo"));
                estadoRepo.save(new Estado(null, "Pendiente"));
                estadoRepo.save(new Estado(null, "Eliminado"));

                System.out.println("✅ Estados iniciales creados");
            } else {
                System.out.println("ℹ️ Datos ya existen. No se cargaron nuevos datos.");
            }
        };
    }
}

