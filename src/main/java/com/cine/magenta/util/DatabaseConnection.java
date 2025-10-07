/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.util;

import com.cine.magenta.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utilidad para manejo de conexiones a la base de datos MySQL.
 * Implementa patron Singleton para garantizar conexion unica.
 * Proporciona metodos seguros para establecer y cerrar conexiones.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class DatabaseConnection {
    
    /** Instancia unica de la clase (patron Singleton) */
    private static DatabaseConnection instance;
    
    /** Pool de conexiones a la base de datos */
    private BlockingQueue<Connection> connectionPool;
    
    /** Conexiones activas en uso */
    private AtomicInteger activeConnections;
    
    /** Conexiones totales creadas */
    private AtomicInteger totalConnections;
    
    /** Indica si el driver JDBC fue cargado exitosamente */
    private static boolean driverLoaded = false;
    
    /** Tiempo maximo de espera para obtener una conexion (ms) */
    private static final long CONNECTION_TIMEOUT_MS = DatabaseConfig.CONNECTION_TIMEOUT;
    
    /**
    * Constructor privado para implementar patron Singleton.
    * Carga automaticamente el driver JDBC al crear la instancia.
    * Inicializa el pool de conexiones con el tamaño configurado.
    */
    private DatabaseConnection() {
    loadJdbcDriver();
    
    // Inicializar el pool de conexiones
    connectionPool = new ArrayBlockingQueue<>(DatabaseConfig.MAX_CONNECTIONS);
    activeConnections = new AtomicInteger(0);
    totalConnections = new AtomicInteger(0);
    
    System.out.println("Pool de conexiones inicializado. Capacidad maxima: " + 
    DatabaseConfig.MAX_CONNECTIONS);
    }
    
    /**
    * Obtiene la instancia unica de DatabaseConnection.
    * Implementa patron Singleton con sincronizacion para thread safety.
    * 
    * @return instancia unica de DatabaseConnection
    */
    public static synchronized DatabaseConnection getInstance() {
    if (instance == null) {
    instance = new DatabaseConnection();
    }
    return instance;
    }
    
    /**
    * Carga el driver JDBC de MySQL en memoria.
    * Solo se ejecuta una vez durante el ciclo de vida de la aplicacion.
    */
    private void loadJdbcDriver() {
    if (!driverLoaded) {
    try {
    Class.forName(DatabaseConfig.JDBC_DRIVER);
    driverLoaded = true;
    System.out.println("Driver JDBC MySQL cargado correctamente");
    } catch (ClassNotFoundException e) {
    System.err.println("ERROR: Driver JDBC MySQL no encontrado");
    System.err.println("Verificar que mysql-connector-j este en el classpath");
    e.printStackTrace();
    
    showErrorDialog("Driver JDBC no encontrado", 
    "Error critico: Driver JDBC MySQL no encontrado\n" +
    "Verificar que mysql-connector-j este en las librerias del proyecto");
    }
    }
    }
    
    /**
    * Obtiene una conexion del pool de conexiones.
    * Si el pool esta vacio y no se ha alcanzado el limite, crea una nueva conexion.
    * Si el pool esta vacio y se alcanzo el limite, espera hasta que haya una disponible.
    * 
    * @return Connection objeto de conexion activa
    * @throws SQLException si ocurre error en la conexion
    */
    public Connection getConnection() throws SQLException {
    Connection conn = null;
    
    try {
    // Intentar obtener una conexion del pool
    conn = connectionPool.poll();
    
    if (conn == null || conn.isClosed()) {
    // No hay conexiones disponibles en el pool
    
    if (totalConnections.get() < DatabaseConfig.MAX_CONNECTIONS) {
    // Podemos crear una nueva conexion
    conn = createNewConnection();
    totalConnections.incrementAndGet();
    System.out.println("Nueva conexion creada. Total: " + totalConnections.get() + 
    " / " + DatabaseConfig.MAX_CONNECTIONS);
    } else {
    // Esperar a que se libere una conexion
    System.out.println("Pool de conexiones lleno. Esperando conexion disponible...");
    try {
    conn = connectionPool.poll(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    
    if (conn == null) {
    throw new SQLException("Timeout al esperar conexion disponible");
    }
    
    // Verificar si la conexion obtenida es valida
    if (conn.isClosed() || !conn.isValid(2)) {
    conn.close();
    conn = createNewConnection();
    System.out.println("Conexion invalida reemplazada");
    }
    } catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    throw new SQLException("Interrupcion al esperar conexion", e);
    }
    }
    }
    
    // Incrementar contador de conexiones activas
    activeConnections.incrementAndGet();
    
    return conn;
    
    } catch (SQLException e) {
    handleConnectionError(e);
    throw e;
    }
    }
    
    /**
    * Crea una nueva conexion a la base de datos.
    * 
    * @return Connection nueva conexion
    * @throws SQLException si ocurre error al crear la conexion
    */
    private Connection createNewConnection() throws SQLException {
    Connection conn = DriverManager.getConnection(
    DatabaseConfig.DB_URL,
    DatabaseConfig.DB_USERNAME,
    DatabaseConfig.DB_PASSWORD
    );
    
    if (conn != null) {
    // Configurar la conexion para que no se cierre automaticamente
    conn.setAutoCommit(true);
    System.out.println("Conexion exitosa a base de datos: " + DatabaseConfig.DB_NAME);
    }
    
    return conn;
    }
    
    /**
    * Maneja errores de conexion mostrando informacion detallada.
    * Proporciona diagnosticos y sugerencias para resolver problemas.
    * 
    * @param e excepcion SQL ocurrida
    */
    private void handleConnectionError(SQLException e) {
    System.err.println("ERROR de conexion a la base de datos");
    System.err.println("Codigo de error SQL: " + e.getErrorCode());
    System.err.println("Mensaje: " + e.getMessage());
    System.err.println("Estado SQL: " + e.getSQLState());
    
    System.err.println("\nVerificaciones necesarias:");
    System.err.println("- Servidor MySQL ejecutandose");
    System.err.println("- Base de datos '" + DatabaseConfig.DB_NAME + "' existe");
    System.err.println("- Credenciales correctas");
    System.err.println("- Firewall/puertos configurados");
    System.err.println("- URL: " + DatabaseConfig.DB_URL);
    
    String errorMessage = String.format(
    "Error de conexion a la base de datos\n\n" +
    "Codigo: %d\n" +
    "Mensaje: %s\n\n" +
    "Verificaciones necesarias:\n" +
    "• Servidor MySQL ejecutandose\n" +
    "• Base de datos '%s' existe\n" +
    "• Usuario y contraseña correctos\n" +
    "• Configuracion de red/firewall",
    e.getErrorCode(), e.getMessage(), DatabaseConfig.DB_NAME
    );
    
    showErrorDialog("Error de Conexion", errorMessage);
    }
    
    /**
    * Devuelve una conexion al pool para su reutilizacion.
    * Si la conexion no es valida, la cierra y reduce el contador total.
    * 
    * @param conn conexion a devolver al pool
    */
    public void releaseConnection(Connection conn) {
    if (conn == null) {
    return;
    }
    
    try {
    // Verificar si la conexion es valida
    if (conn.isClosed() || !conn.isValid(2)) {
    // Cerrar la conexion invalida
    try {
    conn.close();
    } catch (SQLException e) {
    System.err.println("Error al cerrar conexion invalida: " + e.getMessage());
    }
    
    // Reducir contador de conexiones totales
    totalConnections.decrementAndGet();
    System.out.println("Conexion invalida cerrada. Total: " + totalConnections.get());
    } else {
    // Devolver la conexion al pool
    boolean added = connectionPool.offer(conn);
    if (!added) {
    // Si el pool esta lleno, cerrar la conexion
    conn.close();
    totalConnections.decrementAndGet();
    System.out.println("Pool lleno, conexion cerrada. Total: " + totalConnections.get());
    }
    }
    
    // Reducir contador de conexiones activas
    activeConnections.decrementAndGet();
    
    } catch (SQLException e) {
    System.err.println("Error al liberar conexion: " + e.getMessage());
    e.printStackTrace();
    }
    }
    
    /**
    * Cierra todas las conexiones del pool.
    * Utilizado al finalizar la aplicacion.
    */
    public void closeAllConnections() {
    System.out.println("Cerrando todas las conexiones...");
    
    // Cerrar todas las conexiones en el pool
    Connection conn;
    while ((conn = connectionPool.poll()) != null) {
    try {
    if (!conn.isClosed()) {
    conn.close();
    }
    } catch (SQLException e) {
    System.err.println("Error al cerrar conexion: " + e.getMessage());
    }
    }
    
    // Reiniciar contadores
    totalConnections.set(0);
    activeConnections.set(0);
    
    System.out.println("Todas las conexiones cerradas correctamente");
    }
    
    /**
    * Cierra la conexion actual.
    * Alias para mantener compatibilidad con codigo existente.
    * Redirige a closeAllConnections() para cerrar todas las conexiones del pool.
    */
    public void closeConnection() {
    System.out.println("Cerrando conexion...");
    closeAllConnections();
    }
    
    /**
    * Verifica si el pool de conexiones esta activo.
    * 
    * @return true si hay al menos una conexion activa o disponible
    */
    public boolean isConnected() {
    return totalConnections.get() > 0;
    }
    
    /**
    * Obtiene el numero de conexiones activas (en uso).
    * 
    * @return numero de conexiones activas
    */
    public int getActiveConnectionCount() {
    return activeConnections.get();
    }
    
    /**
    * Obtiene el numero total de conexiones creadas.
    * 
    * @return numero total de conexiones
    */
    public int getTotalConnectionCount() {
    return totalConnections.get();
    }
    
    /**
    * Obtiene el numero de conexiones disponibles en el pool.
    * 
    * @return numero de conexiones disponibles
    */
    public int getAvailableConnectionCount() {
    return connectionPool.size();
    }
    
    /**
    * Ejecuta una prueba completa de conexion.
    * Incluye verificacion de metadatos y estado de la base de datos.
    * 
    * @return true si la prueba es exitosa
    */
    public boolean testConnection() {
    Connection testConn = null;
    try {
    testConn = getConnection();
    if (testConn != null) {
    System.out.println("Prueba de conexion exitosa");
    System.out.println("Base de datos activa: " + testConn.getCatalog());
    System.out.println("URL de conexion: " + testConn.getMetaData().getURL());
    System.out.println("Usuario conectado: " + testConn.getMetaData().getUserName());
    System.out.println("Estado del pool: " + getPoolStatus());
    return true;
    }
    } catch (SQLException e) {
    System.err.println("Prueba de conexion fallida: " + e.getMessage());
    return false;
    } finally {
    // Devolver la conexion al pool
    if (testConn != null) {
    releaseConnection(testConn);
    }
    }
    return false;
    }
    
    /**
    * Obtiene un resumen del estado actual del pool de conexiones.
    * 
    * @return String con informacion del estado del pool
    */
    public String getPoolStatus() {
    return String.format(
    "Conexiones activas: %d, Disponibles: %d, Total: %d, Capacidad maxima: %d",
    getActiveConnectionCount(),
    getAvailableConnectionCount(),
    getTotalConnectionCount(),
    DatabaseConfig.MAX_CONNECTIONS
    );
    }
    
    /**
    * Obtiene informacion detallada del estado del pool de conexiones.
    * Utilizado para debugging y monitoreo del sistema.
    * 
    * @return String con informacion detallada de las conexiones
    */
    public String getConnectionInfo() {
    try {
    if (isConnected()) {
    // Obtener una conexion para leer metadatos
    Connection conn = null;
    try {
    conn = getConnection();
    
    String info = String.format(
    "Estado: Conectado\n" +
    "Base de datos: %s\n" +
    "URL: %s\n" +
    "Usuario: %s\n" +
    "Driver: %s\n\n" +
    "Pool de conexiones:\n" +
    "- Conexiones activas: %d\n" +
    "- Conexiones disponibles: %d\n" +
    "- Total conexiones: %d\n" +
    "- Capacidad maxima: %d",
    conn.getCatalog(),
    conn.getMetaData().getURL(),
    conn.getMetaData().getUserName(),
    conn.getMetaData().getDriverName(),
    getActiveConnectionCount(),
    getAvailableConnectionCount(),
    getTotalConnectionCount(),
    DatabaseConfig.MAX_CONNECTIONS
    );
    
    return info;
    } finally {
    // Devolver la conexion al pool
    if (conn != null) {
    releaseConnection(conn);
    }
    }
    } else {
    return "Estado: Desconectado\n" + DatabaseConfig.getConfigInfo();
    }
    } catch (SQLException e) {
    return "Error al obtener informacion: " + e.getMessage();
    }
    }
    
    /**
    * Muestra ventanas de dialogo de error al usuario.
    * Centraliza el manejo de mensajes de error de la interfaz.
    * 
    * @param title titulo de la ventana
    * @param message mensaje de error detallado
    */
    private void showErrorDialog(String title, String message) {
    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
    * Metodo principal para pruebas de conexion.
    * Cumple con los requisitos de evaluacion de la Semana 6.
    * Ejecuta prueba completa y muestra resultados al usuario.
    */
    public static void main(String[] args) {
    System.out.println("=== PRUEBA DE CONEXION - CINE MAGENTA ===");
    System.out.println(DatabaseConfig.getConfigInfo());
    System.out.println("====");
    
    DatabaseConnection dbConnection = DatabaseConnection.getInstance();
    boolean connectionSuccess = dbConnection.testConnection();
    
    if (connectionSuccess) {
    // Mostrar mensaje de exito con informacion detallada
    String successMessage = String.format(
    "CONEXION EXITOSA\n\n" +
    "%s v%s\n" +
    "Conectado exitosamente a la base de datos\n\n" +
    "Detalles de conexion:\n" +
    "Base de datos: %s\n" +
    "Servidor: %s:%s\n" +
    "Usuario: %s\n\n" +
    "Pool de conexiones inicializado\n" +
    "Capacidad maxima: %d conexiones",
    DatabaseConfig.APP_NAME, DatabaseConfig.APP_VERSION,
    DatabaseConfig.DB_NAME,
    DatabaseConfig.DB_HOST, DatabaseConfig.DB_PORT,
    DatabaseConfig.DB_USERNAME,
    DatabaseConfig.MAX_CONNECTIONS
    );
    
    JOptionPane.showMessageDialog(null, successMessage,
    "Conexion Exitosa - Cine Magenta", JOptionPane.INFORMATION_MESSAGE);
    
    System.out.println("Informacion de conexion:");
    System.out.println(dbConnection.getConnectionInfo());
    
    // Prueba de multiples conexiones
    System.out.println("\n=== PRUEBA DE POOL DE CONEXIONES ===");
    testConnectionPool(dbConnection);
    
    } else {
    System.err.println("No se pudo establecer conexion con la base de datos");
    JOptionPane.showMessageDialog(null,
    "ERROR DE CONEXION\n\n" +
    "No se pudo conectar a la base de datos\n" +
    "Revisar configuracion en DatabaseConfig.java\n\n" +
    "Verificar que MySQL este ejecutandose\n" +
    "y que la base de datos 'Cine_DB' exista",
    "Error de Conexion", JOptionPane.ERROR_MESSAGE);
    }
    
    // Limpiar recursos y finalizar
    dbConnection.closeAllConnections();
    System.out.println("=== FIN DE PRUEBA ===");
    System.exit(0);
    }
    
    /**
    * Prueba el funcionamiento del pool de conexiones.
    * Crea multiples conexiones y las libera para verificar la reutilizacion.
    * 
    * @param dbConnection instancia de DatabaseConnection
    */
    private static void testConnectionPool(DatabaseConnection dbConnection) {
    try {
    // Crear varias conexiones
    Connection[] connections = new Connection[5];
    
    System.out.println("Obteniendo 5 conexiones del pool...");
    for (int i = 0; i < connections.length; i++) {
    connections[i] = dbConnection.getConnection();
    System.out.println("Conexion #" + (i+1) + " obtenida. " + dbConnection.getPoolStatus());
    }
    
    // Liberar conexiones
    System.out.println("\nLiberando conexiones...");
    for (int i = 0; i < connections.length; i++) {
    dbConnection.releaseConnection(connections[i]);
    System.out.println("Conexion #" + (i+1) + " liberada. " + dbConnection.getPoolStatus());
    }
    
    System.out.println("\nPrueba de pool de conexiones completada exitosamente");
    
    } catch (SQLException e) {
    System.err.println("Error en prueba de pool: " + e.getMessage());
    }
    }
}