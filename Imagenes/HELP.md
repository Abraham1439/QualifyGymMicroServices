# Getting Started

## Configuración Inicial

1. **Crear la base de datos:**
   - Ejecutar el script `CREAR_TABLA_IMAGENES.sql` en MySQL
   - O crear manualmente la base de datos `ImagenesBD`

2. **Configurar application.properties:**
   - Ajustar la URL de la base de datos si es necesario
   - Configurar las URLs de los otros microservicios

3. **Compilar y ejecutar:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Verificar:**
   - El microservicio debería iniciar en el puerto 8086
   - Acceder a Swagger: http://localhost:8086/swagger-ui.html

## Próximos Pasos

- Integrar con la aplicación Android
- Configurar los endpoints en la app móvil
- Probar subida de imágenes desde la app

