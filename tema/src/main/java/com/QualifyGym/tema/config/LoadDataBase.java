package com.QualifyGym.tema.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.QualifyGym.tema.repository.TemaRepository;

@Configuration
public class LoadDataBase {
    @Bean
    CommandLineRunner initDataBase(TemaRepository temaRepo) {
        return args -> {
            //si no hay registros en las tablas inserto los de defecto
            if(temaRepo.count() == 0) {
                //cargar los Temas
            }
        };
    }

}
