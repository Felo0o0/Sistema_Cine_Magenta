/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.dao;

import com.cine.magenta.model.Movie;
import com.cine.magenta.util.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Make the entire test class lenient
class MovieDAOImplTest {

    @Mock
    private DatabaseConnection dbConnection;
    
    @Mock
    private Connection connection;
    
    @Mock
    private PreparedStatement preparedStatement;
    
    @Mock
    private ResultSet resultSet;
    
    private MovieDAOImpl movieDAO;
    private Movie testMovie;
    
    @BeforeEach
    void setUp() throws SQLException {
        // Setup common mocks with lenient stubs
        when(dbConnection.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        
        movieDAO = new MovieDAOImpl(dbConnection);
        
        // Create a test movie for use in tests with a valid genre from the available list
        testMovie = new Movie("Test Movie", "Test Director", 2023, 120, "Accion");
        testMovie.setId(1);
    }
    
    @Test
    void testCreate() throws SQLException {
        // Setup
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);
        
        // Execute
        int result = movieDAO.create(testMovie);
        
        // Verify
        assertEquals(1, result);
        verify(preparedStatement).setString(1, testMovie.getTitle());
        verify(preparedStatement).setString(2, testMovie.getDirector());
        verify(preparedStatement).setInt(3, testMovie.getYear());
        verify(preparedStatement).setInt(4, testMovie.getDuration());
        verify(preparedStatement).setString(5, testMovie.getGenre());
        verify(preparedStatement).executeUpdate();
    }
    
    @Test
    void testCreateNullMovie() {
        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () -> movieDAO.create(null));
    }
    
    @Test
    void testCreateInvalidMovie() {
        // Setup
        Movie invalidMovie = new Movie();
        // Movie without required fields
        
        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () -> movieDAO.create(invalidMovie));
    }
    
    @Test
    void testFindById() throws SQLException {
        // Setup
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("id")).thenReturn(testMovie.getId());
        when(resultSet.getString("titulo")).thenReturn(testMovie.getTitle());
        when(resultSet.getString("director")).thenReturn(testMovie.getDirector());
        when(resultSet.getInt("ano")).thenReturn(testMovie.getYear());
        when(resultSet.getInt("duracion")).thenReturn(testMovie.getDuration());
        when(resultSet.getString("genero")).thenReturn(testMovie.getGenre());
        
        // Execute
        Movie result = movieDAO.findById(1);
        
        // Verify
        assertNotNull(result);
        assertEquals(testMovie.getId(), result.getId());
        assertEquals(testMovie.getTitle(), result.getTitle());
        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).executeQuery();
    }
    
    @Test
    void testFindByIdNotFound() throws SQLException {
        // Setup
        when(resultSet.next()).thenReturn(false);
        
        // Execute
        Movie result = movieDAO.findById(999);
        
        // Verify
        assertNull(result);
        verify(preparedStatement).setInt(1, 999);
        verify(preparedStatement).executeQuery();
    }
    
    @Test
    void testFindByIdInvalidId() {
        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () -> movieDAO.findById(-1));
    }
    
    @Test
    void testFindAll() throws SQLException {
        // Setup
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getInt("id")).thenReturn(1).thenReturn(2);
        when(resultSet.getString("titulo")).thenReturn("Movie 1").thenReturn("Movie 2");
        when(resultSet.getString("director")).thenReturn("Director 1").thenReturn("Director 2");
        when(resultSet.getInt("ano")).thenReturn(2023).thenReturn(2022);
        when(resultSet.getInt("duracion")).thenReturn(120).thenReturn(90);
        when(resultSet.getString("genero")).thenReturn("Accion").thenReturn("Comedia");
        
        // Execute
        List<Movie> movies = movieDAO.findAll();
        
        // Verify
        assertEquals(2, movies.size());
        assertEquals("Movie 1", movies.get(0).getTitle());
        assertEquals("Movie 2", movies.get(1).getTitle());
        verify(preparedStatement).executeQuery();
    }
    
    @Test
    void testUpdate() throws SQLException {
        // Setup
        when(preparedStatement.executeUpdate()).thenReturn(1);
        
        // Execute
        boolean result = movieDAO.update(testMovie);
        
        // Verify
        assertTrue(result);
        verify(preparedStatement).setString(1, testMovie.getTitle());
        verify(preparedStatement).setString(2, testMovie.getDirector());
        verify(preparedStatement).setInt(3, testMovie.getYear());
        verify(preparedStatement).setInt(4, testMovie.getDuration());
        verify(preparedStatement).setString(5, testMovie.getGenre());
        verify(preparedStatement).setInt(6, testMovie.getId());
        verify(preparedStatement).executeUpdate();
    }
    
    @Test
    void testDelete() throws SQLException {
        // Setup
        when(preparedStatement.executeUpdate()).thenReturn(1);
        
        // Execute
        boolean result = movieDAO.delete(1);
        
        // Verify
        assertTrue(result);
        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).executeUpdate();
    }
    
    @Test
    void testExists() throws SQLException {
        // Setup
        when(resultSet.next()).thenReturn(true);
        
        // Execute
        boolean result = movieDAO.exists(1);
        
        // Verify
        assertTrue(result);
        verify(preparedStatement).setInt(1, 1);
        verify(preparedStatement).executeQuery();
    }
    
    @Test
    void testCount() throws SQLException {
        // Setup
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("total")).thenReturn(10);
        
        // Execute
        int count = movieDAO.count();
        
        // Verify
        assertEquals(10, count);
        verify(preparedStatement).executeQuery();
    }
}