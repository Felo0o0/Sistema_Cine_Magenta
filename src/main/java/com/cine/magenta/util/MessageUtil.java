/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cine.magenta.util;

import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * Utilidades para mostrar mensajes al usuario de forma consistente.
 * Centraliza el manejo de dialogos y notificaciones del sistema.
 * Proporciona metodos estaticos para diferentes tipos de mensajes.
 *
 * @author Sistema Cine Magenta
 * @version 1.0
 */
public class MessageUtil {
    
    /**
     * Muestra un mensaje de informacion al usuario.
     * 
     * @param parent componente padre para el dialogo
     * @param message mensaje a mostrar
     * @param title titulo del dialogo
     */
    public static void showInfo(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Muestra un mensaje de informacion con titulo por defecto.
     * 
     * @param parent componente padre para el dialogo
     * @param message mensaje a mostrar
     */
    public static void showInfo(Component parent, String message) {
        showInfo(parent, message, "Informacion");
    }
    
    /**
     * Muestra un mensaje de advertencia al usuario.
     * 
     * @param parent componente padre para el dialogo
     * @param message mensaje de advertencia
     * @param title titulo del dialogo
     */
    public static void showWarning(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Muestra un mensaje de advertencia con titulo por defecto.
     * 
     * @param parent componente padre para el dialogo
     * @param message mensaje de advertencia
     */
    public static void showWarning(Component parent, String message) {
        showWarning(parent, message, "Advertencia");
    }
    
    /**
     * Muestra un mensaje de error al usuario.
     * 
     * @param parent componente padre para el dialogo
     * @param message mensaje de error
     * @param title titulo del dialogo
     */
    public static void showError(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Muestra un mensaje de error con titulo por defecto.
     * 
     * @param parent componente padre para el dialogo
     * @param message mensaje de error
     */
    public static void showError(Component parent, String message) {
        showError(parent, message, "Error");
    }
    
    /**
     * Muestra un dialogo de confirmacion Si/No.
     * 
     * @param parent componente padre para el dialogo
     * @param message mensaje de confirmacion
     * @param title titulo del dialogo
     * @return true si el usuario selecciono Si
     */
    public static boolean showConfirm(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, 
            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Muestra un dialogo de confirmacion con titulo por defecto.
     * 
     * @param parent componente padre para el dialogo
     * @param message mensaje de confirmacion
     * @return true si el usuario selecciono Si
     */
    public static boolean showConfirm(Component parent, String message) {
        return showConfirm(parent, message, "Confirmacion");
    }
    
    /**
     * Muestra un dialogo de confirmacion para eliminar elemento.
     * 
     * @param parent componente padre para el dialogo
     * @param itemName nombre del elemento a eliminar
     * @return true si el usuario confirma la eliminacion
     */
    public static boolean showDeleteConfirm(Component parent, String itemName) {
        return showConfirm(parent, 
            "Esta seguro que desea eliminar:\n" + itemName + "\n\nEsta accion no se puede deshacer.",
            "Confirmar Eliminacion");
    }
    
    /**
     * Muestra un dialogo para capturar texto del usuario.
     * 
     * @param parent componente padre para el dialogo
     * @param message mensaje descriptivo
     * @param title titulo del dialogo
     * @param initialValue valor inicial del campo
     * @return texto ingresado por el usuario o null si cancelo
     */
    public static String showInput(Component parent, String message, String title, String initialValue) {
        return JOptionPane.showInputDialog(parent, message, title, 
            JOptionPane.QUESTION_MESSAGE, null, null, initialValue).toString();
    }
    
    /**
     * Muestra un dialogo para capturar texto del usuario.
     * 
     * @param parent componente padre para el dialogo
     * @param message mensaje descriptivo
     * @param title titulo del dialogo
     * @return texto ingresado por el usuario o null si cancelo
     */
    public static String showInput(Component parent, String message, String title) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE);
    }
    
    /**
     * Muestra un dialogo para capturar texto con titulo por defecto.
     * 
     * @param parent componente padre para el dialogo
     * @param message mensaje descriptivo
     * @return texto ingresado por el usuario o null si cancelo
     */
    public static String showInput(Component parent, String message) {
        return showInput(parent, message, "Entrada de Datos");
    }
    
    /**
     * Muestra un mensaje de exito para operaciones completadas.
     * 
     * @param parent componente padre para el dialogo
     * @param operation nombre de la operacion realizada
     */
    public static void showSuccess(Component parent, String operation) {
        showInfo(parent, operation + " realizada exitosamente", "Operacion Exitosa");
    }
    
    /**
     * Muestra mensaje de exito para creacion de elementos.
     * 
     * @param parent componente padre para el dialogo
     * @param itemName nombre del elemento creado
     */
    public static void showCreateSuccess(Component parent, String itemName) {
        showSuccess(parent, "Creacion de " + itemName);
    }
    
    /**
     * Muestra mensaje de exito para actualizacion de elementos.
     * 
     * @param parent componente padre para el dialogo
     * @param itemName nombre del elemento actualizado
     */
    public static void showUpdateSuccess(Component parent, String itemName) {
        showSuccess(parent, "Actualizacion de " + itemName);
    }
    
    /**
     * Muestra mensaje de exito para eliminacion de elementos.
     * 
     * @param parent componente padre para el dialogo
     * @param itemName nombre del elemento eliminado
     */
    public static void showDeleteSuccess(Component parent, String itemName) {
        showSuccess(parent, "Eliminacion de " + itemName);
    }
    
    /**
     * Constructor privado para evitar instanciacion.
     * Esta clase contiene solo metodos estaticos.
     */
    private MessageUtil() {
        // Clase de utilidad - no debe ser instanciada
    }
}