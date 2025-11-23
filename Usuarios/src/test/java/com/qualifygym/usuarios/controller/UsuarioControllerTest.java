package com.qualifygym.usuarios.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import com.qualifygym.usuarios.model.Rol;
import com.qualifygym.usuarios.model.Usuario;
import com.qualifygym.usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests de integración para UsuarioController
 * 
 * Esta clase contiene tests que verifican los endpoints REST del controlador de usuarios.
 * Utiliza MockMvc para simular peticiones HTTP y verificar las respuestas.
 * Los tests cubren todos los endpoints: GET, POST, PUT, DELETE y login.
 */
@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false) // Desactiva la seguridad para los tests
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario usuarioTest;
    private Rol rolAdmin;

    /**
     * Configuración inicial antes de cada test
     * Crea objetos de prueba para usuarios y roles
     */
    @BeforeEach
    void setUp() {
        // Crear rol de prueba
        rolAdmin = new Rol();
        rolAdmin.setId(1L);
        rolAdmin.setNombre("Administrador");
        
        // Crear usuario de prueba
        usuarioTest = new Usuario();
        usuarioTest.setId(1L);
        usuarioTest.setUsername("testuser");
        usuarioTest.setEmail("test@test.com");
        usuarioTest.setPhone("123456789");
        usuarioTest.setRol(rolAdmin);
    }

    /**
     * Test: GET /users - Listar todos los usuarios
     * Verifica que el endpoint retorna una lista de usuarios con status 200
     */
    @Test
    void getUsuarios_deberiaRetornarListaYStatus200() throws Exception {
        // Arrange: Preparar lista de usuarios
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuarioTest);
        
        when(usuarioService.obtenerUsuarios()).thenReturn(usuarios);
        
        // Act & Assert: Realizar petición GET y verificar respuesta
        mockMvc.perform(get("/api/v1/usuario/users")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[0].username").value("testuser"))
               .andExpect(jsonPath("$[0].email").value("test@test.com"))
               .andExpect(jsonPath("$[0].phone").value("123456789"));
        
        verify(usuarioService, times(1)).obtenerUsuarios();
    }

    /**
     * Test: GET /users - Lista vacía
     * Verifica que el endpoint retorna status 204 cuando no hay usuarios
     */
    @Test
    void getUsuarios_conListaVacia_deberiaRetornarStatus204() throws Exception {
        // Arrange
        when(usuarioService.obtenerUsuarios()).thenReturn(new ArrayList<>());
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/usuario/users")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());
        
        verify(usuarioService, times(1)).obtenerUsuarios();
    }

    /**
     * Test: GET /users/{id} - Obtener usuario por ID existente
     * Verifica que el endpoint retorna el usuario con status 200
     */
    @Test
    void getUsuarioById_conIdExistente_deberiaRetornarUsuarioYStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        when(usuarioService.obtenerUsuarioPorId(id)).thenReturn(usuarioTest);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/usuario/users/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.username").value("testuser"))
               .andExpect(jsonPath("$.email").value("test@test.com"));
        
        verify(usuarioService, times(1)).obtenerUsuarioPorId(id);
    }

    /**
     * Test: GET /users/{id} - Usuario no encontrado
     * Verifica que el endpoint retorna status 404 cuando el usuario no existe
     */
    @Test
    void getUsuarioById_conIdInexistente_deberiaRetornarStatus404() throws Exception {
        // Arrange
        Long id = 999L;
        when(usuarioService.obtenerUsuarioPorId(id)).thenReturn(null);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/usuario/users/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(content().string("Usuario no encontrado"));
        
        verify(usuarioService, times(1)).obtenerUsuarioPorId(id);
    }

    /**
     * Test: POST /users - Crear usuario exitosamente
     * Verifica que el endpoint crea un usuario y retorna status 201
     */
    @Test
    void crearUsuario_conDatosValidos_deberiaRetornarStatus201() throws Exception {
        // Arrange: Preparar datos del request
        String requestBody = """
            {
                "username": "nuevoUsuario",
                "password": "password123",
                "email": "nuevo@test.com",
                "phone": "987654321",
                "rolId": 1
            }
            """;
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setId(2L);
        nuevoUsuario.setUsername("nuevoUsuario");
        nuevoUsuario.setEmail("nuevo@test.com");
        nuevoUsuario.setPhone("987654321");
        nuevoUsuario.setRol(rolAdmin);
        
        when(usuarioService.crearUsuario("nuevoUsuario", "password123", "nuevo@test.com", "987654321", 1L, null))
            .thenReturn(nuevoUsuario);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/usuario/users")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(2L))
               .andExpect(jsonPath("$.username").value("nuevoUsuario"))
               .andExpect(jsonPath("$.email").value("nuevo@test.com"));
        
        verify(usuarioService, times(1)).crearUsuario("nuevoUsuario", "password123", "nuevo@test.com", "987654321", 1L, null);
    }

    /**
     * Test: POST /users - Crear usuario con campos faltantes
     * Verifica que el endpoint retorna status 400 cuando faltan campos requeridos
     */
    @Test
    void crearUsuario_conCamposFaltantes_deberiaRetornarStatus400() throws Exception {
        // Arrange: Request sin campos requeridos
        String requestBody = """
            {
                "username": "nuevoUsuario",
                "password": "password123"
            }
            """;
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/usuario/users")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(org.hamcrest.Matchers.containsString("Faltan campos requeridos")));
        
        verify(usuarioService, never()).crearUsuario(anyString(), anyString(), anyString(), anyString(), anyLong(), anyString());
    }

    /**
     * Test: POST /users - Crear usuario con username duplicado
     * Verifica que el endpoint retorna status 400 cuando el username ya existe
     */
    @Test
    void crearUsuario_conUsernameDuplicado_deberiaRetornarStatus400() throws Exception {
        // Arrange
        String requestBody = """
            {
                "username": "usuarioExistente",
                "password": "password123",
                "email": "test@test.com",
                "phone": "123456789",
                "rolId": 1
            }
            """;
        
        when(usuarioService.crearUsuario(anyString(), anyString(), anyString(), anyString(), anyLong(), anyString()))
            .thenThrow(new RuntimeException("El username ya está registrado: usuarioExistente"));
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/usuario/users")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(org.hamcrest.Matchers.containsString("El username ya está registrado")));
    }

    /**
     * Test: PUT /users/{id} - Actualizar usuario exitosamente
     * Verifica que el endpoint actualiza un usuario y retorna status 200
     */
    @Test
    void actualizarUsuario_conDatosValidos_deberiaRetornarStatus200() throws Exception {
        // Arrange
        Long id = 1L;
        String requestBody = """
            {
                "username": "usuarioActualizado",
                "email": "actualizado@test.com",
                "phone": "111222333",
                "rolId": 2
            }
            """;
        
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(id);
        usuarioActualizado.setUsername("usuarioActualizado");
        usuarioActualizado.setEmail("actualizado@test.com");
        usuarioActualizado.setPhone("111222333");
        
        when(usuarioService.actualizarUsuario(eq(id), anyString(), isNull(), anyString(), anyString(), anyLong(), isNull()))
            .thenReturn(usuarioActualizado);
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/usuario/users/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.username").value("usuarioActualizado"))
               .andExpect(jsonPath("$.email").value("actualizado@test.com"));
        
        verify(usuarioService, times(1)).actualizarUsuario(eq(id), anyString(), isNull(), anyString(), anyString(), anyLong(), isNull());
    }

    /**
     * Test: PUT /users/{id} - Actualizar usuario inexistente
     * Verifica que el endpoint retorna status 400 cuando el usuario no existe
     */
    @Test
    void actualizarUsuario_conIdInexistente_deberiaRetornarStatus400() throws Exception {
        // Arrange
        Long id = 999L;
        String requestBody = """
            {
                "username": "nuevo",
                "email": "test@test.com"
            }
            """;
        
        when(usuarioService.actualizarUsuario(eq(id), anyString(), isNull(), anyString(), isNull(), isNull(), isNull()))
            .thenThrow(new RuntimeException("Usuario no encontrado ID:" + id));
        
        // Act & Assert
        mockMvc.perform(put("/api/v1/usuario/users/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(org.hamcrest.Matchers.containsString("Usuario no encontrado")));
    }

    /**
     * Test: DELETE /users/{id} - Eliminar usuario exitosamente
     * Verifica que el endpoint elimina un usuario y retorna status 204
     */
    @Test
    void eliminarUsuario_conIdValido_deberiaRetornarStatus204() throws Exception {
        // Arrange
        Long id = 1L;
        doNothing().when(usuarioService).eliminarUsuario(id);
        
        // Act & Assert
        mockMvc.perform(delete("/api/v1/usuario/users/{id}", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());
        
        verify(usuarioService, times(1)).eliminarUsuario(id);
    }

    /**
     * Test: POST /login - Login exitoso
     * Verifica que el endpoint valida credenciales correctas y retorna status 200
     */
    @Test
    void login_conCredencialesCorrectas_deberiaRetornarStatus200() throws Exception {
        // Arrange
        String requestBody = """
            {
                "email": "test@test.com",
                "password": "password123"
            }
            """;
        
        when(usuarioService.validarCredenciales("test@test.com", "password123")).thenReturn(true);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/usuario/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isOk())
               .andExpect(content().string("Login exitoso"));
        
        verify(usuarioService, times(1)).validarCredenciales("test@test.com", "password123");
    }

    /**
     * Test: POST /login - Credenciales incorrectas
     * Verifica que el endpoint rechaza credenciales inválidas y retorna status 401
     */
    @Test
    void login_conCredencialesIncorrectas_deberiaRetornarStatus401() throws Exception {
        // Arrange
        String requestBody = """
            {
                "email": "test@test.com",
                "password": "passwordIncorrecto"
            }
            """;
        
        when(usuarioService.validarCredenciales("test@test.com", "passwordIncorrecto")).thenReturn(false);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/usuario/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isUnauthorized())
               .andExpect(content().string("Credenciales inválidas"));
        
        verify(usuarioService, times(1)).validarCredenciales("test@test.com", "passwordIncorrecto");
    }

    /**
     * Test: POST /login - Campos faltantes
     * Verifica que el endpoint retorna status 400 cuando faltan campos
     */
    @Test
    void login_conCamposFaltantes_deberiaRetornarStatus400() throws Exception {
        // Arrange: Request sin password
        String requestBody = """
            {
                "email": "test@test.com"
            }
            """;
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/usuario/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(org.hamcrest.Matchers.containsString("El campo 'password' es requerido")));
        
        verify(usuarioService, never()).validarCredenciales(anyString(), anyString());
    }

    /**
     * Test: GET /users/email/{email} - Obtener usuario por email
     * Verifica que el endpoint retorna el usuario con status 200
     */
    @Test
    void getUsuarioByEmail_conEmailExistente_deberiaRetornarUsuarioYStatus200() throws Exception {
        // Arrange
        String email = "test@test.com";
        when(usuarioService.obtenerUsuarioPorEmail(email)).thenReturn(usuarioTest);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/usuario/users/email/{email}", email)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.email").value("test@test.com"))
               .andExpect(jsonPath("$.username").value("testuser"));
        
        verify(usuarioService, times(1)).obtenerUsuarioPorEmail(email);
    }

    /**
     * Test: POST /register - Registrar usuario público exitosamente
     * Verifica que el endpoint registra un usuario y retorna status 201
     */
    @Test
    void register_conDatosValidos_deberiaRetornarStatus201() throws Exception {
        // Arrange
        String requestBody = """
            {
                "username": "nuevoUsuario",
                "password": "password123",
                "email": "nuevo@test.com",
                "phone": "987654321"
            }
            """;
        
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setId(2L);
        nuevoUsuario.setUsername("nuevoUsuario");
        nuevoUsuario.setEmail("nuevo@test.com");
        nuevoUsuario.setPhone("987654321");
        nuevoUsuario.setRol(rolAdmin);
        
        when(usuarioService.registrarUsuarioPublico("nuevoUsuario", "password123", "nuevo@test.com", "987654321"))
            .thenReturn(nuevoUsuario);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/usuario/register")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(2L))
               .andExpect(jsonPath("$.username").value("nuevoUsuario"))
               .andExpect(jsonPath("$.email").value("nuevo@test.com"));
        
        verify(usuarioService, times(1)).registrarUsuarioPublico("nuevoUsuario", "password123", "nuevo@test.com", "987654321");
    }

    /**
     * Test: POST /register - Registrar usuario con email duplicado
     * Verifica que el endpoint retorna status 400 cuando el email ya existe
     */
    @Test
    void register_conEmailDuplicado_deberiaRetornarStatus400() throws Exception {
        // Arrange
        String requestBody = """
            {
                "username": "nuevoUsuario",
                "password": "password123",
                "email": "test@test.com",
                "phone": "987654321"
            }
            """;
        
        when(usuarioService.registrarUsuarioPublico(anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("El email ya está registrado: test@test.com"));
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/usuario/register")
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestBody))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(org.hamcrest.Matchers.containsString("El email ya está registrado")));
    }

    /**
     * Test: GET /users/{id}/existe - Verificar existencia de usuario
     * Verifica que el endpoint retorna true cuando el usuario existe
     */
    @Test
    void existeUsuario_conIdExistente_deberiaRetornarTrue() throws Exception {
        // Arrange
        Long id = 1L;
        when(usuarioService.obtenerUsuarioPorId(id)).thenReturn(usuarioTest);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/usuario/users/{id}/existe", id)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().string("true"));
        
        verify(usuarioService, times(1)).obtenerUsuarioPorId(id);
    }
}
