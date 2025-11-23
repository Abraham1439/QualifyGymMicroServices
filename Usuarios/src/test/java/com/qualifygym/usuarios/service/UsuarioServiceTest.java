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
import java.util.List;
import java.util.ArrayList;

/**
 * Tests unitarios para UsuarioService
 * 
 * Esta clase contiene tests que verifican la lógica de negocio del servicio de usuarios,
 * incluyendo creación, actualización, eliminación, búsqueda y validación de credenciales.
 * Utiliza mocks para aislar las pruebas de la capa de persistencia.
 */
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Rol rolAdmin;
    private Usuario usuarioTest;

    /**
     * Configuración inicial antes de cada test
     * Crea objetos de prueba (mocks) para roles y usuarios
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Crear un rol de prueba
        rolAdmin = new Rol();
        rolAdmin.setId(1L);
        rolAdmin.setNombre("Administrador");
        
        // Crear un usuario de prueba
        usuarioTest = new Usuario();
        usuarioTest.setId(1L);
        usuarioTest.setUsername("testuser");
        usuarioTest.setEmail("test@test.com");
        usuarioTest.setPhone("123456789");
        usuarioTest.setPassword("encodedPassword");
        usuarioTest.setRol(rolAdmin);
    }

    /**
     * Test: Obtener todos los usuarios
     * Verifica que el servicio retorna correctamente la lista de usuarios
     */
    @Test
    void obtenerUsuarios_debeRetornarListaDeUsuarios() {
        // Arrange: Preparar datos de prueba
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuarioTest);
        
        // Configurar el mock para que retorne la lista cuando se llame findAll()
        when(usuarioRepository.findAll()).thenReturn(usuarios);
        
        // Act: Ejecutar el método a probar
        List<Usuario> resultado = usuarioService.obtenerUsuarios();
        
        // Assert: Verificar que el resultado es correcto
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("testuser", resultado.get(0).getUsername());
        
        // Verificar que se llamó al repositorio
        verify(usuarioRepository, times(1)).findAll();
    }

    /**
     * Test: Obtener usuario por ID existente
     * Verifica que el servicio retorna el usuario cuando existe
     */
    @Test
    void obtenerUsuarioPorId_conIdExistente_debeRetornarUsuario() {
        // Arrange
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioTest));
        
        // Act
        Usuario resultado = usuarioService.obtenerUsuarioPorId(id);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("testuser", resultado.getUsername());
        verify(usuarioRepository, times(1)).findById(id);
    }

    /**
     * Test: Obtener usuario por ID inexistente
     * Verifica que el servicio retorna null cuando el usuario no existe
     */
    @Test
    void obtenerUsuarioPorId_conIdInexistente_debeRetornarNull() {
        // Arrange
        Long id = 999L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        
        // Act
        Usuario resultado = usuarioService.obtenerUsuarioPorId(id);
        
        // Assert
        assertNull(resultado);
        verify(usuarioRepository, times(1)).findById(id);
    }

    /**
     * Test: Crear usuario exitosamente
     * Verifica que el servicio crea un usuario correctamente con todos los campos
     */
    @Test
    void crearUsuario_conDatosValidos_debeRetornarUsuarioCreado() {
        // Arrange
        String username = "nuevoUsuario";
        String password = "password123";
        String email = "nuevo@test.com";
        String phone = "987654321";
        Long roleId = 1L;
        
        // Configurar mocks
        when(usuarioRepository.existsByUsername(username)).thenReturn(false);
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(rolAdmin));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });
        
        // Act
        Usuario resultado = usuarioService.crearUsuario(username, password, email, phone, roleId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(username, resultado.getUsername());
        assertEquals(email, resultado.getEmail());
        assertEquals(phone, resultado.getPhone());
        assertEquals(rolAdmin, resultado.getRol());
        
        // Verificar que se llamaron los métodos necesarios
        verify(usuarioRepository, times(1)).existsByUsername(username);
        verify(roleRepository, times(1)).findById(roleId);
        verify(passwordEncoder, times(1)).encode(password);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    /**
     * Test: Crear usuario con username duplicado
     * Verifica que el servicio lanza una excepción cuando el username ya existe
     */
    @Test
    void crearUsuario_conUsernameDuplicado_debeLanzarExcepcion() {
        // Arrange
        String username = "usuarioExistente";
        when(usuarioRepository.existsByUsername(username)).thenReturn(true);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.crearUsuario(username, "password", "email@test.com", "123456789", 1L);
        });
        
        assertEquals("El username ya está registrado: " + username, exception.getMessage());
        verify(usuarioRepository, times(1)).existsByUsername(username);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    /**
     * Test: Crear usuario con rol inexistente
     * Verifica que el servicio lanza una excepción cuando el rol no existe
     */
    @Test
    void crearUsuario_conRolInexistente_debeLanzarExcepcion() {
        // Arrange
        Long roleIdInexistente = 999L;
        when(usuarioRepository.existsByUsername(anyString())).thenReturn(false);
        when(roleRepository.findById(roleIdInexistente)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.crearUsuario("nuevo", "password", "email@test.com", "123456789", roleIdInexistente);
        });
        
        assertTrue(exception.getMessage().contains("Rol no encontrado"));
        verify(roleRepository, times(1)).findById(roleIdInexistente);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    /**
     * Test: Actualizar usuario exitosamente
     * Verifica que el servicio actualiza correctamente los campos del usuario
     */
    @Test
    void actualizarUsuario_conDatosValidos_debeRetornarUsuarioActualizado() {
        // Arrange
        Long id = 1L;
        String nuevoUsername = "usuarioActualizado";
        String nuevoEmail = "actualizado@test.com";
        String nuevoPhone = "111222333";
        Long nuevoRoleId = 2L;
        
        Rol nuevoRol = new Rol();
        nuevoRol.setId(2L);
        nuevoRol.setNombre("Usuario");
        
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioTest));
        when(usuarioRepository.existsByUsername(nuevoUsername)).thenReturn(false);
        when(roleRepository.findById(nuevoRoleId)).thenReturn(Optional.of(nuevoRol));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);
        
        // Act
        Usuario resultado = usuarioService.actualizarUsuario(id, nuevoUsername, null, nuevoEmail, nuevoPhone, nuevoRoleId);
        
        // Assert
        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    /**
     * Test: Actualizar usuario inexistente
     * Verifica que el servicio lanza una excepción cuando el usuario no existe
     */
    @Test
    void actualizarUsuario_conIdInexistente_debeLanzarExcepcion() {
        // Arrange
        Long idInexistente = 999L;
        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.actualizarUsuario(idInexistente, "nuevo", null, "email@test.com", "123", 1L);
        });
        
        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
        verify(usuarioRepository, times(1)).findById(idInexistente);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    /**
     * Test: Eliminar usuario
     * Verifica que el servicio elimina correctamente un usuario
     */
    @Test
    void eliminarUsuario_conIdValido_debeEliminarUsuario() {
        // Arrange
        Long id = 1L;
        doNothing().when(usuarioRepository).deleteById(id);
        
        // Act
        usuarioService.eliminarUsuario(id);
        
        // Assert
        verify(usuarioRepository, times(1)).deleteById(id);
    }

    /**
     * Test: Buscar usuario por username existente
     * Verifica que el servicio encuentra un usuario por su username
     */
    @Test
    void buscarPorUsername_conUsernameExistente_debeRetornarUsuario() {
        // Arrange
        String username = "testuser";
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuarioTest));
        
        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorUsername(username);
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(username, resultado.get().getUsername());
        verify(usuarioRepository, times(1)).findByUsername(username);
    }

    /**
     * Test: Buscar usuario por username inexistente
     * Verifica que el servicio retorna Optional vacío cuando el username no existe
     */
    @Test
    void buscarPorUsername_conUsernameInexistente_debeRetornarOptionalVacio() {
        // Arrange
        String username = "noExiste";
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorUsername(username);
        
        // Assert
        assertFalse(resultado.isPresent());
        verify(usuarioRepository, times(1)).findByUsername(username);
    }

    /**
     * Test: Validar credenciales correctas
     * Verifica que el servicio valida correctamente credenciales válidas
     */
    @Test
    void validarCredenciales_conCredencialesCorrectas_debeRetornarTrue() {
        // Arrange
        String email = "test@test.com";
        String password = "password123";
        String encodedPassword = "encodedPassword";
        
        usuarioTest.setPassword(encodedPassword);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioTest));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        
        // Act
        boolean resultado = usuarioService.validarCredenciales(email, password);
        
        // Assert
        assertTrue(resultado);
        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
    }

    /**
     * Test: Validar credenciales incorrectas
     * Verifica que el servicio rechaza credenciales inválidas
     */
    @Test
    void validarCredenciales_conCredencialesIncorrectas_debeRetornarFalse() {
        // Arrange
        String email = "test@test.com";
        String password = "passwordIncorrecto";
        String encodedPassword = "encodedPassword";
        
        usuarioTest.setPassword(encodedPassword);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioTest));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);
        
        // Act
        boolean resultado = usuarioService.validarCredenciales(email, password);
        
        // Assert
        assertFalse(resultado);
        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
    }

    /**
     * Test: Validar credenciales con usuario inexistente
     * Verifica que el servicio retorna false cuando el usuario no existe
     */
    @Test
    void validarCredenciales_conUsuarioInexistente_debeRetornarFalse() {
        // Arrange
        String email = "noexiste@test.com";
        String password = "password123";
        
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        // Act
        boolean resultado = usuarioService.validarCredenciales(email, password);
        
        // Assert
        assertFalse(resultado);
        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    /**
     * Test: Obtener usuario por email existente
     * Verifica que el servicio retorna el usuario cuando existe por email
     */
    @Test
    void obtenerUsuarioPorEmail_conEmailExistente_debeRetornarUsuario() {
        // Arrange
        String email = "test@test.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioTest));
        
        // Act
        Usuario resultado = usuarioService.obtenerUsuarioPorEmail(email);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(email, resultado.getEmail());
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    /**
     * Test: Obtener usuario por email inexistente
     * Verifica que el servicio lanza una excepción cuando el usuario no existe
     */
    @Test
    void obtenerUsuarioPorEmail_conEmailInexistente_debeLanzarExcepcion() {
        // Arrange
        String email = "noexiste@test.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.obtenerUsuarioPorEmail(email);
        });
        
        assertTrue(exception.getMessage().contains("Usuario no encontrado con email"));
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    /**
     * Test: Registrar usuario público exitosamente
     * Verifica que el servicio registra un usuario público con rol Usuario por defecto
     */
    @Test
    void registrarUsuarioPublico_conDatosValidos_debeRetornarUsuarioCreado() {
        // Arrange
        String username = "nuevoUsuario";
        String password = "password123";
        String email = "nuevo@test.com";
        String phone = "987654321";
        
        Rol rolUsuario = new Rol();
        rolUsuario.setId(2L);
        rolUsuario.setNombre("Usuario");
        
        // Configurar mocks
        when(usuarioRepository.existsByEmail(email)).thenReturn(false);
        when(usuarioRepository.existsByPhone(phone)).thenReturn(false);
        List<Rol> roles = new ArrayList<>();
        roles.add(rolAdmin);
        roles.add(rolUsuario);
        when(roleRepository.findAll()).thenReturn(roles);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });
        
        // Act
        Usuario resultado = usuarioService.registrarUsuarioPublico(username, password, email, phone);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(username, resultado.getUsername());
        assertEquals(email, resultado.getEmail());
        assertEquals(phone, resultado.getPhone());
        assertEquals(rolUsuario, resultado.getRol());
        
        verify(usuarioRepository, times(1)).existsByEmail(email);
        verify(usuarioRepository, times(1)).existsByPhone(phone);
        verify(passwordEncoder, times(1)).encode(password);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    /**
     * Test: Registrar usuario público con email duplicado
     * Verifica que el servicio lanza una excepción cuando el email ya existe
     */
    @Test
    void registrarUsuarioPublico_conEmailDuplicado_debeLanzarExcepcion() {
        // Arrange
        String email = "existente@test.com";
        when(usuarioRepository.existsByEmail(email)).thenReturn(true);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuarioPublico("nuevo", "password", email, "123456789");
        });
        
        assertTrue(exception.getMessage().contains("El email ya está registrado"));
        verify(usuarioRepository, times(1)).existsByEmail(email);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    /**
     * Test: Registrar usuario público con teléfono duplicado
     * Verifica que el servicio lanza una excepción cuando el teléfono ya existe
     */
    @Test
    void registrarUsuarioPublico_conTelefonoDuplicado_debeLanzarExcepcion() {
        // Arrange
        String phone = "123456789";
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByPhone(phone)).thenReturn(true);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuarioPublico("nuevo", "password", "nuevo@test.com", phone);
        });
        
        assertTrue(exception.getMessage().contains("El teléfono ya está registrado"));
        verify(usuarioRepository, times(1)).existsByPhone(phone);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
