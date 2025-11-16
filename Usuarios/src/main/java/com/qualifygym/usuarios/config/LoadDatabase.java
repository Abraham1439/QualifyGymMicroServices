package com.qualifygym.usuarios.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.qualifygym.usuarios.model.Rol;
import com.qualifygym.usuarios.model.Usuario;
import com.qualifygym.usuarios.repository.RoleRepository;
import com.qualifygym.usuarios.repository.UsuarioRepository;

@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepo, UsuarioRepository usuarioRepo, PasswordEncoder encoder) {
        return args -> {
            if (roleRepo.count() == 0 && usuarioRepo.count() == 0) {
                Rol admin = new Rol();
                admin.setNombre("Administrador");
                roleRepo.save(admin);

                Rol usuario = new Rol();
                usuario.setNombre("Usuario");
                roleRepo.save(usuario);

                // 🔐 Contraseñas encriptadas
                Usuario adminUser = new Usuario();
                adminUser.setUsername("admin");
                adminUser.setPassword(encoder.encode("admin123"));
                adminUser.setEmail("admin@qualifygym.com");
                adminUser.setRol(admin);
                usuarioRepo.save(adminUser);

                Usuario normalUser = new Usuario();
                normalUser.setUsername("usuario1");
                normalUser.setPassword(encoder.encode("usuario123"));
                normalUser.setEmail("usuario1@qualifygym.com");
                normalUser.setRol(usuario);
                usuarioRepo.save(normalUser);

                System.out.println(" Usuarios creados con contraseña encriptada");
            } else {
                System.out.println("ℹ Datos ya existen. No se cargaron nuevos datos.");
            }
        };
    }
}

