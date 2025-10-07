/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.view;

import com.cine.magenta.config.DatabaseConfig;
import com.cine.magenta.util.DatabaseConnection;
import com.cine.magenta.util.MessageUtil;
import com.cine.magenta.controller.MovieController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Ventana principal del sistema de gestion de cartelera.
 * Contiene la interfaz principal con menu y toolbar.
 * Coordina la navegacion entre diferentes funcionalidades.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class MainFrame extends JFrame {
    
    /** Controlador para operaciones con peliculas */
    private final MovieController movieController;
    
    /** Barra de menu principal */
    private JMenuBar menuBar;
    
    /** Barra de herramientas */
    private JToolBar toolBar;
    
    /** Etiqueta de estado en la parte inferior */
    private JLabel statusLabel;
    
    /** Panel central para mostrar contenido */
    private JPanel centralPanel;
    
    /**
    * Constructor que inicializa la ventana principal.
    */
    public MainFrame() {
        this.movieController = new MovieController();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        testDatabaseConnection();
    }
    
    /**
    * Inicializa todos los componentes de la interfaz.
    */
    private void initializeComponents() {
        // Configuracion basica de la ventana
        setTitle(DatabaseConfig.APP_NAME + " v" + DatabaseConfig.APP_VERSION);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Configurar icono de la aplicacion (opcional)
        try {
            setIconImage(createAppIcon());
        } catch (Exception e) {
            System.err.println("No se pudo cargar icono de aplicacion");
        }
        
        // Crear componentes
        createMenuBar();
        createToolBar();
        createCentralPanel();
        createStatusBar();
    }
    
    /**
    * Crea un icono simple para la aplicacion.
    * 
    * @return Image con el icono
    */
    private Image createAppIcon() {
        // Crear icono simple de 32x32
        Image icon = createImage(32, 32);
        Graphics2D g2d = (Graphics2D) icon.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(new Color(128, 0, 128));
        g2d.fillOval(2, 2, 28, 28);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("CM", 8, 20);
        
        g2d.dispose();
        return icon;
    }
    
    /**
    * Crea la barra de menu principal.
    */
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        // Menu Archivo
        JMenu fileMenu = new JMenu("Archivo");
        fileMenu.setMnemonic('A');
        
        JMenuItem newItem = new JMenuItem("Nuevo");
        newItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        newItem.addActionListener(this::openMovieForm);
        
        JMenuItem exitItem = new JMenuItem("Salir");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> exitApplication());
        
        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Menu Peliculas
        JMenu moviesMenu = new JMenu("Peliculas");
        moviesMenu.setMnemonic('P');
        
        JMenuItem addMovieItem = new JMenuItem("Agregar Pelicula");
        addMovieItem.setAccelerator(KeyStroke.getKeyStroke("ctrl A"));
        addMovieItem.addActionListener(this::openMovieForm);
        
        JMenuItem listMoviesItem = new JMenuItem("Listar Peliculas");
        listMoviesItem.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));
        listMoviesItem.addActionListener(this::openMovieList);
        
        JMenuItem searchMoviesItem = new JMenuItem("Buscar Peliculas");
        searchMoviesItem.setAccelerator(KeyStroke.getKeyStroke("ctrl B"));
        searchMoviesItem.addActionListener(this::openSearchMovies);
        
        JMenuItem deleteMovieItem = new JMenuItem("Eliminar Pelicula");
        deleteMovieItem.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        deleteMovieItem.addActionListener(this::openMovieList);
        
        moviesMenu.add(addMovieItem);
        moviesMenu.addSeparator();
        moviesMenu.add(listMoviesItem);
        moviesMenu.add(searchMoviesItem);
        moviesMenu.add(deleteMovieItem);
        
        // Menu Herramientas
        JMenu toolsMenu = new JMenu("Herramientas");
        toolsMenu.setMnemonic('H');
        
        JMenuItem testConnectionItem = new JMenuItem("Probar Conexion BD");
        testConnectionItem.addActionListener(this::testConnection);
        
        JMenuItem refreshItem = new JMenuItem("Actualizar");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        refreshItem.addActionListener(this::refreshData);
        
        toolsMenu.add(testConnectionItem);
        toolsMenu.add(refreshItem);
        
        // Menu Ayuda
        JMenu helpMenu = new JMenu("Ayuda");
        helpMenu.setMnemonic('Y');
        
        JMenuItem aboutItem = new JMenuItem("Acerca de");
        aboutItem.addActionListener(this::showAbout);
        
        helpMenu.add(aboutItem);
        
        // Agregar menus a la barra
        menuBar.add(fileMenu);
        menuBar.add(moviesMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
    * Crea la barra de herramientas.
    */
    private void createToolBar() {
        toolBar = new JToolBar("Herramientas Principales");
        toolBar.setRollover(true);
        toolBar.setFloatable(false);
        
        // Boton Agregar
        JButton addButton = createToolbarButton("Agregar", "Agregar nueva pelicula (Ctrl+A)", "+", this::openMovieForm);
        
        // Boton Listar
        JButton listButton = createToolbarButton("Listar", "Ver todas las peliculas (Ctrl+L)", "L", this::openMovieList);
        
        // Boton Buscar
        JButton searchButton = createToolbarButton("Buscar", "Buscar peliculas (Ctrl+B)", "?", this::openSearchMovies);
        
        // Boton Borrar (NUEVO)
        JButton deleteButton = createToolbarButton("Borrar", "Eliminar pelicula (Ctrl+D)", "X", e -> {
            // Crear un nuevo ActionEvent con la fuente correcta para indicar modo eliminación
            ActionEvent deleteEvent = new ActionEvent(
                menuBar.getMenu(1).getItem(3), // Referencia al menú "Eliminar Pelicula"
                ActionEvent.ACTION_PERFORMED,
                "Eliminar"
            );
            openMovieList(deleteEvent);
        });
        // Personalizar el botón de borrar para que sea más visible
        deleteButton.setBackground(new Color(200, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        
        // Boton Conexion
        JButton connectionButton = createToolbarButton("Conexion BD", "Probar conexion a base de datos", "DB", this::testConnection);
        
        // Agregar botones a la barra
        toolBar.add(addButton);
        toolBar.addSeparator();
        toolBar.add(listButton);
        toolBar.add(searchButton);
        toolBar.add(deleteButton); // Agregar el nuevo botón
        toolBar.addSeparator();
        toolBar.add(connectionButton);
    }
    
    /**
    * Crea un boton para la barra de herramientas.
    * 
    * @param text texto del boton
    * @param tooltip tooltip descriptivo
    * @param iconText texto del icono
    * @param actionListener listener de accion
    * @return JButton configurado
    */
    private JButton createToolbarButton(String text, String tooltip, String iconText, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setIcon(createIcon(iconText));
        button.addActionListener(actionListener);
        button.setFocusable(false);
        return button;
    }
    
    /**
    * Crea el panel central de la aplicacion.
    */
    private void createCentralPanel() {
        centralPanel = new JPanel(new BorderLayout());
        
        // Panel de bienvenida
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(new Color(245, 245, 245));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        
        // Logo/titulo principal
        JLabel titleLabel = new JLabel(DatabaseConfig.APP_NAME);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(128, 0, 128)); // Magenta
        gbc.gridx = 0; gbc.gridy = 0;
        welcomePanel.add(titleLabel, gbc);
        
        // Subtitulo
        JLabel subtitleLabel = new JLabel(DatabaseConfig.APP_DESCRIPTION);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        gbc.gridy = 1;
        welcomePanel.add(subtitleLabel, gbc);
        
        // Version
        JLabel versionLabel = new JLabel("Version " + DatabaseConfig.APP_VERSION);
        versionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        versionLabel.setForeground(Color.GRAY);
        gbc.gridy = 2;
        welcomePanel.add(versionLabel, gbc);
        
        // Instrucciones
        JLabel instructionsLabel = new JLabel(
            "<html><center>Utilice el menu o la barra de herramientas<br>" +
            "para gestionar las peliculas en cartelera<br><br>" +
            "- <b>Agregar:</b> Crear nueva pelicula<br>" +
            "- <b>Listar:</b> Ver todas las peliculas<br>" +
            "- <b>Buscar:</b> Encontrar peliculas especificas<br>" +
            "- <b>Borrar:</b> Eliminar peliculas existentes</center></html>"
        );
        instructionsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        instructionsLabel.setForeground(Color.DARK_GRAY);
        gbc.gridy = 3;
        welcomePanel.add(instructionsLabel, gbc);
        
        centralPanel.add(welcomePanel, BorderLayout.CENTER);
    }
    
    /**
    * Crea la barra de estado inferior.
    */
    private void createStatusBar() {
        statusLabel = new JLabel("Iniciando sistema...");
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLoweredBevelBorder(),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));
        statusLabel.setPreferredSize(new Dimension(0, 25));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
    }
    
    /**
    * Configura el layout principal de la ventana.
    */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Panel norte para menu y toolbar
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(toolBar, BorderLayout.CENTER);
        
        add(northPanel, BorderLayout.NORTH);
        add(centralPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    /**
    * Configura los event listeners adicionales.
    */
    private void setupEventListeners() {
        // Listener para cerrar aplicacion
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                exitApplication();
            }
        });
    }
    
    /**
    * Crea un icono simple para los botones.
    * 
    * @param text texto del icono
    * @return Icon creado
    */
    private Icon createIcon(final String text) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Color especial para el botón de borrar
                if (text.equals("X")) {
                    g2d.setColor(new Color(200, 0, 0));
                } else {
                    g2d.setColor(new Color(70, 130, 180));
                }
                
                g2d.fillRoundRect(x, y, getIconWidth(), getIconHeight(), 3, 3);
                
                // Usar color negro para todos los textos de los iconos
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 9));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (getIconWidth() - fm.stringWidth(text)) / 2;
                int textY = y + (getIconHeight() + fm.getAscent()) / 2 - 1;
                g2d.drawString(text, textX, textY);
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() { return 20; }
            
            @Override
            public int getIconHeight() { return 16; }
        };
    }
    
    /**
    * Prueba la conexion a la base de datos al iniciar.
    */
    private void testDatabaseConnection() {
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseConnection dbConn = DatabaseConnection.getInstance();
                if (dbConn.testConnection()) {
                    updateStatus("Conectado a la base de datos " + DatabaseConfig.DB_NAME, true);
                } else {
                    updateStatus("Sin conexion a la base de datos", false);
                }
                dbConn.closeConnection();
            } catch (Exception e) {
                updateStatus("Error de conexion: " + e.getMessage(), false);
            }
        });
    }
    
    /**
    * Actualiza la barra de estado.
    * 
    * @param message mensaje a mostrar
    * @param success indica si es mensaje de exito o error
    */
    private void updateStatus(String message, boolean success) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(success ? new Color(0, 128, 0) : Color.RED);
        });
    }
    
    // Event Handlers
    
    /**
    * Abre el formulario para agregar/editar peliculas.
    * 
    * @param e evento de accion
    */
    private void openMovieForm(ActionEvent e) {
        updateStatus("Abriendo formulario de peliculas...", true);
        
        try {
            MovieFormView movieForm = new MovieFormView(this, movieController);
            movieForm.setVisible(true);
            updateStatus("Listo", true);
        } catch (Exception ex) {
            updateStatus("Error al abrir formulario", false);
            MessageUtil.showError(this, "Error al abrir formulario:\n" + ex.getMessage());
        }
    }
    
    /**
    * Abre la lista de peliculas.
    * 
    * @param e evento de accion
    */
    private void openMovieList(ActionEvent e) {
        // Determinar si se está llamando desde el menú "Eliminar Película" o el botón de borrar
        boolean isDeleteMode = false;
        Object source = e.getSource();
        
        if (source instanceof JMenuItem) {
            JMenuItem menuItem = (JMenuItem) source;
            if (menuItem.getText().equals("Eliminar Pelicula")) {
                isDeleteMode = true;
                updateStatus("Abriendo lista de películas en modo eliminación...", true);
            } else {
                updateStatus("Abriendo lista de películas...", true);
            }
        } else if (e.getActionCommand() != null && e.getActionCommand().equals("Eliminar")) {
            // Esta condición captura el evento creado por el botón de borrar
            isDeleteMode = true;
            updateStatus("Abriendo lista de películas en modo eliminación...", true);
        } else {
            updateStatus("Abriendo lista de películas...", true);
        }
        
        try {
            // Crear un JDialog para contener el panel MovieListView
            JDialog dialog = new JDialog(this, "Lista de Películas - Cine Magenta", true);
            MovieListView movieList = new MovieListView(movieController);
            
            // Si estamos en modo eliminación, configurar la vista para destacar la funcionalidad de eliminación
            if (isDeleteMode) {
                dialog.setTitle("ELIMINAR Películas - Cine Magenta");
                movieList.setDeleteMode(true);
                
                // Mostrar un mensaje informativo al usuario
                JOptionPane.showMessageDialog(this,
                    "Ha seleccionado el modo de ELIMINACIÓN de películas.\n\n" +
                    "Para eliminar una película:\n" +
                    "1. Seleccione la película de la lista\n" +
                    "2. Haga clic en el botón ELIMINAR\n" +
                    "3. Confirme la eliminación\n\n" +
                    "ADVERTENCIA: Esta acción no se puede deshacer.",
                    "Modo Eliminación Activado",
                    JOptionPane.WARNING_MESSAGE);
            }
            
            // Configurar el diálogo
            dialog.setContentPane(movieList);
            dialog.setSize(900, 600);
            dialog.setLocationRelativeTo(this);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            
            dialog.setVisible(true);
            updateStatus("Listo", true);
        } catch (Exception ex) {
            updateStatus("Error al abrir lista", false);
            MessageUtil.showError(this, "Error al abrir lista:\n" + ex.getMessage());
        }
    }
    
    /**
    * Abre la funcionalidad de busqueda avanzada.
    * 
    * @param e evento de accion
    */
    private void openSearchMovies(ActionEvent e) {
        updateStatus("Abriendo búsqueda avanzada...", true);
        
        try {
            MovieSearchView searchView = new MovieSearchView(this, movieController);
            searchView.setVisible(true);
            updateStatus("Listo", true);
        } catch (Exception ex) {
            updateStatus("Error al abrir búsqueda avanzada", false);
            MessageUtil.showError(this, "Error al abrir búsqueda avanzada:\n" + ex.getMessage());
        }
    }
    
    /**
    * Prueba la conexion a la base de datos.
    * 
    * @param e evento de accion
    */
    private void testConnection(ActionEvent e) {
        updateStatus("Probando conexion...", true);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                DatabaseConnection dbConn = DatabaseConnection.getInstance();
                boolean result = dbConn.testConnection();
                dbConn.closeConnection();
                return result;
            }
            
            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    if (connected) {
                        updateStatus("Conexion exitosa a " + DatabaseConfig.DB_NAME, true);
                        MessageUtil.showInfo(MainFrame.this, 
                            "Conexion a la base de datos exitosa\n\n" +
                            "Base de datos: " + DatabaseConfig.DB_NAME + "\n" +
                            "Servidor: " + DatabaseConfig.DB_HOST + ":" + DatabaseConfig.DB_PORT + "\n" +
                            "Usuario: " + DatabaseConfig.DB_USERNAME,
                            "Conexion Exitosa - " + DatabaseConfig.APP_NAME);
                    } else {
                        updateStatus("Error de conexion a la base de datos", false);
                        MessageUtil.showError(MainFrame.this, 
                            "No se pudo conectar a la base de datos\n\n" +
                            "Verificaciones necesarias:\n" +
                            "• Servidor MySQL ejecutandose\n" +
                            "• Base de datos '" + DatabaseConfig.DB_NAME + "' existe\n" +
                            "• Credenciales correctas en DatabaseConfig.java\n" +
                            "• Puerto " + DatabaseConfig.DB_PORT + " disponible");
                    }
                } catch (Exception ex) {
                    updateStatus("Error inesperado en conexion", false);
                    MessageUtil.showError(MainFrame.this, 
                        "Error inesperado al probar conexion:\n" + ex.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    
    /**
    * Actualiza los datos mostrados.
    * 
    * @param e evento de accion
    */
    private void refreshData(ActionEvent e) {
        updateStatus("Actualizando sistema...", true);
        testDatabaseConnection();
        MessageUtil.showInfo(this, "Sistema actualizado correctamente", "Actualizar - " + DatabaseConfig.APP_NAME);
    }
    
    /**
    * Muestra informacion sobre la aplicacion.
    * 
    * @param e evento de accion
    */
    private void showAbout(ActionEvent e) {
        String aboutText = String.format(
            "%s\nVersion %s\n\n%s\n\n" +
            "Desarrollado para PRY2203\n" +
            "Desarrollo Orientado a Objetos II\n\n" +
            "Caracteristicas:\n" +
            "• Arquitectura MVC\n" +
            "• Base de datos MySQL con JDBC\n" +
            "• Interfaz grafica Swing\n" +
            "• Validaciones de datos\n" +
            "• Operaciones CRUD completas\n\n" +
            "Configuracion actual:\n" +
            "• Base de datos: %s\n" +
            "• Servidor: %s:%s",
            DatabaseConfig.APP_NAME,
            DatabaseConfig.APP_VERSION,
            DatabaseConfig.APP_DESCRIPTION,
            DatabaseConfig.DB_NAME,
            DatabaseConfig.DB_HOST,
            DatabaseConfig.DB_PORT
        );
        
        MessageUtil.showInfo(this, aboutText, "Acerca de " + DatabaseConfig.APP_NAME);
    }
    
    /**
    * Cierra la aplicacion de forma segura.
    */
    private void exitApplication() {
        boolean confirmed = MessageUtil.showConfirm(this, 
            "Esta seguro que desea salir de la aplicacion?\n\n" +
            "Se cerraran todas las ventanas y conexiones activas.", 
            "Confirmar Salida - " + DatabaseConfig.APP_NAME);
        
        if (confirmed) {
            updateStatus("Cerrando aplicacion...", true);
            
            // Cerrar conexiones de base de datos
            try {
                DatabaseConnection.getInstance().closeConnection();
                System.out.println("Conexiones cerradas correctamente");
            } catch (Exception e) {
                System.err.println("Error al cerrar conexion: " + e.getMessage());
            }
            
            System.out.println("Cerrando " + DatabaseConfig.APP_NAME + " v" + DatabaseConfig.APP_VERSION);
            
            // Dispose de todas las ventanas
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                window.dispose();
            }
            
            System.exit(0);
        }
    }
}