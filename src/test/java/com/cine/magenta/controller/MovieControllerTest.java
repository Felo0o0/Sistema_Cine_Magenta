/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.controller;

import com.cine.magenta.model.Movie;
import com.cine.magenta.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para MovieController")
public class MovieControllerTest {
    
    @Mock
    private MovieService mockMovieService;
    
    private MovieController movieController;
    
    @BeforeEach
    void setUp() {
        // Inicializar el controlador con el servicio mock
        movieController = new MovieController(mockMovieService);
    }
    
    @Test
    @DisplayName("Debe crear película con parámetros individuales correctamente")
    void testCreateMovieWithParams() throws Exception {
        // Configurar el comportamiento del mock
        when(mockMovieService.createMovie(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setId(1);
            return movie;
        });
        
        // Ejecutar el método a probar
        Movie result = movieController.createMovie("Título", "Director", 2020, 120, "Drama");
        
        // Verificar el resultado
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Título", result.getTitle());
        assertEquals("Director", result.getDirector());
        assertEquals(2020, result.getYear());
        assertEquals(120, result.getDuration());
        assertEquals("Drama", result.getGenre());
        
        // Verificar que se llamó al servicio
        verify(mockMovieService).createMovie(any(Movie.class));
    }
    
    @Test
    @DisplayName("Debe crear película con objeto Movie correctamente")
    void testCreateMovieWithObject() throws Exception {
        // Crear película de prueba
        Movie testMovie = new Movie("Título", "Director", 2020, 120, "Drama");
        
        // Configurar el comportamiento del mock
        when(mockMovieService.createMovie(any(Movie.class))).thenAnswer(invocation -> {
            Movie movie = invocation.getArgument(0);
            movie.setId(1);
            return movie;
        });
        
        // Ejecutar el método a probar
        Movie result = movieController.createMovie(testMovie);
        
        // Verificar el resultado
        assertNotNull(result);
        assertEquals(1, result.getId());
        
        // Verificar que se llamó al servicio
        verify(mockMovieService).createMovie(testMovie);
    }
    
    @Test
    @DisplayName("Debe obtener película por ID correctamente")
    void testGetMovieById() throws Exception {
        // Crear película esperada
        Movie expectedMovie = new Movie(1, "Título", "Director", 2020, 120, "Drama");
        
        // Configurar el comportamiento del mock
        when(mockMovieService.getMovieById(1)).thenReturn(expectedMovie);
        
        // Ejecutar el método a probar
        Movie result = movieController.getMovieById(1);
        
        // Verificar el resultado
        assertNotNull(result);
        assertEquals(expectedMovie.getId(), result.getId());
        assertEquals(expectedMovie.getTitle(), result.getTitle());
        
        // Verificar que se llamó al servicio
        verify(mockMovieService).getMovieById(1);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al buscar con ID inválido")
    void testGetMovieByIdInvalidId() {
        // Ejecutar el método con ID inválido y verificar que lanza excepción
        assertThrows(IllegalArgumentException.class, () -> movieController.getMovieById(0));
        assertThrows(IllegalArgumentException.class, () -> movieController.getMovieById(-1));
    }
    
    @Test
    @DisplayName("Debe obtener todas las películas correctamente")
    void testGetAllMovies() throws Exception {
        // Crear lista de películas esperada
        List<Movie> expectedMovies = new ArrayList<>();
        expectedMovies.add(new Movie(1, "Título 1", "Director 1", 2020, 120, "Drama"));
        expectedMovies.add(new Movie(2, "Título 2", "Director 2", 2021, 130, "Comedia"));
        
        // Configurar el comportamiento del mock
        when(mockMovieService.getAllMovies()).thenReturn(expectedMovies);
        
        // Ejecutar el método a probar
        List<Movie> results = movieController.getAllMovies();
        
        // Verificar el resultado
        assertNotNull(results);
        assertEquals(2, results.size());
        
        // Verificar que se llamó al servicio
        verify(mockMovieService).getAllMovies();
    }
    
    @Test
    @DisplayName("Debe buscar películas por título correctamente")
    void testSearchMoviesByTitle() throws Exception {
        // Crear lista de películas esperada
        List<Movie> expectedMovies = new ArrayList<>();
        expectedMovies.add(new Movie(1, "Título de Prueba", "Director 1", 2020, 120, "Drama"));
        
        // Configurar el comportamiento del mock
        when(mockMovieService.searchMoviesByTitle(anyString())).thenReturn(expectedMovies);
        
        // Ejecutar el método a probar
        List<Movie> results = movieController.searchMoviesByTitle("Título");
        
        // Verificar el resultado
        assertNotNull(results);
        assertEquals(1, results.size());
        
        // Verificar que se llamó al servicio
        verify(mockMovieService).searchMoviesByTitle("Título");
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al buscar con título vacío")
    void testSearchMoviesByTitleEmpty() {
        // Ejecutar el método con título vacío y verificar que lanza excepción
        assertThrows(IllegalArgumentException.class, () -> movieController.searchMoviesByTitle(""));
        assertThrows(IllegalArgumentException.class, () -> movieController.searchMoviesByTitle(null));
    }
    
    @Test
    @DisplayName("Debe actualizar película correctamente")
    void testUpdateMovie() throws Exception {
        // Crear película para actualizar
        Movie movieToUpdate = new Movie(1, "Título Actualizado", "Director Actualizado", 2021, 130, "Comedia");
        
        // Configurar el comportamiento del mock
        when(mockMovieService.updateMovie(any(Movie.class))).thenReturn(movieToUpdate);
        
        // Ejecutar el método a probar
        Movie result = movieController.updateMovie(movieToUpdate);
        
        // Verificar el resultado
        assertNotNull(result);
        assertEquals(movieToUpdate.getId(), result.getId());
        assertEquals(movieToUpdate.getTitle(), result.getTitle());
        
        // Verificar que se llamó al servicio
        verify(mockMovieService).updateMovie(movieToUpdate);
    }
    
    @Test
    @DisplayName("Debe actualizar película con parámetros individuales correctamente")
    void testUpdateMovieWithParams() throws Exception {
        // Configurar el comportamiento del mock
        when(mockMovieService.updateMovie(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Ejecutar el método a probar
        Movie result = movieController.updateMovie(1, "Título", "Director", 2020, 120, "Drama");
        
        // Verificar el resultado
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Título", result.getTitle());
        
        // Verificar que se llamó al servicio
        verify(mockMovieService).updateMovie(any(Movie.class));
    }
    
    @Test
    @DisplayName("Debe eliminar película correctamente")
    void testDeleteMovie() throws Exception {
        // Configurar el comportamiento del mock
        when(mockMovieService.movieExists(1)).thenReturn(true);
        when(mockMovieService.deleteMovie(1)).thenReturn(true);
        
        // Ejecutar el método a probar
        boolean result = movieController.deleteMovie(1);
        
        // Verificar el resultado
        assertTrue(result);
        
        // Verificar que se llamaron los métodos esperados
        verify(mockMovieService).movieExists(1);
        verify(mockMovieService).deleteMovie(1);
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al eliminar película inexistente")
    void testDeleteNonExistentMovie() throws Exception {
        // Configurar el mock para indicar que la película no existe
        when(mockMovieService.movieExists(999)).thenReturn(false);
        
        // Ejecutar el método y verificar que lanza excepción
        Exception exception = assertThrows(IllegalArgumentException.class, () -> movieController.deleteMovie(999));
        assertTrue(exception.getMessage().contains("No existe pelicula"));
    }
    
    @Test
    @DisplayName("Debe verificar existencia de película correctamente")
    void testMovieExists() throws Exception {
        // Configurar el comportamiento del mock
        when(mockMovieService.movieExists(1)).thenReturn(true);
        when(mockMovieService.movieExists(999)).thenReturn(false);
        
        // Verificar película existente
        assertTrue(movieController.movieExists(1));
        
        // Verificar película inexistente
        assertFalse(movieController.movieExists(999));
    }
    
    @Test
    @DisplayName("Debe obtener total de películas correctamente")
    void testGetTotalMoviesCount() throws Exception {
        // Configurar el comportamiento del mock
        when(mockMovieService.getTotalMovies()).thenReturn(10);
        
        // Ejecutar el método a probar
        int result = movieController.getTotalMoviesCount();
        
        // Verificar el resultado
        assertEquals(10, result);
        
        // Verificar que se llamó al servicio
        verify(mockMovieService).getTotalMovies();
    }
}