/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.dao;

import com.cine.magenta.model.Movie;
import com.cine.magenta.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacion concreta del DAO para operaciones sobre Movie.
 * Maneja todas las operaciones CRUD contra la tabla Cartelera.
 * Utiliza prepared statements para seguridad y performance.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class MovieDAOImpl implements MovieDAO {
    
    /** Instancia de conexion a la base de datos */
    private final DatabaseConnection dbConnection;
    
    /** Consulta SQL para insertar nueva pelicula */
    private static final String INSERT_MOVIE = 
    "INSERT INTO Cartelera (titulo, director, ano, duracion, genero) VALUES (?, ?, ?, ?, ?)";
    
    /** Consulta SQL para buscar pelicula por ID */
    private static final String SELECT_BY_ID = 
    "SELECT id, titulo, director, ano, duracion, genero FROM Cartelera WHERE id = ?";
    
    /** Consulta SQL para obtener todas las peliculas */
    private static final String SELECT_ALL = 
    "SELECT id, titulo, director, ano, duracion, genero FROM Cartelera ORDER BY titulo";
    
    /** Consulta SQL para buscar por titulo */
    private static final String SELECT_BY_TITLE = 
    "SELECT id, titulo, director, ano, duracion, genero FROM Cartelera WHERE titulo LIKE ? ORDER BY titulo";
    
    /** Consulta SQL para buscar por director */
    private static final String SELECT_BY_DIRECTOR = 
    "SELECT id, titulo, director, ano, duracion, genero FROM Cartelera WHERE director LIKE ? ORDER BY titulo";
    
    /** Consulta SQL para buscar por genero */
    private static final String SELECT_BY_GENRE = 
    "SELECT id, titulo, director, ano, duracion, genero FROM Cartelera WHERE genero = ? ORDER BY titulo";
    
    /** Consulta SQL para buscar por año */
    private static final String SELECT_BY_YEAR = 
    "SELECT id, titulo, director, ano, duracion, genero FROM Cartelera WHERE ano = ? ORDER BY titulo";
    
    /** Consulta SQL para actualizar pelicula */
    private static final String UPDATE_MOVIE = 
    "UPDATE Cartelera SET titulo = ?, director = ?, ano = ?, duracion = ?, genero = ? WHERE id = ?";
    
    /** Consulta SQL para eliminar pelicula */
    private static final String DELETE_MOVIE = 
    "DELETE FROM Cartelera WHERE id = ?";
    
    /** Consulta SQL para verificar existencia */
    private static final String EXISTS_QUERY = 
    "SELECT 1 FROM Cartelera WHERE id = ? LIMIT 1";
    
    /** Consulta SQL para buscar por rango de años */
    private static final String SELECT_BY_YEAR_RANGE = 
    "SELECT id, titulo, director, ano, duracion, genero FROM Cartelera WHERE ano BETWEEN ? AND ? ORDER BY titulo";
    
    /** Consulta SQL para contar registros */
    private static final String COUNT_QUERY = 
    "SELECT COUNT(*) as total FROM Cartelera";
    
    /**
    * Constructor que inicializa la conexion a la base de datos.
    */
    public MovieDAOImpl() {
    this.dbConnection = DatabaseConnection.getInstance();
    }
    
    /**
    * Constructor con inyeccion de dependencias para testing.
    * 
    * @param dbConnection conexion a utilizar
    */
    public MovieDAOImpl(DatabaseConnection dbConnection) {
    this.dbConnection = dbConnection;
    }
    
    @Override
    public int create(Movie movie) throws SQLException {
    if (movie == null) {
    throw new IllegalArgumentException("Movie no puede ser null");
    }
    
    if (!movie.isValid()) {
    throw new IllegalArgumentException("Datos de Movie no validos: " + 
    movie.validate().getErrorMessages());
    }
    
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet generatedKeys = null;
    
    try {
    conn = dbConnection.getConnection();
    stmt = conn.prepareStatement(INSERT_MOVIE, Statement.RETURN_GENERATED_KEYS);
    
    // Establecer parametros
    stmt.setString(1, movie.getTitle());
    stmt.setString(2, movie.getDirector());
    stmt.setInt(3, movie.getYear());
    stmt.setInt(4, movie.getDuration());
    stmt.setString(5, movie.getGenre());
    
    int rowsAffected = stmt.executeUpdate();
    
    if (rowsAffected > 0) {
    generatedKeys = stmt.getGeneratedKeys();
    if (generatedKeys.next()) {
    int generatedId = generatedKeys.getInt(1);
    movie.setId(generatedId);
    return generatedId;
    }
    }
    
    throw new SQLException("Error al crear pelicula, no se genero ID");
    
    } catch (SQLException e) {
    throw new SQLException("Error al insertar pelicula: " + e.getMessage(), e);
    } finally {
    closeResources(generatedKeys, stmt, null);
    }
    }
    
    @Override
    public Movie findById(int id) throws SQLException {
    if (id <= 0) {
    throw new IllegalArgumentException("ID debe ser positivo, recibido: " + id);
    }
    
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    try {
    conn = dbConnection.getConnection();
    stmt = conn.prepareStatement(SELECT_BY_ID);
    stmt.setInt(1, id);
    
    rs = stmt.executeQuery();
    
    if (rs.next()) {
    return mapResultSetToMovie(rs);
    }
    
    return null; // No encontrada
    
    } catch (SQLException e) {
    throw new SQLException("Error al buscar pelicula por ID " + id + ": " + e.getMessage(), e);
    } finally {
    closeResources(rs, stmt, null);
    }
    }
    
    @Override
    public List<Movie> findAll() throws SQLException {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    List<Movie> movies = new ArrayList<>();
    
    try {
    conn = dbConnection.getConnection();
    stmt = conn.prepareStatement(SELECT_ALL);
    rs = stmt.executeQuery();
    
    while (rs.next()) {
    movies.add(mapResultSetToMovie(rs));
    }
    
    return movies;
    
    } catch (SQLException e) {
    throw new SQLException("Error al obtener todas las peliculas: " + e.getMessage(), e);
    } finally {
    closeResources(rs, stmt, null);
    }
    }
    
    @Override
    public List<Movie> findByTitle(String title) throws SQLException {
    if (title == null || title.trim().isEmpty()) {
    throw new IllegalArgumentException("Titulo no puede estar vacio");
    }
    
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    List<Movie> movies = new ArrayList<>();
    
    try {
    conn = dbConnection.getConnection();
    stmt = conn.prepareStatement(SELECT_BY_TITLE);
    stmt.setString(1, "%" + title.trim() + "%");
    
    rs = stmt.executeQuery();
    
    while (rs.next()) {
    movies.add(mapResultSetToMovie(rs));
    }
    
    return movies;
    
    } catch (SQLException e) {
    throw new SQLException("Error al buscar peliculas por titulo '" + title + "': " + e.getMessage(), e);
    } finally {
    closeResources(rs, stmt, null);
    }
    }
    
    @Override
    public List<Movie> findByDirector(String director) throws SQLException {
    if (director == null || director.trim().isEmpty()) {
    throw new IllegalArgumentException("Director no puede estar vacio");
    }
    
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    List<Movie> movies = new ArrayList<>();
    
    try {
    conn = dbConnection.getConnection();
    stmt = conn.prepareStatement(SELECT_BY_DIRECTOR);
    stmt.setString(1, "%" + director.trim() + "%");
    
    rs = stmt.executeQuery();
    
    while (rs.next()) {
    movies.add(mapResultSetToMovie(rs));
    }
    
    return movies;
    
    } catch (SQLException e) {
    throw new SQLException("Error al buscar peliculas por director '" + director + "': " + e.getMessage(), e);
    } finally {
    closeResources(rs, stmt, null);
    }
    }
    
    @Override
    public List<Movie> findByGenre(String genre) throws SQLException {
    if (genre == null || genre.trim().isEmpty()) {
    throw new IllegalArgumentException("Genero no puede estar vacio");
    }
    
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    List<Movie> movies = new ArrayList<>();
    
    try {
    conn = dbConnection.getConnection();
    stmt = conn.prepareStatement(SELECT_BY_GENRE);
    stmt.setString(1, genre.trim());
    
    rs = stmt.executeQuery();
    
    while (rs.next()) {
    movies.add(mapResultSetToMovie(rs));
    }
    
    return movies;
    
    } catch (SQLException e) {
    throw new SQLException("Error al buscar peliculas por genero '" + genre + "': " + e.getMessage(), e);
    } finally {
    closeResources(rs, stmt, null);
    }
    }
    
    @Override
    public List<Movie> findByYear(int year) throws SQLException {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    List<Movie> movies = new ArrayList<>();
    
    try {
    conn = dbConnection.getConnection();
    stmt = conn.prepareStatement(SELECT_BY_YEAR);
    stmt.setInt(1, year);
    
    rs = stmt.executeQuery();
    
    while (rs.next()) {
    movies.add(mapResultSetToMovie(rs));
    }
    
    return movies;
    
    } catch (SQLException e) {
    throw new SQLException("Error al buscar peliculas por año " + year + ": " + e.getMessage(), e);
    } finally {
    closeResources(rs, stmt, null);
    }
    }
    
    @Override
    public boolean update(Movie movie) throws SQLException {
    if (movie == null) {
    throw new IllegalArgumentException("Movie no puede ser null");
    }
    
    if (movie.getId() <= 0) {
    throw new IllegalArgumentException("ID de Movie debe ser positivo, recibido: " + movie.getId());
    }
    
    if (!movie.isValid()) {
    throw new IllegalArgumentException("Datos de Movie no validos: " + 
    movie.validate().getErrorMessages());
    }
    
    Connection conn = null;
    PreparedStatement stmt = null;
    
    try {
    conn = dbConnection.getConnection();
    stmt = conn.prepareStatement(UPDATE_MOVIE);
    
    // Establecer parametros
    stmt.setString(1, movie.getTitle());
    stmt.setString(2, movie.getDirector());
    stmt.setInt(3, movie.getYear());
    stmt.setInt(4, movie.getDuration());
    stmt.setString(5, movie.getGenre());
    stmt.setInt(6, movie.getId());
    
    int rowsAffected = stmt.executeUpdate();
    return rowsAffected > 0;
    
    } catch (SQLException e) {
    throw new SQLException("Error al actualizar pelicula ID " + movie.getId() + ": " + e.getMessage(), e);
    } finally {
    closeResources(null, stmt, null);
    }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
    if (id <= 0) {
    throw new IllegalArgumentException("ID debe ser positivo, recibido: " + id);
    }
    
    Connection conn = null;
    PreparedStatement stmt = null;
    
    try {
    conn = dbConnection.getConnection();
    stmt = conn.prepareStatement(DELETE_MOVIE);
    stmt.setInt(1, id);
    
    int rowsAffected = stmt.executeUpdate();
    return rowsAffected > 0;
    
    } catch (SQLException e) {
    throw new SQLException("Error al eliminar pelicula ID " + id + ": " + e.getMessage(), e);
    } finally {
    closeResources(null, stmt, null);
    }
    }
    
    @Override
    public boolean exists(int id) throws SQLException {
    if (id <= 0) {
    return false;
    }
    
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    try {
    conn = dbConnection.getConnection();
    stmt = conn.prepareStatement(EXISTS_QUERY);
    stmt.setInt(1, id);
    
    rs = stmt.executeQuery();
    return rs.next();
    
    } catch (SQLException e) {
    throw new SQLException("Error al verificar existencia de pelicula ID " + id + ": " + e.getMessage(), e);
    } finally {
    closeResources(rs, stmt, null);
    }
    }
    
    @Override
    public List<Movie> findByYearRange(int startYear, int endYear) throws SQLException {
    if (startYear > endYear) {
    throw new IllegalArgumentException("El año inicial debe ser menor o igual al año final");
    }
    
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    List<Movie> movies = new ArrayList<>();
    
    try {
    conn = dbConnection.getConnection();
    stmt = conn.prepareStatement(SELECT_BY_YEAR_RANGE);
    stmt.setInt(1, startYear);
    stmt.setInt(2, endYear);
    
    rs = stmt.executeQuery();
    
    while (rs.next()) {
    movies.add(mapResultSetToMovie(rs));
    }
    
    return movies;
    
    } catch (SQLException e) {
    throw new SQLException("Error al buscar peliculas por rango de años " + startYear + "-" + endYear + ": " + e.getMessage(), e);
    } finally {
    closeResources(rs, stmt, null);
    }
    }
    
    @Override
    public int count() throws SQLException {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    try {
    conn = dbConnection.getConnection();
    stmt = conn.prepareStatement(COUNT_QUERY);
    rs = stmt.executeQuery();
    
    if (rs.next()) {
    return rs.getInt("total");
    }
    
    return 0;
    
    } catch (SQLException e) {
    throw new SQLException("Error al contar peliculas: " + e.getMessage(), e);
    } finally {
    closeResources(rs, stmt, null);
    }
    }
    
    /**
    * Mapea un ResultSet a un objeto Movie.
    * Metodo de utilidad para convertir resultados de consulta.
    * 
    * @param rs ResultSet con datos de la consulta
    * @return Movie construido desde los datos
    * @throws SQLException si ocurre error al leer datos
    */
    private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
    try {
    return new Movie(
    rs.getInt("id"),
    rs.getString("titulo"),
    rs.getString("director"),
    rs.getInt("ano"),
    rs.getInt("duracion"),
    rs.getString("genero")
    );
    } catch (SQLException e) {
    throw new SQLException("Error al mapear ResultSet a Movie: " + e.getMessage(), e);
    }
    }
    
    /**
    * Cierra recursos de base de datos de forma segura.
    * Metodo de utilidad para limpiar recursos.
    * 
    * @param rs ResultSet a cerrar (puede ser null)
    * @param stmt Statement a cerrar (puede ser null) 
    * @param conn Connection a cerrar (puede ser null)
    */
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
    // Cerrar ResultSet
    if (rs != null) {
    try {
    rs.close();
    } catch (SQLException e) {
    System.err.println("Error al cerrar ResultSet: " + e.getMessage());
    }
    }
    
    // Cerrar Statement
    if (stmt != null) {
    try {
    stmt.close();
    } catch (SQLException e) {
    System.err.println("Error al cerrar Statement: " + e.getMessage());
    }
    }
    
    // NOTA: No cerrar Connection aqui porque DatabaseConnection maneja su ciclo de vida
    // La conexion se reutiliza a traves del patron Singleton en DatabaseConnection
    }
}