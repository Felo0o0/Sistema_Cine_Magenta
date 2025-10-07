/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.util;

import com.cine.magenta.config.DatabaseConfig;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import java.awt.Color;

/**
 * Utilidades para validacion de datos del sistema.
 * Proporciona metodos estaticos para validaciones comunes.
 * Centraliza la logica de validacion para reutilizacion.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class ValidationUtil {
    
    /**
    * Valida si una cadena no esta vacia o nula.
    * 
    * @param value cadena a validar
    * @return true si la cadena es valida
    */
    public static boolean isNotEmpty(String value) {
    return value != null && !value.trim().isEmpty();
    }
    
    /**
    * Valida la longitud de una cadena.
    * 
    * @param value cadena a validar
    * @param maxLength longitud maxima permitida
    * @return true si la longitud es valida
    */
    public static boolean isValidLength(String value, int maxLength) {
    return value != null && value.length() <= maxLength;
    }
    
    /**
    * Valida que un numero este dentro de un rango.
    * 
    * @param value numero a validar
    * @param min valor minimo inclusivo
    * @param max valor maximo inclusivo
    * @return true si el valor esta en el rango
    */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
    * Valida si un texto puede convertirse a un numero entero.
    * 
    * @param text texto a validar
    * @return true si el texto es un numero entero valido
    */
    public static boolean isValidInteger(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(text.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
    * Valida el titulo de una pelicula.
    * 
    * @param title titulo a validar
    * @return true si el titulo es valido
    */
    public static boolean isValidTitle(String title) {
        return isNotEmpty(title) && 
               isValidLength(title, DatabaseConfig.MAX_TITLE_LENGTH) &&
               isValidTitleFormat(title);
    }
    
    /**
    * Valida el formato del titulo de una pelicula.
    * Verifica que no contenga caracteres especiales no permitidos.
    * 
    * @param title titulo a validar
    * @return true si el formato del titulo es valido
    */
    public static boolean isValidTitleFormat(String title) {
        if (title == null) return false;
        // Permite letras, números, espacios y algunos caracteres especiales comunes en títulos
        String pattern = "^[\\p{L}\\p{N}\\s\\-_:;,.!¡?¿'\"()\\[\\]{}]+$";
        return Pattern.matches(pattern, title);
    }
    
    /**
    * Valida el nombre de un director.
    * 
    * @param director nombre del director a validar
    * @return true si el nombre es valido
    */
    public static boolean isValidDirector(String director) {
        return isNotEmpty(director) && 
               isValidLength(director, DatabaseConfig.MAX_DIRECTOR_LENGTH) &&
               isValidDirectorFormat(director);
    }
    
    /**
    * Valida el formato del nombre de un director.
    * Verifica que contenga solo caracteres validos para nombres.
    * 
    * @param director nombre del director a validar
    * @return true si el formato del nombre es valido
    */
    public static boolean isValidDirectorFormat(String director) {
        if (director == null) return false;
        // Permite letras, espacios y algunos caracteres especiales comunes en nombres
        String pattern = "^[\\p{L}\\s\\-'.]+$";
        return Pattern.matches(pattern, director);
    }
    
    /**
    * Valida un año de estreno.
    * 
    * @param year año a validar
    * @return true si el año es valido
    */
    public static boolean isValidYear(int year) {
    return isInRange(year, DatabaseConfig.MIN_YEAR, DatabaseConfig.MAX_YEAR);
    }
    
    /**
    * Valida la duracion de una pelicula.
    * 
    * @param duration duracion en minutos a validar
    * @return true si la duracion es valida
    */
    public static boolean isValidDuration(int duration) {
    return isInRange(duration, DatabaseConfig.MIN_DURATION, DatabaseConfig.MAX_DURATION);
    }
    
    /**
    * Valida si un genero esta en la lista de generos permitidos.
    * 
    * @param genre genero a validar
    * @return true si el genero es valido
    */
    public static boolean isValidGenre(String genre) {
    return isNotEmpty(genre) && Arrays.asList(DatabaseConfig.AVAILABLE_GENRES).contains(genre);
    }
    
    /**
    * Obtiene mensaje de error para titulo invalido.
    * 
    * @param title titulo que fallo la validacion
    * @return mensaje de error descriptivo
    */
    public static String getTitleErrorMessage(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "El titulo es obligatorio";
        } else if (title.length() > DatabaseConfig.MAX_TITLE_LENGTH) {
            return "El titulo no puede exceder " + DatabaseConfig.MAX_TITLE_LENGTH + " caracteres";
        } else if (!isValidTitleFormat(title)) {
            return "El titulo contiene caracteres no permitidos";
        }
        return "Titulo valido";
    }
    
    /**
    * Obtiene mensaje de error para director invalido.
    * 
    * @param director director que fallo la validacion
    * @return mensaje de error descriptivo
    */
    public static String getDirectorErrorMessage(String director) {
        if (director == null || director.trim().isEmpty()) {
            return "El director es obligatorio";
        } else if (director.length() > DatabaseConfig.MAX_DIRECTOR_LENGTH) {
            return "El director no puede exceder " + DatabaseConfig.MAX_DIRECTOR_LENGTH + " caracteres";
        } else if (!isValidDirectorFormat(director)) {
            return "El nombre del director contiene caracteres no permitidos";
        }
        return "Director valido";
    }
    
    /**
    * Obtiene mensaje de error para año invalido.
    * 
    * @param year año que fallo la validacion
    * @return mensaje de error descriptivo
    */
    public static String getYearErrorMessage(int year) {
    if (year < DatabaseConfig.MIN_YEAR) {
    return "El año no puede ser menor a " + DatabaseConfig.MIN_YEAR;
    } else if (year > DatabaseConfig.MAX_YEAR) {
    return "El año no puede ser mayor a " + DatabaseConfig.MAX_YEAR;
    }
    return "Año valido";
    }
    
    /**
    * Obtiene mensaje de error para duracion invalida.
    * 
    * @param duration duracion que fallo la validacion
    * @return mensaje de error descriptivo
    */
    public static String getDurationErrorMessage(int duration) {
    if (duration < DatabaseConfig.MIN_DURATION) {
    return "La duracion debe ser al menos " + DatabaseConfig.MIN_DURATION + " minuto";
    } else if (duration > DatabaseConfig.MAX_DURATION) {
    return "La duracion no puede exceder " + DatabaseConfig.MAX_DURATION + " minutos";
    }
    return "Duracion valida";
    }
    
    /**
    * Obtiene mensaje de error para genero invalido.
    * 
    * @param genre genero que fallo la validacion
    * @return mensaje de error descriptivo
    */
    public static String getGenreErrorMessage(String genre) {
    if (genre == null || genre.trim().isEmpty()) {
    return "El genero es obligatorio";
    } else if (!Arrays.asList(DatabaseConfig.AVAILABLE_GENRES).contains(genre)) {
    return "Genero no valido. Generos disponibles: " + Arrays.toString(DatabaseConfig.AVAILABLE_GENRES);
    }
    return "Genero valido";
    }
    
    /**
    * Aplica validacion visual en tiempo real a un campo de texto.
    * Cambia el color de fondo y el tooltip segun la validacion.
    * 
    * @param field campo de texto a validar
    * @param isValid resultado de la validacion
    * @param errorMessage mensaje de error si la validacion falla
    * @param validMessage mensaje si la validacion es exitosa
    */
    public static void applyVisualValidation(JTextField field, boolean isValid, String errorMessage, String validMessage) {
        if (isValid) {
            field.setBackground(Color.WHITE);
            field.setToolTipText(validMessage);
        } else {
            field.setBackground(new Color(255, 200, 200));
            field.setToolTipText(errorMessage);
        }
    }
    
    /**
    * Aplica validacion visual en tiempo real a un spinner.
    * Cambia el color de fondo y el tooltip segun la validacion.
    * 
    * @param spinner spinner a validar
    * @param isValid resultado de la validacion
    * @param errorMessage mensaje de error si la validacion falla
    * @param validMessage mensaje si la validacion es exitosa
    */
    public static void applyVisualValidation(JSpinner spinner, boolean isValid, String errorMessage, String validMessage) {
        JComponent editor = spinner.getEditor();
        JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor)editor;
        JTextField textField = spinnerEditor.getTextField();
        
        if (isValid) {
            textField.setBackground(Color.WHITE);
            spinner.setToolTipText(validMessage);
        } else {
            textField.setBackground(new Color(255, 200, 200));
            spinner.setToolTipText(errorMessage);
        }
    }
    
    /**
    * Aplica validacion visual en tiempo real a un combobox.
    * Cambia el color de fondo y el tooltip segun la validacion.
    * 
    * @param comboBox combobox a validar
    * @param isValid resultado de la validacion
    * @param errorMessage mensaje de error si la validacion falla
    * @param validMessage mensaje si la validacion es exitosa
    */
    public static void applyVisualValidation(JComboBox<?> comboBox, boolean isValid, String errorMessage, String validMessage) {
        if (isValid) {
            comboBox.setBackground(Color.WHITE);
            comboBox.setToolTipText(validMessage);
        } else {
            comboBox.setBackground(new Color(255, 200, 200));
            comboBox.setToolTipText(errorMessage);
        }
    }
    
    /**
    * Valida todos los campos de una pelicula.
    * 
    * @param title titulo de la pelicula
    * @param director director de la pelicula
    * @param yearStr año de la pelicula como texto
    * @param durationStr duracion de la pelicula como texto
    * @param genre genero de la pelicula
    * @return array de booleanos indicando si cada campo es valido [titulo, director, año, duracion, genero]
    */
    public static boolean[] validateAllFields(String title, String director, String yearStr, String durationStr, String genre) {
        boolean[] results = new boolean[5];
        
        // Validar título
        results[0] = isValidTitle(title);
        
        // Validar director
        results[1] = isValidDirector(director);
        
        // Validar año
        results[2] = isValidInteger(yearStr);
        if (results[2]) {
            int year = Integer.parseInt(yearStr);
            results[2] = isValidYear(year);
        }
        
        // Validar duración
        results[3] = isValidInteger(durationStr);
        if (results[3]) {
            int duration = Integer.parseInt(durationStr);
            results[3] = isValidDuration(duration);
        }
        
        // Validar género
        results[4] = isValidGenre(genre);
        
        return results;
    }
    
    /**
    * Obtiene mensajes de error para todos los campos de una pelicula.
    * 
    * @param title titulo de la pelicula
    * @param director director de la pelicula
    * @param yearStr año de la pelicula como texto
    * @param durationStr duracion de la pelicula como texto
    * @param genre genero de la pelicula
    * @return array de mensajes de error para cada campo
    */
    public static String[] getAllErrorMessages(String title, String director, String yearStr, String durationStr, String genre) {
        String[] messages = new String[5];
        
        // Mensaje para título
        messages[0] = getTitleErrorMessage(title);
        
        // Mensaje para director
        messages[1] = getDirectorErrorMessage(director);
        
        // Mensaje para año
        if (!isValidInteger(yearStr)) {
            messages[2] = "El año debe ser un numero entero valido";
        } else {
            int year = Integer.parseInt(yearStr);
            messages[2] = getYearErrorMessage(year);
        }
        
        // Mensaje para duración
        if (!isValidInteger(durationStr)) {
            messages[3] = "La duracion debe ser un numero entero valido";
        } else {
            int duration = Integer.parseInt(durationStr);
            messages[3] = getDurationErrorMessage(duration);
        }
        
        // Mensaje para género
        messages[4] = getGenreErrorMessage(genre);
        
        return messages;
    }
    
    /**
    * Constructor privado para evitar instanciacion.
    * Esta clase contiene solo metodos estaticos.
    */
    private ValidationUtil() {
        // Clase de utilidad - no debe ser instanciada
    }
}