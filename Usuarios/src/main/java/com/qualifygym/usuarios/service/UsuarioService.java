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
        return crearUsuario(username, password, email, phone, roleId, null);
    }

    public Usuario crearUsuario(String username, String password, String email, String phone, Long roleId, String photoUrl) {
        Rol rol = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado ID:" + roleId));

        Usuario nuevo = new Usuario();
        nuevo.setUsername(username);
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setEmail(email);
        nuevo.setPhone(phone);
        nuevo.setRol(rol);
        nuevo.setPhotoUrl(photoUrl);
        return usuarioRepository.save(nuevo);
    }

    public Usuario actualizarUsuario(Long id, String username, String password, String email, String phone, Long roleId) {
        return actualizarUsuario(id, username, password, email, phone, roleId, null);
    }

    public Usuario actualizarUsuario(Long id, String username, String password, String email, String phone, Long roleId, String photoUrl) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado ID:" + id));

        if (username != null && !username.trim().isEmpty()) {
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
        // Actualizar photoUrl si se proporciona (puede ser null para eliminarlo)
        if (photoUrl != null || (photoUrl == null && existente.getPhotoUrl() != null)) {
            existente.setPhotoUrl(photoUrl);
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
     * 
     * NOTA: El username puede repetirse (no es único). Solo el email y teléfono deben ser únicos.
     */
    public Usuario registrarUsuarioPublico(String username, String password, String email, String phone) {
        // Validar que el email no esté duplicado (el email debe ser único)
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado: " + email);
        }

        // Validar que el teléfono no esté duplicado (el teléfono debe ser único)
        if (usuarioRepository.existsByPhone(phone)) {
            throw new RuntimeException("El teléfono ya está registrado: " + phone);
        }

        // Buscar el rol "Usuario" por defecto
        Rol rolUsuario = roleRepository.findAll().stream()
                .filter(rol -> "Usuario".equals(rol.getNombre()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Rol 'Usuario' no encontrado en el sistema"));

        Usuario nuevo = new Usuario();
        nuevo.setUsername(username); // El username puede repetirse, no se valida como único
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setEmail(email);
        nuevo.setPhone(phone);
        nuevo.setRol(rolUsuario);
        
        try {
            return usuarioRepository.save(nuevo);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Manejar errores de integridad de base de datos
            String errorMsg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            
            // Si el error es por username (restricción UNIQUE en BD), 
            // esto NO debería pasar ya que username puede repetirse
            // Si ocurre, significa que hay una restricción UNIQUE en la base de datos que debe eliminarse
            if (errorMsg.contains("username") || errorMsg.contains("uk_username") || 
                errorMsg.contains("unique") && errorMsg.contains("username")) {
                throw new RuntimeException(
                    "Error: La base de datos tiene una restricción UNIQUE en el campo 'username' que debe eliminarse. " +
                    "El username NO debe ser único. Ejecuta: ALTER TABLE usuarios DROP INDEX nombre_del_indice;"
                );
            }
            
            // Si el error es por email o phone (que SÍ deben ser únicos), mostrar mensaje específico
            if (errorMsg.contains("email")) {
                throw new RuntimeException("El email ya está registrado: " + email);
            }
            if (errorMsg.contains("phone") || errorMsg.contains("tel")) {
                throw new RuntimeException("El teléfono ya está registrado: " + phone);
            }
            
            // Otro error de integridad
            throw new RuntimeException("Error al registrar usuario: " + e.getMessage());
        }
    }
}

