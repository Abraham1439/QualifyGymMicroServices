# QualifyGym Estado Microservice

Microservicio de gestión de estados para la aplicación QualifyGym.

## Características

- ✅ CRUD completo de estados
- ✅ Búsqueda de estados por nombre
- ✅ Validación de nombres únicos
- ✅ Función obtener o crear (útil para evitar duplicados)
- ✅ Verificación de existencia de estados
- ✅ API REST documentada con Swagger
- ✅ Datos iniciales (Activo, Inactivo, Pendiente, Eliminado)

## Requisitos Previos

- Java 21 o superior
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Configuración

1. Crear la base de datos en MySQL:
```sql
CREATE DATABASE EstadosBD;
```

2. Configurar las credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=tu_password
```

3. El microservicio se ejecutará en el puerto **8084** por defecto.

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

### GET - Obtener estados

- `GET /api/v1/estado/estados` - Obtener todos los estados
- `GET /api/v1/estado/estados/{id}` - Obtener estado por ID
- `GET /api/v1/estado/estados/nombre/{nombre}` - Obtener estado por nombre
- `GET /api/v1/estado/estados/existe/{nombre}` - Verificar si existe un estado por nombre

### POST - Crear estado

```bash
POST /api/v1/estado/estados
Content-Type: application/json

{
  "nombre": "Nuevo Estado"
}
```

### POST - Obtener o crear estado

```bash
POST /api/v1/estado/estados/obtener-o-crear
Content-Type: application/json

{
  "nombre": "Activo"
}
```

Si el estado existe, lo retorna. Si no existe, lo crea y lo retorna.

### PUT - Actualizar estado

```bash
PUT /api/v1/estado/estados/{id}
Content-Type: application/json

{
  "nombre": "Estado Actualizado"
}
```

### DELETE - Eliminar estado

```bash
DELETE /api/v1/estado/estados/{id}
```

## Estructura del Modelo

El modelo `Estado` contiene:

- `idEstado` (Long) - ID único del estado
- `nombre` (String) - Nombre del estado (único, no puede repetirse)

## Datos Iniciales

Al iniciar la aplicación por primera vez, se crean automáticamente:

- **Activo**
- **Inactivo**
- **Pendiente**
- **Eliminado**

## Estructura del Proyecto

```
Estados/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/qualifygym/estados/
│   │   │       ├── Application.java
│   │   │       ├── config/
│   │   │       │   ├── LoadDatabase.java
│   │   │       │   └── SeguridadConfig.java
│   │   │       ├── controller/
│   │   │       │   └── EstadoController.java
│   │   │       ├── model/
│   │   │       │   └── Estado.java
│   │   │       ├── repository/
│   │   │       │   └── EstadoRepository.java
│   │   │       └── service/
│   │   │           └── EstadoService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/qualifygym/estados/
│               ├── ApplicationTests.java
│               ├── controller/
│               │   └── EstadoControllerTest.java
│               └── service/
│                   └── EstadoServiceTest.java
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

- **Microservicio de Temas** (futuro) - Para relacionar temas con estados
- Otros microservicios que necesiten estados (como "Activo", "Inactivo", etc.)

**Nota**: La integración completa se implementará más adelante cuando se conecten los microservicios.

## Seguridad

Actualmente, todos los endpoints son públicos. La autenticación y autorización se implementarán cuando se integren los microservicios con el microservicio de usuarios.

## Documentación API

Una vez iniciado el microservicio:
- Swagger UI: `http://localhost:8084/swagger-ui.html`
- API Docs: `http://localhost:8084/v3/api-docs`

## Próximos Pasos

- [ ] Integración con microservicio de temas
- [ ] Implementar autenticación y autorización
- [ ] Agregar más estados iniciales según necesidad

## Contribución

Este proyecto forma parte de QualifyGym - Grupo 13.

