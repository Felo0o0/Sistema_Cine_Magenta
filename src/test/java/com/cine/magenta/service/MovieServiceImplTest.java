/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.service;

import com.cine.magenta.dao.MovieDAO;
import com.cine.magenta.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para MovieServiceImpl")
public class MovieServiceImplTest {
    
    @Mock
    private MovieDAO mockMovieDAO;
    
    private MovieServiceImpl movieService;
    private Movie testMovie;
    
    @BeforeEach
    void setUp() {
        // Inicializar el servicio con el DAO mock
        movieService = new MovieServiceImpl(mockMovieDAO);
        
        // Crear una película de prueba
        testMovie = new Movie("Título de Prueba", "Director de Prueba", 2020, 120, "Drama");
    }
    
    @Test
    @DisplayName("Debe crear una película correctamente")
    void testCreateMovie() throws Exception {
        // Configurar el comportamiento del mock
        when(mockMovieDAO.create(any(Movie.class))).thenReturn(1);
        
        // Ejecutar el método a probar
        Movie result = movieService.createMovie(testMovie);
        
        // Verificar el resultado
        assertNotNull(result);
        assertEquals(1, result.getId());
        
        // Verificar que se llamó al DAO
        verify(mockMovieDAO).create(testMovie);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al crear película nula")
    void testCreateNullMovie() {
        // Ejecutar el método con película nula y verificar que lanza excepción
        assertThrows(IllegalArgumentException.class, () -> movieService.createMovie(null));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción cuando el DAO falla al crear")
    void testCreateMovieDAOException() throws SQLException {
        // Configurar el mock para lanzar excepción
        when(mockMovieDAO.create(any(Movie.class))).thenThrow(new SQLException("Error de prueba"));
        
        // Ejecutar el método y verificar que propaga la excepción
        Exception exception = assertThrows(Exception.class, () -> movieService.createMovie(testMovie));
        assertTrue(exception.getMessage().contains("Error de base de datos"));
    }
    
    @Test
    @DisplayName("Debe obtener película por ID correctamente")
    void testGetMovieById() throws Exception {
        // Crear película esperada
        Movie expectedMovie = new Movie(1, "Título", "Director", 2020, 120, "Drama");
        
        // Configurar el comportamiento del mock
        when(mockMovieDAO.findById(1)).thenReturn(expectedMovie);
        
        // Ejecutar el método a probar
        Movie result = movieService.getMovieById(1);
        
        // Verificar el resultado
        assertNotNull(result);
        assertEquals(expectedMovie.getId(), result.getId());
        assertEquals(expectedMovie.getTitle(), result.getTitle());
        
        // Verificar que se llamó al DAO
        verify(mockMovieDAO).findById(1);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción cuando no encuentra película por ID")
    void testGetMovieByIdNotFound() throws SQLException {
        // Configurar el mock para retornar null (no encontrado)
        when(mockMovieDAO.findById(anyInt())).thenReturn(null);
        
        // Ejecutar el método y verificar que lanza excepción
        Exception exception = assertThrows(Exception.class, () -> movieService.getMovieById(1));
        assertTrue(exception.getMessage().contains("No se encontro pelicula"));
    }
    
    @Test
    @DisplayName("Debe obtener todas las películas correctamente")
    void testGetAllMovies() throws Exception {
        // Crear lista de películas esperada
        List<Movie> expectedMovies = new ArrayList<>();
        expectedMovies.add(new Movie(1, "Título 1", "Director 1", 2020, 120, "Drama"));
        expectedMovies.add(new Movie(2, "Título 2", "Director 2", 2021, 130, "Comedia"));
        
        // Configurar el comportamiento del mock
        when(mockMovieDAO.findAll()).thenReturn(expectedMovies);
        
        // Ejecutar el método a probar
        List<Movie> results = movieService.getAllMovies();
        
        // Verificar el resultado
        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(expectedMovies.get(0).getId(), results.get(0).getId());
        assertEquals(expectedMovies.get(1).getId(), results.get(1).getId());
        
        // Verificar que se llamó al DAO
        verify(mockMovieDAO).findAll();
    }
    
    @Test
    @DisplayName("Debe buscar películas por título correctamente")
    void testSearchMoviesByTitle() throws Exception {
        // Crear lista de películas esperada
        List<Movie> expectedMovies = new ArrayList<>();
        expectedMovies.add(new Movie(1, "Título de Prueba", "Director 1", 2020, 120, "Drama"));
        
        // Configurar el comportamiento del mock
        when(mockMovieDAO.findByTitle(anyString())).thenReturn(expectedMovies);
        
        // Ejecutar el método a probar
        List<Movie> results = movieService.searchMoviesByTitle("Título");
        
        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Título de Prueba", results.get(0).getTitle());
        
        // Verificar que se llamó al DAO
        verify(mockMovieDAO).findByTitle("Título");
    }
    
    @Test
    @DisplayName("Debe actualizar película correctamente")
    void testUpdateMovie() throws Exception {
        // Crear película para actualizar
        Movie movieToUpdate = new Movie(1, "Título Actualizado", "Director Actualizado", 2021, 130, "Comedia");
        
        // Configurar el comportamiento del mock
        when(mockMovieDAO.exists(1)).thenReturn(true);
        when(mockMovieDAO.update(any(Movie.class))).thenReturn(true);
        
        // Ejecutar el método a probar
        Movie result = movieService.updateMovie(movieToUpdate);
        
        // Verificar el resultado
        assertNotNull(result);
        assertEquals(movieToUpdate.getId(), result.getId());
        assertEquals(movieToUpdate.getTitle(), result.getTitle());
        
        // Verificar que se llamaron los métodos esperados
        verify(mockMovieDAO).exists(1);
        verify(mockMovieDAO).update(movieToUpdate);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al actualizar película inexistente")
    void testUpdateNonExistentMovie() throws SQLException {
        // Crear película con ID que no existe
        Movie nonExistentMovie = new Movie(999, "Título", "Director", 2020, 120, "Drama");
        
        // Configurar el mock para indicar que la película no existe
        when(mockMovieDAO.exists(999)).thenReturn(false);
        
        // Ejecutar el método y verificar que lanza excepción
        Exception exception = assertThrows(Exception.class, () -> movieService.updateMovie(nonExistentMovie));
        assertTrue(exception.getMessage().contains("No existe pelicula"));
    }
    
    @Test
    @DisplayName("Debe eliminar película correctamente")
    void testDeleteMovie() throws Exception {
        // Configurar el comportamiento del mock
        when(mockMovieDAO.exists(1)).thenReturn(true);
        when(mockMovieDAO.delete(1)).thenReturn(true);
        
        // Ejecutar el método a probar
        boolean result = movieService.deleteMovie(1);
        
        // Verificar el resultado
        assertTrue(result);
        
        // Verificar que se llamaron los métodos esperados
        verify(mockMovieDAO).exists(1);
        verify(mockMovieDAO).delete(1);
    }
    
    @Test
    @DisplayName("Debe verificar existencia de película correctamente")
    void testMovieExists() throws Exception {
        // Configurar el comportamiento del mock
        when(mockMovieDAO.exists(1)).thenReturn(true);
        when(mockMovieDAO.exists(999)).thenReturn(false);
        
        // Verificar película existente
        assertTrue(movieService.movieExists(1));
        
        // Verificar película inexistente
        assertFalse(movieService.movieExists(999));
    }
    
    @Test
    @DisplayName("Debe obtener total de películas correctamente")
    void testGetTotalMovies() throws Exception {
        // Configurar el comportamiento del mock
        when(mockMovieDAO.count()).thenReturn(10);
        
        // Ejecutar el método a probar
        int result = movieService.getTotalMovies();
        
        // Verificar el resultado
        assertEquals(10, result);
        
        // Verificar que se llamó al DAO
        verify(mockMovieDAO).count();
    }
    
    @Test
    @DisplayName("Debe validar película correctamente")
    void testValidateMovie() throws Exception {
        // Ejecutar el método con una película válida
        assertDoesNotThrow(() -> movieService.validateMovie(testMovie));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al validar película inválida")
    void testValidateInvalidMovie() {
        // Crear película inválida (sin título)
        Movie invalidMovie = new Movie(null, "Director", 2020, 120, "Drama");
        
        // Ejecutar el método y verificar que lanza excepción
        Exception exception = assertThrows(Exception.class, () -> movieService.validateMovie(invalidMovie));
        assertTrue(exception.getMessage().contains("Datos de pelicula invalidos"));
    }
}