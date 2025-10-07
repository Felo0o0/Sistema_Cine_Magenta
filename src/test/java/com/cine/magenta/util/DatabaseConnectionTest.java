/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.util;

import com.cine.magenta.config.DatabaseConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para DatabaseConnection")
public class DatabaseConnectionTest {
    
    // Método para determinar si se deben ejecutar las pruebas de integración
    // Esto evita fallos en entornos sin base de datos configurada
    static boolean isDatabaseConfigured() {
        return DatabaseConfig.isConfigValid() && 
               !DatabaseConfig.DB_USERNAME.equals("root") || 
               !DatabaseConfig.DB_PASSWORD.equals("Mono2025@@@@");
    }
    
    @Test
    @DisplayName("Debe implementar patrón Singleton correctamente")
    void testSingletonPattern() {
        // Obtener dos instancias
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        DatabaseConnection instance2 = DatabaseConnection.getInstance();
        
        // Verificar que son la misma instancia
        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }
    
    @Test
    @EnabledIf("isDatabaseConfigured")
    @DisplayName("Debe obtener conexión correctamente")
    void testGetConnection() {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        
        try {
            // Obtener conexión
            Connection conn = dbConnection.getConnection();
            
            // Verificar que la conexión es válida
            assertNotNull(conn);
            assertTrue(conn.isValid(1));
            
            // Liberar conexión
            dbConnection.releaseConnection(conn);
        } catch (SQLException e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }
    
    @Test
    @EnabledIf("isDatabaseConfigured")
    @DisplayName("Debe gestionar múltiples conexiones correctamente")
    void testMultipleConnections() {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        
        try {
            // Obtener varias conexiones
            Connection[] connections = new Connection[5];
            for (int i = 0; i < connections.length; i++) {
                connections[i] = dbConnection.getConnection();
                assertNotNull(connections[i]);
                assertTrue(connections[i].isValid(1));
            }
            
            // Verificar contadores
            assertTrue(dbConnection.getActiveConnectionCount() >= connections.length);
            
            // Liberar conexiones
            for (Connection conn : connections) {
                dbConnection.releaseConnection(conn);
            }
            
        } catch (SQLException e) {
            fail("No debería lanzar excepción: " + e.getMessage());
        }
    }
    
    @Test
    @EnabledIf("isDatabaseConfigured")
    @DisplayName("Debe probar conexión correctamente")
    void testTestConnection() {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        
        // Probar conexión
        boolean result = dbConnection.testConnection();
        
        // Verificar resultado
        assertTrue(result);
    }
    
    @Test
    @DisplayName("Debe obtener información de pool correctamente")
    void testGetPoolStatus() {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        
        // Obtener estado del pool
        String poolStatus = dbConnection.getPoolStatus();
        
        // Verificar que contiene información básica
        assertNotNull(poolStatus);
        assertTrue(poolStatus.contains("Conexiones activas"));
        assertTrue(poolStatus.contains("Disponibles"));
        assertTrue(poolStatus.contains("Total"));
        assertTrue(poolStatus.contains("Capacidad maxima"));
    }
    
    @Test
    @DisplayName("Debe obtener información de conexión correctamente")
    void testGetConnectionInfo() {
        DatabaseConnection dbConnection = DatabaseConnection.getInstance();
        
        // Obtener información de conexión
        String connectionInfo = dbConnection.getConnectionInfo();
        
        // Verificar que contiene información básica
        assertNotNull(connectionInfo);
        
        // La información puede variar dependiendo del estado de la conexión
        if (dbConnection.isConnected()) {
            assertTrue(connectionInfo.contains("Estado: Conectado"));
        } else {
            assertTrue(connectionInfo.contains("Estado: Desconectado"));
        }
    }
}