package com.duran_jimenez.baddopocream.presentation;

import com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions;
import java.awt.*;
import java.net.URL;
import javax.swing.*;

/**
 * Clase base abstracta para configuradores de imágenes
 * Centraliza la lógica común de carga optimizada de GIFs animados
 * Usa getResource() con Toolkit para mejor caché y rendimiento
 */
public abstract class BaseImageConfigurator {
    
    // MediaTracker compartido para todas las subclases
    protected static final MediaTracker tracker = new MediaTracker(new JLabel());
    protected static int trackerID = 0;
    
    /**
     * Carga una imagen desde recursos usando getResource() para mejor caché
     * Usa MediaTracker para asegurar que la imagen esté completamente cargada
     * @param resourcePath Ruta del recurso (relativa a )
     * @return Image con la imagen/animación o null si no se pudo cargar
     */
    protected static Image loadImage(String resourcePath) {
        try {
            // Normalizar la ruta para usar / en lugar de \
            String normalizedPath = "/" + resourcePath.replace("\\", "/");
            
            // Intentar cargar desde classpath usando getResource()
            URL resourceUrl = BaseImageConfigurator.class.getResource(normalizedPath);
            
            if (resourceUrl != null) {
                // Usar Toolkit.createImage() con URL para mejor caché
                Image img = Toolkit.getDefaultToolkit().createImage(resourceUrl);
                
                // Agregar al MediaTracker para pre-cargar la imagen
                tracker.addImage(img, trackerID++);
                
                return img;
            } else {
                // Si no se encuentra en classpath, intentar desde filesystem
                Image fallbackImg = loadImageFromFile(resourcePath);
                if (fallbackImg == null) {
                    // Imagen no encontrada ni en classpath ni en filesystem
                    throw new BadDopoCream_Exceptions(
                        BadDopoCream_Exceptions.MISSING_IMAGE + ": " + resourcePath);
                }
                return fallbackImg;
            }
        } catch (BadDopoCream_Exceptions e) {
            System.err.println("⚠️  " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("⚠️  Error inesperado cargando imagen: " + resourcePath + " - " + e.getMessage());
            BadDopoCream_Exceptions ex = 
                new BadDopoCream_Exceptions(
                    BadDopoCream_Exceptions.IMAGE_LOAD_ERROR + ": " + resourcePath, e);
            System.err.println("⚠️  " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * Carga una imagen desde el sistema de archivos como fallback
     * @param filePath Ruta del archivo
     * @return Image o null si no se pudo cargar
     */
    private static Image loadImageFromFile(String filePath) {
        try {
            java.io.File imageFile = new java.io.File(filePath);
            if (imageFile.exists()) {
                Image img = Toolkit.getDefaultToolkit().createImage(filePath);
                tracker.addImage(img, trackerID++);
                return img;
            }
            return null;
        } catch (Exception e) {
            // Captura cualquier error en la carga desde filesystem
            BadDopoCream_Exceptions ex = 
                new BadDopoCream_Exceptions(
                    BadDopoCream_Exceptions.IMAGE_LOAD_ERROR + ": " + filePath, e);
            System.err.println("⚠️  " + ex.getMessage());
            return null;
        }
    }
    
    /**
     * Espera a que todas las imágenes registradas en el tracker se carguen
     * Debe llamarse después de cargar todas las imágenes
     * @param resourceType Tipo de recurso que se está cargando (para logging)
     */
    protected static void waitForAllImages(String resourceType) {
        try {
            tracker.waitForAll();
            System.out.println("✓ Todas las imágenes de " + resourceType + " precargadas en memoria");
        } catch (InterruptedException e) {
            BadDopoCream_Exceptions ex = 
                new BadDopoCream_Exceptions(
                    BadDopoCream_Exceptions.GAME_INTERRUPTED + 
                    ": Carga de imágenes de " + resourceType, e);
            System.err.println("⚠️  " + ex.getMessage());
        }
    }
    
    /**
     * Verifica si un recurso existe
     * @param resourcePath Ruta del recurso
     * @return true si existe, false en caso contrario
     */
    protected static boolean resourceExists(String resourcePath) {
        String normalizedPath = "/" + resourcePath.replace("\\", "/");
        URL resourceUrl = BaseImageConfigurator.class.getResource(normalizedPath);
        if (resourceUrl != null) {
            return true;
        }
        // Verificar en filesystem como fallback
        return new java.io.File(resourcePath).exists();
    }
    
    /**
     * Dibuja una imagen en las coordenadas especificadas
     * @param g Graphics2D para dibujar
     * @param img Image a dibujar
     * @param x Coordenada X
     * @param y Coordenada Y
     * @param width Ancho de la imagen
     * @param height Alto de la imagen
     */
    protected static void drawImage(Graphics2D g, Image img, int x, int y, int width, int height) {
        if (img != null) {
            g.drawImage(img, x, y, width, height, null);
        }
    }
    
    /**
     * Dibuja un rectángulo de color como fallback
     * @param g Graphics2D para dibujar
     * @param color Color del rectángulo
     * @param x Coordenada X
     * @param y Coordenada Y
     * @param width Ancho
     * @param height Alto
     * @param isOval Si es true dibuja óvalo, si es false dibuja rectángulo
     */
    protected static void drawFallback(Graphics2D g, Color color, int x, int y, 
                                      int width, int height, boolean isOval) {
        g.setColor(color);
        if (isOval) {
            g.fillOval(x, y, width, height);
        } else {
            g.fillRect(x, y, width, height);
        }
        g.setColor(Color.WHITE);
        if (isOval) {
            g.drawOval(x, y, width, height);
        } else {
            g.drawRect(x, y, width, height);
        }
    }
    
    /**
     * Construye una ruta completa de recurso
     * @param basePath Ruta base (ej: "Personajes/")
     * @param subPath Subruta o subcarpeta
     * @param fileName Nombre del archivo
     * @return Ruta completa
     */
    protected static String buildPath(String basePath, String subPath, String fileName) {
        if (subPath != null && !subPath.isEmpty()) {
            return basePath + subPath + "/" + fileName;
        }
        return basePath + fileName;
    }
    
    /**
     * Imprime un mensaje de carga exitosa
     * @param resourceName Nombre del recurso
     * @param resourcePath Ruta del recurso
     */
    protected static void logSuccess(String resourceName, String resourcePath) {
        System.out.println("  ✓ " + resourceName + " cargada");
    }
    
    /**
     * Imprime un mensaje de error de carga
     * @param resourceName Nombre del recurso
     * @param resourcePath Ruta del recurso
     */
    protected static void logError(String resourceName, String resourcePath) {
        System.err.println("  ✗ " + resourceName + " no encontrada: " + resourcePath);
    }
    
    /**
     * Imprime un encabezado de sección de carga
     * @param sectionName Nombre de la sección
     */
    protected static void logSectionHeader(String sectionName) {
        System.out.println("📦 Cargando " + sectionName);
    }
    
    /**
     * Imprime el total de recursos cargados
     * @param loaded Cantidad cargada
     * @param total Cantidad total
     */
    protected static void logTotal(int loaded, int total) {
        System.out.println("  Total: " + loaded + "/" + total + " animaciones\n");
    }
    
    /**
     * Carga una imagen y retorna ImageIcon (para compatibilidad con código existente)
     * @param resourcePath Ruta del recurso
     * @return ImageIcon o null si no se pudo cargar
     */
    protected static ImageIcon loadImageIcon(String resourcePath) {
        Image img = loadImage(resourcePath);
        return img != null ? new ImageIcon(img) : null;
    }
}
