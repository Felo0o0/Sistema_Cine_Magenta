/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.service;

import com.cine.magenta.model.Movie;
import java.util.List;

/**
 * Interface que define los servicios de negocio para Movie.
 * Encapsula la logica de negocio y coordina operaciones complejas.
 * Actua como capa intermedia entre controladores y DAOs.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public interface MovieService {
    
    /**
    * Crea una nueva pelicula aplicando validaciones de negocio.
    * 
    * @param movie pelicula a crear
    * @return Movie creada con ID asignado
    * @throws Exception si la operacion falla o datos invalidos
    */
    Movie createMovie(Movie movie) throws Exception;
    
    /**
    * Busca una pelicula por su ID.
    * 
    * @param id identificador de la pelicula
    * @return Movie encontrada o null
    * @throws Exception si ocurre error en la busqueda
    */
    Movie getMovieById(int id) throws Exception;
    
    /**
    * Obtiene todas las peliculas ordenadas por titulo.
    * 
    * @return Lista de todas las peliculas
    * @throws Exception si ocurre error en la consulta
    */
    List<Movie> getAllMovies() throws Exception;
    
    /**
    * Busca peliculas por titulo (busqueda parcial).
    * 
    * @param title titulo o fragmento a buscar
    * @return Lista de peliculas coincidentes
    * @throws Exception si ocurre error en la busqueda
    */
    List<Movie> searchMoviesByTitle(String title) throws Exception;
    
    /**
    * Busca peliculas por director.
    * 
    * @param director nombre del director
    * @return Lista de peliculas del director
    * @throws Exception si ocurre error en la busqueda
    */
    List<Movie> getMoviesByDirector(String director) throws Exception;
    
    /**
    * Obtiene peliculas filtradas por genero.
    * 
    * @param genre genero cinematografico
    * @return Lista de peliculas del genero
    * @throws Exception si ocurre error en la consulta
    */
    List<Movie> getMoviesByGenre(String genre) throws Exception;
    
    /**
    * Busca peliculas por año de estreno.
    * 
    * @param year año de estreno
    * @return Lista de peliculas del año
    * @throws Exception si ocurre error en la busqueda
    */
    List<Movie> getMoviesByYear(int year) throws Exception;
    
    /**
    * Busca peliculas por rango de años.
    * 
    * @param startYear año inicial del rango
    * @param endYear año final del rango
    * @return Lista de peliculas dentro del rango de años especificado
    * @throws Exception si ocurre error en la busqueda
    */
    List<Movie> getMoviesByYearRange(int startYear, int endYear) throws Exception;
    
    /**
    * Actualiza una pelicula existente con validaciones.
    * 
    * @param movie pelicula con datos actualizados
    * @return Movie actualizada
    * @throws Exception si la actualizacion falla
    */
    Movie updateMovie(Movie movie) throws Exception;
    
    /**
    * Elimina una pelicula del sistema.
    * 
    * @param id identificador de la pelicula
    * @return true si la eliminacion fue exitosa
    * @throws Exception si la eliminacion falla
    */
    boolean deleteMovie(int id) throws Exception;
    
    /**
    * Verifica si existe una pelicula con el ID dado.
    * 
    * @param id identificador a verificar
    * @return true si la pelicula existe
    * @throws Exception si ocurre error en la verificacion
    */
    boolean movieExists(int id) throws Exception;
    
    /**
    * Obtiene estadisticas generales del sistema.
    * 
    * @return numero total de peliculas registradas
    * @throws Exception si ocurre error en el calculo
    */
    int getTotalMovies() throws Exception;
    
    /**
    * Valida los datos de una pelicula antes de operaciones.
    * 
    * @param movie pelicula a validar
    * @throws Exception si los datos no son validos
    */
    void validateMovie(Movie movie) throws Exception;
}