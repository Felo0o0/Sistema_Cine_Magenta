/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta;

import com.cine.magenta.dao.MovieDAO;
import com.cine.magenta.dao.MovieDAOImpl;
import com.cine.magenta.model.Movie;
import com.cine.magenta.util.DatabaseConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntegrationTest {

    private MovieDAO movieDAO;
    private DatabaseConnection dbConnection;
    
    @BeforeAll
    void setUp() {
        dbConnection = DatabaseConnection.getInstance();
        movieDAO = new MovieDAOImpl();
    }
    
    @AfterAll
    void tearDown() {
        // Close all connections in the pool
        dbConnection.closeAllConnections();
    }
    
    @Test
    void testDatabaseConnection() {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        } finally {
            // Important: Always release the connection back to the pool
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
    
    @Test
    void testMovieCount() {
        Connection conn = null;
        try {
            // Get a connection for this test
            conn = dbConnection.getConnection();
            
            // Use the connection through the DAO
            int count = movieDAO.count();
            
            // Just verify we can get a count (could be 0 in a test database)
            assertTrue(count >= 0);
        } catch (SQLException e) {
            fail("Failed to count movies: " + e.getMessage());
        } finally {
            // Important: Always release the connection back to the pool
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
    
    @Test
    void testMovieCRUD() {
        Connection conn = null;
        try {
            // Get a connection for this test
            conn = dbConnection.getConnection();
            
            // Create a test movie with valid genre
            Movie testMovie = new Movie("Test Integration Movie", "Test Director", 2023, 120, "Accion");
            
            // Test create
            int id = movieDAO.create(testMovie);
            assertTrue(id > 0, "Movie creation should return a valid ID");
            testMovie.setId(id);
            
            // Test findById
            Movie foundMovie = movieDAO.findById(id);
            assertNotNull(foundMovie, "Should find the movie by ID");
            assertEquals(testMovie.getTitle(), foundMovie.getTitle());
            
            // Test update
            testMovie.setTitle("Updated Test Movie");
            boolean updateResult = movieDAO.update(testMovie);
            assertTrue(updateResult, "Update should be successful");
            
            // Verify update
            Movie updatedMovie = movieDAO.findById(id);
            assertEquals("Updated Test Movie", updatedMovie.getTitle());
            
            // Test delete
            boolean deleteResult = movieDAO.delete(id);
            assertTrue(deleteResult, "Delete should be successful");
            
            // Verify delete
            Movie deletedMovie = movieDAO.findById(id);
            assertNull(deletedMovie, "Movie should be deleted");
            
        } catch (SQLException e) {
            fail("CRUD operations failed: " + e.getMessage());
        } finally {
            // Important: Always release the connection back to the pool
            if (conn != null) {
                dbConnection.releaseConnection(conn);
            }
        }
    }
}