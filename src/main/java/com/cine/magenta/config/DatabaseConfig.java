/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.config;

/**
 * Configuracion centralizada de la base de datos y parametros del sistema.
 * Contiene todas las constantes necesarias para la conexion y validaciones.
 * Implementa el patron de configuracion centralizada para el sistema de cartelera.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class DatabaseConfig {
    
    /** Host del servidor de base de datos */
    public static final String DB_HOST = "localhost";
    
    /** Puerto del servidor MySQL */
    public static final String DB_PORT = "3306";
    
    /** Nombre de la base de datos */
    public static final String DB_NAME = "Cine_DB";
    
    /** URL completa de conexion a la base de datos */
    public static final String DB_URL = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC", 
                                                      DB_HOST, DB_PORT, DB_NAME);
    
    /** Nombre de usuario para la conexion - MODIFICAR segun configuracion local */
    public static final String DB_USERNAME = "root";
    
    /** Contraseña de la base de datos - MODIFICAR segun configuracion local */
    public static final String DB_PASSWORD = "Mono2025@@@@";
    
    /** Driver JDBC para MySQL */
    public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    /** Numero maximo de conexiones simultaneas */
    public static final int MAX_CONNECTIONS = 10;
    
    /** Timeout de conexion en milisegundos */
    public static final int CONNECTION_TIMEOUT = 30000;
    
    /** Nombre de la aplicacion */
    public static final String APP_NAME = "Sistema Cine Magenta";
    
    /** Version actual de la aplicacion */
    public static final String APP_VERSION = "1.0";
    
    /** Descripcion de la aplicacion */
    public static final String APP_DESCRIPTION = "Sistema de Gestion de Cartelera";
    
    /** Longitud maxima para el titulo de pelicula */
    public static final int MAX_TITLE_LENGTH = 150;
    
    /** Longitud maxima para el nombre del director */
    public static final int MAX_DIRECTOR_LENGTH = 50;
    
    /** Año minimo valido para peliculas */
    public static final int MIN_YEAR = 1800;
    
    /** Año maximo valido para peliculas */
    public static final int MAX_YEAR = 2100;
    
    /** Duracion minima en minutos */
    public static final int MIN_DURATION = 1;
    
    /** Duracion maxima en minutos */
    public static final int MAX_DURATION = 500;
    
    /** Array de generos cinematograficos disponibles */
    public static final String[] AVAILABLE_GENRES = {
        "Comedia", "Drama", "Accion", "Terror", 
        "Ciencia Ficcion", "Romance", "Aventura", "Animacion"
    };
    
    /**
     * Valida si la configuracion actual es correcta.
     * Verifica que todos los parametros necesarios esten definidos.
     * 
     * @return true si la configuracion es valida
     */
    public static boolean isConfigValid() {
        return DB_HOST != null && !DB_HOST.trim().isEmpty() &&
               DB_NAME != null && !DB_NAME.trim().isEmpty() &&
               DB_USERNAME != null &&
               JDBC_DRIVER != null && !JDBC_DRIVER.trim().isEmpty();
    }
    
    /**
     * Obtiene informacion detallada de la configuracion actual.
     * Utilizado para debugging y logs del sistema.
     * 
     * @return String con informacion de configuracion
     */
    public static String getConfigInfo() {
        return String.format(
            "%s v%s\n" +
            "Base de datos: %s\n" +
            "Host: %s:%s\n" +
            "Usuario: %s\n" +
            "Driver: %s",
            APP_NAME, APP_VERSION,
            DB_NAME,
            DB_HOST, DB_PORT,
            DB_USERNAME,
            JDBC_DRIVER
        );
    }
    
    /**
     * Constructor privado para evitar instanciacion.
     * Esta clase contiene solo metodos y constantes estaticas.
     */
    private DatabaseConfig() {
        // Clase de utilidad - no debe ser instanciada
    }
}