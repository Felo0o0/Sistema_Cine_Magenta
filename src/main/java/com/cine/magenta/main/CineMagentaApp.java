/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.cine.magenta.main;

import com.cine.magenta.config.DatabaseConfig;
import com.cine.magenta.util.DatabaseConnection;
import com.cine.magenta.view.MainFrame;
import javax.swing.*;
import java.awt.*;

/**
 * Aplicacion principal del Sistema Cine Magenta.
 * Punto de entrada con configuracion inicial y manejo de errores.
 * Inicializa todos los componentes necesarios del sistema.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class CineMagentaApp {
    
    /** Titulo de la ventana de splash */
    private static final String SPLASH_TITLE = "Iniciando " + DatabaseConfig.APP_NAME;
    
    /**
     * Metodo principal de la aplicacion.
     * 
     * @param args argumentos de linea de comandos
     */
    public static void main(String[] args) {
        // Configurar el Look and Feel del sistema
        configureLookAndFeel();
        
        // Mostrar splash screen
        showSplashScreen();
        
        // Validar configuracion
        if (!validateConfiguration()) {
            showErrorAndExit("Error de Configuracion", 
                "La configuracion de la aplicacion no es valida.\n" +
                "Verificar parametros en DatabaseConfig.java");
            return;
        }
        
        // Probar conexion a la base de datos
        if (!testDatabaseConnection()) {
            showErrorAndExit("Error de Conexion", 
                "No se pudo conectar a la base de datos.\n" +
                "Verificar que MySQL este ejecutandose y\n" +
                "que la base de datos 'Cine_DB' exista.");
            return;
        }
        
        // Iniciar la aplicacion principal
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                
                // Mostrar mensaje de bienvenida
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(mainFrame,
                        "Bienvenido al " + DatabaseConfig.APP_NAME + "\n" +
                        "Sistema inicializado correctamente\n" +
                        "Version: " + DatabaseConfig.APP_VERSION,
                        "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
                });
                
            } catch (Exception e) {
                showErrorAndExit("Error al Inicializar",
                    "Error inesperado al inicializar la aplicacion:\n" + e.getMessage());
            }
        });
    }
    
    /**
     * Configura el Look and Feel del sistema operativo.
     * Mejora la apariencia visual de la aplicacion.
     */
    private static void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("Look and Feel del sistema configurado");
        } catch (Exception e) {
            System.err.println("No se pudo establecer el Look and Feel del sistema");
            System.err.println("Utilizando Look and Feel por defecto");
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra una pantalla de splash durante la inicializacion.
     * Proporciona feedback visual al usuario.
     */
    private static void showSplashScreen() {
        JWindow splash = null;
        
        try {
            splash = new JWindow();
            splash.getContentPane().setLayout(new BorderLayout());
            
            // Panel principal con informacion
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(new Color(128, 0, 128)); // Color magenta
            panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            
            // Titulo
            JLabel titleLabel = new JLabel(DatabaseConfig.APP_NAME);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setForeground(Color.WHITE);
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(titleLabel, gbc);
            
            // Version
            JLabel versionLabel = new JLabel("Version " + DatabaseConfig.APP_VERSION);
            versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            versionLabel.setForeground(Color.WHITE);
            gbc.gridy = 1;
            panel.add(versionLabel, gbc);
            
            // Mensaje de carga
            JLabel loadingLabel = new JLabel("Inicializando sistema...");
            loadingLabel.setFont(new Font("Arial", Font.ITALIC, 10));
            loadingLabel.setForeground(Color.WHITE);
            gbc.gridy = 2;
            panel.add(loadingLabel, gbc);
            
            splash.add(panel);
            splash.pack();
            splash.setLocationRelativeTo(null);
            splash.setVisible(true);
            
            // Mostrar por 2 segundos
            Thread.sleep(2000);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Splash screen interrumpido");
        } catch (Exception e) {
            System.err.println("Error al mostrar splash screen: " + e.getMessage());
        } finally {
            if (splash != null) {
                splash.setVisible(false);
                splash.dispose();
            }
        }
    }
    
    /**
     * Valida la configuracion de la aplicacion.
     * 
     * @return true si la configuracion es valida
     */
    private static boolean validateConfiguration() {
        try {
            boolean configValid = DatabaseConfig.isConfigValid();
            
            if (!configValid) {
                System.err.println("ERROR: Configuracion no valida");
                System.err.println("Verificar parametros en DatabaseConfig.java");
                return false;
            } else {
                System.out.println("Configuracion validada correctamente");
                System.out.println(DatabaseConfig.getConfigInfo());
                return true;
            }
            
        } catch (Exception e) {
            System.err.println("Error al validar configuracion: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Prueba la conexion a la base de datos.
     * 
     * @return true si la conexion es exitosa
     */
    private static boolean testDatabaseConnection() {
        System.out.println("Probando conexion a la base de datos...");
        
        try {
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            boolean connectionSuccess = dbConnection.testConnection();
            
            if (connectionSuccess) {
                System.out.println("Conexion a base de datos exitosa");
                dbConnection.closeConnection();
                return true;
            } else {
                System.err.println("Fallo la conexion a la base de datos");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error al probar conexion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Muestra un mensaje de error y termina la aplicacion.
     * 
     * @param title titulo del dialogo de error
     * @param message mensaje de error detallado
     */
    private static void showErrorAndExit(String title, String message) {
        System.err.println("ERROR CRITICO: " + title);
        System.err.println(message);
        
        try {
            // Intentar mostrar dialogo grafico
            JOptionPane.showMessageDialog(null, message, title + " - " + DatabaseConfig.APP_NAME, 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("No se pudo mostrar dialogo de error: " + e.getMessage());
        }
        
        System.err.println("La aplicacion se cerrara...");
        System.exit(1);
    }
}