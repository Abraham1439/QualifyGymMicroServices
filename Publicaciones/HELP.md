# QualifyGym Publicacion Microservice

Microservicio de gestión de publicaciones para la aplicación QualifyGym.

## Descripción

Este microservicio proporciona funcionalidades de gestión de publicaciones, incluyendo:
- Creación, lectura, actualización y eliminación de publicaciones
- Gestión de publicaciones por tema
- Gestión de publicaciones por usuario
- Búsqueda de publicaciones
- Sistema de moderación (ocultar/mostrar publicaciones)
- Gestión de imágenes
- Contadores de publicaciones

## Configuración

### Base de Datos

El microservicio está configurado para usar MySQL. Asegúrate de tener:
- MySQL instalado y ejecutándose
- Una base de datos llamada `PublicacionesBD` creada
- Credenciales configuradas en `application.properties`

### Configuración del Puerto

Por defecto, el microservicio corre en el puerto **8083** para evitar conflictos con otros microservicios.

## Endpoints

### Públicos

Todos los endpoints son públicos por ahora. La autenticación se implementará más adelante.

- `GET /api/v1/publicacion/publicaciones` - Listar todas las publicaciones
- `GET /api/v1/publicacion/publicaciones/{id}` - Obtener publicación por ID
- `GET /api/v1/publicacion/publicaciones/tema/{temaId}` - Obtener publicaciones por tema
- `GET /api/v1/publicacion/publicaciones/usuario/{usuarioId}` - Obtener publicaciones por usuario
- `GET /api/v1/publicacion/publicaciones/buscar?query=texto` - Buscar publicaciones
- `POST /api/v1/publicacion/publicaciones` - Crear nueva publicación
- `PUT /api/v1/publicacion/publicaciones/{id}` - Actualizar publicación
- `PUT /api/v1/publicacion/publicaciones/{id}/imagen` - Actualizar imagen
- `PUT /api/v1/publicacion/publicaciones/{id}/ocultar` - Ocultar publicación
- `PUT /api/v1/publicacion/publicaciones/{id}/mostrar` - Mostrar publicación
- `DELETE /api/v1/publicacion/publicaciones/{id}` - Eliminar publicación

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/qualifygym/publicaciones/
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
        └── com/qualifygym/publicaciones/
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
- Spring Data JPA
- Spring Security (configurado pero sin restricciones por ahora)
- MySQL
- Lombok
- JUnit 5
- Mockito

## Referencias

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Spring Security Documentation](https://spring.io/projects/spring-security)

