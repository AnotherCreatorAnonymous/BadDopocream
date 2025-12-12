package domain;

/**
 * Fábrica de niveles predefinidos del juego (Campaña/Historia)
 * Utiliza LevelBuilder internamente para construir niveles con configuraciones fijas
 * Esta clase define los niveles oficiales del juego con dificultad progresiva
 */
public class LevelConfigurator {
    
    /**
     * Crea y configura un nivel predefinido según el número especificado
     * @param levelNumber Número del nivel (1-5)
     * @param player Jugador principal que se asignará al nivel
     * @return Nivel completamente configurado
     */
    public static Level createLevel(int levelNumber, IceCream player) {
        return createLevel(levelNumber, player, null);
    }
    
    /**
     * Crea y configura un nivel predefinido con soporte para 2 jugadores
     * @param levelNumber Número del nivel (1-5)
     * @param player1 Jugador 1
     * @param player2 Jugador 2 (puede ser null para modo single-player)
     * @return Nivel completamente configurado
     */
    public static Level createLevel(int levelNumber, IceCream player1, IceCream player2) {
        switch (levelNumber) {
            case 1:
                return createLevel1(player1, player2);
            case 2:
                return createLevel2(player1, player2);
            case 3:
                return createLevel3(player1, player2);
            case 4:
                return createLevel4(player1, player2);
            case 5:
                return createLevel5(player1, player2);
            default:
                return createLevel1(player1, player2); // Nivel por defecto
        }
    }
    
    /**
     * NIVEL 1 - Tutorial básico
     * Mapa pequeño, pocas paredes, frutas estáticas, pocos enemigos
     */
    private static Level createLevel1(IceCream player1, IceCream player2) {
        LevelBuilder builder = LevelBuilder.createCustomLevel(1)
            .setDimensions(25, 18)
            .setPlayer1(player1)
            // Frutas: Solo básicas (Bananas y Uvas)
            .addBananas(4)
            .addGrapes(4)
            // Enemigos: Solo 2 Trolls (básicos)
            .addTrolls(2)
            // Obstáculos mínimos
            .addHotTiles(2)
            // Paredes de hielo moderadas
            .addIceWalls(20)
            .setPerimeterWalls(true);
        
        if (player2 != null) {
            builder.setPlayer2(player2);
        }
        
        return builder.build();
    }
    
    /**
     * NIVEL 2 - Introducción a Piñas
     * Más frutas, introducción de Piñas móviles, más enemigos
     */
    private static Level createLevel2(IceCream player1, IceCream player2) {
        LevelBuilder builder = LevelBuilder.createCustomLevel(2)
            .setDimensions(25, 18)
            .setPlayer1(player1)
            // Frutas: Más variedad
            .addBananas(6)
            .addGrapes(4)
            .addPineapples(4) // Piñas móviles
            // Enemigos: Trolls + Macetas
            .addTrolls(2)
            .addMacetas(2)
            // Más obstáculos
            .addHotTiles(4)
            .addCampfires(2)
            // Más paredes de hielo
            .addIceWalls(30)
            .setPerimeterWalls(true);
        
        if (player2 != null) {
            builder.setPlayer2(player2);
        }
        
        return builder.build();
    }
    
    /**
     * NIVEL 3 - Desafío de Cerezas
     * Cerezas que rompen hielo, enemigos más agresivos
     */
    private static Level createLevel3(IceCream player1, IceCream player2) {
        LevelBuilder builder = LevelBuilder.createCustomLevel(3)
            .setDimensions(25, 18)
            .setPlayer1(player1)
            // Frutas: Introducción de Cerezas
            .addBananas(5)
            .addCherries(4) // Cerezas que rompen hielo
            .addGrapes(3)
            .addPineapples(3)
            // Enemigos: Más variedad
            .addTrolls(2)
            .addMacetas(2)
            .addCalamares(2) // Calamares que rompen hielo
            // Obstáculos aumentan
            .addHotTiles(5)
            .addCampfires(3)
            // Muchas paredes de hielo
            .addIceWalls(40)
            .setPerimeterWalls(true);
        
        if (player2 != null) {
            builder.setPlayer2(player2);
        }
        
        return builder.build();
    }
    
    /**
     * NIVEL 4 - Peligro de Cactus
     * Cactus con espinas, enemigos peligrosos
     */
    private static Level createLevel4(IceCream player1, IceCream player2) {
        LevelBuilder builder = LevelBuilder.createCustomLevel(4)
            .setDimensions(25, 18)
            .setPlayer1(player1)
            // Frutas: Introducción de Cactus
            .addBananas(4)
            .addCherries(3)
            .addGrapes(3)
            .addPineapples(3)
            .addCactus(3) // Cactus con espinas peligrosas
            // Enemigos: Todos menos Narval
            .addTrolls(2)
            .addMacetas(3)
            .addCalamares(2)
            // Muchos obstáculos
            .addHotTiles(6)
            .addCampfires(4)
            // Laberinto de hielo
            .addIceWalls(50)
            .setPerimeterWalls(true);
        
        if (player2 != null) {
            builder.setPlayer2(player2);
        }
        
        return builder.build();
    }
    
    /**
     * NIVEL 5 - Desafío Final
     * Todos los tipos de frutas y enemigos, máxima dificultad
     */
    private static Level createLevel5(IceCream player1, IceCream player2) {
        LevelBuilder builder = LevelBuilder.createCustomLevel(5)
            .setDimensions(25, 18)
            .setPlayer1(player1)
            // Frutas: TODAS las variedades
            .addBananas(5)
            .addCherries(4)
            .addGrapes(4)
            .addPineapples(4)
            .addCactus(3)
            // Enemigos: TODOS los tipos
            .addTrolls(2)
            .addMacetas(3)
            .addCalamares(3)
            .addNarvales(2) // Narvales (los más peligrosos)
            // Máximos obstáculos
            .addHotTiles(8)
            .addCampfires(5)
            // Laberinto complejo
            .addIceWalls(60)
            .setPerimeterWalls(true);
        
        if (player2 != null) {
            builder.setPlayer2(player2);
        }
        
        return builder.build();
    }
}
