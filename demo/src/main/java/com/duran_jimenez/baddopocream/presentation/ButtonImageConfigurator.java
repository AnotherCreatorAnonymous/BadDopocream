package com.duran_jimenez.baddopocream.presentation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * Clase utilitaria para configurar botones con imágenes de manera global
 * Reutilizable en todas las clases de presentación
 * Hereda de BaseImageConfigurator para usar carga optimizada con getResource()
 * @param button Botón a configurar
 * @param normalImagePath Ruta a la imagen normal (no hover)
 * @param pressedImagePath Ruta a la imagen al presionar/hover
 */
public class ButtonImageConfigurator extends BaseImageConfigurator {
    
    // Valores por defecto para tamaño de botones
    private static final int DEFAULT_WIDTH = 200;
    private static final int DEFAULT_HEIGHT = 70;
    
    /**
     * Configura un botón con imágenes normal y presionada (tamaño por defecto)
     * @param button Botón a configurar
     * @param normalImagePath Ruta a la imagen normal (no hover)
     * @param pressedImagePath Ruta a la imagen al presionar/hover
     */
    public static void configureImageButton(JButton button, String normalImagePath, String pressedImagePath) {
        configureImageButton(button, normalImagePath, pressedImagePath, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    // Método loadImageIcon ahora es heredado de BaseImageConfigurator
    
    /**
     * Configura un botón con imágenes normal y presionada (tamaño personalizado)
     * @param button Botón a configurar
     * @param normalImagePath Ruta a la imagen normal (no hover)
     * @param pressedImagePath Ruta a la imagen al presionar/hover
     * @param width Ancho del botón en píxeles
     * @param height Alto del botón en píxeles
     */
    public static void configureImageButton(JButton button, String normalImagePath, String pressedImagePath, int width, int height) {
        // Configurar propiedades visuales del botón
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);

        // Cargar imágenes
        ImageIcon normalIconTemp = loadImageIcon(normalImagePath);
        ImageIcon pressedIconTemp = loadImageIcon(pressedImagePath);

        // Validar que las imágenes se cargaron correctamente
        if (normalIconTemp == null || normalIconTemp.getImage() == null || 
            pressedIconTemp == null || pressedIconTemp.getImage() == null) {
            System.err.println("⚠️  Error cargando imágenes: " + normalImagePath + " o " + pressedImagePath);
            System.err.println("    Revisa que las rutas sean correctas y los archivos existan");
            // Fallback: usar el botón estándar si las imágenes no cargan
            button.setBorderPainted(true);
            button.setContentAreaFilled(true);
            button.setOpaque(true);
            button.setText("IMG ERR");
            return;
        }

        // Escalar imágenes al tamaño definido
        Image scaledNormal = normalIconTemp.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        Image scaledPressed = pressedIconTemp.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        
        // Crear nuevos ImageIcons con las imágenes escaladas (hacer final para listener)
        final ImageIcon normalIcon = new ImageIcon(scaledNormal);
        final ImageIcon pressedIcon = new ImageIcon(scaledPressed);

        // Establecer icono normal
        button.setIcon(normalIcon);
        button.setPressedIcon(pressedIcon);
        
        // Establecer tamaño preferido del botón
        button.setPreferredSize(new Dimension(width, height));

        // Agregar listener para cambiar icono al pasar el ratón (hover effect)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setIcon(pressedIcon);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setIcon(normalIcon);
            }
        });
    }
    
    /**
     * Retorna el ancho por defecto de los botones
     */
    public static int getDefaultWidth() {
        return DEFAULT_WIDTH;
    }
    
    /**
     * Retorna el alto por defecto de los botones
     */
    public static int getDefaultHeight() {
        return DEFAULT_HEIGHT;
    }
}
