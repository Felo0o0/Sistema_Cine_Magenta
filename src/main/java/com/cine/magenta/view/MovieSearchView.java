/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.view;

import com.cine.magenta.controller.MovieController;
import com.cine.magenta.model.Movie;
import com.cine.magenta.config.DatabaseConfig;
import com.cine.magenta.util.MessageUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Vista para busqueda avanzada de peliculas.
 * Proporciona interfaz para filtrar peliculas por diferentes criterios.
 * Incluye busqueda por titulo, director, genero y año.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class MovieSearchView extends JFrame {
    
    /** Controlador para operaciones con peliculas */
    private final MovieController movieController;
    
    /** Ventana padre */
    private final Frame parentFrame;
    
    /** Tabla para mostrar resultados */
    private JTable resultsTable;
    
    /** Modelo de datos para la tabla */
    private DefaultTableModel tableModel;
    
    /** Sorter para ordenar la tabla */
    private TableRowSorter<DefaultTableModel> tableSorter;
    
    /** Campos de búsqueda */
    private JTextField titleField;
    private JTextField directorField;
    private JComboBox<String> genreComboBox;
    private JSpinner yearFromSpinner;
    private JSpinner yearToSpinner;
    
    /** Botones de acción */
    private JButton searchButton;
    private JButton clearButton;
    private JButton viewButton;
    private JButton closeButton;
    
    /** Barra de estado */
    private JLabel statusLabel;
    
    /** Columnas de la tabla */
    private static final String[] COLUMN_NAMES = {
    "ID", "Título", "Director", "Año", "Duración", "Género"
    };
    
    /**
    * Constructor que inicializa la vista de búsqueda.
    * 
    * @param parent ventana padre
    * @param movieController controlador de películas
    */
    public MovieSearchView(Frame parent, MovieController movieController) {
    this.parentFrame = parent;
    this.movieController = movieController;
    
    initializeComponents();
    setupLayout();
    setupEventListeners();
    
    // Inicialmente mostrar todas las películas
    searchMovies();
    
    pack();
    setLocationRelativeTo(parent);
    }
    
    /**
    * Inicializa todos los componentes de la vista.
    */
    private void initializeComponents() {
    // Configuración básica de la ventana
    setTitle("Búsqueda Avanzada de Películas - Cine Magenta");
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setResizable(true);
    setSize(900, 600);
    
    // Crear modelo de tabla
    tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
    return false; // Tabla de solo lectura
    }
    };
    
    // Crear tabla y sorter
    resultsTable = new JTable(tableModel);
    tableSorter = new TableRowSorter<>(tableModel);
    resultsTable.setRowSorter(tableSorter);
    resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    resultsTable.setRowHeight(25);
    resultsTable.getTableHeader().setReorderingAllowed(false);
    
    // Configurar anchos de columnas
    resultsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
    resultsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Título
    resultsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Director
    resultsTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // Año
    resultsTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Duración
    resultsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Género
    
    // Campos de búsqueda
    titleField = new JTextField(20);
    directorField = new JTextField(20);
    
    // ComboBox para géneros con opción "Todos"
    String[] genreOptions = new String[DatabaseConfig.AVAILABLE_GENRES.length + 1];
    genreOptions[0] = "Todos";
    System.arraycopy(DatabaseConfig.AVAILABLE_GENRES, 0, genreOptions, 1, DatabaseConfig.AVAILABLE_GENRES.length);
    genreComboBox = new JComboBox<>(genreOptions);
    
    // Spinners para rango de años
    int currentYear = java.time.Year.now().getValue();
    
    // Crear spinners sin separadores de miles
    yearFromSpinner = new JSpinner(new SpinnerNumberModel(
        DatabaseConfig.MIN_YEAR, // valor inicial
        DatabaseConfig.MIN_YEAR, // min
        currentYear, // max
        1
    ));
    JSpinner.NumberEditor yearFromEditor = new JSpinner.NumberEditor(yearFromSpinner, "#");
    yearFromSpinner.setEditor(yearFromEditor);
    
    yearToSpinner = new JSpinner(new SpinnerNumberModel(
        currentYear, // valor inicial
        DatabaseConfig.MIN_YEAR, // min
        DatabaseConfig.MAX_YEAR, // max
        1
    ));
    JSpinner.NumberEditor yearToEditor = new JSpinner.NumberEditor(yearToSpinner, "#");
    yearToSpinner.setEditor(yearToEditor);
    
    // Botones
    searchButton = new JButton("Buscar");
    clearButton = new JButton("Limpiar Filtros");
    viewButton = new JButton("Ver Detalles");
    closeButton = new JButton("Cerrar");
    
    // Configurar botones
    searchButton.setIcon(createIcon("🔍"));
    clearButton.setIcon(createIcon("✖"));
    viewButton.setIcon(createIcon("👁"));
    
    // Colores de botones
    searchButton.setBackground(new Color(70, 130, 180));
    searchButton.setForeground(Color.BLACK); // Texto negro
    clearButton.setBackground(new Color(255, 165, 0));
    clearButton.setForeground(Color.BLACK);
    viewButton.setBackground(new Color(0, 128, 0));
    viewButton.setForeground(Color.BLACK); // Texto negro
    
    // Deshabilitar botón de ver detalles inicialmente
    viewButton.setEnabled(false);
    
    // Barra de estado
    statusLabel = new JLabel("Listo");
    statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
    statusLabel.setPreferredSize(new Dimension(0, 25));
    }
    
    /**
    * Configura el layout de la ventana.
    */
    private void setupLayout() {
    setLayout(new BorderLayout());
    
    // Panel superior con título
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
    
    JLabel titleLabel = new JLabel("Búsqueda Avanzada de Películas");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
    titleLabel.setForeground(new Color(128, 0, 128));
    titlePanel.add(titleLabel);
    
    // Panel de filtros
    JPanel filterPanel = new JPanel(new GridBagLayout());
    filterPanel.setBorder(BorderFactory.createTitledBorder(
    BorderFactory.createEtchedBorder(), 
    "Filtros de Búsqueda", 
    TitledBorder.LEFT, 
    TitledBorder.TOP,
    new Font("Arial", Font.BOLD, 12),
    new Color(128, 0, 128)
    ));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    
    // Título
    gbc.gridx = 0; gbc.gridy = 0;
    filterPanel.add(new JLabel("Título:"), gbc);
    gbc.gridx = 1; gbc.weightx = 1.0;
    filterPanel.add(titleField, gbc);
    
    // Director
    gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
    filterPanel.add(new JLabel("Director:"), gbc);
    gbc.gridx = 1; gbc.weightx = 1.0;
    filterPanel.add(directorField, gbc);
    
    // Género
    gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
    filterPanel.add(new JLabel("Género:"), gbc);
    gbc.gridx = 1; gbc.weightx = 1.0;
    filterPanel.add(genreComboBox, gbc);
    
    // Rango de años
    JPanel yearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    yearPanel.add(new JLabel("Desde:"));
    yearPanel.add(yearFromSpinner);
    yearPanel.add(Box.createHorizontalStrut(20));
    yearPanel.add(new JLabel("Hasta:"));
    yearPanel.add(yearToSpinner);
    
    gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
    filterPanel.add(new JLabel("Año:"), gbc);
    gbc.gridx = 1; gbc.weightx = 1.0;
    filterPanel.add(yearPanel, gbc);
    
    // Botones de búsqueda
    JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    searchButtonPanel.add(searchButton);
    searchButtonPanel.add(clearButton);
    
    gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
    filterPanel.add(searchButtonPanel, gbc);
    
    // Panel central con tabla de resultados
    JScrollPane scrollPane = new JScrollPane(resultsTable);
    scrollPane.setBorder(BorderFactory.createTitledBorder(
    BorderFactory.createEtchedBorder(), 
    "Resultados de Búsqueda", 
    TitledBorder.LEFT, 
    TitledBorder.TOP,
    new Font("Arial", Font.BOLD, 12),
    new Color(128, 0, 128)
    ));
    scrollPane.setPreferredSize(new Dimension(850, 350));
    
    // Panel de botones inferiores
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    
    buttonPanel.add(viewButton);
    buttonPanel.add(Box.createHorizontalStrut(20));
    buttonPanel.add(closeButton);
    
    // Panel norte con título y filtros
    JPanel northPanel = new JPanel(new BorderLayout());
    northPanel.add(titlePanel, BorderLayout.NORTH);
    northPanel.add(filterPanel, BorderLayout.CENTER);
    
    // Ensamblar layout
    add(northPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
    add(statusLabel, BorderLayout.PAGE_END);
    }
    
    /**
    * Configura los event listeners.
    */
    private void setupEventListeners() {
    // Botones
    searchButton.addActionListener(this::searchMovies);
    clearButton.addActionListener(this::clearFilters);
    viewButton.addActionListener(this::viewMovieDetails);
    closeButton.addActionListener(e -> dispose());
    
    // Selección de tabla
    resultsTable.getSelectionModel().addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
    updateButtonStates();
    }
    });
    
    // Doble click para ver detalles
    resultsTable.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2 && resultsTable.getSelectedRow() != -1) {
    viewMovieDetails(null);
    }
    }
    });
    
    // Búsqueda al presionar Enter en los campos de texto
    titleField.addActionListener(this::searchMovies);
    directorField.addActionListener(this::searchMovies);
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
    
    // Establecer color negro para el texto del icono
    g2d.setColor(Color.BLACK);
    g2d.setFont(new Font("Arial", Font.BOLD, 12));
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
    * Actualiza el estado de los botones según la selección.
    */
    private void updateButtonStates() {
    boolean hasSelection = resultsTable.getSelectedRow() != -1;
    viewButton.setEnabled(hasSelection);
    }
    
    /**
    * Actualiza la barra de estado.
    * 
    * @param message mensaje a mostrar
    * @param success indica si es mensaje de éxito
    */
    private void updateStatus(String message, boolean success) {
    statusLabel.setText(message);
    statusLabel.setForeground(success ? new Color(0, 128, 0) : Color.RED);
    }
    
    /**
    * Obtiene la película seleccionada en la tabla.
    * 
    * @return Movie seleccionada o null si no hay selección
    */
    private Movie getSelectedMovie() {
    int selectedRow = resultsTable.getSelectedRow();
    if (selectedRow == -1) {
    return null;
    }
    
    try {
    // Convertir índice de vista a índice de modelo
    int modelRow = resultsTable.convertRowIndexToModel(selectedRow);
    int movieId = (Integer) tableModel.getValueAt(modelRow, 0);
    return movieController.getMovieById(movieId);
    } catch (Exception e) {
    MessageUtil.showError(this, "Error al obtener película seleccionada: " + e.getMessage());
    return null;
    }
    }
    
    /**
    * Realiza la búsqueda de películas según los filtros.
    * 
    * @param e evento de acción
    */
    private void searchMovies(ActionEvent e) {
    searchMovies();
    }
    
    /**
    * Realiza la búsqueda de películas según los filtros.
    */
    private void searchMovies() {
    updateStatus("Buscando películas...", true);
    
    // Obtener valores de los filtros
    final String title = titleField.getText().trim();
    final String director = directorField.getText().trim();
    final String genre = genreComboBox.getSelectedIndex() == 0 ? null : (String) genreComboBox.getSelectedItem();
    final int yearFrom;
    final int yearTo = (Integer) yearToSpinner.getValue();
    
    // Validar rango de años
    int tempYearFrom = (Integer) yearFromSpinner.getValue();
    if (tempYearFrom > yearTo) {
    MessageUtil.showWarning(this, "El año inicial no puede ser mayor que el año final");
    yearFromSpinner.setValue(yearTo);
    tempYearFrom = yearTo;
    }
    yearFrom = tempYearFrom;
    
    // Mostrar criterios de búsqueda
    final StringBuilder criteria = new StringBuilder("Criterios: ");
    final boolean hasCriteria;
    
    boolean tempHasCriteria = false;
    
    if (!title.isEmpty()) {
    criteria.append("Título='").append(title).append("' ");
    tempHasCriteria = true;
    }
    
    if (!director.isEmpty()) {
    criteria.append("Director='").append(director).append("' ");
    tempHasCriteria = true;
    }
    
    if (genre != null) {
    criteria.append("Género='").append(genre).append("' ");
    tempHasCriteria = true;
    }
    
    if (yearFrom != DatabaseConfig.MIN_YEAR || yearTo != DatabaseConfig.MAX_YEAR) {
    criteria.append("Año=").append(yearFrom).append("-").append(yearTo).append(" ");
    tempHasCriteria = true;
    }
    
    hasCriteria = tempHasCriteria;
    
    if (!hasCriteria) {
    criteria.append("Todas las películas");
    }
    
    // Realizar búsqueda en segundo plano
    SwingWorker<List<Movie>, Void> worker = new SwingWorker<List<Movie>, Void>() {
    @Override
    protected List<Movie> doInBackground() throws Exception {
    // Si no hay criterios, obtener todas las películas
    if (!hasCriteria) {
    return movieController.getAllMovies();
    }
    
    // Implementar búsqueda combinada
    // Nota: En una implementación real, esto debería hacerse en el DAO con una consulta SQL optimizada
    List<Movie> results = movieController.getAllMovies();
    
    // Filtrar por título
    if (!title.isEmpty()) {
    results.removeIf(movie -> !movie.getTitle().toLowerCase().contains(title.toLowerCase()));
    }
    
    // Filtrar por director
    if (!director.isEmpty()) {
    results.removeIf(movie -> !movie.getDirector().toLowerCase().contains(director.toLowerCase()));
    }
    
    // Filtrar por género
    if (genre != null) {
    results.removeIf(movie -> !movie.getGenre().equals(genre));
    }
    
    // Filtrar por rango de años
    results.removeIf(movie -> movie.getYear() < yearFrom || movie.getYear() > yearTo);
    
    return results;
    }
    
    @Override
    protected void done() {
    try {
    List<Movie> movies = get();
    updateTable(movies);
    updateStatus("Resultados: " + movies.size() + " películas. " + criteria.toString(), true);
    } catch (Exception e) {
    updateStatus("Error al buscar películas", false);
    MessageUtil.showError(MovieSearchView.this, 
    "Error al buscar películas:\n" + e.getMessage());
    }
    }
    };
    
    worker.execute();
    }
    
    /**
    * Actualiza la tabla con la lista de películas.
    * 
    * @param movies lista de películas
    */
    private void updateTable(List<Movie> movies) {
    // Limpiar tabla
    tableModel.setRowCount(0);
    
    // Agregar películas
    for (Movie movie : movies) {
    Object[] rowData = {
    movie.getId(),
    movie.getTitle(),
    movie.getDirector(),
    movie.getYear(),
    movie.getFormattedDuration(),
    movie.getGenre()
    };
    tableModel.addRow(rowData);
    }
    
    updateButtonStates();
    }
    
    /**
    * Limpia todos los filtros de búsqueda.
    * 
    * @param e evento de acción
    */
    private void clearFilters(ActionEvent e) {
    titleField.setText("");
    directorField.setText("");
    genreComboBox.setSelectedIndex(0);
    yearFromSpinner.setValue(DatabaseConfig.MIN_YEAR);
    yearToSpinner.setValue(java.time.Year.now().getValue());
    
    // Realizar búsqueda sin filtros
    searchMovies();
    }
    
    /**
    * Muestra los detalles de la película seleccionada.
    * 
    * @param e evento de acción
    */
    private void viewMovieDetails(ActionEvent e) {
    Movie selectedMovie = getSelectedMovie();
    if (selectedMovie == null) {
    MessageUtil.showWarning(this, "Debe seleccionar una película para ver detalles");
    return;
    }
    
    // Mostrar detalles en un diálogo
    StringBuilder details = new StringBuilder();
    details.append("<html><body style='width: 300px; padding: 10px;'>");
    details.append("<h2 style='color: #800080;'>").append(selectedMovie.getTitle()).append("</h2>");
    details.append("<hr>");
    details.append("<p><b>Director:</b> ").append(selectedMovie.getDirector()).append("</p>");
    details.append("<p><b>Año:</b> ").append(selectedMovie.getYear()).append("</p>");
    details.append("<p><b>Duración:</b> ").append(selectedMovie.getFormattedDuration()).append("</p>");
    details.append("<p><b>Género:</b> ").append(selectedMovie.getGenre()).append("</p>");
    details.append("<hr>");
    details.append("<p><i>ID: ").append(selectedMovie.getId()).append("</i></p>");
    details.append("</body></html>");
    
    JOptionPane.showMessageDialog(this, 
    new JLabel(details.toString()), 
    "Detalles de Película", 
    JOptionPane.INFORMATION_MESSAGE);
    }
}