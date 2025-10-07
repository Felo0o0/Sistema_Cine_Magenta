/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.view;

import com.cine.magenta.controller.MovieController;
import com.cine.magenta.model.Movie;
import com.cine.magenta.config.DatabaseConfig;
import com.cine.magenta.util.MessageUtil;
import com.cine.magenta.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 * Formulario para agregar y editar peliculas.
 * Proporciona interfaz para operaciones CRUD sobre Movie.
 * Incluye validaciones y manejo de errores en tiempo real.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class MovieFormView extends JDialog {
    
    /** Controlador para operaciones con peliculas */
    private final MovieController movieController;
    
    /** Pelicula en edicion (null para nueva pelicula) */
    private Movie currentMovie;
    
    /** Indica si el formulario esta en modo edicion */
    private boolean editMode;
    
    // Componentes del formulario
    private JTextField titleField;
    private JTextField directorField;
    private JSpinner yearSpinner;
    private JSpinner durationSpinner;
    private JComboBox<String> genreComboBox;
    
    // Botones de accion
    private JButton saveButton;
    private JButton clearButton;
    private JButton cancelButton;
    private JButton deleteButton; // Nuevo botón para eliminar
    
    /**
    * Constructor para crear nueva pelicula.
    * 
    * @param parent ventana padre
    * @param movieController controlador de peliculas
    */
    public MovieFormView(Frame parent, MovieController movieController) {
    this(parent, movieController, null);
    }
    
    /**
    * Constructor para editar pelicula existente.
    * 
    * @param parent ventana padre
    * @param movieController controlador de peliculas
    * @param movie pelicula a editar (null para nueva)
    */
    public MovieFormView(Frame parent, MovieController movieController, Movie movie) {
    super(parent, true);
    this.movieController = movieController;
    this.currentMovie = movie;
    this.editMode = (movie != null);
    
    initializeComponents();
    setupLayout();
    setupEventListeners();
    
    // Inicialmente deshabilitar el botón de guardar hasta que todos los campos sean válidos
    saveButton.setEnabled(false);
    
    if (editMode) {
    loadMovieData();
    } else {
    clearForm(null);
    }
    
    pack();
    setLocationRelativeTo(parent);
    }
    
    /**
    * Inicializa todos los componentes del formulario.
    */
    private void initializeComponents() {
    // Configuracion basica del dialogo
    setTitle(editMode ? "Editar Película" : "Agregar Nueva Película");
    setModal(true);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setResizable(false);
    
    // Campo titulo
    titleField = new JTextField(20);
    titleField.setToolTipText("Título de la película (máximo " + DatabaseConfig.MAX_TITLE_LENGTH + " caracteres)");
    
    // Campo director
    directorField = new JTextField(20);
    directorField.setToolTipText("Director de la película (máximo " + DatabaseConfig.MAX_DIRECTOR_LENGTH + " caracteres)");
    
    // Spinner año
    yearSpinner = new JSpinner(new SpinnerNumberModel(
    DatabaseConfig.MIN_YEAR, // valor inicial válido
    DatabaseConfig.MIN_YEAR, // min
    DatabaseConfig.MAX_YEAR, // max
    1
    ));
    JSpinner.NumberEditor yearEditor = new JSpinner.NumberEditor(yearSpinner, "#");
    yearEditor.getFormat().setGroupingUsed(false);
    yearSpinner.setEditor(yearEditor);
    yearSpinner.setToolTipText("Año de estreno (" + DatabaseConfig.MIN_YEAR + "-" + DatabaseConfig.MAX_YEAR + ")");
    // Mostrar vacío visualmente
    ((JSpinner.NumberEditor) yearSpinner.getEditor()).getTextField().setText("");
    
    // Spinner duración
    durationSpinner = new JSpinner(new SpinnerNumberModel(
    DatabaseConfig.MIN_DURATION, // valor inicial válido
    DatabaseConfig.MIN_DURATION, // min
    DatabaseConfig.MAX_DURATION, // max
    1
    ));
    JSpinner.NumberEditor durationEditor = new JSpinner.NumberEditor(durationSpinner, "#");
    durationEditor.getFormat().setGroupingUsed(false);
    durationSpinner.setEditor(durationEditor);
    durationSpinner.setToolTipText("Duración en minutos (" + DatabaseConfig.MIN_DURATION + "-" + DatabaseConfig.MAX_DURATION + ")");
    // Mostrar vacío visualmente
    ((JSpinner.NumberEditor) durationSpinner.getEditor()).getTextField().setText("");
    
    // ComboBox género
    genreComboBox = new JComboBox<>(DatabaseConfig.AVAILABLE_GENRES);
    genreComboBox.setToolTipText("Seleccionar género cinematográfico");
    
    // Botones de acción
    saveButton = new JButton(editMode ? "Actualizar" : "Guardar");
    clearButton = new JButton("Limpiar");
    cancelButton = new JButton("Cancelar");
    
    // Nuevo botón de eliminar (solo visible en modo edición)
    deleteButton = new JButton("ELIMINAR");
    deleteButton.setVisible(editMode);
    
    // Configurar botones
    saveButton.setPreferredSize(new Dimension(100, 30));
    clearButton.setPreferredSize(new Dimension(100, 30));
    cancelButton.setPreferredSize(new Dimension(100, 30));
    deleteButton.setPreferredSize(new Dimension(100, 30));
    
    // Colores de botones (texto negro para mejor visibilidad)
    saveButton.setBackground(new Color(0, 128, 0));
    saveButton.setForeground(Color.BLACK);
    clearButton.setBackground(new Color(255, 165, 0));
    clearButton.setForeground(Color.BLACK);
    cancelButton.setBackground(new Color(128, 128, 128));
    cancelButton.setForeground(Color.BLACK);
    
    // Configuración especial para el botón de eliminar
    deleteButton.setBackground(new Color(200, 0, 0));
    deleteButton.setForeground(Color.WHITE);
    deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
    deleteButton.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(139, 0, 0), 2),
        BorderFactory.createEmptyBorder(5, 10, 5, 10)
    ));
    deleteButton.setToolTipText("Eliminar esta película permanentemente");
    }
    
    /**
    * Configura el layout del formulario.
    */
    private void setupLayout() {
    setLayout(new BorderLayout());
    
    // Panel principal con formulario
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;
    
    // Título
    gbc.gridx = 0; gbc.gridy = 0;
    formPanel.add(new JLabel("Título:"), gbc);
    gbc.gridx = 1;
    formPanel.add(titleField, gbc);
    
    // Director
    gbc.gridx = 0; gbc.gridy = 1;
    formPanel.add(new JLabel("Director:"), gbc);
    gbc.gridx = 1;
    formPanel.add(directorField, gbc);
    
    // Año
    gbc.gridx = 0; gbc.gridy = 2;
    formPanel.add(new JLabel("Año:"), gbc);
    gbc.gridx = 1;
    formPanel.add(yearSpinner, gbc);
    
    // Duración
    gbc.gridx = 0; gbc.gridy = 3;
    formPanel.add(new JLabel("Duración (min):"), gbc);
    gbc.gridx = 1;
    formPanel.add(durationSpinner, gbc);
    
    // Género
    gbc.gridx = 0; gbc.gridy = 4;
    formPanel.add(new JLabel("Género:"), gbc);
    gbc.gridx = 1;
    formPanel.add(genreComboBox, gbc);
    
    // Panel de botones
    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    
    buttonPanel.add(saveButton);
    buttonPanel.add(clearButton);
    buttonPanel.add(cancelButton);
    
    // Añadir botón de eliminar solo en modo edición
    if (editMode) {
        // Crear un panel separado para el botón de eliminar con un borde especial
        JPanel deleteButtonPanel = new JPanel();
        deleteButtonPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(139, 0, 0)), 
            "Eliminar Película", 
            javax.swing.border.TitledBorder.CENTER, 
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(139, 0, 0)
        ));
        deleteButtonPanel.add(deleteButton);
        
        // Añadir el panel de eliminar al panel de botones
        buttonPanel.add(Box.createHorizontalStrut(20)); // Espacio entre botones normales y eliminar
        buttonPanel.add(deleteButtonPanel);
    }
    
    add(formPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
    * Configura los event listeners.
    */
    private void setupEventListeners() {
    saveButton.addActionListener(this::saveMovie);
    clearButton.addActionListener(this::clearForm);
    cancelButton.addActionListener(e -> dispose());
    
    // Listener para el botón de eliminar
    if (editMode) {
        deleteButton.addActionListener(this::deleteMovie);
    }
    
    // Validación en tiempo real para título
    titleField.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyReleased(java.awt.event.KeyEvent evt) {
    validateTitle();
    }
    });
    
    // Validación en tiempo real para director
    directorField.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyReleased(java.awt.event.KeyEvent evt) {
    validateDirector();
    }
    });
    
    // Validación en tiempo real para año
    ((JSpinner.NumberEditor) yearSpinner.getEditor()).getTextField().addKeyListener(
    new java.awt.event.KeyAdapter() {
    public void keyReleased(java.awt.event.KeyEvent evt) {
    validateYear();
    }
    }
    );
    yearSpinner.addChangeListener(e -> validateYear());
    
    // Validación en tiempo real para duración
    ((JSpinner.NumberEditor) durationSpinner.getEditor()).getTextField().addKeyListener(
    new java.awt.event.KeyAdapter() {
    public void keyReleased(java.awt.event.KeyEvent evt) {
    validateDuration();
    }
    }
    );
    durationSpinner.addChangeListener(e -> validateDuration());
    
    // Validación en tiempo real para género
    genreComboBox.addActionListener(e -> validateGenre());
    }
    
    /**
    * Carga datos de la película en modo edición.
    */
    private void loadMovieData() {
    if (currentMovie != null) {
    titleField.setText(currentMovie.getTitle());
    directorField.setText(currentMovie.getDirector());
    yearSpinner.setValue(currentMovie.getYear());
    durationSpinner.setValue(currentMovie.getDuration());
    genreComboBox.setSelectedItem(currentMovie.getGenre());
    
    // Validar todos los campos después de cargar los datos
    validateAllFields();
    }
    }
    
    /**
    * Valida todos los campos del formulario.
    */
    private void validateAllFields() {
    validateTitle();
    validateDirector();
    validateYear();
    validateDuration();
    validateGenre();
    }
    
    /**
    * Actualiza el estado del botón de guardar según la validación de todos los campos.
    * El botón se habilita solo si todos los campos son válidos.
    */
    private void updateSaveButtonState() {
    String title = titleField.getText();
    String director = directorField.getText();
    String yearText = ((JSpinner.NumberEditor) yearSpinner.getEditor()).getTextField().getText().trim();
    String durationText = ((JSpinner.NumberEditor) durationSpinner.getEditor()).getTextField().getText().trim();
    String genre = (String) genreComboBox.getSelectedItem();
    
    boolean[] validations = ValidationUtil.validateAllFields(title, director, yearText, durationText, genre);
    
    // Verificar si todos los campos son válidos
    boolean allValid = true;
    for (boolean valid : validations) {
    if (!valid) {
    allValid = false;
    break;
    }
    }
    
    // Habilitar o deshabilitar el botón de guardar
    saveButton.setEnabled(allValid);
    }
    
    /**
    * Valida el campo título en tiempo real.
    */
    private void validateTitle() {
    String title = titleField.getText();
    boolean isValid = ValidationUtil.isValidTitle(title);
    String message = ValidationUtil.getTitleErrorMessage(title);
    ValidationUtil.applyVisualValidation(titleField, isValid, message, "Título válido");
    updateSaveButtonState();
    }
    
    /**
    * Valida el campo director en tiempo real.
    */
    private void validateDirector() {
    String director = directorField.getText();
    boolean isValid = ValidationUtil.isValidDirector(director);
    String message = ValidationUtil.getDirectorErrorMessage(director);
    ValidationUtil.applyVisualValidation(directorField, isValid, message, "Director válido");
    updateSaveButtonState();
    }
    
    /**
    * Valida el campo año en tiempo real.
    */
    private void validateYear() {
    String yearText = ((JSpinner.NumberEditor) yearSpinner.getEditor()).getTextField().getText().trim();
    boolean isValid = false;
    String message;
    
    if (yearText.isEmpty()) {
    message = "El año es obligatorio";
    } else if (!ValidationUtil.isValidInteger(yearText)) {
    message = "El año debe ser un número entero válido";
    } else {
    int year = Integer.parseInt(yearText);
    isValid = ValidationUtil.isValidYear(year);
    message = ValidationUtil.getYearErrorMessage(year);
    }
    
    ValidationUtil.applyVisualValidation(yearSpinner, isValid, message, "Año válido");
    updateSaveButtonState();
    }
    
    /**
    * Valida el campo duración en tiempo real.
    */
    private void validateDuration() {
    String durationText = ((JSpinner.NumberEditor) durationSpinner.getEditor()).getTextField().getText().trim();
    boolean isValid = false;
    String message;
    
    if (durationText.isEmpty()) {
    message = "La duración es obligatoria";
    } else if (!ValidationUtil.isValidInteger(durationText)) {
    message = "La duración debe ser un número entero válido";
    } else {
    int duration = Integer.parseInt(durationText);
    isValid = ValidationUtil.isValidDuration(duration);
    message = ValidationUtil.getDurationErrorMessage(duration);
    }
    
    ValidationUtil.applyVisualValidation(durationSpinner, isValid, message, "Duración válida");
    updateSaveButtonState();
    }
    
    /**
    * Valida el campo género en tiempo real.
    */
    private void validateGenre() {
    String genre = (String) genreComboBox.getSelectedItem();
    boolean isValid = ValidationUtil.isValidGenre(genre);
    String message = ValidationUtil.getGenreErrorMessage(genre);
    
    ValidationUtil.applyVisualValidation(genreComboBox, isValid, message, "Género válido");
    updateSaveButtonState();
    }
    
    /**
    * Guarda o actualiza la película.
    * 
    * @param e evento de acción
    */
    private void saveMovie(ActionEvent e) {
    try {
    // Obtener datos del formulario
    String title = titleField.getText().trim();
    String director = directorField.getText().trim();
    String yearText = ((JSpinner.NumberEditor) yearSpinner.getEditor()).getTextField().getText().trim();
    String durationText = ((JSpinner.NumberEditor) durationSpinner.getEditor()).getTextField().getText().trim();
    String genre = (String) genreComboBox.getSelectedItem();
    
    // Validar todos los campos
    boolean[] validations = ValidationUtil.validateAllFields(title, director, yearText, durationText, genre);
    String[] errorMessages = ValidationUtil.getAllErrorMessages(title, director, yearText, durationText, genre);
    
    // Verificar si hay errores y mostrar el primero
    if (!validations[0]) {
    MessageUtil.showError(this, "Título inválido", errorMessages[0]);
    titleField.requestFocus();
    return;
    }
    
    if (!validations[1]) {
    MessageUtil.showError(this, "Director inválido", errorMessages[1]);
    directorField.requestFocus();
    return;
    }
    
    if (!validations[2]) {
    MessageUtil.showError(this, "Año inválido", errorMessages[2]);
    yearSpinner.requestFocus();
    return;
    }
    
    if (!validations[3]) {
    MessageUtil.showError(this, "Duración inválida", errorMessages[3]);
    durationSpinner.requestFocus();
    return;
    }
    
    if (!validations[4]) {
    MessageUtil.showError(this, "Género inválido", errorMessages[4]);
    genreComboBox.requestFocus();
    return;
    }
    
    // Si llegamos aquí, todos los campos son válidos
    int year = Integer.parseInt(yearText);
    int duration = Integer.parseInt(durationText);
    
    // Crear o actualizar película
    Movie movie;
    if (editMode) {
    movie = new Movie(currentMovie.getId(), title, director, year, duration, genre);
    movie = movieController.updateMovie(movie);
    MessageUtil.showUpdateSuccess(this, "película '" + movie.getTitle() + "'");
    } else {
    movie = movieController.createMovie(title, director, year, duration, genre);
    MessageUtil.showCreateSuccess(this, "película '" + movie.getTitle() + "'");
    clearForm(null);
    }
    
    } catch (Exception ex) {
    MessageUtil.showError(this, 
    "Error al " + (editMode ? "actualizar" : "guardar") + " película:\n" + ex.getMessage());
    }
    }
    
    /**
    * Elimina la película actual.
    * 
    * @param e evento de acción
    */
    private void deleteMovie(ActionEvent e) {
        if (currentMovie == null) {
            return;
        }
        
        // Mostrar información detallada de la película a eliminar
        String movieInfo = String.format(
            "Título: %s\nDirector: %s\nAño: %d\nDuración: %s\nGénero: %s",
            currentMovie.getTitle(),
            currentMovie.getDirector(),
            currentMovie.getYear(),
            currentMovie.getFormattedDuration(),
            currentMovie.getGenre()
        );
        
        // Confirmar eliminación con información detallada
        int option = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea ELIMINAR PERMANENTEMENTE esta película?\n\n" + 
            movieInfo + "\n\nEsta acción NO se puede deshacer.",
            "CONFIRMAR ELIMINACIÓN",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            try {
                boolean deleted = movieController.deleteMovie(currentMovie.getId());
                
                if (deleted) {
                    MessageUtil.showDeleteSuccess(this, 
                        "película '" + currentMovie.getTitle() + "'");
                    dispose(); // Cerrar el formulario después de eliminar
                } else {
                    MessageUtil.showError(this, 
                        "No se pudo eliminar la película. Verifique que no existan dependencias.");
                }
            } catch (Exception ex) {
                MessageUtil.showError(this, 
                    "Error al eliminar película:\n" + ex.getMessage());
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }
    
    /**
    * Limpia todos los campos del formulario.
    * 
    * @param e evento de acción
    */
    private void clearForm(ActionEvent e) {
    titleField.setText("");
    directorField.setText("");
    ((JSpinner.NumberEditor) yearSpinner.getEditor()).getTextField().setText("");
    ((JSpinner.NumberEditor) durationSpinner.getEditor()).getTextField().setText("");
    genreComboBox.setSelectedIndex(0);
    
    // Restablecer colores de fondo
    titleField.setBackground(Color.WHITE);
    directorField.setBackground(Color.WHITE);
    ((JSpinner.NumberEditor) yearSpinner.getEditor()).getTextField().setBackground(Color.WHITE);
    ((JSpinner.NumberEditor) durationSpinner.getEditor()).getTextField().setBackground(Color.WHITE);
    genreComboBox.setBackground(Color.WHITE);
    
    // Restablecer tooltips
    titleField.setToolTipText("Título de la película (máximo " + DatabaseConfig.MAX_TITLE_LENGTH + " caracteres)");
    directorField.setToolTipText("Director de la película (máximo " + DatabaseConfig.MAX_DIRECTOR_LENGTH + " caracteres)");
    yearSpinner.setToolTipText("Año de estreno (" + DatabaseConfig.MIN_YEAR + "-" + DatabaseConfig.MAX_YEAR + ")");
    durationSpinner.setToolTipText("Duración en minutos (" + DatabaseConfig.MIN_DURATION + "-" + DatabaseConfig.MAX_DURATION + ")");
    genreComboBox.setToolTipText("Seleccionar género cinematográfico");
    
    // Validar campos vacíos
    validateAllFields();
    
    // Enfocar el primer campo
    titleField.requestFocus();
    }
}