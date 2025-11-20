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
            // Verificar y crear roles si no existen
            Rol admin = null;
            Rol usuario = null;
            Rol moderador = null;
            
            if (roleRepo.count() == 0) {
                admin = new Rol();
                admin.setNombre("Administrador");
                roleRepo.save(admin);

                usuario = new Rol();
                usuario.setNombre("Usuario");
                roleRepo.save(usuario);

                moderador = new Rol();
                moderador.setNombre("Moderador");
                roleRepo.save(moderador);
            } else {
                // Verificar si existe el rol Moderador
                boolean existeModerador = roleRepo.findAll().stream()
                    .anyMatch(rol -> "Moderador".equals(rol.getNombre()));
                
                if (!existeModerador) {
                    moderador = new Rol();
                    moderador.setNombre("Moderador");
                    roleRepo.save(moderador);
                    System.out.println("Rol 'Moderador' agregado");
                }
                
                // Obtener roles existentes para crear usuarios
                admin = roleRepo.findAll().stream()
                    .filter(rol -> "Administrador".equals(rol.getNombre()))
                    .findFirst()
                    .orElse(null);
                    
                usuario = roleRepo.findAll().stream()
                    .filter(rol -> "Usuario".equals(rol.getNombre()))
                    .findFirst()
                    .orElse(null);
                    
                moderador = roleRepo.findAll().stream()
                    .filter(rol -> "Moderador".equals(rol.getNombre()))
                    .findFirst()
                    .orElse(null);
            }

            // Crear usuarios iniciales solo si no existen
            if (usuarioRepo.count() == 0 && admin != null && usuario != null && moderador != null) {
                Usuario adminUser = new Usuario();
                adminUser.setUsername("admin");
                adminUser.setPassword(encoder.encode("admin123"));
                adminUser.setEmail("admin@qualifygym.com");
                adminUser.setPhone("123456789");
                adminUser.setRol(admin);
                usuarioRepo.save(adminUser);

                Usuario normalUser = new Usuario();
                normalUser.setUsername("usuario1");
                normalUser.setPassword(encoder.encode("usuario123"));
                normalUser.setEmail("usuario1@qualifygym.com");
                normalUser.setPhone("987654321");
                normalUser.setRol(usuario);
                usuarioRepo.save(normalUser);

                Usuario moderadorUser = new Usuario();
                moderadorUser.setUsername("moderador");
                moderadorUser.setPassword(encoder.encode("moderador123."));
                moderadorUser.setEmail("moderador@qualifygym.com");
                moderadorUser.setPhone("555555555");
                moderadorUser.setRol(moderador);
                usuarioRepo.save(moderadorUser);

                System.out.println("Usuarios creados con contraseña encriptada");
            } else if (usuarioRepo.count() > 0) {
                // Verificar si existe el usuario moderador de prueba
                boolean existeModeradorUser = usuarioRepo.findAll().stream()
                    .anyMatch(u -> "moderador@qualifygym.com".equals(u.getEmail()));
                
                if (!existeModeradorUser && moderador != null) {
                    Usuario moderadorUser = new Usuario();
                    moderadorUser.setUsername("moderador");
                    moderadorUser.setPassword(encoder.encode("moderador123"));
                    moderadorUser.setEmail("moderador@qualifygym.com");
                    moderadorUser.setPhone("555555555");
                    moderadorUser.setRol(moderador);
                    usuarioRepo.save(moderadorUser);
                    System.out.println("Usuario moderador de prueba creado");
                } else {
                    System.out.println("ℹ Datos ya existen. No se cargaron nuevos datos.");
                }
            }
        };
    }
}

