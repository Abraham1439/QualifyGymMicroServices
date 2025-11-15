# QualifyGym Estado Microservice

Microservicio de gestión de estados para la aplicación QualifyGym.

## Descripción

Este microservicio proporciona funcionalidades de gestión de estados, incluyendo:
- Creación, lectura, actualización y eliminación de estados
- Búsqueda de estados por nombre
- Verificación de existencia
- Función obtener o crear (evita duplicados)

## Configuración

### Base de Datos

El microservicio está configurado para usar MySQL. Asegúrate de tener:
- MySQL instalado y ejecutándose
- Una base de datos llamada `EstadosBD` creada
- Credenciales configuradas en `application.properties`

### Configuración del Puerto

Por defecto, el microservicio corre en el puerto **8084** para evitar conflictos con otros microservicios.

## Endpoints

### Públicos

Todos los endpoints son públicos por ahora. La autenticación se implementará más adelante.

- `GET /api/v1/estado/estados` - Listar todos los estados
- `GET /api/v1/estado/estados/{id}` - Obtener estado por ID
- `GET /api/v1/estado/estados/nombre/{nombre}` - Obtener estado por nombre
- `GET /api/v1/estado/estados/existe/{nombre}` - Verificar si existe
- `POST /api/v1/estado/estados` - Crear nuevo estado
- `POST /api/v1/estado/estados/obtener-o-crear` - Obtener o crear estado
- `PUT /api/v1/estado/estados/{id}` - Actualizar estado
- `DELETE /api/v1/estado/estados/{id}` - Eliminar estado

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/qualifygym/estados/
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
        └── com/qualifygym/estados/
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

