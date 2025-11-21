# Microservicio de Imágenes - QualifyGym

Microservicio dedicado a la gestión de imágenes del sistema QualifyGym. Almacena fotos de perfil de usuarios y fotos de publicaciones en la base de datos usando tipo de dato LONGBLOB.

## Características

- ✅ Almacenamiento de imágenes en base de datos (LONGBLOB)
- ✅ Soporte para fotos de perfil de usuarios
- ✅ Soporte para fotos de publicaciones
- ✅ Validación de tamaño máximo (10MB por imagen)
- ✅ Validación de tipos MIME permitidos (JPEG, PNG, GIF, WEBP)
- ✅ Integración con microservicios de Usuarios y Publicaciones
- ✅ API REST completa con documentación Swagger

## Requisitos

- Java 21
- Maven 3.6+
- MySQL 8.0+
- Spring Boot 3.5.7

## Configuración

### 1. Base de Datos

Ejecutar el script SQL para crear la base de datos y tabla:

```sql
-- Ver archivo CREAR_TABLA_IMAGENES.sql
```

### 2. application.properties

Configurar las siguientes propiedades según tu entorno:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ImagenesBD
spring.datasource.username=root
spring.datasource.password=tu_password

# URLs de otros microservicios
usuario-service.url=http://localhost:8081/api/v1/usuario
publicacion-service.url=http://localhost:8083/api/v1/publicacion
```

### 3. Puerto

El microservicio corre en el puerto **8086** por defecto.

## Endpoints Principales

### Fotos de Perfil

- `POST /api/v1/imagen/perfil/{usuarioId}` - Subir foto de perfil
- `GET /api/v1/imagen/perfil/{usuarioId}` - Obtener foto de perfil
- `DELETE /api/v1/imagen/perfil/{usuarioId}` - Eliminar foto de perfil

### Fotos de Publicaciones

- `POST /api/v1/imagen/publicacion/{publicacionId}` - Subir foto de publicación
- `GET /api/v1/imagen/publicacion/{publicacionId}` - Obtener imágenes de publicación
- `GET /api/v1/imagen/{idImagen}` - Obtener imagen por ID

### Utilidades

- `GET /api/v1/imagen/usuario/{usuarioId}/count` - Contar imágenes por usuario
- `GET /api/v1/imagen/publicacion/{publicacionId}/count` - Contar imágenes por publicación
- `DELETE /api/v1/imagen/{idImagen}` - Eliminar imagen

## Documentación API

Una vez iniciado el microservicio, acceder a Swagger UI en:

```
http://localhost:8086/swagger-ui.html
```

## Validaciones

- **Tamaño máximo**: 10MB por imagen
- **Tipos permitidos**: JPEG, JPG, PNG, GIF, WEBP
- **Validación de usuarios**: Verifica que el usuario existe antes de guardar foto de perfil
- **Validación de publicaciones**: Verifica que la publicación existe antes de guardar foto

## Estructura del Proyecto

```
Imagenes/
├── src/
│   ├── main/
│   │   ├── java/com/qualifygym/imagenes/
│   │   │   ├── ImagenesApplication.java
│   │   │   ├── model/
│   │   │   │   └── Imagen.java
│   │   │   ├── repository/
│   │   │   │   └── ImagenRepository.java
│   │   │   ├── service/
│   │   │   │   └── ImagenService.java
│   │   │   ├── controller/
│   │   │   │   └── ImagenController.java
│   │   │   ├── client/
│   │   │   │   ├── UsuarioClient.java
│   │   │   │   └── PublicacionClient.java
│   │   │   └── config/
│   │   │       ├── SeguridadConfig.java
│   │   │       └── OpenAPIConfig.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── CREAR_TABLA_IMAGENES.sql
└── README.md
```

## Ejecución

```bash
mvn clean install
mvn spring-boot:run
```

## Notas Importantes

- Las imágenes se almacenan como LONGBLOB en MySQL
- El límite de tamaño es de 10MB por imagen
- Si un usuario sube una nueva foto de perfil, la anterior se reemplaza automáticamente
- Las imágenes se pueden obtener directamente mediante su ID o por usuario/publicación

