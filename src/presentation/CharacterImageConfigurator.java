package presentation;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Configurador de imágenes y animaciones para los personajes del juego
 * Gestiona la carga y almacenamiento de todas las animaciones de los helados
 * Optimizado para renderizado rápido de GIFs animados
 * Hereda de BaseImageConfigurator para usar carga optimizada con getResource()
 */
public class CharacterImageConfigurator extends BaseImageConfigurator {
    
    private static final String BASE_PATH = "resources\\Personajes\\";
    
    // Enumeración de tipos de animación disponibles
    public enum AnimationType {
        WALK_DOWN("Caminando Abajo animation.gif"),
        WALK_UP("Caminando Arriba animation.gif"),
        WALK_RIGHT("Caminando Derecha animation.gif"),
        WALK_LEFT("Caminando Izquierda animation.gif"),
        IDLE_DOWN("Mirando Abajo animation.gif"),
        IDLE_UP("Mirando Arriba animation.gif"),
        IDLE_RIGHT("Mirando Derecha animation.gif"),
        IDLE_LEFT("Mirando Izquierda animation.gif"),
        ICE_DOWN("Hielo abajo animation.gif"),
        ICE_UP("Hielo arriba animation.gif"),
        ICE_RIGHT("Hielo derecha animation.gif"),
        ICE_LEFT("Hielo Izquierda animation.gif"),
        KICK("Patada animation.gif"),
        DEATH("Muerte animation.gif"),
        VICTORY("Victoria animation.gif");
        
        private final String fileName;
        
        AnimationType(String fileName) {
            this.fileName = fileName;
        }
        
        public String getFileName() {
            return fileName;
        }
    }
    
    // Mapa que almacena las animaciones: Character -> AnimationType -> Image
    private static final Map<CharacterType, Map<AnimationType, Image>> characterAnimations = new HashMap<>();
    
    // Inicialización estática - carga todas las animaciones al inicio
    static {
        // Cargar animaciones para cada personaje
        for (CharacterType character : CharacterType.values()) {
            loadCharacterAnimations(character);
        }
        
        // Esperar a que todas las imágenes se carguen completamente usando método heredado
        waitForAllImages("personajes");
    }
    
    /**
     * Carga todas las animaciones de un personaje específico
     * Usa Toolkit para carga optimizada y MediaTracker para pre-carga
     * @param character Tipo de personaje
     */
    private static void loadCharacterAnimations(CharacterType character) {
        Map<AnimationType, Image> animations = new HashMap<>();
        String characterPath = BASE_PATH + character.getFolderName() + "\\";
        
        System.out.println("📦 Cargando animaciones para: " + character.getFolderName());
        
        for (AnimationType animationType : AnimationType.values()) {
            String fullPath = characterPath + animationType.getFileName();
            Image img = loadAnimationImage(fullPath);
            
            if (img != null) {
                animations.put(animationType, img);
                System.out.println("  ✓ " + animationType.name() + " cargada");
            } else {
                System.err.println("  ✗ " + animationType.name() + " no encontrada");
            }
        }
        
        characterAnimations.put(character, animations);
        System.out.println("  Total: " + animations.size() + "/" + AnimationType.values().length + " animaciones\n");
    }
    
    /**
     * Carga una animación desde recursos usando el método heredado de BaseImageConfigurator
     * @param resourcePath Ruta del recurso de animación
     * @return Image con la animación o null si no se pudo cargar
     */
    private static Image loadAnimationImage(String resourcePath) {
        Image img = loadImage(resourcePath);
        if (img == null) {
            System.err.println("⚠️  Archivo no encontrado: " + resourcePath);
        }
        return img;
    }
    
    /**
     * Obtiene la animación de un personaje específico
     * @param character Tipo de personaje
     * @param animation Tipo de animación
     * @return Image con la animación o null si no existe
     */
    public static Image getAnimation(CharacterType character, AnimationType animation) {
        Map<AnimationType, Image> animations = characterAnimations.get(character);
        if (animations != null) {
            return animations.get(animation);
        }
        return null;
    }
    
    /**
     * Obtiene la animación apropiada según la dirección de movimiento
     * @param character Tipo de personaje
     * @param dx Desplazamiento en X (-1 izquierda, 0 sin movimiento, 1 derecha)
     * @param dy Desplazamiento en Y (-1 arriba, 0 sin movimiento, 1 abajo)
     * @param isMoving Si el personaje está en movimiento o idle
     * @return Image con la animación apropiada
     */
    public static Image getAnimationByDirection(CharacterType character, int dx, int dy, boolean isMoving) {
        AnimationType animationType;
        
        if (isMoving) {
            // Animaciones de movimiento
            if (dy > 0) {
                animationType = AnimationType.WALK_DOWN;
            } else if (dy < 0) {
                animationType = AnimationType.WALK_UP;
            } else if (dx > 0) {
                animationType = AnimationType.WALK_RIGHT;
            } else if (dx < 0) {
                animationType = AnimationType.WALK_LEFT;
            } else {
                animationType = AnimationType.IDLE_DOWN; // Default
            }
        } else {
            // Animaciones idle (mirando)
            if (dy > 0) {
                animationType = AnimationType.IDLE_DOWN;
            } else if (dy < 0) {
                animationType = AnimationType.IDLE_UP;
            } else if (dx > 0) {
                animationType = AnimationType.IDLE_RIGHT;
            } else if (dx < 0) {
                animationType = AnimationType.IDLE_LEFT;
            } else {
                animationType = AnimationType.IDLE_DOWN; // Default
            }
        }
        
        return getAnimation(character, animationType);
    }
    
    /**
     * Obtiene la animación de creación de hielo según la dirección
     * @param character Tipo de personaje
     * @param dx Desplazamiento en X
     * @param dy Desplazamiento en Y
     * @return Image con la animación de hielo
     */
    public static Image getIceAnimation(CharacterType character, int dx, int dy) {
        AnimationType animationType;
        
        if (dy > 0) {
            animationType = AnimationType.ICE_DOWN;
        } else if (dy < 0) {
            animationType = AnimationType.ICE_UP;
        } else if (dx > 0) {
            animationType = AnimationType.ICE_RIGHT;
        } else if (dx < 0) {
            animationType = AnimationType.ICE_LEFT;
        } else {
            animationType = AnimationType.ICE_DOWN; // Default
        }
        
        return getAnimation(character, animationType);
    }
    
    /**
     * Dibuja una animación de personaje en las coordenadas especificadas
     * @param g Graphics2D para dibujar
     * @param character Tipo de personaje
     * @param animation Tipo de animación
     * @param x Coordenada X
     * @param y Coordenada Y
     * @param width Ancho de la imagen
     * @param height Alto de la imagen
     */
    public static void drawCharacter(Graphics2D g, CharacterType character, AnimationType animation, 
                                     int x, int y, int width, int height) {
        Image img = getAnimation(character, animation);
        if (img != null) {
            g.drawImage(img, x, y, width, height, null);
        } else {
            // Fallback: dibujar rectángulo de color si no hay animación
            drawFallbackCharacter(g, character, x, y, width, height);
        }
    }
    
    /**
     * Dibuja un rectángulo de color como fallback si no hay animación
     * @param g Graphics2D para dibujar
     * @param character Tipo de personaje
     * @param x Coordenada X
     * @param y Coordenada Y
     * @param width Ancho
     * @param height Alto
     */
    private static void drawFallbackCharacter(Graphics2D g, CharacterType character, 
                                             int x, int y, int width, int height) {
        Color characterColor;
        switch (character) {
            case FRESA:
                characterColor = new Color(255, 100, 150); // Rosa
                break;
            case VAINILLA:
                characterColor = new Color(255, 255, 150); // Amarillo claro
                break;
            case CHOCOLATE:
                characterColor = new Color(150, 75, 0); // Marrón
                break;
            default:
                characterColor = Color.GRAY;
        }
        
        g.setColor(characterColor);
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }
    
    /**
     * Convierte un color de personaje a CharacterType
     * @param color Color del personaje ("pink", "vanilla", "chocolate")
     * @return CharacterType correspondiente
     */
    public static CharacterType getCharacterTypeByColor(String color) {
        if (color == null) return CharacterType.FRESA;
        
        switch (color.toLowerCase()) {
            case "pink":
            case "rosa":
            case "fresa":
                return CharacterType.FRESA;
            case "vanilla":
            case "vainilla":
                return CharacterType.VAINILLA;
            case "chocolate":
            case "brown":
            case "marrón":
                return CharacterType.CHOCOLATE;
            default:
                return CharacterType.FRESA;
        }
    }
    
    /**
     * Verifica si todas las animaciones de un personaje están cargadas
     * @param character Tipo de personaje
     * @return true si todas las animaciones están disponibles
     */
    public static boolean areAllAnimationsLoaded(CharacterType character) {
        Map<AnimationType, Image> animations = characterAnimations.get(character);
        if (animations == null) return false;
        
        for (AnimationType animationType : AnimationType.values()) {
            if (animations.get(animationType) == null) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Obtiene el número de animaciones cargadas para un personaje
     * @param character Tipo de personaje
     * @return Número de animaciones cargadas
     */
    public static int getLoadedAnimationsCount(CharacterType character) {
        Map<AnimationType, Image> animations = characterAnimations.get(character);
        return animations != null ? animations.size() : 0;
    }
    
    /**
     * Imprime un reporte del estado de carga de animaciones
     */
    public static void printLoadingReport() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("  REPORTE DE CARGA DE ANIMACIONES");
        System.out.println("═══════════════════════════════════════");
        
        for (CharacterType character : CharacterType.values()) {
            int loaded = getLoadedAnimationsCount(character);
            int total = AnimationType.values().length;
            boolean allLoaded = areAllAnimationsLoaded(character);
            
            String status = allLoaded ? "✓ COMPLETO" : "⚠ INCOMPLETO";
            System.out.println(String.format("%-15s: %d/%d %s", 
                character.getFolderName(), loaded, total, status));
        }
        
        System.out.println("═══════════════════════════════════════\n");
    }
}
