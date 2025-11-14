package com.qualifygym.usuarios.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.qualifygym.usuarios.model.Rol;
import com.qualifygym.usuarios.model.Usuario;
import com.qualifygym.usuarios.repository.UsuarioRepository;
import com.qualifygym.usuarios.repository.RoleRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearUsuario_debeRetornarUsuarioCreado() {
        String username = "testuser";
        String password = "password";
        String email = "testuser@qualifygym.com";
        Long roleId = 1L;

        Rol rolMock = new Rol(roleId, "Administrador", null);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(rolMock));
        when(usuarioRepository.existsByUsername(username)).thenReturn(false);
        when(usuarioRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encryptedPass");

        Usuario guardado = new Usuario(null, username, "encryptedPass", email, rolMock);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(guardado);

        Usuario resultado = usuarioService.crearUsuario(username, password, email, roleId);

        assertNotNull(resultado);
        assertEquals(username, resultado.getUsername());
        assertEquals("encryptedPass", resultado.getPassword());
        assertEquals(email, resultado.getEmail());
        assertEquals("Administrador", resultado.getRol().getNombre());
    }
}

