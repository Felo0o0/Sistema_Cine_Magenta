/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.model;

import com.cine.magenta.config.DatabaseConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase Movie")
public class MovieTest {
    
    private Movie validMovie;
    
    @BeforeEach
    void setUp() {
        // Crear una película válida para usar en las pruebas
        validMovie = new Movie("Título de Prueba", "Director de Prueba", 2020, 120, "Drama");
    }
    
    @Test
    @DisplayName("Debe crear una película válida")
    void testCreateValidMovie() {
        // Verificar que la película se creó correctamente
        assertNotNull(validMovie);
        assertEquals("Título de Prueba", validMovie.getTitle());
        assertEquals("Director de Prueba", validMovie.getDirector());
        assertEquals(2020, validMovie.getYear());
        assertEquals(120, validMovie.getDuration());
        assertEquals("Drama", validMovie.getGenre());
        assertNotNull(validMovie.getCreatedAt());
        assertNotNull(validMovie.getUpdatedAt());
        assertTrue(validMovie.isValid());
    }
    
    @Test
    @DisplayName("Debe actualizar timestamp al modificar datos")
    void testUpdateTimestamp() {
        LocalDateTime originalTimestamp = validMovie.getUpdatedAt();
        
        // Esperar un momento para asegurar que el timestamp sea diferente
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Modificar un dato
        validMovie.setTitle("Nuevo Título");
        
        // Verificar que el timestamp se actualizó
        assertNotEquals(originalTimestamp, validMovie.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Debe validar correctamente una película válida")
    void testValidateValidMovie() {
        Movie.ValidationResult result = validMovie.validate();
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
        assertEquals("", result.getErrorMessages());
    }
    
    @Test
    @DisplayName("Debe detectar título inválido")
    void testValidateInvalidTitle() {
        // Título nulo
        validMovie.setTitle(null);
        Movie.ValidationResult result = validMovie.validate();
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertTrue(result.getErrorMessages().contains("titulo es obligatorio"));
        
        // Título vacío
        validMovie.setTitle("  ");
        result = validMovie.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessages().contains("titulo es obligatorio"));
        
        // Título demasiado largo
        StringBuilder longTitle = new StringBuilder();
        for (int i = 0; i < DatabaseConfig.MAX_TITLE_LENGTH + 10; i++) {
            longTitle.append("a");
        }
        validMovie.setTitle(longTitle.toString());
        result = validMovie.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessages().contains("titulo no puede exceder"));
    }
    
    @Test
    @DisplayName("Debe detectar director inválido")
    void testValidateInvalidDirector() {
        // Director nulo
        validMovie.setDirector(null);
        Movie.ValidationResult result = validMovie.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessages().contains("director es obligatorio"));
        
        // Director vacío
        validMovie.setDirector("  ");
        result = validMovie.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessages().contains("director es obligatorio"));
        
        // Director demasiado largo
        StringBuilder longDirector = new StringBuilder();
        for (int i = 0; i < DatabaseConfig.MAX_DIRECTOR_LENGTH + 10; i++) {
            longDirector.append("a");
        }
        validMovie.setDirector(longDirector.toString());
        result = validMovie.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessages().contains("director no puede exceder"));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {DatabaseConfig.MIN_YEAR - 1, DatabaseConfig.MAX_YEAR + 1})
    @DisplayName("Debe detectar año inválido")
    void testValidateInvalidYear(int invalidYear) {
        validMovie.setYear(invalidYear);
        Movie.ValidationResult result = validMovie.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessages().contains("año debe estar entre"));
    }
    
    @ParameterizedTest
    @ValueSource(ints = {DatabaseConfig.MIN_DURATION - 1, DatabaseConfig.MAX_DURATION + 1})
    @DisplayName("Debe detectar duración inválida")
    void testValidateInvalidDuration(int invalidDuration) {
        validMovie.setDuration(invalidDuration);
        Movie.ValidationResult result = validMovie.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessages().contains("duracion debe estar entre"));
    }
    
    @Test
    @DisplayName("Debe detectar género inválido")
    void testValidateInvalidGenre() {
        // Género nulo
        validMovie.setGenre(null);
        Movie.ValidationResult result = validMovie.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessages().contains("genero es obligatorio"));
        
        // Género vacío
        validMovie.setGenre("  ");
        result = validMovie.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessages().contains("genero es obligatorio"));
        
        // Género no válido
        validMovie.setGenre("Género Inexistente");
        result = validMovie.validate();
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessages().contains("Genero no valido"));
    }
    
    @ParameterizedTest
    @CsvSource({
        "60, 1h",
        "90, 1h 30m",
        "45, 45m",
        "150, 2h 30m",
        "0, No especificada"
    })
    @DisplayName("Debe formatear correctamente la duración")
    void testFormattedDuration(int duration, String expected) {
        validMovie.setDuration(duration);
        assertEquals(expected, validMovie.getFormattedDuration());
    }
    
    @Test
    @DisplayName("Debe implementar equals y hashCode correctamente")
    void testEqualsAndHashCode() {
        // Crear dos películas con el mismo ID
        Movie movie1 = new Movie(1, "Título 1", "Director 1", 2020, 120, "Drama");
        Movie movie2 = new Movie(1, "Título 2", "Director 2", 2021, 130, "Comedia");
        
        // Crear una película con ID diferente
        Movie movie3 = new Movie(2, "Título 1", "Director 1", 2020, 120, "Drama");
        
        // Verificar equals
        assertEquals(movie1, movie2, "Películas con mismo ID deben ser iguales");
        assertNotEquals(movie1, movie3, "Películas con diferente ID no deben ser iguales");
        
        // Verificar hashCode
        assertEquals(movie1.hashCode(), movie2.hashCode(), "HashCode debe ser igual para películas con mismo ID");
        assertNotEquals(movie1.hashCode(), movie3.hashCode(), "HashCode debe ser diferente para películas con diferente ID");
    }
    
    @Test
    @DisplayName("Debe implementar toString correctamente")
    void testToString() {
        Movie movie = new Movie(1, "Título", "Director", 2020, 120, "Drama");
        String toString = movie.toString();
        
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("title='Título'"));
        assertTrue(toString.contains("director='Director'"));
        assertTrue(toString.contains("year=2020"));
        assertTrue(toString.contains("duration=2h"));
        assertTrue(toString.contains("genre='Drama'"));
    }
}