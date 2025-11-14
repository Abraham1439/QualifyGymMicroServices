# QualifyGym Comentario Microservice

Microservicio de gestión de comentarios para la aplicación QualifyGym.

## Características

- ✅ CRUD completo de comentarios
- ✅ Gestión de comentarios por publicación
- ✅ Gestión de comentarios por usuario
- ✅ Funcionalidad de ocultar/mostrar comentarios
- ✅ Sistema de moderación (banear comentarios con motivo)
- ✅ Contador de comentarios por publicación y usuario
- ✅ API REST documentada con Swagger
- ✅ Filtrado de comentarios ocultos

## Requisitos Previos

- Java 21 o superior
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Configuración

1. Crear la base de datos en MySQL:
```sql
CREATE DATABASE db_qualifygym_comentarios;
```

2. Configurar las credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=tu_password
```

3. El microservicio se ejecutará en el puerto **8082** por defecto.

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

### GET - Obtener comentarios

- `GET /api/v1/comentario/comentarios` - Obtener todos los comentarios
- `GET /api/v1/comentario/comentarios/{id}` - Obtener comentario por ID
- `GET /api/v1/comentario/comentarios/publicacion/{publicacionId}` - Obtener comentarios por publicación
  - Parámetro opcional: `?incluirOcultos=true` para incluir comentarios ocultos
- `GET /api/v1/comentario/comentarios/usuario/{usuarioId}` - Obtener comentarios por usuario
- `GET /api/v1/comentario/comentarios/publicacion/{publicacionId}/count` - Contar comentarios por publicación
- `GET /api/v1/comentario/comentarios/usuario/{usuarioId}/count` - Contar comentarios por usuario

### POST - Crear comentario

```bash
POST /api/v1/comentario/comentarios
Content-Type: application/json

{
  "comentario": "Este es un comentario de prueba",
  "usuarioId": 1,
  "publicacionId": 1
}
```

### PUT - Actualizar comentario

```bash
PUT /api/v1/comentario/comentarios/{id}
Content-Type: application/json

{
  "comentario": "Comentario actualizado"
}
```

### PUT - Ocultar comentario (moderación)

```bash
PUT /api/v1/comentario/comentarios/{id}/ocultar
Content-Type: application/json

{
  "motivoBaneo": "Contenido inapropiado"
}
```

### PUT - Mostrar comentario (desocultar)

```bash
PUT /api/v1/comentario/comentarios/{id}/mostrar
```

### DELETE - Eliminar comentario

```bash
DELETE /api/v1/comentario/comentarios/{id}
```

## Estructura del Modelo

El modelo `Comentario` contiene:

- `idComentario` (Long) - ID único del comentario
- `comentario` (String) - Texto del comentario
- `fechaRegistro` (Long) - Timestamp en milisegundos
- `oculto` (Boolean) - Indica si el comentario está oculto
- `fechaBaneo` (Long) - Timestamp del baneo (opcional)
- `motivoBaneo` (String) - Motivo del baneo (opcional)
- `usuarioId` (Long) - FK al usuario (microservicio de usuarios)
- `publicacionId` (Long) - FK a la publicación (microservicio de publicaciones)

## Estructura del Proyecto

```
Comentarios/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/qualifygym/comentarios/
│   │   │       ├── Application.java
│   │   │       ├── config/
│   │   │       │   └── SeguridadConfig.java
│   │   │       ├── controller/
│   │   │       │   └── ComentarioController.java
│   │   │       ├── model/
│   │   │       │   └── Comentario.java
│   │   │       ├── repository/
│   │   │       │   └── ComentarioRepository.java
│   │   │       └── service/
│   │   │           └── ComentarioService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/qualifygym/comentarios/
│               ├── ApplicationTests.java
│               ├── controller/
│               │   └── ComentarioControllerTest.java
│               └── service/
│                   └── ComentarioServiceTest.java
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
- **Spring Data JPA** - Persistencia de datos
- **MySQL** - Base de datos
- **Lombok** - Reducción de código boilerplate
- **JUnit 5** - Testing
- **Mockito** - Mocking para tests
- **Swagger/OpenAPI** - Documentación de API

## Integración con Otros Microservicios

Este microservicio está diseñado para trabajar junto con:

- **Microservicio de Usuarios** (puerto 8081) - Para validar que los usuarios existan
- **Microservicio de Publicaciones** (puerto futuro) - Para validar que las publicaciones existan

**Nota**: La integración completa se implementará más adelante cuando se conecten los microservicios.

## Seguridad

Actualmente, todos los endpoints son públicos. La autenticación y autorización se implementarán cuando se integren los microservicios con el microservicio de usuarios.

## Documentación API

Una vez iniciado el microservicio:
- Swagger UI: `http://localhost:8082/swagger-ui.html`
- API Docs: `http://localhost:8082/v3/api-docs`

## Próximos Pasos

- [ ] Integración con microservicio de usuarios
- [ ] Integración con microservicio de publicaciones
- [ ] Implementar autenticación y autorización
- [ ] Agregar validación de existencia de usuario y publicación antes de crear comentarios

## Contribución

Este proyecto forma parte de QualifyGym - Grupo 13.

