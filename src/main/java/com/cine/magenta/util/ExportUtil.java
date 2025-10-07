/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.util;

import com.cine.magenta.model.Movie;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Utilidad para exportar datos del sistema a diferentes formatos.
 * Proporciona metodos para exportar peliculas a CSV y otros formatos.
 * Facilita la generacion de reportes y respaldos de datos.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class ExportUtil {
    
    /** Formato de fecha para nombres de archivos */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    
    /**
     * Exporta una lista de peliculas a un archivo CSV.
     * Permite al usuario seleccionar la ubicacion del archivo.
     * 
     * @param movies lista de peliculas a exportar
     * @param parentComponent componente padre para dialogo
     * @return true si la exportacion fue exitosa
     */
    public static boolean exportMoviesToCSV(List<Movie> movies, java.awt.Component parentComponent) {
        if (movies == null || movies.isEmpty()) {
            MessageUtil.showWarning(parentComponent, "No hay datos para exportar");
            return false;
        }
        
        // Crear selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Películas como CSV");
        
        // Sugerir nombre de archivo con fecha actual
        String defaultFileName = "peliculas_" + DATE_FORMAT.format(new Date()) + ".csv";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        // Filtrar por archivos CSV
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv");
        fileChooser.setFileFilter(filter);
        
        // Mostrar dialogo de guardado
        int userSelection = fileChooser.showSaveDialog(parentComponent);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Asegurar que tenga extension .csv
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }
            
            // Confirmar sobrescritura si el archivo existe
            if (fileToSave.exists()) {
                int response = JOptionPane.showConfirmDialog(
                    parentComponent,
                    "El archivo ya existe. ¿Desea sobrescribirlo?",
                    "Confirmar sobrescritura",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (response != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                // Escribir encabezados
                writer.write("ID,Título,Director,Año,Duración,Género");
                writer.newLine();
                
                // Escribir datos de cada película
                for (Movie movie : movies) {
                    StringBuilder line = new StringBuilder();
                    line.append(movie.getId()).append(",");
                    line.append(escapeCSV(movie.getTitle())).append(",");
                    line.append(escapeCSV(movie.getDirector())).append(",");
                    line.append(movie.getYear()).append(",");
                    line.append(movie.getDuration()).append(",");
                    line.append(escapeCSV(movie.getGenre()));
                    
                    writer.write(line.toString());
                    writer.newLine();
                }
                
                MessageUtil.showInfo(
                    parentComponent, 
                    "Exportación exitosa\n\n" +
                    "Se exportaron " + movies.size() + " películas al archivo:\n" + 
                    fileToSave.getAbsolutePath(),
                    "Exportar a CSV"
                );
                
                return true;
                
            } catch (IOException e) {
                MessageUtil.showError(
                    parentComponent, 
                    "Error al exportar datos:\n" + e.getMessage(),
                    "Error de Exportación"
                );
                e.printStackTrace();
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * Exporta una lista de peliculas a un archivo de texto plano.
     * Permite al usuario seleccionar la ubicacion del archivo.
     * 
     * @param movies lista de peliculas a exportar
     * @param parentComponent componente padre para dialogo
     * @return true si la exportacion fue exitosa
     */
    public static boolean exportMoviesToTextFile(List<Movie> movies, java.awt.Component parentComponent) {
        if (movies == null || movies.isEmpty()) {
            MessageUtil.showWarning(parentComponent, "No hay datos para exportar");
            return false;
        }
        
        // Crear selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Películas como Texto");
        
        // Sugerir nombre de archivo con fecha actual
        String defaultFileName = "peliculas_" + DATE_FORMAT.format(new Date()) + ".txt";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        // Filtrar por archivos de texto
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        
        // Mostrar dialogo de guardado
        int userSelection = fileChooser.showSaveDialog(parentComponent);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Asegurar que tenga extension .txt
            if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
            }
            
            // Confirmar sobrescritura si el archivo existe
            if (fileToSave.exists()) {
                int response = JOptionPane.showConfirmDialog(
                    parentComponent,
                    "El archivo ya existe. ¿Desea sobrescribirlo?",
                    "Confirmar sobrescritura",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (response != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                // Escribir encabezado
                writer.write("LISTADO DE PELÍCULAS - CINE MAGENTA");
                writer.newLine();
                writer.write("Fecha de exportación: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                writer.newLine();
                writer.write("Total de películas: " + movies.size());
                writer.newLine();
                writer.write("=".repeat(50));
                writer.newLine();
                writer.newLine();
                
                // Escribir datos de cada película en formato legible
                for (Movie movie : movies) {
                    writer.write("ID: " + movie.getId());
                    writer.newLine();
                    writer.write("Título: " + movie.getTitle());
                    writer.newLine();
                    writer.write("Director: " + movie.getDirector());
                    writer.newLine();
                    writer.write("Año: " + movie.getYear());
                    writer.newLine();
                    writer.write("Duración: " + movie.getFormattedDuration());
                    writer.newLine();
                    writer.write("Género: " + movie.getGenre());
                    writer.newLine();
                    writer.write("-".repeat(50));
                    writer.newLine();
                }
                
                MessageUtil.showInfo(
                    parentComponent, 
                    "Exportación exitosa\n\n" +
                    "Se exportaron " + movies.size() + " películas al archivo:\n" + 
                    fileToSave.getAbsolutePath(),
                    "Exportar a Texto"
                );
                
                return true;
                
            } catch (IOException e) {
                MessageUtil.showError(
                    parentComponent, 
                    "Error al exportar datos:\n" + e.getMessage(),
                    "Error de Exportación"
                );
                e.printStackTrace();
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * Escapa caracteres especiales para formato CSV.
     * Previene problemas con comas y comillas en los datos.
     * 
     * @param value texto a escapar
     * @return texto escapado para CSV
     */
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        
        // Si contiene comas, comillas o saltos de línea, encerrar en comillas
        boolean needsQuotes = value.contains(",") || value.contains("\"") || 
                             value.contains("\n") || value.contains("\r");
        
        if (needsQuotes) {
            // Duplicar comillas dentro del texto
            String escaped = value.replace("\"", "\"\"");
            return "\"" + escaped + "\"";
        }
        
        return value;
    }
}