package com.qualifygym.usuarios.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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

    public Usuario crearUsuario(String username, String password, String email, Long roleId) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new RuntimeException("El nombre de usuario ya existe: " + username);
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado: " + email);
        }

        Rol rol = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado ID:" + roleId));

        Usuario nuevo = new Usuario();
        nuevo.setUsername(username);
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setEmail(email);
        nuevo.setRol(rol);
        return usuarioRepository.save(nuevo);
    }

    public Usuario actualizarUsuario(Long id, String username, String password, String email, Long roleId) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado ID:" + id));

        if (username != null && !username.equals(existente.getUsername())) {
            if (usuarioRepository.existsByUsername(username)) {
                throw new RuntimeException("El nombre de usuario ya existe: " + username);
            }
            existente.setUsername(username);
        }
        if (password != null && !password.isEmpty()) {
            existente.setPassword(passwordEncoder.encode(password));
        }
        if (email != null && !email.equals(existente.getEmail())) {
            if (usuarioRepository.existsByEmail(email)) {
                throw new RuntimeException("El email ya está registrado: " + email);
            }
            existente.setEmail(email);
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
     * Registra un nuevo usuario con validaciones completas
     * @param username Nombre de usuario (único)
     * @param email Email del usuario (formato válido)
     * @param phone Teléfono del usuario (9 dígitos)
     * @param password Contraseña (mínimo 8 caracteres, mayúscula, minúscula, número, símbolo, sin espacios)
     * @param confirmPassword Confirmación de contraseña
     * @return Usuario creado
     */
    public Usuario registrarUsuario(String username, String email, String phone, String password, String confirmPassword) {
        // Validar username
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("El nombre de usuario es obligatorio");
        }
        if (usuarioRepository.existsByUsername(username.trim())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Validar email
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("El email es obligatorio");
        }
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!Pattern.matches(emailPattern, email.trim())) {
            throw new RuntimeException("Formato de email inválido");
        }
        if (usuarioRepository.existsByEmail(email.trim())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Validar teléfono
        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("El teléfono es obligatorio");
        }
        String phoneDigits = phone.replaceAll("[^0-9]", "");
        if (!phoneDigits.matches("^[0-9]+$")) {
            throw new RuntimeException("El teléfono solo debe contener números");
        }
        if (phoneDigits.length() != 9) {
            throw new RuntimeException("El teléfono debe tener 9 dígitos");
        }

        // Validar contraseña
        List<String> passwordErrors = validarContrasena(password);
        if (!passwordErrors.isEmpty()) {
            throw new RuntimeException("Errores en la contraseña: " + String.join(", ", passwordErrors));
        }

        // Validar confirmación
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            throw new RuntimeException("Debe confirmar la contraseña");
        }
        if (!password.equals(confirmPassword)) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        // Obtener rol "Usuario" por defecto (ID 2)
        Rol rolUsuario = roleRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Rol 'Usuario' no encontrado. Asegúrese de que la base de datos esté inicializada."));

        // Crear usuario
        Usuario nuevo = new Usuario();
        nuevo.setUsername(username.trim());
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setEmail(email.trim());
        nuevo.setPhone(phoneDigits);
        nuevo.setRol(rolUsuario);

        return usuarioRepository.save(nuevo);
    }

    /**
     * Valida la seguridad de la contraseña
     * @param password Contraseña a validar
     * @return Lista de errores (vacía si es válida)
     */
    private List<String> validarContrasena(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.trim().isEmpty()) {
            errors.add("La contraseña es obligatoria");
            return errors;
        }

        if (password.length() < 8) {
            errors.add("Debe tener mínimo 8 caracteres");
        }
        if (!password.chars().anyMatch(Character::isUpperCase)) {
            errors.add("Debe incluir una mayúscula");
        }
        if (!password.chars().anyMatch(Character::isLowerCase)) {
            errors.add("Debe incluir una minúscula");
        }
        if (!password.chars().anyMatch(Character::isDigit)) {
            errors.add("Debe incluir un número");
        }
        if (!password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch))) {
            errors.add("Debe incluir un símbolo");
        }
        if (password.contains(" ")) {
            errors.add("No debe contener espacios");
        }

        return errors;
    }
}

