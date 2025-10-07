-- =============================================
-- SCRIPT: Crear Base de Datos para Cine Magenta
-- Objetivo: Sistema compatible con Java
-- =============================================

-- Paso 1: Crear la base de datos
CREATE DATABASE Cine_DB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Paso 2: Usar la base de datos
USE Cine_DB;

-- Paso 3: Crear tabla Cartelera con nombres en español (sin tildes)
CREATE TABLE Cartelera (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    director VARCHAR(50) NOT NULL,
    ano INT NOT NULL,
    duracion INT NOT NULL,
    genero VARCHAR(50) NOT NULL,
    
    -- Restricciones
    CONSTRAINT chk_titulo CHECK (CHAR_LENGTH(TRIM(titulo)) BETWEEN 1 AND 100),
    CONSTRAINT chk_director CHECK (CHAR_LENGTH(TRIM(director)) BETWEEN 1 AND 100),
    CONSTRAINT chk_ano CHECK (ano BETWEEN 1900 AND 2100),
    CONSTRAINT chk_duracion CHECK (duracion BETWEEN 1 AND 300)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Paso 4: Insertar datos de prueba
INSERT INTO Cartelera (titulo, director, ano, duracion, genero) VALUES
('El Padrino', 'Francis Ford Coppola', 1972, 175, 'Drama'),
('Toy Story', 'John Lasseter', 1995, 81, 'Animación'),
('Interestelar', 'Christopher Nolan', 2014, 169, 'Ciencia Ficción'),
('El Rey León', 'Roger Allers', 1994, 88, 'Animación'),
('El Caballero Oscuro', 'Christopher Nolan', 2008, 152, 'Acción');

-- Paso 5: Verificar que todo se creó correctamente
SELECT 
    'Base de datos Cine_DB y tabla Cartelera creadas correctamente.' AS mensaje,
    COUNT(*) AS total_peliculas_cargadas
FROM Cartelera;

-- Paso 6: Mostrar todas las películas
SELECT * FROM Cartelera;