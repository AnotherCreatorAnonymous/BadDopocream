package com.duran_jimenez.baddopocream.presentation;

import com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Configurador de imágenes para las frutas del juego
 * Carga y almacena las imágenes de cada tipo de fruta
 * Optimizado para renderizado rápido de GIFs animados
 * Hereda de BaseImageConfigurator para usar carga optimizada con getResource()
 */
public class FruitImageConfigurator extends BaseImageConfigurator {
    
    private static final String BASE_PATH = "Frutas\\";
    private static final int DEFAULT_SIZE = 30; // Tamaño de celda por defecto
    
    // Mapa que almacena las imágenes de cada fruta por nombre
    private static final Map<String, Image> fruitImages = new HashMap<>();
    
    // Inicialización estática de las imágenes
    static {
        loadFruitImage("Banana", "Banana\\Normal.gif");
        loadFruitImage("Cherry", "Cherry\\Normal.gif");
        loadFruitImage("Grapes", "Grapes\\Normal.gif");
        loadFruitImage("Pineapple", "Pineapple\\Movement.gif");
        loadFruitImage("Cactus", "Cactus\\Normal.gif");
        
        // Esperar a que todas las imágenes se carguen completamente usando método heredado
        waitForAllImages("frutas");
    }
    
    /**
     * Carga una imagen de fruta y la almacena en el mapa
     * Usa método heredado de BaseImageConfigurator con getResource()
     * @param fruitName Nombre de la fruta
     * @param relativePath Ruta relativa desde la carpeta Frutas
     */
    private static void loadFruitImage(String fruitName, String relativePath) {
        try {
            String resourcePath = BASE_PATH + relativePath;
            Image img = loadImage(resourcePath);
            
            if (img != null) {
                fruitImages.put(fruitName, img);
                System.out.println("✓ Imagen cargada: " + fruitName + " -> " + resourcePath);
            } else {
                throw new BadDopoCream_Exceptions(
                    BadDopoCream_Exceptions.MISSING_IMAGE + ": " + resourcePath);
            }
        } catch (BadDopoCream_Exceptions e) {
            System.err.println("✗ Error cargando imagen de " + fruitName + ": " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la imagen de una fruta por su nombre
     * @param fruitName Nombre de la fruta (Banana, Cherry, Grapes, Pineapple)
     * @return Image de la fruta o null si no existe
     */
    public static Image getFruitImage(String fruitName) {
        return fruitImages.get(fruitName);
    }
    
    /**
     * Obtiene una imagen de fruta escalada a un tamaño específico
     * NOTA: Para GIFs animados, NO usar getScaledInstance() porque rompe la animación
     * @param fruitName Nombre de la fruta
     * @param size Tamaño deseado (ancho y alto) - IGNORADO para mantener animación
     * @return Image original o null si no existe
     */
    public static Image getFruitImage(String fruitName, int size) {
        // Retornar imagen original sin escalar para preservar animación GIF
        return fruitImages.get(fruitName);
    }
    
    /**
     * Verifica si existe la imagen de una fruta
     * @param fruitName Nombre de la fruta
     * @return true si existe la imagen, false en caso contrario
     */
    public static boolean hasFruitImage(String fruitName) {
        return fruitImages.containsKey(fruitName);
    }
    
    /**
     * Dibuja la imagen de una fruta en las coordenadas especificadas
     * @param g Graphics2D para dibujar
     * @param fruitName Nombre de la fruta
     * @param x Coordenada X
     * @param y Coordenada Y
     * @param size Tamaño de la imagen
     */
    public static void drawFruit(Graphics2D g, String fruitName, int x, int y, int size) {
        Image img = getFruitImage(fruitName, size);
        if (img != null) {
            // Dibujar con tamaño especificado para ajustar al tamaño de celda
            g.drawImage(img, x, y, size, size, null);
        } else {
            // Fallback: dibujar círculo de color si no hay imagen
            drawFallbackFruit(g, fruitName, x, y, size);
        }
    }
    
    /**
     * Dibuja un círculo de color como fallback si no hay imagen
     * @param g Graphics2D para dibujar
     * @param fruitName Nombre de la fruta
     * @param x Coordenada X
     * @param y Coordenada Y
     * @param size Tamaño del círculo
     */
    private static void drawFallbackFruit(Graphics2D g, String fruitName, int x, int y, int size) {
        Color color = switch (fruitName.toLowerCase()) {
            case "banana" -> new Color(255, 255, 0);
            case "cherry" -> new Color(255, 0, 0);
            case "grapes" -> new Color(128, 0, 128);
            case "pineapple" -> new Color(255, 165, 0);
            case "cactus" -> new Color(34, 139, 34);
            default -> Color.ORANGE;
        };
        
        g.setColor(color);
        g.fillOval(x, y, size, size);
        g.setColor(Color.WHITE);
        g.drawOval(x, y, size, size);
    }
}
