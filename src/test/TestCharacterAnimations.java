package test;

import presentation.CharacterImageConfigurator;
import presentation.CharacterImageConfigurator.CharacterType;
import presentation.CharacterImageConfigurator.AnimationType;

/**
 * Clase de prueba para verificar la carga de animaciones de personajes
 */
public class TestCharacterAnimations {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║   TEST DE CARGA DE ANIMACIONES DE PERSONAJES        ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");
        
        // Probar carga de animaciones
        testAnimationLoading();
        
        // Probar obtención por dirección
        testDirectionBasedAnimation();
        
        // Probar conversión de color a tipo
        testColorConversion();
        
        // Imprimir reporte final
        CharacterImageConfigurator.printLoadingReport();
        
        System.out.println("\n✅ Pruebas completadas");
    }
    
    private static void testAnimationLoading() {
        System.out.println("🔍 Probando carga de animaciones específicas...\n");
        
        // Probar cada personaje
        for (CharacterType character : CharacterType.values()) {
            System.out.println("Personaje: " + character.getFolderName());
            
            // Probar algunas animaciones clave
            testAnimation(character, AnimationType.WALK_DOWN);
            testAnimation(character, AnimationType.IDLE_DOWN);
            testAnimation(character, AnimationType.ICE_DOWN);
            testAnimation(character, AnimationType.VICTORY);
            
            System.out.println();
        }
    }
    
    private static void testAnimation(CharacterType character, AnimationType animation) {
        var icon = CharacterImageConfigurator.getAnimation(character, animation);
        String status = (icon != null) ? "✓" : "✗";
        System.out.println("  " + status + " " + animation.name());
    }
    
    private static void testDirectionBasedAnimation() {
        System.out.println("\n🎮 Probando animaciones por dirección...\n");
        
        CharacterType testChar = CharacterType.FRESA;
        
        // Probar movimiento en cada dirección
        System.out.println("Movimiento:");
        testDirection(testChar, 0, 1, true, "Abajo");
        testDirection(testChar, 0, -1, true, "Arriba");
        testDirection(testChar, 1, 0, true, "Derecha");
        testDirection(testChar, -1, 0, true, "Izquierda");
        
        System.out.println("\nIdle:");
        testDirection(testChar, 0, 1, false, "Mirando Abajo");
        testDirection(testChar, 0, -1, false, "Mirando Arriba");
        testDirection(testChar, 1, 0, false, "Mirando Derecha");
        testDirection(testChar, -1, 0, false, "Mirando Izquierda");
        
        System.out.println("\nHielo:");
        var ice = CharacterImageConfigurator.getIceAnimation(testChar, 0, 1);
        System.out.println("  " + (ice != null ? "✓" : "✗") + " Hielo Abajo");
    }
    
    private static void testDirection(CharacterType character, int dx, int dy, 
                                      boolean isMoving, String description) {
        var icon = CharacterImageConfigurator.getAnimationByDirection(character, dx, dy, isMoving);
        String status = (icon != null) ? "✓" : "✗";
        System.out.println("  " + status + " " + description);
    }
    
    private static void testColorConversion() {
        System.out.println("\n🎨 Probando conversión de colores...\n");
        
        testColorMapping("pink", CharacterType.FRESA);
        testColorMapping("vanilla", CharacterType.VAINILLA);
        testColorMapping("chocolate", CharacterType.CHOCOLATE);
        testColorMapping("fresa", CharacterType.FRESA);
        testColorMapping("rosa", CharacterType.FRESA);
    }
    
    private static void testColorMapping(String color, CharacterType expected) {
        CharacterType result = CharacterImageConfigurator.getCharacterTypeByColor(color);
        boolean matches = result == expected;
        String status = matches ? "✓" : "✗";
        System.out.println("  " + status + " '" + color + "' -> " + result.getFolderName());
    }
}
