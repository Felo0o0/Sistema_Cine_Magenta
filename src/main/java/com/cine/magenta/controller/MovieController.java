/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.controller;

import com.cine.magenta.model.Movie;
import com.cine.magenta.service.MovieService;
import com.cine.magenta.service.MovieServiceImpl;
import java.util.List;

/**
 * Controlador principal para operaciones sobre Movie.
 * Coordina la comunicacion entre la vista y los servicios de negocio.
 * Implementa el patron MVC actuando como intermediario.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class MovieController {
    
    /** Servicio de negocio para operaciones sobre peliculas */
    private final MovieService movieService;
    
    /**
    * Constructor que inicializa el servicio de peliculas.
    */
    public MovieController() {
    this.movieService = new MovieServiceImpl();
    }
    
    /**
    * Constructor con inyeccion de dependencias para testing.
    * 
    * @param movieService servicio de peliculas a utilizar
    */
    public MovieController(MovieService movieService) {
    this.movieService = movieService;
    }
    
    /**
    * Crea una nueva pelicula en el sistema.
    * 
    * @param title titulo de la pelicula
    * @param director director de la pelicula
    * @param year año de estreno
    * @param duration duracion en minutos
    * @param genre genero cinematografico
    * @return Movie creada con ID asignado
    * @throws Exception si la creacion falla
    */
    public Movie createMovie(String title, String director, int year, int duration, String genre) throws Exception {
    Movie movie = new Movie(title, director, year, duration, genre);
    return movieService.createMovie(movie);
    }
    
    /**
    * Crea una nueva pelicula usando objeto Movie.
    * 
    * @param movie objeto Movie a crear
    * @return Movie creada con ID asignado
    * @throws Exception si la creacion falla
    */
    public Movie createMovie(Movie movie) throws Exception {
    return movieService.createMovie(movie);
    }
    
    /**
    * Busca una pelicula por su identificador unico.
    * 
    * @param id identificador de la pelicula
    * @return Movie encontrada o null si no existe
    * @throws Exception si ocurre error en la busqueda
    */
    public Movie getMovieById(int id) throws Exception {
    if (id <= 0) {
    throw new IllegalArgumentException("ID debe ser un numero positivo");
    }
    return movieService.getMovieById(id);
    }
    
    /**
    * Obtiene todas las peliculas del sistema.
    * 
    * @return Lista de todas las peliculas ordenadas por titulo
    * @throws Exception si ocurre error en la consulta
    */
    public List<Movie> getAllMovies() throws Exception {
    return movieService.getAllMovies();
    }
    
    /**
    * Busca peliculas por titulo usando busqueda parcial.
    * 
    * @param title titulo o fragmento a buscar
    * @return Lista de peliculas que coinciden con el criterio
    * @throws Exception si ocurre error en la busqueda
    */
    public List<Movie> searchMoviesByTitle(String title) throws Exception {
    if (title == null || title.trim().isEmpty()) {
    throw new IllegalArgumentException("Titulo de busqueda no puede estar vacio");
    }
    return movieService.searchMoviesByTitle(title.trim());
    }
    
    /**
    * Obtiene peliculas filtradas por director.
    * 
    * @param director nombre del director
    * @return Lista de peliculas del director especificado
    * @throws Exception si ocurre error en la consulta
    */
    public List<Movie> getMoviesByDirector(String director) throws Exception {
    if (director == null || director.trim().isEmpty()) {
    throw new IllegalArgumentException("Nombre del director no puede estar vacio");
    }
    return movieService.getMoviesByDirector(director.trim());
    }
    
    /**
    * Obtiene peliculas de un genero especifico.
    * 
    * @param genre genero cinematografico
    * @return Lista de peliculas del genero
    * @throws Exception si ocurre error en la consulta
    */
    public List<Movie> getMoviesByGenre(String genre) throws Exception {
    if (genre == null || genre.trim().isEmpty()) {
    throw new IllegalArgumentException("Genero no puede estar vacio");
    }
    return movieService.getMoviesByGenre(genre.trim());
    }
    
    /**
    * Busca peliculas por año de estreno.
    * 
    * @param year año de estreno
    * @return Lista de peliculas del año especificado
    * @throws Exception si ocurre error en la busqueda
    */
    public List<Movie> getMoviesByYear(int year) throws Exception {
    return movieService.getMoviesByYear(year);
    }
    
    /**
    * Busca peliculas por rango de años.
    * 
    * @param startYear año inicial del rango
    * @param endYear año final del rango
    * @return Lista de peliculas dentro del rango de años especificado
    * @throws Exception si ocurre error en la busqueda
    */
    public List<Movie> getMoviesByYearRange(int startYear, int endYear) throws Exception {
    if (startYear > endYear) {
    throw new IllegalArgumentException("El año inicial debe ser menor o igual al año final");
    }
    return movieService.getMoviesByYearRange(startYear, endYear);
    }
    
    /**
    * Actualiza los datos de una pelicula existente.
    * 
    * @param movie pelicula con datos actualizados
    * @return Movie actualizada
    * @throws Exception si la actualizacion falla
    */
    public Movie updateMovie(Movie movie) throws Exception {
    if (movie == null) {
    throw new IllegalArgumentException("Movie no puede ser null");
    }
    if (movie.getId() <= 0) {
    throw new IllegalArgumentException("Movie debe tener un ID valido");
    }
    return movieService.updateMovie(movie);
    }
    
    /**
    * Actualiza una pelicula usando parametros individuales.
    * 
    * @param id identificador de la pelicula
    * @param title nuevo titulo
    * @param director nuevo director
    * @param year nuevo año
    * @param duration nueva duracion
    * @param genre nuevo genero
    * @return Movie actualizada
    * @throws Exception si la actualizacion falla
    */
    public Movie updateMovie(int id, String title, String director, int year, int duration, String genre) throws Exception {
    Movie movie = new Movie(id, title, director, year, duration, genre);
    return updateMovie(movie);
    }
    
    /**
    * Elimina una pelicula del sistema.
    * 
    * @param id identificador de la pelicula a eliminar
    * @return true si la eliminacion fue exitosa
    * @throws Exception si la eliminacion falla
    */
    public boolean deleteMovie(int id) throws Exception {
    if (id <= 0) {
    throw new IllegalArgumentException("ID debe ser un numero positivo");
    }
    
    // Verificar que la pelicula existe antes de eliminar
    if (!movieService.movieExists(id)) {
    throw new IllegalArgumentException("No existe pelicula con ID: " + id);
    }
    
    return movieService.deleteMovie(id);
    }
    
    /**
    * Verifica si existe una pelicula con el ID especificado.
    * 
    * @param id identificador a verificar
    * @return true si la pelicula existe
    * @throws Exception si ocurre error en la verificacion
    */
    public boolean movieExists(int id) throws Exception {
    return movieService.movieExists(id);
    }
    
    /**
    * Obtiene el numero total de peliculas registradas.
    * 
    * @return cantidad total de peliculas
    * @throws Exception si ocurre error en el calculo
    */
    public int getTotalMoviesCount() throws Exception {
    return movieService.getTotalMovies();
    }
    
    /**
    * Valida los datos de una pelicula.
    * 
    * @param movie pelicula a validar
    * @throws Exception si los datos no son validos
    */
    public void validateMovie(Movie movie) throws Exception {
    movieService.validateMovie(movie);
    }
}