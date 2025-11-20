-- Script para crear la tabla de notificaciones si no existe
-- Ejecutar este script en la base de datos PublicacionesBD

USE PublicacionesBD;

-- Verificar si la tabla existe
SHOW TABLES LIKE 'notificaciones';

-- Crear la tabla de notificaciones
CREATE TABLE IF NOT EXISTS notificaciones (
    id_notificacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    publicacion_id BIGINT NOT NULL,
    mensaje TEXT NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_publicacion_id (publicacion_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Verificar que la tabla se creó correctamente
DESCRIBE notificaciones;

