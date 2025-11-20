# QualifyGym Usuario Microservice

Microservicio de gestión de usuarios para la aplicación QualifyGym.

## Características

- ✅ Registro y autenticación de usuarios
- ✅ Gestión de roles (Administrador, Usuario, Moderador)
- ✅ CRUD completo de usuarios
- ✅ Validación de credenciales con Spring Security
- ✅ Encriptación de contraseñas con BCrypt
- ✅ API REST documentada con Swagger
- ✅ Validación de email único

## Requisitos Previos

- Java 21 o superior
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Configuración

1. Crear la base de datos en MySQL:
```sql
CREATE DATABASE db_qualifygym_usuarios;
```

2. Configurar las credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=tu_password
```

3. El microservicio se ejecutará en el puerto **8081** por defecto.

## Instalación y Ejecución

### Usando Maven Wrapper (recomendado)

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Usando Maven instalado

```bash
mvn spring-boot:run
```

## Endpoints Principales

### Públicos

- `POST /api/v1/usuario/login` - Iniciar sesión
- `GET /api/v1/usuario/users` - Listar todos los usuarios

### Protegidos (requieren autenticación)

- `GET /api/v1/usuario/users/{id}` - Obtener usuario por ID
- `POST /api/v1/usuario/users` - Crear nuevo usuario
- `PUT /api/v1/usuario/users/{id}` - Actualizar usuario
- `DELETE /api/v1/usuario/users/{id}` - Eliminar usuario

### Ejemplo de creación de usuario

```bash
POST /api/v1/usuario/users
Content-Type: application/json

{
  "username": "nuevo_usuario",
  "password": "password123",
  "email": "usuario@qualifygym.com",
  "rolId": 2
}
```

### Ejemplo de login

```bash
POST /api/v1/usuario/login
Content-Type: application/json

{
  "email": "admin@qualifygym.com",
  "password": "admin123"
}
```

## Documentación API

Una vez iniciado el microservicio:
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- API Docs: `http://localhost:8081/v3/api-docs`

**Nota**: Swagger solo es accesible para usuarios con rol "Administrador".

## Datos Iniciales

Al iniciar la aplicación por primera vez, se crean automáticamente:

### Roles
- **Administrador** (ID: 1)
- **Usuario** (ID: 2)
- **Moderador** (ID: 3)

### Usuarios de Prueba
- **Admin**: username: `admin`, password: `admin123`, email: `admin@qualifygym.com`
- **Usuario**: username: `usuario1`, password: `usuario123`, email: `usuario1@qualifygym.com`
- **Moderador**: username: `moderador`, password: `moderador123`, email: `moderador@qualifygym.com`

## Estructura del Proyecto

```
QualifyGym-Usuario-Microservice/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/qualifygym/usuarios/
│   │   │       ├── Application.java
│   │   │       ├── config/
│   │   │       │   ├── CustomUserDetailsService.java
│   │   │       │   ├── LoadDatabase.java
│   │   │       │   └── SeguridadConfig.java
│   │   │       ├── controller/
│   │   │       │   └── UsuarioController.java
│   │   │       ├── model/
│   │   │       │   ├── Rol.java
│   │   │       │   └── Usuario.java
│   │   │       ├── repository/
│   │   │       │   ├── RoleRepository.java
│   │   │       │   └── UsuarioRepository.java
│   │   │       └── service/
│   │   │           └── UsuarioService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/qualifygym/usuarios/
│               ├── ApplicationTests.java
│               ├── controller/
│               │   └── UsuarioControllerTest.java
│               └── service/
│                   └── UsuarioServiceTest.java
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Testing

```bash
mvn test
```

## Tecnologías

- **Spring Boot** 3.5.2
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **MySQL** - Base de datos
- **Lombok** - Reducción de código boilerplate
- **JUnit 5** - Testing
- **Mockito** - Mocking para tests
- **Swagger/OpenAPI** - Documentación de API

## Próximos Microservicios

Este es el primer microservicio de QualifyGym. Los siguientes serán:
- 📝 Microservicio de Publicaciones
- 💬 Microservicio de Comentarios
- 📊 Microservicio de Estados

## Contribución

Este proyecto forma parte de QualifyGym - Grupo 13.

