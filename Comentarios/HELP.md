# QualifyGym Comentario Microservice

Microservicio de gestión de comentarios para la aplicación QualifyGym.

## Descripción

Este microservicio proporciona funcionalidades de gestión de comentarios, incluyendo:
- Creación, lectura, actualización y eliminación de comentarios
- Gestión de comentarios por publicación
- Gestión de comentarios por usuario
- Sistema de moderación (ocultar/mostrar comentarios)
- Contadores de comentarios

## Configuración

### Base de Datos

El microservicio está configurado para usar MySQL. Asegúrate de tener:
- MySQL instalado y ejecutándose
- Una base de datos llamada `db_qualifygym_comentarios` creada
- Credenciales configuradas en `application.properties`

### Configuración del Puerto

Por defecto, el microservicio corre en el puerto **8082** para evitar conflictos con otros microservicios.

## Endpoints

### Públicos

Todos los endpoints son públicos por ahora. La autenticación se implementará más adelante.

- `GET /api/v1/comentario/comentarios` - Listar todos los comentarios
- `GET /api/v1/comentario/comentarios/{id}` - Obtener comentario por ID
- `GET /api/v1/comentario/comentarios/publicacion/{publicacionId}` - Obtener comentarios por publicación
- `GET /api/v1/comentario/comentarios/usuario/{usuarioId}` - Obtener comentarios por usuario
- `POST /api/v1/comentario/comentarios` - Crear nuevo comentario
- `PUT /api/v1/comentario/comentarios/{id}` - Actualizar comentario
- `PUT /api/v1/comentario/comentarios/{id}/ocultar` - Ocultar comentario
- `PUT /api/v1/comentario/comentarios/{id}/mostrar` - Mostrar comentario
- `DELETE /api/v1/comentario/comentarios/{id}` - Eliminar comentario

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/qualifygym/comentarios/
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
        └── com/qualifygym/comentarios/
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

