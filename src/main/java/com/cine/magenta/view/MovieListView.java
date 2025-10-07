/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.view;

import com.cine.magenta.controller.MovieController;
import com.cine.magenta.model.Movie;
import com.cine.magenta.util.ExportUtil;
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
 * Panel para mostrar y gestionar la lista de peliculas.
 * Proporciona funcionalidades para visualizar, editar y eliminar peliculas.
 * Se muestra dentro de un dialogo creado por MainFrame.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class MovieListView extends JPanel {
    
    /** Controlador para operaciones con peliculas */
    private final MovieController movieController;
    
    /** Tabla para mostrar peliculas */
    private JTable moviesTable;
    
    /** Modelo de datos para la tabla */
    private DefaultTableModel tableModel;
    
    /** Sorter para ordenar la tabla */
    private TableRowSorter<DefaultTableModel> tableSorter;
    
    /** Botones de accion */
    private JButton viewButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton exportButton;
    
    /** Barra de estado */
    private JLabel statusLabel;
    
    /** Columnas de la tabla */
    private static final String[] COLUMN_NAMES = {
        "ID", "Titulo", "Director", "Año", "Duracion", "Genero"
    };
    
    /** Indica si estamos en modo eliminacion */
    private boolean deleteMode = false;
    
    /**
     * Constructor que inicializa la vista de lista.
     * 
     * @param movieController controlador de peliculas
     */
    public MovieListView(MovieController movieController) {
        this.movieController = movieController;
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        
        // Cargar datos iniciales
        loadMovies();
    }
    
    /**
     * Inicializa todos los componentes del panel.
     */
    private void initializeComponents() {
        // Configuracion basica del panel
        setLayout(new BorderLayout());
        
        // Crear modelo de tabla
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // ID
                if (columnIndex == 3) return Integer.class; // Año
                return String.class;
            }
        };
        
        // Crear tabla y sorter
        moviesTable = new JTable(tableModel);
        tableSorter = new TableRowSorter<>(tableModel);
        moviesTable.setRowSorter(tableSorter);
        moviesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        moviesTable.setRowHeight(25);
        moviesTable.getTableHeader().setReorderingAllowed(false);
        
        // Configurar anchos de columnas
        moviesTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        moviesTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Titulo
        moviesTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Director
        moviesTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // Año
        moviesTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Duracion
        moviesTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Genero
        
        // Botones
        viewButton = new JButton("Ver Detalles");
        editButton = new JButton("Editar");
        deleteButton = new JButton("Eliminar");
        refreshButton = new JButton("Actualizar");
        exportButton = new JButton("Exportar");
        
        // Configurar botones
        viewButton.setIcon(createIcon("👁"));
        editButton.setIcon(createIcon("✏"));
        deleteButton.setIcon(createIcon("🗑"));
        refreshButton.setIcon(createIcon("🔄"));
        exportButton.setIcon(createIcon("📋"));
        
        // Colores de botones
        viewButton.setBackground(new Color(70, 130, 180));
        viewButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(0, 128, 0));
        editButton.setForeground(Color.WHITE);
        deleteButton.setBackground(new Color(200, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        
        // Deshabilitar botones inicialmente
        viewButton.setEnabled(false);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // Barra de estado
        statusLabel = new JLabel("Cargando peliculas...");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setPreferredSize(new Dimension(0, 25));
    }
    
    /**
     * Configura el layout del panel.
     */
    private void setupLayout() {
        // Panel superior con titulo
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        JLabel titleLabel = new JLabel("Listado de Peliculas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(128, 0, 128));
        titlePanel.add(titleLabel);
        
        // Panel central con tabla de peliculas
        JScrollPane scrollPane = new JScrollPane(moviesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Peliculas Disponibles", 
            TitledBorder.LEFT, 
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12),
            new Color(128, 0, 128)
        ));
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        buttonPanel.add(viewButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        
        // Ensamblar layout
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.PAGE_END);
    }
    
    /**
     * Configura los event listeners.
     */
    private void setupEventListeners() {
        // Botones
        viewButton.addActionListener(this::viewMovieDetails);
        editButton.addActionListener(this::editMovie);
        deleteButton.addActionListener(this::deleteMovie);
        refreshButton.addActionListener(e -> loadMovies());
        exportButton.addActionListener(this::exportMovies);
        
        // Seleccion de tabla
        moviesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
        
        // Doble click para ver detalles o editar/eliminar según el modo
        moviesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && moviesTable.getSelectedRow() != -1) {
                    if (deleteMode) {
                        // En modo eliminación, mostrar diálogo de confirmación
                        deleteMovie(null);
                    } else {
                        // En modo normal, abrir para editar en lugar de solo ver
                        editMovie(null);
                    }
                }
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
     * Actualiza el estado de los botones segun la seleccion.
     */
    private void updateButtonStates() {
        boolean hasSelection = moviesTable.getSelectedRow() != -1;
        
        // En modo eliminación, destacar el botón de eliminar y ocultar editar/exportar
        if (deleteMode) {
            deleteButton.setEnabled(hasSelection);
            viewButton.setEnabled(hasSelection);
            // Asegurarse de que los botones estén visibles/invisibles según corresponda
            deleteButton.setVisible(true);
            viewButton.setVisible(true);
            editButton.setVisible(false);
            exportButton.setVisible(false);
        } else {
            // Modo normal: todos los botones visibles, habilitados según selección
            viewButton.setEnabled(hasSelection);
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
            viewButton.setVisible(true);
            editButton.setVisible(true);
            deleteButton.setVisible(true);
            exportButton.setVisible(true);
        }
    }
    
    /**
     * Actualiza la barra de estado.
     * 
     * @param message mensaje a mostrar
     * @param success indica si es mensaje de exito
     */
    private void updateStatus(String message, boolean success) {
        statusLabel.setText(message);
        statusLabel.setForeground(success ? new Color(0, 128, 0) : Color.RED);
    }
    
    /**
     * Carga las peliculas desde el controlador.
     */
    private void loadMovies() {
        updateStatus("Cargando peliculas...", true);
        
        // Limpiar tabla
        tableModel.setRowCount(0);
        
        // Cargar peliculas en segundo plano
        SwingWorker<List<Movie>, Void> worker = new SwingWorker<List<Movie>, Void>() {
            @Override
            protected List<Movie> doInBackground() throws Exception {
                return movieController.getAllMovies();
            }
            
            @Override
            protected void done() {
                try {
                    List<Movie> movies = get();
                    updateTable(movies);
                    updateStatus("Se encontraron " + movies.size() + " peliculas", true);
                } catch (Exception e) {
                    updateStatus("Error al cargar peliculas: " + e.getMessage(), false);
                    MessageUtil.showError(MovieListView.this, 
                        "Error al cargar peliculas:\n" + e.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Actualiza la tabla con la lista de peliculas.
     * 
     * @param movies lista de peliculas
     */
    private void updateTable(List<Movie> movies) {
        // Limpiar tabla
        tableModel.setRowCount(0);
        
        // Agregar peliculas
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
     * Obtiene la pelicula seleccionada en la tabla.
     * 
     * @return Movie seleccionada o null si no hay seleccion
     */
    private Movie getSelectedMovie() {
        int selectedRow = moviesTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        
        try {
            // Convertir indice de vista a indice de modelo
            int modelRow = moviesTable.convertRowIndexToModel(selectedRow);
            int movieId = (Integer) tableModel.getValueAt(modelRow, 0);
            return movieController.getMovieById(movieId);
        } catch (Exception e) {
            MessageUtil.showError(this, "Error al obtener pelicula seleccionada: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Muestra los detalles de la pelicula seleccionada.
     * 
     * @param e evento de accion
     */
    private void viewMovieDetails(ActionEvent e) {
        Movie selectedMovie = getSelectedMovie();
        if (selectedMovie == null) {
            MessageUtil.showWarning(this, "Debe seleccionar una pelicula para ver detalles");
            return;
        }
        
        // Mostrar detalles en un dialogo
        StringBuilder details = new StringBuilder();
        details.append("<html><body style='width: 300px; padding: 10px;'>");
        details.append("<h2 style='color: #800080;'>").append(selectedMovie.getTitle()).append("</h2>");
        details.append("<hr>");
        details.append("<p><b>Director:</b> ").append(selectedMovie.getDirector()).append("</p>");
        details.append("<p><b>Año:</b> ").append(selectedMovie.getYear()).append("</p>");
        details.append("<p><b>Duracion:</b> ").append(selectedMovie.getFormattedDuration()).append("</p>");
        details.append("<p><b>Genero:</b> ").append(selectedMovie.getGenre()).append("</p>");
        details.append("<hr>");
        details.append("<p><i>ID: ").append(selectedMovie.getId()).append("</i></p>");
        details.append("</body></html>");
        
        JOptionPane.showMessageDialog(this, 
            new JLabel(details.toString()), 
            "Detalles de Pelicula", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Abre el formulario para editar la pelicula seleccionada.
     * 
     * @param e evento de accion
     */
    private void editMovie(ActionEvent e) {
        Movie selectedMovie = getSelectedMovie();
        if (selectedMovie == null) {
            MessageUtil.showWarning(this, "Debe seleccionar una pelicula para editar");
            return;
        }
        
        try {
            // Obtener el frame padre
            Window window = SwingUtilities.getWindowAncestor(this);
            Frame parent = null;
            if (window instanceof Frame) {
                parent = (Frame) window;
            } else if (window instanceof Dialog) {
                parent = (Frame) ((Dialog) window).getOwner();
            }
            
            // Abrir formulario de edicion
            MovieFormView movieForm = new MovieFormView(parent, movieController, selectedMovie);
            movieForm.setVisible(true);
            
            // Recargar datos despues de editar
            loadMovies();
        } catch (Exception ex) {
            MessageUtil.showError(this, "Error al abrir formulario de edicion:\n" + ex.getMessage());
        }
    }
    
    /**
     * Elimina la pelicula seleccionada.
     * 
     * @param e evento de accion
     */
    private void deleteMovie(ActionEvent e) {
        Movie selectedMovie = getSelectedMovie();
        if (selectedMovie == null) {
            MessageUtil.showWarning(this, "Debe seleccionar una pelicula para eliminar");
            return;
        }
        
        // Mostrar informacion detallada de la pelicula a eliminar
        String movieInfo = String.format(
            "Titulo: %s\nDirector: %s\nAño: %d\nDuracion: %s\nGenero: %s",
            selectedMovie.getTitle(),
            selectedMovie.getDirector(),
            selectedMovie.getYear(),
            selectedMovie.getFormattedDuration(),
            selectedMovie.getGenre()
        );
        
        // Confirmar eliminacion con informacion detallada
        int option = JOptionPane.showConfirmDialog(
            this,
            "¿Esta seguro que desea ELIMINAR PERMANENTEMENTE esta pelicula?\n\n" + 
            movieInfo + "\n\nEsta accion NO se puede deshacer.",
            "CONFIRMAR ELIMINACION",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            try {
                boolean deleted = movieController.deleteMovie(selectedMovie.getId());
                
                if (deleted) {
                    MessageUtil.showDeleteSuccess(this, 
                        "pelicula '" + selectedMovie.getTitle() + "'");
                    loadMovies(); // Recargar lista
                } else {
                    MessageUtil.showError(this, 
                        "No se pudo eliminar la pelicula. Verifique que no existan dependencias.");
                }
            } catch (Exception ex) {
                MessageUtil.showError(this, 
                    "Error al eliminar pelicula:\n" + ex.getMessage());
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }
    
    /**
     * Exporta las peliculas a un archivo.
     * 
     * @param e evento de accion
     */
    private void exportMovies(ActionEvent e) {
        try {
            // Obtener todas las peliculas
            List<Movie> movies = movieController.getAllMovies();
            
            if (movies.isEmpty()) {
                MessageUtil.showWarning(this, "No hay peliculas para exportar");
                return;
            }
            
            // Preguntar formato de exportacion
            String[] options = {"CSV", "Texto Plano", "Cancelar"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "Seleccione el formato de exportacion:",
                "Exportar Peliculas",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            if (choice == 0) {
                // Exportar a CSV
                ExportUtil.exportMoviesToCSV(movies, this);
            } else if (choice == 1) {
                // Exportar a texto plano
                ExportUtil.exportMoviesToTextFile(movies, this);
            }
            
        } catch (Exception ex) {
            MessageUtil.showError(this, "Error al exportar peliculas:\n" + ex.getMessage());
        }
    }
    
    /**
     * Establece el modo de eliminacion.
     * 
     * @param deleteMode true para activar modo eliminacion
     */
    public void setDeleteMode(boolean deleteMode) {
        this.deleteMode = deleteMode;
        
        // Configurar interfaz para modo eliminacion
        if (deleteMode) {
            // Destacar boton de eliminar
            deleteButton.setText("ELIMINAR PELÍCULA");
            deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
            deleteButton.setBackground(new Color(200, 0, 0));
            deleteButton.setForeground(Color.WHITE);
            deleteButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 0, 0), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            
            // Cambiar el título del panel para indicar modo eliminación
            JPanel titlePanel = (JPanel) getComponent(0);
            JLabel titleLabel = (JLabel) titlePanel.getComponent(0);
            titleLabel.setText("ELIMINAR Películas");
            titleLabel.setForeground(new Color(200, 0, 0));
            
            // Ocultar botones no relevantes para eliminación
            editButton.setVisible(false);
            exportButton.setVisible(false);
            
            // Forzar la actualización de los botones
            SwingUtilities.invokeLater(this::updateButtonStates);
        } else {
            // Restaurar configuración normal
            deleteButton.setText("Eliminar");
            deleteButton.setFont(null); // Usar fuente predeterminada
            deleteButton.setBorder(null); // Usar borde predeterminado
            deleteButton.setBackground(new Color(200, 0, 0)); // Restaurar color original
            deleteButton.setForeground(Color.WHITE);
            
            // Restaurar título normal
            JPanel titlePanel = (JPanel) getComponent(0);
            JLabel titleLabel = (JLabel) titlePanel.getComponent(0);
            titleLabel.setText("Listado de Películas");
            titleLabel.setForeground(new Color(128, 0, 128));
            
            // Mostrar todos los botones
            editButton.setVisible(true);
            exportButton.setVisible(true);
            
            // Forzar la actualización de los botones
            SwingUtilities.invokeLater(this::updateButtonStates);
        }
    }
}