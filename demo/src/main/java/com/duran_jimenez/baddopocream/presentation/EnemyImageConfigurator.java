package com.duran_jimenez.baddopocream.presentation;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Configurador de imágenes para los enemigos del juego
 * Gestiona la carga y almacenamiento de todas las animaciones de enemigos
 * Optimizado para renderizado rápido de GIFs animados
 * Hereda de BaseImageConfigurator para usar carga optimizada con getResource()
 */
public class EnemyImageConfigurator extends BaseImageConfigurator {
    
    private static final String BASE_PATH = "Monstruos\\";
    
    // Enumeración de tipos de animación para enemigos
    public enum AnimationType {
        // Troll - Animaciones simples
        TROLL_WALK_DOWN("DownWalk.gif"),
        TROLL_WALK_UP("UpWalk.gif"),
        TROLL_WALK_RIGHT("RightWalk.gif"),
        TROLL_WALK_LEFT("LeftWalk.gif"),
        TROLL_STAND("Stand.gif"),
        
        // Maceta (Pot) - Animaciones complejas
        POT_WALK_DOWN("DownWalk1.gif"),
        POT_WALK_UP("UpWalk1.gif"),
        POT_WALK_RIGHT("RightWalk1.gif"),
        POT_WALK_LEFT("LeftWalk1.gif"),
        POT_POPUP_DOWN("DownPopUp2.gif"),
        POT_POPUP_UP("UpPopUp2.gif"),
        POT_POPUP_RIGHT("RightPopUp2.gif"),
        POT_POPUP_LEFT("LeftPopUp2.gif"),
        
        // Calamar (YellowSquid) - Animaciones de caminar y romper
        SQUID_WALK_DOWN("DownWalk.gif"),
        SQUID_WALK_UP("UpWalk.gif"),
        SQUID_WALK_RIGHT("RightWalk.gif"),
        SQUID_WALK_LEFT("LeftWalk.gif"),
        SQUID_BREAK_DOWN("DownBreak.gif"),
        SQUID_BREAK_UP("UpBreak.gif"),
        SQUID_BREAK_RIGHT("RightBreak.gif"),
        SQUID_BREAK_LEFT("LeftBreak.gif"),
        
        // Narval - Animaciones de patrulla, embestida y perforación
        NARVAL_WALK_DOWN("DownWalk.gif"),
        NARVAL_WALK_UP("UpWalk.gif"),
        NARVAL_WALK_RIGHT("RightWalk.gif"),
        NARVAL_WALK_LEFT("LeftWalk.gif"),
        NARVAL_CHARGE_DOWN("DownDrill1.gif"),
        NARVAL_CHARGE_UP("UpDrill1.gif"),
        NARVAL_CHARGE_RIGHT("RightDrill1.gif"),
        NARVAL_CHARGE_LEFT("LeftDrill1.gif"),
        NARVAL_BREAK_DOWN("DownBreak.gif"),
        NARVAL_BREAK_UP("UpBreak.gif"),
        NARVAL_BREAK_RIGHT("RightBreak.gif"),
        NARVAL_BREAK_LEFT("LeftBreak.gif");
        
        private final String fileName;
        
        AnimationType(String fileName) {
            this.fileName = fileName;
        }
        
        public String getFileName() {
            return fileName;
        }
    }
    
    // Mapa que almacena las animaciones: EnemyType -> AnimationType -> Image
    private static final Map<EnemyType, Map<AnimationType, Image>> enemyAnimations = new HashMap<>();
    
    // Inicialización estática - carga todas las animaciones al inicio
    static {
        loadEnemyAnimations(EnemyType.TROLL, new AnimationType[]{
            AnimationType.TROLL_WALK_DOWN,
            AnimationType.TROLL_WALK_UP,
            AnimationType.TROLL_WALK_RIGHT,
            AnimationType.TROLL_WALK_LEFT,
            AnimationType.TROLL_STAND
        });
        
        loadEnemyAnimations(EnemyType.MACETA, new AnimationType[]{
            AnimationType.POT_WALK_DOWN,
            AnimationType.POT_WALK_UP,
            AnimationType.POT_WALK_RIGHT,
            AnimationType.POT_WALK_LEFT,
            AnimationType.POT_POPUP_DOWN,
            AnimationType.POT_POPUP_UP,
            AnimationType.POT_POPUP_RIGHT,
            AnimationType.POT_POPUP_LEFT
        });
        
        loadEnemyAnimations(EnemyType.CALAMAR, new AnimationType[]{
            AnimationType.SQUID_WALK_DOWN,
            AnimationType.SQUID_WALK_UP,
            AnimationType.SQUID_WALK_RIGHT,
            AnimationType.SQUID_WALK_LEFT,
            AnimationType.SQUID_BREAK_DOWN,
            AnimationType.SQUID_BREAK_UP,
            AnimationType.SQUID_BREAK_RIGHT,
            AnimationType.SQUID_BREAK_LEFT
        });
        
        loadEnemyAnimations(EnemyType.NARVAL, new AnimationType[]{
            AnimationType.NARVAL_WALK_DOWN,
            AnimationType.NARVAL_WALK_UP,
            AnimationType.NARVAL_WALK_RIGHT,
            AnimationType.NARVAL_WALK_LEFT,
            AnimationType.NARVAL_CHARGE_DOWN,
            AnimationType.NARVAL_CHARGE_UP,
            AnimationType.NARVAL_CHARGE_RIGHT,
            AnimationType.NARVAL_CHARGE_LEFT,
            AnimationType.NARVAL_BREAK_DOWN,
            AnimationType.NARVAL_BREAK_UP,
            AnimationType.NARVAL_BREAK_RIGHT,
            AnimationType.NARVAL_BREAK_LEFT
        });
        
        // Esperar a que todas las imágenes se carguen completamente usando método heredado
        waitForAllImages("enemigos");
    }
    
    /**
     * Carga las animaciones de un enemigo específico
     * Usa Toolkit para carga optimizada y MediaTracker para pre-carga
     * @param enemyType Tipo de enemigo
     * @param animations Array de animaciones a cargar
     */
    private static void loadEnemyAnimations(EnemyType enemyType, AnimationType[] animations) {
        Map<AnimationType, Image> animationMap = new HashMap<>();
        String enemyPath = BASE_PATH + enemyType.getFolderName() + "\\";
        
        System.out.println("📦 Cargando animaciones para: " + enemyType.getFolderName());
        
        for (AnimationType animationType : animations) {
            String fullPath = enemyPath + animationType.getFileName();
            Image img = loadAnimationImage(fullPath);
            
            if (img != null) {
                animationMap.put(animationType, img);
                System.out.println("  ✓ " + animationType.name() + " cargada");
            } else {
                System.err.println("  ✗ " + animationType.name() + " no encontrada");
            }
        }
        
        enemyAnimations.put(enemyType, animationMap);
        System.out.println("  Total: " + animationMap.size() + "/" + animations.length + " animaciones\n");
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
     * Obtiene la animación de un enemigo específico
     * @param enemyType Tipo de enemigo
     * @param animation Tipo de animación
     * @return Image con la animación o null si no existe
     */
    public static Image getAnimation(EnemyType enemyType, AnimationType animation) {
        Map<AnimationType, Image> animations = enemyAnimations.get(enemyType);
        if (animations != null) {
            return animations.get(animation);
        }
        return null;
    }
    
    /**
     * Obtiene la animación apropiada según la dirección de movimiento del enemigo
     * @param enemyType Tipo de enemigo
     * @param dx Desplazamiento en X (-1 izquierda, 0 sin movimiento, 1 derecha)
     * @param dy Desplazamiento en Y (-1 arriba, 0 sin movimiento, 1 abajo)
     * @return Image con la animación apropiada
     */
    public static Image getAnimationByDirection(EnemyType enemyType, int dx, int dy) {
        AnimationType animationType;
        
        switch (enemyType) {
            case TROLL:
                if (dy > 0) {
                    animationType = AnimationType.TROLL_WALK_DOWN;
                } else if (dy < 0) {
                    animationType = AnimationType.TROLL_WALK_UP;
                } else if (dx > 0) {
                    animationType = AnimationType.TROLL_WALK_RIGHT;
                } else if (dx < 0) {
                    animationType = AnimationType.TROLL_WALK_LEFT;
                } else {
                    animationType = AnimationType.TROLL_STAND;
                }
                break;
                
            case MACETA:
                if (dy > 0) {
                    animationType = AnimationType.POT_WALK_DOWN;
                } else if (dy < 0) {
                    animationType = AnimationType.POT_WALK_UP;
                } else if (dx > 0) {
                    animationType = AnimationType.POT_WALK_RIGHT;
                } else if (dx < 0) {
                    animationType = AnimationType.POT_WALK_LEFT;
                } else {
                    animationType = AnimationType.POT_WALK_DOWN; // Default
                }
                break;
                
            case CALAMAR:
                if (dy > 0) {
                    animationType = AnimationType.SQUID_WALK_DOWN;
                } else if (dy < 0) {
                    animationType = AnimationType.SQUID_WALK_UP;
                } else if (dx > 0) {
                    animationType = AnimationType.SQUID_WALK_RIGHT;
                } else if (dx < 0) {
                    animationType = AnimationType.SQUID_WALK_LEFT;
                } else {
                    animationType = AnimationType.SQUID_WALK_DOWN; // Default
                }
                break;
                
            case NARVAL:
                // Para Narval, usa animación de caminar normal (patrulla)
                if (dy > 0) {
                    animationType = AnimationType.NARVAL_WALK_DOWN;
                } else if (dy < 0) {
                    animationType = AnimationType.NARVAL_WALK_UP;
                } else if (dx > 0) {
                    animationType = AnimationType.NARVAL_WALK_RIGHT;
                } else if (dx < 0) {
                    animationType = AnimationType.NARVAL_WALK_LEFT;
                } else {
                    animationType = AnimationType.NARVAL_WALK_DOWN; // Default
                }
                break;
                
            default:
                return null;
        }
        
        return getAnimation(enemyType, animationType);
    }
    
    /**
     * Obtiene el tipo de enemigo según el nombre de la clase
     * @param enemyClassName Nombre de la clase del enemigo
     * @return EnemyType correspondiente
     */
    public static EnemyType getEnemyTypeByClassName(String enemyClassName) {
        if (enemyClassName == null) return EnemyType.TROLL;
        
        String lowerName = enemyClassName.toLowerCase();
        if (lowerName.contains("troll")) {
            return EnemyType.TROLL;
        } else if (lowerName.contains("maceta") || lowerName.contains("pot")) {
            return EnemyType.MACETA;
        } else if (lowerName.contains("calamar") || lowerName.contains("squid")) {
            return EnemyType.CALAMAR;
        } else if (lowerName.contains("narval")) {
            return EnemyType.NARVAL;
        }
        return EnemyType.TROLL; // Default
    }
    
    /**
     * Dibuja un enemigo en las coordenadas especificadas
     * @param g Graphics2D para dibujar
     * @param enemyType Tipo de enemigo
     * @param animation Tipo de animación
     * @param x Coordenada X
     * @param y Coordenada Y
     * @param width Ancho de la imagen
     * @param height Alto de la imagen
     */
    public static void drawEnemy(Graphics2D g, EnemyType enemyType, AnimationType animation, 
                                 int x, int y, int width, int height) {
        Image img = getAnimation(enemyType, animation);
        if (img != null) {
            g.drawImage(img, x, y, width, height, null);
        } else {
            // Fallback: dibujar rectángulo de color si no hay animación
            drawFallbackEnemy(g, enemyType, x, y, width, height);
        }
    }
    
    /**
     * Dibuja un rectángulo de color como fallback si no hay animación
     * @param g Graphics2D para dibujar
     * @param enemyType Tipo de enemigo
     * @param x Coordenada X
     * @param y Coordenada Y
     * @param width Ancho
     * @param height Alto
     */
    private static void drawFallbackEnemy(Graphics2D g, EnemyType enemyType, 
                                         int x, int y, int width, int height) {
        Color enemyColor;
        switch (enemyType) {
            case TROLL:
                enemyColor = new Color(200, 100, 200); // Púrpura
                break;
            case MACETA:
                enemyColor = new Color(100, 200, 100); // Verde
                break;
            case CALAMAR:
                enemyColor = new Color(255, 150, 50); // Naranja
                break;
            case NARVAL:
                enemyColor = new Color(100, 150, 200); // Azul
                break;
            default:
                enemyColor = Color.GRAY;
        }
        
        g.setColor(enemyColor);
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }
    
    /**
     * Obtiene la animación del Narval según su estado
     * @param isCharging Si el Narval está embistiendo
     * @param isBreakingIce Si el Narval está rompiendo hielo
     * @param dx Dirección X
     * @param dy Dirección Y
     * @return Image con la animación apropiada
     */
    public static Image getNarvalAnimation(boolean isCharging, boolean isBreakingIce, int dx, int dy) {
        AnimationType animationType;
        
        if (isBreakingIce) {
            // Animación de romper hielo
            if (dy > 0) {
                animationType = AnimationType.NARVAL_BREAK_DOWN;
            } else if (dy < 0) {
                animationType = AnimationType.NARVAL_BREAK_UP;
            } else if (dx > 0) {
                animationType = AnimationType.NARVAL_BREAK_RIGHT;
            } else {
                animationType = AnimationType.NARVAL_BREAK_LEFT;
            }
        } else if (isCharging) {
            // Animación de embestida (drill)
            if (dy > 0) {
                animationType = AnimationType.NARVAL_CHARGE_DOWN;
            } else if (dy < 0) {
                animationType = AnimationType.NARVAL_CHARGE_UP;
            } else if (dx > 0) {
                animationType = AnimationType.NARVAL_CHARGE_RIGHT;
            } else {
                animationType = AnimationType.NARVAL_CHARGE_LEFT;
            }
        } else {
            // Animación de patrulla normal
            if (dy > 0) {
                animationType = AnimationType.NARVAL_WALK_DOWN;
            } else if (dy < 0) {
                animationType = AnimationType.NARVAL_WALK_UP;
            } else if (dx > 0) {
                animationType = AnimationType.NARVAL_WALK_RIGHT;
            } else {
                animationType = AnimationType.NARVAL_WALK_LEFT;
            }
        }
        
        return getAnimation(EnemyType.NARVAL, animationType);
    }
    
    /**
     * Verifica si todas las animaciones de un enemigo están cargadas
     * @param enemyType Tipo de enemigo
     * @return true si todas las animaciones están disponibles
     */
    public static boolean areAllAnimationsLoaded(EnemyType enemyType) {
        Map<AnimationType, Image> animations = enemyAnimations.get(enemyType);
        return animations != null && !animations.isEmpty();
    }
    
    /**
     * Obtiene el número de animaciones cargadas para un enemigo
     * @param enemyType Tipo de enemigo
     * @return Número de animaciones cargadas
     */
    public static int getLoadedAnimationsCount(EnemyType enemyType) {
        Map<AnimationType, Image> animations = enemyAnimations.get(enemyType);
        return animations != null ? animations.size() : 0;
    }
}
