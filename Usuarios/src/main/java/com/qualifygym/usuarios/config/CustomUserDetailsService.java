package com.qualifygym.usuarios.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.qualifygym.usuarios.model.Usuario;
import com.qualifygym.usuarios.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Devuelve el nombre del rol exacto como está en tu BD ("Administrador")
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                Collections.singletonList(() -> usuario.getRol().getNombre())
        );
    }
}

