/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.model;

import com.cine.magenta.config.DatabaseConfig;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Modelo de datos para la entidad Movie del sistema de cartelera.
 * Implementa validaciones de negocio y metodos de utilidad.
 * Representa una pelicula en el sistema de gestion de cartelera del Cine Magenta.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class Movie {
    
    /** Identificador unico de la pelicula */
    private int id;
    
    /** Titulo de la pelicula */
    private String title;
    
    /** Director de la pelicula */
    private String director;
    
    /** Año de estreno */
    private int year;
    
    /** Duracion en minutos */
    private int duration;
    
    /** Genero cinematografico */
    private String genre;
    
    /** Fecha de creacion del registro */
    private LocalDateTime createdAt;
    
    /** Fecha de ultima actualizacion */
    private LocalDateTime updatedAt;
    
    /**
     * Constructor por defecto.
     * Inicializa las fechas con el momento actual.
     */
    public Movie() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor para nueva pelicula sin ID.
     * Utilizado para crear registros nuevos en la base de datos.
     * 
     * @param title titulo de la pelicula
     * @param director director de la pelicula  
     * @param year año de estreno
     * @param duration duracion en minutos
     * @param genre genero cinematografico
     */
    public Movie(String title, String director, int year, int duration, String genre) {
        this();
        this.title = title != null ? title.trim() : null;
        this.director = director != null ? director.trim() : null;
        this.year = year;
        this.duration = duration;
        this.genre = genre != null ? genre.trim() : null;
    }
    
    /**
     * Constructor completo para peliculas existentes.
     * Utilizado para instanciar objetos desde registros de base de datos.
     * 
     * @param id identificador unico
     * @param title titulo de la pelicula
     * @param director director de la pelicula
     * @param year año de estreno
     * @param duration duracion en minutos
     * @param genre genero cinematografico
     */
    public Movie(int id, String title, String director, int year, int duration, String genre) {
        this(title, director, year, duration, genre);
        this.id = id;
    }
    
    // Getters y Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title != null ? title.trim() : null;
        updateTimestamp();
    }
    
    public String getDirector() {
        return director;
    }
    
    public void setDirector(String director) {
        this.director = director != null ? director.trim() : null;
        updateTimestamp();
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
        updateTimestamp();
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
        updateTimestamp();
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre != null ? genre.trim() : null;
        updateTimestamp();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Actualiza la marca de tiempo de modificacion.
     * Metodo privado llamado automaticamente al modificar datos.
     */
    private void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Valida si todos los datos de la pelicula son correctos.
     * Aplica las reglas de negocio definidas en DatabaseConfig.
     * 
     * @return ValidationResult con el resultado de la validacion
     */
    public ValidationResult validate() {
        ValidationResult result = new ValidationResult();
        
        // Validar titulo
        if (title == null || title.trim().isEmpty()) {
            result.addError("El titulo es obligatorio");
        } else if (title.length() > DatabaseConfig.MAX_TITLE_LENGTH) {
            result.addError("El titulo no puede exceder " + DatabaseConfig.MAX_TITLE_LENGTH + " caracteres");
        }
        
        // Validar director
        if (director == null || director.trim().isEmpty()) {
            result.addError("El director es obligatorio");
        } else if (director.length() > DatabaseConfig.MAX_DIRECTOR_LENGTH) {
            result.addError("El nombre del director no puede exceder " + DatabaseConfig.MAX_DIRECTOR_LENGTH + " caracteres");
        }
        
        // Validar año
        if (year < DatabaseConfig.MIN_YEAR || year > DatabaseConfig.MAX_YEAR) {
            result.addError("El año debe estar entre " + DatabaseConfig.MIN_YEAR + " y " + DatabaseConfig.MAX_YEAR);
        }
        
        // Validar duracion
        if (duration < DatabaseConfig.MIN_DURATION || duration > DatabaseConfig.MAX_DURATION) {
            result.addError("La duracion debe estar entre " + DatabaseConfig.MIN_DURATION + " y " + DatabaseConfig.MAX_DURATION + " minutos");
        }
        
        // Validar genero
        if (genre == null || genre.trim().isEmpty()) {
            result.addError("El genero es obligatorio");
        } else if (!isValidGenre(genre)) {
            result.addError("Genero no valido. Generos disponibles: " + Arrays.toString(DatabaseConfig.AVAILABLE_GENRES));
        }
        
        return result;
    }
    
    /**
     * Verifica si el genero es valido segun la configuracion.
     * 
     * @param genre genero a validar
     * @return true si el genero es valido
     */
    private boolean isValidGenre(String genre) {
        return Arrays.asList(DatabaseConfig.AVAILABLE_GENRES).contains(genre);
    }
    
    /**
     * Verifica si la pelicula es valida para operaciones basicas.
     * Metodo de conveniencia para validacion rapida.
     * 
     * @return true si los datos basicos son validos
     */
    public boolean isValid() {
        return validate().isValid();
    }
    
    /**
     * Convierte la duracion a formato legible horas:minutos.
     * 
     * @return String con formato "Xh Ym" o variaciones
     */
    public String getFormattedDuration() {
        if (duration <= 0) return "No especificada";
        
        int hours = duration / 60;
        int minutes = duration % 60;
        
        if (hours > 0 && minutes > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (hours > 0) {
            return String.format("%dh", hours);
        } else {
            return String.format("%dm", minutes);
        }
    }
    
    /**
     * Obtiene el año como cadena de texto para visualizacion.
     * 
     * @return año formateado como String
     */
    public String getYearAsString() {
        return String.valueOf(year);
    }
    
    /**
     * Metodo util para mostrar cadenas nulas de forma segura.
     * Devuelve texto por defecto cuando el valor es null.
     * 
     * @param value valor a verificar
     * @return valor original o texto por defecto
     */
    private String safeDisplay(String value) {
        return value == null ? "(vacio)" : value;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Movie{id=%d, title='%s', director='%s', year=%d, duration=%s, genre='%s'}", 
            id, safeDisplay(title), safeDisplay(director), year, getFormattedDuration(), safeDisplay(genre)
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Movie movie = (Movie) obj;
        return id == movie.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    /**
     * Clase interna para manejar resultados de validacion.
     * Proporciona una forma estructurada de reportar errores de validacion.
     */
    public static class ValidationResult {
        
        /** StringBuilder para acumular mensajes de error */
        private StringBuilder errors = new StringBuilder();
        
        /** Indica si la validacion fue exitosa */
        private boolean valid = true;
        
        /**
         * Agrega un mensaje de error al resultado.
         * 
         * @param error mensaje de error a agregar
         */
        public void addError(String error) {
            if (errors.length() > 0) {
                errors.append("\n");
            }
            errors.append("• ").append(error);
            valid = false;
        }
        
        /**
         * Verifica si la validacion fue exitosa.
         * 
         * @return true si no hay errores
         */
        public boolean isValid() {
            return valid;
        }
        
        /**
         * Obtiene todos los mensajes de error concatenados.
         * 
         * @return String con todos los errores
         */
        public String getErrorMessages() {
            return errors.toString();
        }
        
        /**
         * Verifica si existen errores de validacion.
         * 
         * @return true si hay errores
         */
        public boolean hasErrors() {
            return !valid;
        }
    }
}