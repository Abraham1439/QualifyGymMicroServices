# QualifyGym Tema Microservice

Microservicio de gestión de temas para la aplicación QualifyGym.

## Características

- ✅ CRUD completo de temas
- ✅ Búsqueda de temas por nombre
- ✅ Gestión de temas por estado
- ✅ Validación de nombres únicos
- ✅ Verificación de existencia de temas
- ✅ API REST documentada con Swagger
- ✅ Datos iniciales (Rutinas de Fuerza, Cardio, Nutrición, etc.)

## Requisitos Previos

- Java 21 o superior
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Configuración

1. Crear la base de datos en MySQL:
```sql
CREATE DATABASE TemasBD;
```

2. Configurar las credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=tu_password
```

3. El microservicio se ejecutará en el puerto **8085** por defecto.

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

### GET - Obtener temas

- `GET /api/v1/tema/temas` - Obtener todos los temas
- `GET /api/v1/tema/temas/{id}` - Obtener tema por ID
- `GET /api/v1/tema/temas/estado/{estadoId}` - Obtener temas por estado
- `GET /api/v1/tema/temas/buscar?query=texto` - Buscar temas por nombre
- `GET /api/v1/tema/temas/nombre/{nombre}` - Obtener tema por nombre exacto
- `GET /api/v1/tema/temas/existe/{nombre}` - Verificar si existe un tema por nombre
- `GET /api/v1/tema/temas/estado/{estadoId}/count` - Contar temas por estado

### POST - Crear tema

```bash
POST /api/v1/tema/temas
Content-Type: application/json

{
  "nombreTema": "Nuevo Tema",
  "estadoId": 1
}
```

### PUT - Actualizar tema

```bash
PUT /api/v1/tema/temas/{id}
Content-Type: application/json

{
  "nombreTema": "Tema Actualizado",
  "estadoId": 1
}
```

### DELETE - Eliminar tema

```bash
DELETE /api/v1/tema/temas/{id}
```

## Estructura del Modelo

El modelo `Tema` contiene:

- `idTema` (Long) - ID único del tema
- `nombreTema` (String) - Nombre del tema (único, no puede repetirse)
- `estadoId` (Long) - FK al estado (microservicio de estados)

## Datos Iniciales

Al iniciar la aplicación por primera vez, se crean automáticamente:

- **Rutinas de Fuerza** (estadoId: 1)
- **Cardio y Resistencia** (estadoId: 1)
- **Nutrición** (estadoId: 1)
- **Suplementos** (estadoId: 1)
- **Recuperación** (estadoId: 1)
- **Motivación** (estadoId: 1)

**Nota**: Se asume que el estado con ID 1 (Activo) existe en el microservicio de estados.

## Estructura del Proyecto

```
tema/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/QualifyGym/tema/
│   │   │       ├── TemaApplication.java
│   │   │       ├── config/
│   │   │       │   ├── LoadDataBase.java
│   │   │       │   ├── OpenApliConfig.java
│   │   │       │   └── SeguridadConfig.java
│   │   │       ├── controller/
│   │   │       │   └── TemaController.java
│   │   │       ├── model/
│   │   │       │   └── Tema.java
│   │   │       ├── repository/
│   │   │       │   └── TemaRepository.java
│   │   │       ├── service/
│   │   │       │   └── TemaService.java
│   │   │       └── webpublicacion/
│   │   │           └── PublicacionCat.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/QualifyGym/tema/
│               ├── TemaApplicationTests.java
│               ├── controller/
│               │   └── TemaControllerTest.java
│               └── service/
│                   └── TemaServiceTest.java
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

- **Spring Boot** 3.5.7
- **Spring Data JPA** - Persistencia de datos
- **Spring Security** - Seguridad (configurado pero sin restricciones por ahora)
- **MySQL** - Base de datos
- **Lombok** - Reducción de código boilerplate
- **JUnit 5** - Testing
- **Mockito** - Mocking para tests
- **Swagger/OpenAPI** - Documentación de API
- **WebFlux** - Para comunicación con otros microservicios

## Integración con Otros Microservicios

Este microservicio está diseñado para trabajar junto con:

- **Microservicio de Estados** (puerto 8084) - Para validar que los estados existan
- **Microservicio de Publicaciones** (puerto 8083) - Para relacionar publicaciones con temas

**Nota**: La integración completa se implementará más adelante cuando se conecten los microservicios.

## Seguridad

Actualmente, todos los endpoints son públicos. La autenticación y autorización se implementarán cuando se integren los microservicios con el microservicio de usuarios.

## Documentación API

Una vez iniciado el microservicio:
- Swagger UI: `http://localhost:8085/swagger-ui.html`
- API Docs: `http://localhost:8085/v3/api-docs`

## Próximos Pasos

- [ ] Integración con microservicio de estados
- [ ] Integración con microservicio de publicaciones
- [ ] Implementar autenticación y autorización
- [ ] Agregar validación de existencia de estado antes de crear temas

## Contribución

Este proyecto forma parte de QualifyGym - Grupo 13.

