package domain;

/**
 * Clase que contiene todas las excepciones personalizadas del juego Bad Dopo Cream
 * Agrupa las excepciones específicas del dominio para mejor manejo de errores
 * @author Bad Dopo Cream Team
 * @version 1.0
 */
public class BadDopoCream_Exceptions {
    
    /**
     * Excepción base para todas las excepciones del juego
     */
    public static class GameException extends Exception {
        public GameException(String message) {
            super(message);
        }
        
        public GameException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    // ========== EXCEPCIONES DE RECURSOS (Imágenes, Archivos) ==========
    
    /**
     * Excepción cuando no se puede cargar una imagen requerida
     */
    public static class ImageLoadException extends GameException {
        private final String imagePath;
        
        public ImageLoadException(String imagePath, String message) {
            super("Error al cargar la imagen: " + imagePath + " - " + message);
            this.imagePath = imagePath;
        }
        
        public ImageLoadException(String imagePath, Throwable cause) {
            super("Error al cargar la imagen: " + imagePath, cause);
            this.imagePath = imagePath;
        }
        
        public String getImagePath() {
            return imagePath;
        }
    }
    
    /**
     * Excepción cuando falta una imagen crítica del juego
     */
    public static class MissingImageException extends ImageLoadException {
        public MissingImageException(String imagePath) {
            super(imagePath, "La imagen no existe en la ruta especificada");
        }
    }
    
    /**
     * Excepción cuando una imagen tiene un formato no soportado
     */
    public static class InvalidImageFormatException extends ImageLoadException {
        public InvalidImageFormatException(String imagePath) {
            super(imagePath, "Formato de imagen no soportado");
        }
    }
    
    // ========== EXCEPCIONES DE GUARDADO/CARGA ==========
    
    /**
     * Excepción cuando falla el guardado de una partida
     */
    public static class SaveGameException extends GameException {
        private final String saveName;
        
        public SaveGameException(String saveName, String message) {
            super("Error al guardar la partida '" + saveName + "': " + message);
            this.saveName = saveName;
        }
        
        public SaveGameException(String saveName, Throwable cause) {
            super("Error al guardar la partida '" + saveName + "'", cause);
            this.saveName = saveName;
        }
        
        public String getSaveName() {
            return saveName;
        }
    }
    
    /**
     * Excepción cuando falla la carga de una partida
     */
    public static class LoadGameException extends GameException {
        private final String saveName;
        
        public LoadGameException(String saveName, String message) {
            super("Error al cargar la partida '" + saveName + "': " + message);
            this.saveName = saveName;
        }
        
        public LoadGameException(String saveName, Throwable cause) {
            super("Error al cargar la partida '" + saveName + "'", cause);
            this.saveName = saveName;
        }
        
        public String getSaveName() {
            return saveName;
        }
    }
    
    /**
     * Excepción cuando el archivo de guardado está corrupto
     */
    public static class CorruptedSaveFileException extends LoadGameException {
        public CorruptedSaveFileException(String saveName) {
            super(saveName, "El archivo de guardado está corrupto o dañado");
        }
        
        public CorruptedSaveFileException(String saveName, Throwable cause) {
            super(saveName, cause);
        }
    }
    
    /**
     * Excepción cuando no se puede acceder al directorio de guardado
     */
    public static class SaveDirectoryAccessException extends GameException {
        private final String directoryPath;
        
        public SaveDirectoryAccessException(String directoryPath, String message) {
            super("No se puede acceder al directorio de guardado: " + directoryPath + " - " + message);
            this.directoryPath = directoryPath;
        }
        
        public String getDirectoryPath() {
            return directoryPath;
        }
    }
    
    // ========== EXCEPCIONES DE CONFIGURACIÓN ==========
    
    /**
     * Excepción cuando la configuración del nivel es inválida
     */
    public static class InvalidLevelConfigurationException extends GameException {
        private final int levelNumber;
        
        public InvalidLevelConfigurationException(int levelNumber, String message) {
            super("Configuración inválida para el nivel " + levelNumber + ": " + message);
            this.levelNumber = levelNumber;
        }
        
        public int getLevelNumber() {
            return levelNumber;
        }
    }
    
    /**
     * Excepción cuando se intenta cargar un nivel que no existe
     */
    public static class LevelNotFoundException extends GameException {
        private final int levelNumber;
        
        public LevelNotFoundException(int levelNumber) {
            super("El nivel " + levelNumber + " no existe o no está disponible");
            this.levelNumber = levelNumber;
        }
        
        public int getLevelNumber() {
            return levelNumber;
        }
    }
    
    /**
     * Excepción cuando falla la inicialización del juego
     */
    public static class GameInitializationException extends GameException {
        public GameInitializationException(String message) {
            super("Error al inicializar el juego: " + message);
        }
        
        public GameInitializationException(String message, Throwable cause) {
            super("Error al inicializar el juego: " + message, cause);
        }
    }
    
    // ========== EXCEPCIONES DE RECURSOS DEL SISTEMA ==========
    
    /**
     * Excepción cuando se agota la memoria durante la carga de recursos
     */
    public static class OutOfMemoryResourceException extends GameException {
        private final String resourceType;
        
        public OutOfMemoryResourceException(String resourceType) {
            super("Memoria insuficiente para cargar " + resourceType);
            this.resourceType = resourceType;
        }
        
        public String getResourceType() {
            return resourceType;
        }
    }
    
    /**
     * Excepción cuando hay un timeout en la carga de recursos
     */
    public static class ResourceLoadTimeoutException extends GameException {
        private final String resourceName;
        private final long timeout;
        
        public ResourceLoadTimeoutException(String resourceName, long timeout) {
            super("Timeout al cargar " + resourceName + " después de " + timeout + "ms");
            this.resourceName = resourceName;
            this.timeout = timeout;
        }
        
        public String getResourceName() {
            return resourceName;
        }
        
        public long getTimeout() {
            return timeout;
        }
    }
    
    // ========== EXCEPCIONES DE INTERFAZ ==========
    
    /**
     * Excepción cuando falla la carga de una fuente tipográfica
     */
    public static class FontLoadException extends GameException {
        private final String fontName;
        
        public FontLoadException(String fontName, String message) {
            super("Error al cargar la fuente '" + fontName + "': " + message);
            this.fontName = fontName;
        }
        
        public FontLoadException(String fontName, Throwable cause) {
            super("Error al cargar la fuente '" + fontName + "'", cause);
            this.fontName = fontName;
        }
        
        public String getFontName() {
            return fontName;
        }
    }
    
    /**
     * Excepción cuando falla la inicialización de un componente de UI
     */
    public static class UIComponentInitializationException extends GameException {
        private final String componentName;
        
        public UIComponentInitializationException(String componentName, String message) {
            super("Error al inicializar el componente UI '" + componentName + "': " + message);
            this.componentName = componentName;
        }
        
        public UIComponentInitializationException(String componentName, Throwable cause) {
            super("Error al inicializar el componente UI '" + componentName + "'", cause);
            this.componentName = componentName;
        }
        
        public String getComponentName() {
            return componentName;
        }
    }
    
    // ========== EXCEPCIONES DE LÓGICA DE JUEGO ==========
    
    /**
     * Excepción cuando se detecta un estado inválido del juego
     */
    public static class InvalidGameStateException extends GameException {
        public InvalidGameStateException(String message) {
            super("Estado de juego inválido: " + message);
        }
    }
    
    /**
     * Excepción cuando se intenta una operación no soportada
     */
    public static class UnsupportedGameOperationException extends GameException {
        private final String operation;
        
        public UnsupportedGameOperationException(String operation) {
            super("Operación no soportada: " + operation);
            this.operation = operation;
        }
        
        public String getOperation() {
            return operation;
        }
    }
    
    // ========== EXCEPCIONES DE THREADING ==========
    
    /**
     * Excepción cuando hay un problema con hilos de ejecución
     */
    public static class GameThreadException extends GameException {
        private final String threadName;
        
        public GameThreadException(String threadName, String message) {
            super("Error en el hilo '" + threadName + "': " + message);
            this.threadName = threadName;
        }
        
        public GameThreadException(String threadName, Throwable cause) {
            super("Error en el hilo '" + threadName + "'", cause);
            this.threadName = threadName;
        }
        
        public String getThreadName() {
            return threadName;
        }
    }
    
    /**
     * Excepción cuando se interrumpe un proceso crítico del juego
     */
    public static class GameInterruptedException extends GameThreadException {
        public GameInterruptedException(String threadName) {
            super(threadName, "El proceso fue interrumpido inesperadamente");
        }
        
        public GameInterruptedException(String threadName, InterruptedException cause) {
            super(threadName, cause);
        }
    }
}
