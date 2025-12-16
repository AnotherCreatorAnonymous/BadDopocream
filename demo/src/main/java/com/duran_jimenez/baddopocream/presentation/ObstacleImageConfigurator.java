package com.duran_jimenez.baddopocream.presentation;

import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Configurador de imágenes para obstáculos (baldosas calientes y fogatas)
 */
public class ObstacleImageConfigurator extends BaseImageConfigurator {
    
    public enum ObstacleType {
        HOT_TILE,
        CAMPFIRE
    }
    
    public enum AnimationType {
        HOT_TILE_ACTIVE,
        CAMPFIRE_LIT,
        CAMPFIRE_EXTINGUISHED
    }
    
    private static Map<AnimationType, Image> obstacleImages = new HashMap<>();
    
    // Cargar todas las imágenes al iniciar
    static {
        loadAllObstacleImages();
    }
    
    /**
     * Carga todas las imágenes de obstáculos
     */
    private static void loadAllObstacleImages() {
        // Baldosa caliente
        loadObstacleImage(AnimationType.HOT_TILE_ACTIVE, 
            "Obstaculos/Obstaculos/Baldosa_Caliente/frames.gif");
        
        // Fogata encendida
        loadObstacleImage(AnimationType.CAMPFIRE_LIT, 
            "Obstaculos/Obstaculos/Fogata/Flame.gif");
        
        // Fogata apagada (usa la misma imagen pero más oscura, o una imagen diferente)
        // Por ahora usamos la llama para diferenciarlo visualmente
        loadObstacleImage(AnimationType.CAMPFIRE_EXTINGUISHED, 
            "Obstaculos/Obstaculos/Fogata/CampFire.gif");
    }
    
    /**
     * Carga una imagen de obstáculo individual
     */
    private static void loadObstacleImage(AnimationType type, String path) {
        try {
            URL resourceUrl = ObstacleImageConfigurator.class.getClassLoader().getResource(path);
            if (resourceUrl != null) {
                Image image = java.awt.Toolkit.getDefaultToolkit().createImage(resourceUrl);
                obstacleImages.put(type, image);
            } else {
                throw new com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions(
                    com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions.MISSING_IMAGE + ": " + path);
            }
        } catch (com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions e) {
            System.err.println("⚠️ Error cargando imagen de obstáculo " + type + ": " + e.getMessage());
        } catch (Exception e) {
            com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions ex = 
                new com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions(
                    com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions.IMAGE_LOAD_ERROR + ": " + path, e);
            System.err.println("❌ " + ex.getMessage());
        }
    }
    
    /**
     * Obtiene la imagen de una baldosa caliente
     */
    public static Image getHotTileImage() {
        return obstacleImages.get(AnimationType.HOT_TILE_ACTIVE);
    }
    
    /**
     * Obtiene la imagen de una fogata según su estado
     * @param isLit true si está encendida, false si está apagada
     */
    public static Image getCampfireImage(boolean isLit) {
        if (isLit) {
            return obstacleImages.get(AnimationType.CAMPFIRE_LIT);
        } else {
            return obstacleImages.get(AnimationType.CAMPFIRE_EXTINGUISHED);
        }
    }
    
    /**
     * Verifica si todas las imágenes de obstáculos fueron cargadas correctamente
     */
    public static boolean allImagesLoaded() {
        for (AnimationType type : AnimationType.values()) {
            if (obstacleImages.get(type) == null) {
                System.err.println("⚠️ Imagen no cargada: " + type);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Obtiene el conteo de imágenes cargadas
     */
    public static int getLoadedImageCount() {
        int count = 0;
        for (Image img : obstacleImages.values()) {
            if (img != null) count++;
        }
        return count;
    }
}
