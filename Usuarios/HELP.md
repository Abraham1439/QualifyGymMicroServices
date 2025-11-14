# QualifyGym Usuario Microservice

Microservicio de gestión de usuarios para la aplicación QualifyGym.

## Descripción

Este microservicio proporciona funcionalidades de gestión de usuarios, incluyendo:
- Registro y autenticación de usuarios
- Gestión de roles (Administrador, Usuario)
- CRUD completo de usuarios
- Validación de credenciales

## Configuración

### Base de Datos

El microservicio está configurado para usar MySQL. Asegúrate de tener:
- MySQL instalado y ejecutándose
- Una base de datos llamada `db_qualifygym_usuarios` creada
- Credenciales configuradas en `application.properties`

### Configuración del Puerto

Por defecto, el microservicio corre en el puerto **8081** para evitar conflictos con otros microservicios.

## Endpoints

### Públicos

- `POST /api/v1/usuario/login` - Iniciar sesión
- `GET /api/v1/usuario/users` - Listar todos los usuarios

### Protegidos (requieren autenticación)

- `GET /api/v1/usuario/users/{id}` - Obtener usuario por ID
- `POST /api/v1/usuario/users` - Crear nuevo usuario
- `PUT /api/v1/usuario/users/{id}` - Actualizar usuario
- `DELETE /api/v1/usuario/users/{id}` - Eliminar usuario

### Swagger

- Swagger UI disponible en: `http://localhost:8081/swagger-ui.html`
- Documentación API: `http://localhost:8081/v3/api-docs`

**Nota**: Swagger solo es accesible para usuarios con rol "Administrador".

## Datos Iniciales

Al iniciar la aplicación, se crean automáticamente:
- **Roles**: Administrador, Usuario
- **Usuario admin**: username: `admin`, password: `admin123`, email: `admin@qualifygym.com`
- **Usuario de prueba**: username: `usuario1`, password: `usuario123`, email: `usuario1@qualifygym.com`

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/qualifygym/usuarios/
│   │       ├── Application.java
│   │       ├── config/
│   │       ├── controller/
│   │       ├── model/
│   │       ├── repository/
│   │       └── service/
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/qualifygym/usuarios/
```

## Ejecución

```bash
mvn spring-boot:run
```

## Testing

```bash
mvn test
```

## Tecnologías Utilizadas

- Spring Boot 3.5.2
- Spring Security
- Spring Data JPA
- MySQL
- Lombok
- JUnit 5
- Mockito

## Referencias

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)

