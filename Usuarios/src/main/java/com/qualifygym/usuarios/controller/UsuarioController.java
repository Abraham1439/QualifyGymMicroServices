package com.qualifygym.usuarios.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qualifygym.usuarios.model.Usuario;
import com.qualifygym.usuarios.service.UsuarioService;

import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/usuario")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios del sistema QualifyGym")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios registrados en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
            @ApiResponse(responseCode = "204", description = "No hay usuarios registrados")
    })
    @GetMapping("/users")
    public ResponseEntity<List<Usuario>> getUsuarios() {
        List<Usuario> users = usuarioService.obtenerUsuarios();
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Retorna la información de un usuario específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUsuarioById(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtener usuario por email", description = "Retorna la información de un usuario específico por su email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/users/email/{email}")
    public ResponseEntity<?> getUsuarioByEmail(@PathVariable String email) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email.trim());
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario en el sistema. Requiere autenticación con rol Administrador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o faltantes"),
            @ApiResponse(responseCode = "401", description = "No autorizado - requiere rol Administrador")
    })
    @PostMapping("/users")
    public ResponseEntity<?> crearUsuario(@RequestBody Map<String, Object> datos) {
        try {
            String username = (String) datos.get("username");
            String password = (String) datos.get("password");
            String email = (String) datos.get("email");
            String phone = (String) datos.get("phone");
            Long rolId = datos.get("rolId") != null ? Long.valueOf(datos.get("rolId").toString()) : null;
            String photoUrl = datos.get("photoUrl") != null ? (String) datos.get("photoUrl") : null;
            
            if (username == null || password == null || email == null || phone == null || rolId == null) {
                return ResponseEntity.badRequest().body("Faltan campos requeridos: username, password, email, phone, rolId");
            }
            
            Usuario nuevo = usuarioService.crearUsuario(username, password, email, phone, rolId, photoUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza la información de un usuario existente. Requiere autenticación con rol Administrador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado - requiere rol Administrador")
    })
    @PutMapping("/users/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Map<String, Object> datos) {
        try {
            String username = (String) datos.get("username");
            String password = (String) datos.get("password");
            String email = (String) datos.get("email");
            String phone = (String) datos.get("phone");
            Long rolId = datos.get("rolId") != null ? Long.valueOf(datos.get("rolId").toString()) : null;
            String photoUrl = datos.get("photoUrl") != null ? (String) datos.get("photoUrl") : null;
            
            Usuario actualizado = usuarioService.actualizarUsuario(id, username, password, email, phone, rolId, photoUrl);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema. Requiere autenticación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error al eliminar usuario")
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error al eliminar usuario: " + e.getMessage());
        }
    }

    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales de un usuario usando su email y contraseña para permitir el acceso al sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "400", description = "Faltan campos 'email' o 'password'"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> datos) {
        try {
            String email = datos.get("email");
            String password = datos.get("password");

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El campo 'email' es requerido");
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El campo 'password' es requerido");
            }

            boolean valido = usuarioService.validarCredenciales(email.trim(), password);
            if (valido) {
                return ResponseEntity.ok("Login exitoso");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas. Verifica tu email y contraseña.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Registro público de usuario", description = "Permite a cualquier usuario registrarse en el sistema. Asigna automáticamente el rol 'Usuario'. Este endpoint es público y no requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, faltantes o duplicados (username/email ya existe)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> datos) {
        try {
            String username = datos.get("username");
            String password = datos.get("password");
            String email = datos.get("email");
            String phone = datos.get("phone");

            // Validar que todos los campos requeridos estén presentes
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El campo 'username' es requerido");
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El campo 'password' es requerido");
            }
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El campo 'email' es requerido");
            }
            if (phone == null || phone.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El campo 'phone' es requerido");
            }

            // Crear el usuario con rol "Usuario" por defecto
            Usuario nuevo = usuarioService.registrarUsuarioPublico(
                username.trim(), 
                password, 
                email.trim(), 
                phone.trim()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error interno: " + e.getMessage());
        }
    }

    @Operation(summary = "Verificar si un usuario existe", description = "Retorna true si el usuario existe, false en caso contrario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación exitosa"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/users/{id}/existe")
    public ResponseEntity<Boolean> existeUsuario(@PathVariable Long id) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
            return ResponseEntity.ok(usuario != null);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}

