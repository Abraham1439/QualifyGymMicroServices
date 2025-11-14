# QualifyGym Publicacion Microservice

Microservicio de gestión de publicaciones para la aplicación QualifyGym.

## Características

- ✅ CRUD completo de publicaciones
- ✅ Gestión de publicaciones por tema
- ✅ Gestión de publicaciones por usuario
- ✅ Búsqueda de publicaciones por título o descripción
- ✅ Funcionalidad de ocultar/mostrar publicaciones
- ✅ Sistema de moderación (banear publicaciones con motivo)
- ✅ Gestión de imágenes de publicaciones
- ✅ Contador de publicaciones por tema y usuario
- ✅ API REST documentada con Swagger
- ✅ Filtrado de publicaciones ocultas

## Requisitos Previos

- Java 21 o superior
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Configuración

1. Crear la base de datos en MySQL:
```sql
CREATE DATABASE PublicacionesBD;
```

2. Configurar las credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=tu_password
```

3. El microservicio se ejecutará en el puerto **8083** por defecto.

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

### GET - Obtener publicaciones

- `GET /api/v1/publicacion/publicaciones` - Obtener todas las publicaciones
  - Parámetro opcional: `?incluirOcultas=true` para incluir publicaciones ocultas
- `GET /api/v1/publicacion/publicaciones/{id}` - Obtener publicación por ID
- `GET /api/v1/publicacion/publicaciones/tema/{temaId}` - Obtener publicaciones por tema
- `GET /api/v1/publicacion/publicaciones/usuario/{usuarioId}` - Obtener publicaciones por usuario
- `GET /api/v1/publicacion/publicaciones/buscar?query=texto` - Buscar publicaciones
- `GET /api/v1/publicacion/publicaciones/tema/{temaId}/count` - Contar publicaciones por tema
- `GET /api/v1/publicacion/publicaciones/usuario/{usuarioId}/count` - Contar publicaciones por usuario

### POST - Crear publicación

```bash
POST /api/v1/publicacion/publicaciones
Content-Type: application/json

{
  "titulo": "Título de la publicación",
  "descripcion": "Descripción completa de la publicación",
  "usuarioId": 1,
  "temaId": 1,
  "imageUrl": "url_de_la_imagen.jpg"  // Opcional
}
```

### PUT - Actualizar publicación

```bash
PUT /api/v1/publicacion/publicaciones/{id}
Content-Type: application/json

{
  "titulo": "Título actualizado",
  "descripcion": "Descripción actualizada"
}
```

### PUT - Actualizar imagen de publicación

```bash
PUT /api/v1/publicacion/publicaciones/{id}/imagen
Content-Type: application/json

{
  "imageUrl": "nueva_url_imagen.jpg"
}
```

### PUT - Ocultar publicación (moderación)

```bash
PUT /api/v1/publicacion/publicaciones/{id}/ocultar
Content-Type: application/json

{
  "motivoBaneo": "Contenido inapropiado"
}
```

### PUT - Mostrar publicación (desocultar)

```bash
PUT /api/v1/publicacion/publicaciones/{id}/mostrar
```

### DELETE - Eliminar publicación

```bash
DELETE /api/v1/publicacion/publicaciones/{id}
```

## Estructura del Modelo

El modelo `Publicacion` contiene:

- `idPublicacion` (Long) - ID único de la publicación
- `titulo` (String) - Título de la publicación
- `descripcion` (String) - Descripción completa
- `fecha` (Long) - Timestamp en milisegundos
- `oculta` (Boolean) - Indica si la publicación está oculta
- `fechaBaneo` (Long) - Timestamp del baneo (opcional)
- `motivoBaneo` (String) - Motivo del baneo (opcional)
- `usuarioId` (Long) - FK al usuario (microservicio de usuarios)
- `temaId` (Long) - FK al tema (microservicio de temas)
- `imageUrl` (String) - URL o path de la imagen (opcional)

## Estructura del Proyecto

```
Publicaciones/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/qualifygym/publicaciones/
│   │   │       ├── Application.java
│   │   │       ├── config/
│   │   │       │   └── SeguridadConfig.java
│   │   │       ├── controller/
│   │   │       │   └── PublicacionController.java
│   │   │       ├── model/
│   │   │       │   └── Publicacion.java
│   │   │       ├── repository/
│   │   │       │   └── PublicacionRepository.java
│   │   │       └── service/
│   │   │           └── PublicacionService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/qualifygym/publicaciones/
│               ├── ApplicationTests.java
│               ├── controller/
│               │   └── PublicacionControllerTest.java
│               └── service/
│                   └── PublicacionServiceTest.java
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
- **Microservicio de Comentarios** (puerto 8082) - Para relacionar comentarios con publicaciones
- **Microservicio de Temas** (futuro) - Para validar que los temas existan

**Nota**: La integración completa se implementará más adelante cuando se conecten los microservicios.

## Seguridad

Actualmente, todos los endpoints son públicos. La autenticación y autorización se implementarán cuando se integren los microservicios con el microservicio de usuarios.

## Documentación API

Una vez iniciado el microservicio:
- Swagger UI: `http://localhost:8083/swagger-ui.html`
- API Docs: `http://localhost:8083/v3/api-docs`

## Próximos Pasos

- [ ] Integración con microservicio de usuarios
- [ ] Integración con microservicio de temas
- [ ] Integración con microservicio de comentarios
- [ ] Implementar autenticación y autorización
- [ ] Agregar validación de existencia de usuario y tema antes de crear publicaciones

## Contribución

Este proyecto forma parte de QualifyGym - Grupo 13.

