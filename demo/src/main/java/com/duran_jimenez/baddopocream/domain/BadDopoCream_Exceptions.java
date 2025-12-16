package com.duran_jimenez.baddopocream.domain;

/**
 * Clase que contiene todas las excepciones personalizadas del juego Bad Dopo Cream
 * Excepción única para manejar todos los errores del dominio del juego
 * @author Bad Dopo Cream Team
 * @version 2.0
 */
public class BadDopoCream_Exceptions extends Exception {
    
    // ========== CONSTANTES DE ERRORES - RECURSOS (Imágenes, Archivos) ==========
    
    public static final String IMAGE_LOAD_ERROR = "Error al cargar la imagen";
    public static final String MISSING_IMAGE = "La imagen no existe en la ruta especificada";
    public static final String INVALID_IMAGE_FORMAT = "Formato de imagen no soportado";
    
    // ========== CONSTANTES DE ERRORES - GUARDADO/CARGA ==========
    
    public static final String SAVE_GAME_ERROR = "Error al guardar la partida";
    public static final String LOAD_GAME_ERROR = "Error al cargar la partida";
    public static final String CORRUPTED_SAVE_FILE = "El archivo de guardado está corrupto o dañado";
    public static final String SAVE_DIRECTORY_ACCESS_ERROR = "No se puede acceder al directorio de guardado";
    public static final String FILE_NOT_FOUND = "Archivo no encontrado";
    
    // ========== CONSTANTES DE ERRORES - CONFIGURACIÓN ==========
    
    public static final String INVALID_LEVEL_CONFIGURATION = "Configuración inválida para el nivel";
    public static final String LEVEL_NOT_FOUND = "El nivel no existe o no está disponible";
    public static final String GAME_INITIALIZATION_ERROR = "Error al inicializar el juego";
    
    // ========== CONSTANTES DE ERRORES - RECURSOS DEL SISTEMA ==========
    
    public static final String OUT_OF_MEMORY_RESOURCE = "Memoria insuficiente para cargar recursos";
    public static final String RESOURCE_LOAD_TIMEOUT = "Timeout al cargar recursos";
    
    // ========== CONSTANTES DE ERRORES - INTERFAZ ==========
    
    public static final String FONT_LOAD_ERROR = "Error al cargar la fuente tipográfica";
    public static final String UI_COMPONENT_INITIALIZATION_ERROR = "Error al inicializar el componente UI";
    
    // ========== CONSTANTES DE ERRORES - LÓGICA DE JUEGO ==========
    
    public static final String INVALID_GAME_STATE = "Estado de juego inválido";
    public static final String UNSUPPORTED_OPERATION = "Operación no soportada";
    public static final String INVALID_POSITION = "Posición inválida en el mapa";
    
    // ========== CONSTANTES DE ERRORES - THREADING ==========
    
    public static final String GAME_THREAD_ERROR = "Error en el hilo de ejecución del juego";
    public static final String GAME_INTERRUPTED = "El proceso fue interrumpido inesperadamente";
    
    /**
     * Crea una nueva excepción BadDopoCream_Exceptions con un mensaje específico.
     * 
     * @param message el mensaje detallado que explica la excepción
     */
    public BadDopoCream_Exceptions(String message) {
        super(message);
    }
    
    /**
     * Crea una nueva excepción BadDopoCream_Exceptions con un mensaje y una causa.
     * 
     * @param message el mensaje detallado que explica la excepción
     * @param cause la causa de la excepción
     */
    public BadDopoCream_Exceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
