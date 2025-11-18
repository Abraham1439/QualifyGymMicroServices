package com.qualifygym.usuarios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.qualifygym.usuarios.model.Rol;
import com.qualifygym.usuarios.model.Usuario;
import com.qualifygym.usuarios.repository.RoleRepository;
import com.qualifygym.usuarios.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario crearUsuario(String username, String password, String email, String phone, Long roleId) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new RuntimeException("El username ya está registrado: " + username);
        }

        Rol rol = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado ID:" + roleId));

        Usuario nuevo = new Usuario();
        nuevo.setUsername(username);
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setEmail(email);
        nuevo.setPhone(phone);
        nuevo.setRol(rol);
        return usuarioRepository.save(nuevo);
    }

    public Usuario actualizarUsuario(Long id, String username, String password, String email, String phone, Long roleId) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado ID:" + id));

        if (username != null && !username.trim().isEmpty()) {
            if (!username.equals(existente.getUsername()) && usuarioRepository.existsByUsername(username)) {
                throw new RuntimeException("El username ya está registrado: " + username);
            }
            existente.setUsername(username.trim());
        }
        if (password != null && !password.isEmpty()) {
            existente.setPassword(passwordEncoder.encode(password));
        }
        if (email != null && !email.equals(existente.getEmail())) {
            existente.setEmail(email);
        }
        if (phone != null && !phone.trim().isEmpty()) {
            existente.setPhone(phone.trim());
        }
        if (roleId != null) {
            Rol rol = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado ID:" + roleId));
            existente.setRol(rol);
        }
        return usuarioRepository.save(existente);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public boolean validarCredenciales(String email, String rawPassword) {
        Optional<Usuario> opt = usuarioRepository.findByEmail(email);
        return opt.isPresent() && passwordEncoder.matches(rawPassword, opt.get().getPassword());
    }

    /**
     * Obtiene un usuario por su email
     */
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    /**
     * Registro público de usuarios. Crea un nuevo usuario con el rol "Usuario" por defecto.
     * Este método es para registro público, a diferencia de crearUsuario que requiere rol Administrador.
     */
    public Usuario registrarUsuarioPublico(String username, String password, String email, String phone) {
        // Validar que el username no esté duplicado
        if (usuarioRepository.existsByUsername(username)) {
            throw new RuntimeException("El username ya está registrado: " + username);
        }

        // Validar que el email no esté duplicado
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado: " + email);
        }

        // Buscar el rol "Usuario" por defecto
        Rol rolUsuario = roleRepository.findAll().stream()
                .filter(rol -> "Usuario".equals(rol.getNombre()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Rol 'Usuario' no encontrado en el sistema"));

        Usuario nuevo = new Usuario();
        nuevo.setUsername(username);
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setEmail(email);
        nuevo.setPhone(phone);
        nuevo.setRol(rolUsuario);
        return usuarioRepository.save(nuevo);
    }
}

