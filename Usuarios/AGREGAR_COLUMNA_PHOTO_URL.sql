-- Script para agregar la columna photo_url a la tabla usuarios
-- Este campo almacenará el ID de la imagen del microservicio de Imágenes

USE UsuariosBD;

-- Agregar columna photo_url si no existe
ALTER TABLE usuarios 
ADD COLUMN IF NOT EXISTS photo_url VARCHAR(255) NULL 
COMMENT 'ID de la imagen de perfil del microservicio de Imágenes';

-- Verificar que la columna se agregó correctamente
DESCRIBE usuarios;

