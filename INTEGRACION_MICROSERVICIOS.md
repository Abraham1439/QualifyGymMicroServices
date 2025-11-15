# Integración de Microservicios - QualifyGym

Este documento describe cómo están conectados los microservicios de QualifyGym para comunicarse entre sí.

## 🔗 Arquitectura de Comunicación

Los microservicios se comunican mediante **HTTP REST** usando **WebClient** (Spring WebFlux) para realizar llamadas síncronas entre servicios.

## 📊 Diagrama de Dependencias

```
┌─────────────┐
│  Comentarios│
│  (Puerto    │
│   8082)     │
└──────┬──────┘
       │
       ├───► Usuarios (8081) - Validar usuario existe
       └───► Publicaciones (8083) - Validar publicación existe

┌─────────────┐
│ Publicaciones│
│  (Puerto    │
│   8083)     │
└──────┬──────┘
       │
       ├───► Usuarios (8081) - Validar usuario existe
       └───► Temas (8085) - Validar tema existe

┌─────────────┐
│    Temas    │
│  (Puerto    │
│   8085)     │
└──────┬──────┘
       │
       └───► Estados (8084) - Validar estado existe

┌─────────────┐
│   Estados   │
│  (Puerto    │
│   8084)     │
└─────────────┘
   (Sin dependencias)

┌─────────────┐
│   Usuarios  │
│  (Puerto    │
│   8081)     │
└─────────────┘
   (Sin dependencias)
```

## 🔌 Clientes de Comunicación

### 1. Comentarios → Usuarios y Publicaciones

**Archivo**: `Comentarios/src/main/java/com/qualifygym/comentarios/client/`

- **UsuarioClient.java**: Valida que un usuario existe antes de crear un comentario
- **PublicacionClient.java**: Valida que una publicación existe antes de crear un comentario

**Validaciones**:
- Al crear un comentario, se valida que el `usuarioId` existe en el microservicio de Usuarios
- Al crear un comentario, se valida que el `publicacionId` existe en el microservicio de Publicaciones

### 2. Publicaciones → Usuarios y Temas

**Archivo**: `Publicaciones/src/main/java/com/qualifygym/publicaciones/client/`

- **UsuarioClient.java**: Valida que un usuario existe antes de crear una publicación
- **TemaClient.java**: Valida que un tema existe antes de crear una publicación

**Validaciones**:
- Al crear una publicación, se valida que el `usuarioId` existe en el microservicio de Usuarios
- Al crear una publicación, se valida que el `temaId` existe en el microservicio de Temas

### 3. Temas → Estados

**Archivo**: `Tema/src/main/java/com/QualifyGym/tema/client/`

- **EstadoClient.java**: Valida que un estado existe antes de crear o actualizar un tema

**Validaciones**:
- Al crear un tema, se valida que el `estadoId` existe en el microservicio de Estados
- Al actualizar un tema, se valida que el nuevo `estadoId` existe (si se proporciona)

## ⚙️ Configuración

### URLs de Microservicios

Cada microservicio tiene configuradas las URLs de los otros microservicios en `application.properties`:

#### Comentarios (`application.properties`)
```properties
usuario-service.url=http://localhost:8081/api/v1/usuario
publicacion-service.url=http://localhost:8083/api/v1/publicacion
```

#### Publicaciones (`application.properties`)
```properties
usuario-service.url=http://localhost:8081/api/v1/usuario
tema-service.url=http://localhost:8085/api/v1/tema
```

#### Temas (`application.properties`)
```properties
estado-service.url=http://localhost:8084/api/v1/estado
publicacion-service.url=http://localhost:8083/api/v1/publicacion
```

## 🔐 Seguridad

### Endpoints Públicos para Comunicación entre Microservicios

Para permitir la comunicación entre microservicios, los siguientes endpoints son públicos:

#### Microservicio de Usuarios
- `GET /api/v1/usuario/users` - Listar usuarios
- `GET /api/v1/usuario/users/{id}` - Obtener usuario por ID (para validación)

#### Microservicio de Publicaciones
- `GET /api/v1/publicacion/publicaciones/{id}` - Obtener publicación por ID (para validación)

#### Microservicio de Temas
- `GET /api/v1/tema/temas/{id}` - Obtener tema por ID (para validación)

#### Microservicio de Estados
- `GET /api/v1/estado/estados/{id}` - Obtener estado por ID (para validación)

## 📝 Flujo de Validación

### Ejemplo: Crear un Comentario

1. **Cliente** envía POST a `/api/v1/comentario/comentarios` con:
   ```json
   {
     "comentario": "Excelente publicación",
     "usuarioId": 1,
     "publicacionId": 5
   }
   ```

2. **ComentarioService** recibe la solicitud

3. **ComentarioService** llama a `usuarioClient.existeUsuario(1)`
   - Realiza GET a `http://localhost:8081/api/v1/usuario/users/1`
   - Si el usuario existe → continúa
   - Si no existe → lanza excepción: "El usuario con ID 1 no existe"

4. **ComentarioService** llama a `publicacionClient.existePublicacion(5)`
   - Realiza GET a `http://localhost:8083/api/v1/publicacion/publicaciones/5`
   - Si la publicación existe → continúa
   - Si no existe → lanza excepción: "La publicación con ID 5 no existe"

5. Si ambas validaciones pasan, se crea el comentario en la base de datos

### Ejemplo: Crear una Publicación

1. **Cliente** envía POST a `/api/v1/publicacion/publicaciones` con:
   ```json
   {
     "titulo": "Nueva rutina",
     "descripcion": "Descripción de la rutina",
     "usuarioId": 1,
     "temaId": 2
   }
   ```

2. **PublicacionService** valida:
   - Usuario existe (llamada a Usuarios)
   - Tema existe (llamada a Temas)

3. Si ambas validaciones pasan, se crea la publicación

### Ejemplo: Crear un Tema

1. **Cliente** envía POST a `/api/v1/tema/temas` con:
   ```json
   {
     "nombreTema": "Yoga",
     "estadoId": 1
   }
   ```

2. **TemaService** valida:
   - Estado existe (llamada a Estados)

3. Si la validación pasa, se crea el tema

## ⚠️ Manejo de Errores

### Errores de Validación

Cuando un microservicio intenta validar una relación y falla, se lanza una `RuntimeException` con un mensaje descriptivo:

- `"El usuario con ID {id} no existe"`
- `"La publicación con ID {id} no existe"`
- `"El tema con ID {id} no existe"`
- `"El estado con ID {id} no existe"`

### Errores de Comunicación

Si un microservicio no está disponible o hay un error de red:

- Se lanza una `RuntimeException` con el mensaje: `"Error al verificar {entidad}: {mensaje}"`
- El cliente recibe un error 500 Internal Server Error

## 🚀 Orden de Inicio Recomendado

Para evitar errores de comunicación, inicia los microservicios en este orden:

1. **Estados** (puerto 8084) - No tiene dependencias
2. **Usuarios** (puerto 8081) - No tiene dependencias
3. **Temas** (puerto 8085) - Depende de Estados
4. **Publicaciones** (puerto 8083) - Depende de Usuarios y Temas
5. **Comentarios** (puerto 8082) - Depende de Usuarios y Publicaciones

## 📦 Dependencias Maven

Todos los microservicios ya tienen la dependencia de **WebFlux** en sus `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

## ✅ Ventajas de esta Arquitectura

1. **Validación de Integridad Referencial**: Los microservicios validan que las relaciones existan antes de crear registros
2. **Desacoplamiento**: Cada microservicio mantiene su propia base de datos
3. **Escalabilidad**: Cada microservicio puede escalarse independientemente
4. **Mantenibilidad**: Cambios en un microservicio no afectan directamente a otros

## 🔄 Próximas Mejoras

- [ ] Implementar circuit breakers (Resilience4j) para manejar fallos de comunicación
- [ ] Agregar timeouts configurables para las llamadas HTTP
- [ ] Implementar caché para reducir llamadas repetidas
- [ ] Agregar logging estructurado para rastrear llamadas entre microservicios
- [ ] Considerar usar un API Gateway para centralizar la comunicación

## 📚 Referencias

- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [WebClient Documentation](https://docs.spring.io/spring-framework/reference/web/webflux/webclient.html)

