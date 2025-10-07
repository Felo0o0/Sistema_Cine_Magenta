/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.dao;

import com.cine.magenta.model.Movie;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface que define las operaciones de acceso a datos para Movie.
 * Define el contrato para las operaciones CRUD sobre la entidad Movie.
 * Implementa el patron DAO para separar la logica de acceso a datos.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public interface MovieDAO {
    
    /**
    * Crea una nueva pelicula en la base de datos.
    * 
    * @param movie objeto Movie a crear
    * @return ID generado para la nueva pelicula
    * @throws SQLException si ocurre error en la operacion
    */
    int create(Movie movie) throws SQLException;
    
    /**
    * Busca una pelicula por su ID unico.
    * 
    * @param id identificador de la pelicula
    * @return Movie encontrada o null si no existe
    * @throws SQLException si ocurre error en la consulta
    */
    Movie findById(int id) throws SQLException;
    
    /**
    * Obtiene todas las peliculas de la base de datos.
    * 
    * @return Lista de todas las peliculas
    * @throws SQLException si ocurre error en la consulta
    */
    List<Movie> findAll() throws SQLException;
    
    /**
    * Busca peliculas por titulo (busqueda parcial).
    * 
    * @param title titulo o parte del titulo a buscar
    * @return Lista de peliculas que coinciden
    * @throws SQLException si ocurre error en la consulta
    */
    List<Movie> findByTitle(String title) throws SQLException;
    
    /**
    * Busca peliculas por director.
    * 
    * @param director nombre del director
    * @return Lista de peliculas del director
    * @throws SQLException si ocurre error en la consulta
    */
    List<Movie> findByDirector(String director) throws SQLException;
    
    /**
    * Busca peliculas por genero.
    * 
    * @param genre genero cinematografico
    * @return Lista de peliculas del genero especificado
    * @throws SQLException si ocurre error en la consulta
    */
    List<Movie> findByGenre(String genre) throws SQLException;
    
    /**
    * Busca peliculas por año de estreno.
    * 
    * @param year año de estreno
    * @return Lista de peliculas del año especificado
    * @throws SQLException si ocurre error en la consulta
    */
    List<Movie> findByYear(int year) throws SQLException;
    
    /**
    * Actualiza los datos de una pelicula existente.
    * 
    * @param movie objeto Movie con datos actualizados
    * @return true si la actualizacion fue exitosa
    * @throws SQLException si ocurre error en la operacion
    */
    boolean update(Movie movie) throws SQLException;
    
    /**
    * Elimina una pelicula de la base de datos.
    * 
    * @param id identificador de la pelicula a eliminar
    * @return true si la eliminacion fue exitosa
    * @throws SQLException si ocurre error en la operacion
    */
    boolean delete(int id) throws SQLException;
    
    /**
    * Verifica si existe una pelicula con el ID especificado.
    * 
    * @param id identificador a verificar
    * @return true si la pelicula existe
    * @throws SQLException si ocurre error en la consulta
    */
    boolean exists(int id) throws SQLException;
    
    /**
    * Obtiene el total de peliculas registradas.
    * 
    * @return numero total de peliculas
    * @throws SQLException si ocurre error en la consulta
    */
    int count() throws SQLException;
    
    /**
    * Busca peliculas por rango de años.
    * 
    * @param startYear año inicial del rango
    * @param endYear año final del rango
    * @return Lista de peliculas dentro del rango de años especificado
    * @throws SQLException si ocurre error en la consulta
    */
    List<Movie> findByYearRange(int startYear, int endYear) throws SQLException;
}