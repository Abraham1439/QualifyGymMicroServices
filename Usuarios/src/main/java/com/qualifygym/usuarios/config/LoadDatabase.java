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
                usuarioRepo.save(new Usuario(null, "admin", encoder.encode("admin123"), "admin@qualifygym.com", admin));
                usuarioRepo.save(new Usuario(null, "usuario1", encoder.encode("usuario123"), "usuario1@qualifygym.com", usuario));

                System.out.println(" Usuarios creados con contraseña encriptada");
            } else {
                System.out.println("ℹ Datos ya existen. No se cargaron nuevos datos.");
            }
        };
    }
}

