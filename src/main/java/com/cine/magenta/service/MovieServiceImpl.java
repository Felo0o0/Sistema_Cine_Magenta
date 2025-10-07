/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.service;

import com.cine.magenta.dao.MovieDAO;
import com.cine.magenta.dao.MovieDAOImpl;
import com.cine.magenta.model.Movie;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementacion de los servicios de negocio para Movie.
 * Coordina operaciones complejas y aplica reglas de negocio.
 * Actua como capa intermedia entre controladores y acceso a datos.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class MovieServiceImpl implements MovieService {
    
    /** DAO para operaciones de acceso a datos */
    private final MovieDAO movieDAO;
    
    /**
    * Constructor que inicializa el DAO.
    */
    public MovieServiceImpl() {
    this.movieDAO = new MovieDAOImpl();
    }
    
    /**
    * Constructor con inyeccion de dependencias para testing.
    * 
    * @param movieDAO DAO a utilizar
    */
    public MovieServiceImpl(MovieDAO movieDAO) {
    if (movieDAO == null) {
    throw new IllegalArgumentException("MovieDAO no puede ser null");
    }
    this.movieDAO = movieDAO;
    }
    
    @Override
    public Movie createMovie(Movie movie) throws Exception {
    if (movie == null) {
    throw new IllegalArgumentException("Movie no puede ser null");
    }
    
    try {
    // Validar datos de la pelicula
    validateMovie(movie);
    
    // Crear la pelicula en la base de datos
    int generatedId = movieDAO.create(movie);
    
    if (generatedId > 0) {
    movie.setId(generatedId);
    return movie;
    } else {
    throw new Exception("Error al crear la pelicula: no se genero ID valido");
    }
    
    } catch (SQLException e) {
    throw new Exception("Error de base de datos al crear pelicula: " + e.getMessage(), e);
    } catch (Exception e) {
    throw new Exception("Error al crear pelicula: " + e.getMessage(), e);
    }
    }
    
    @Override
    public Movie getMovieById(int id) throws Exception {
    if (id <= 0) {
    throw new IllegalArgumentException("ID debe ser positivo, recibido: " + id);
    }
    
    try {
    Movie movie = movieDAO.findById(id);
    if (movie == null) {
    throw new Exception("No se encontro pelicula con ID: " + id);
    }
    
    return movie;
    
    } catch (SQLException e) {
    throw new Exception("Error de base de datos al buscar pelicula ID " + id + ": " + e.getMessage(), e);
    }
    }
    
    @Override
    public List<Movie> getAllMovies() throws Exception {
    try {
    List<Movie> movies = movieDAO.findAll();
    
    if (movies == null) {
    throw new Exception("Error al obtener lista de peliculas: resultado null");
    }
    
    return movies;
    
    } catch (SQLException e) {
    throw new Exception("Error de base de datos al obtener peliculas: " + e.getMessage(), e);
    }
    }
    
    @Override
    public List<Movie> searchMoviesByTitle(String title) throws Exception {
    if (title == null || title.trim().isEmpty()) {
    throw new IllegalArgumentException("Titulo de busqueda no puede estar vacio");
    }
    
    try {
    List<Movie> movies = movieDAO.findByTitle(title.trim());
    
    if (movies == null) {
    throw new Exception("Error al buscar peliculas por titulo: resultado null");
    }
    
    return movies;
    
    } catch (SQLException e) {
    throw new Exception("Error de base de datos al buscar peliculas por titulo '" + title + "': " + e.getMessage(), e);
    }
    }
    
    @Override
    public List<Movie> getMoviesByDirector(String director) throws Exception {
    if (director == null || director.trim().isEmpty()) {
    throw new IllegalArgumentException("Director no puede estar vacio");
    }
    
    try {
    List<Movie> movies = movieDAO.findByDirector(director.trim());
    
    if (movies == null) {
    throw new Exception("Error al buscar peliculas por director: resultado null");
    }
    
    return movies;
    
    } catch (SQLException e) {
    throw new Exception("Error de base de datos al buscar peliculas por director '" + director + "': " + e.getMessage(), e);
    }
    }
    
    @Override
    public List<Movie> getMoviesByGenre(String genre) throws Exception {
    if (genre == null || genre.trim().isEmpty()) {
    throw new IllegalArgumentException("Genero no puede estar vacio");
    }
    
    try {
    List<Movie> movies = movieDAO.findByGenre(genre.trim());
    
    if (movies == null) {
    throw new Exception("Error al buscar peliculas por genero: resultado null");
    }
    
    return movies;
    
    } catch (SQLException e) {
    throw new Exception("Error de base de datos al buscar peliculas por genero '" + genre + "': " + e.getMessage(), e);
    }
    }
    
    @Override
    public List<Movie> getMoviesByYear(int year) throws Exception {
    try {
    List<Movie> movies = movieDAO.findByYear(year);
    
    if (movies == null) {
    throw new Exception("Error al buscar peliculas por año: resultado null");
    }
    
    return movies;
    
    } catch (SQLException e) {
    throw new Exception("Error de base de datos al buscar peliculas por año " + year + ": " + e.getMessage(), e);
    }
    }
    
    @Override
    public List<Movie> getMoviesByYearRange(int startYear, int endYear) throws Exception {
    if (startYear > endYear) {
    throw new IllegalArgumentException("El año inicial debe ser menor o igual al año final");
    }
    
    try {
    List<Movie> movies = movieDAO.findByYearRange(startYear, endYear);
    
    if (movies == null) {
    throw new Exception("Error al buscar peliculas por rango de años: resultado null");
    }
    
    return movies;
    
    } catch (SQLException e) {
    throw new Exception("Error de base de datos al buscar peliculas por rango de años " + 
    startYear + "-" + endYear + ": " + e.getMessage(), e);
    }
    }
    
    @Override
    public Movie updateMovie(Movie movie) throws Exception {
    if (movie == null) {
    throw new IllegalArgumentException("Movie no puede ser null");
    }
    
    if (movie.getId() <= 0) {
    throw new IllegalArgumentException("Movie debe tener un ID valido, recibido: " + movie.getId());
    }
    
    try {
    // Verificar que la pelicula existe
    if (!movieDAO.exists(movie.getId())) {
    throw new Exception("No existe pelicula con ID: " + movie.getId());
    }
    
    // Validar nuevos datos
    validateMovie(movie);
    
    // Actualizar en la base de datos
    boolean updated = movieDAO.update(movie);
    
    if (!updated) {
    throw new Exception("No se pudo actualizar la pelicula ID " + movie.getId() + ": operacion no afecto filas");
    }
    
    return movie;
    
    } catch (SQLException e) {
    throw new Exception("Error de base de datos al actualizar pelicula ID " + movie.getId() + ": " + e.getMessage(), e);
    }
    }
    
    @Override
    public boolean deleteMovie(int id) throws Exception {
    if (id <= 0) {
    throw new IllegalArgumentException("ID debe ser positivo, recibido: " + id);
    }
    
    try {
    // Verificar que la pelicula existe
    if (!movieDAO.exists(id)) {
    throw new Exception("No existe pelicula con ID: " + id);
    }
    
    boolean deleted = movieDAO.delete(id);
    
    if (!deleted) {
    throw new Exception("No se pudo eliminar la pelicula ID " + id + ": operacion no afecto filas");
    }
    
    return deleted;
    
    } catch (SQLException e) {
    throw new Exception("Error de base de datos al eliminar pelicula ID " + id + ": " + e.getMessage(), e);
    }
    }
    
    @Override
    public boolean movieExists(int id) throws Exception {
    if (id <= 0) {
    return false;
    }
    
    try {
    return movieDAO.exists(id);
    } catch (SQLException e) {
    throw new Exception("Error de base de datos al verificar existencia de pelicula ID " + id + ": " + e.getMessage(), e);
    }
    }
    
    @Override
    public int getTotalMovies() throws Exception {
    try {
    int count = movieDAO.count();
    
    if (count < 0) {
    throw new Exception("Error al obtener contador: valor negativo recibido");
    }
    
    return count;
    
    } catch (SQLException e) {
    throw new Exception("Error de base de datos al contar peliculas: " + e.getMessage(), e);
    }
    }
    
    @Override
    public void validateMovie(Movie movie) throws Exception {
    if (movie == null) {
    throw new IllegalArgumentException("Movie no puede ser null");
    }
    
    Movie.ValidationResult validation = movie.validate();
    
    if (!validation.isValid()) {
    throw new IllegalArgumentException("Datos de pelicula invalidos:\n" + validation.getErrorMessages());
    }
    }
}