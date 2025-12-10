package domain;

/**
 * Clase para configurar (hardcodear) los niveles del juego
 * Aquí se "queman" todos los datos de niveles, paredes, frutas y enemigos
 */
public class LevelConfigurator {
    
    /**
     * Crea y configura un nivel según el número especificado
     * @param levelNumber Número del nivel (1-10)
     * @param player Jugador que se asignará al nivel
     * @return Nivel completamente configurado
     */
    public static Level createLevel(int levelNumber, IceCream player) {
        switch (levelNumber) {
            case 1:
                return createLevel1(player);
            case 2:
                return createLevel2(player);
            case 3:
                return createLevel3(player);
            case 4:
                return createLevel4(player);
            case 5:
                return createLevel5(player);
            default:
                return createLevel1(player); // Nivel por defecto
        }
    }
    
    /**
     * NIVEL 1 - Tutorial básico
     * Mapa pequeño, pocas paredes, 4 frutas estáticas, 1 enemigo
     */
    private static Level createLevel1(IceCream player) {
        Level level = new Level(1, 25, 18);
        
        // Paredes perimetrales
        addPerimeterWalls(level, 25, 18);
        
        // Obstáculos internos - Bloques de hielo destruibles
        // Pared horizontal central
        for (int x = 8; x <= 16; x++) {
            level.addIceWall(new Location(x, 9));
        }
        
        // Columnas verticales
        for (int y = 3; y <= 6; y++) {
            level.addIceWall(new Location(6, y));
            level.addIceWall(new Location(18, y));
        }
        
        for (int y = 11; y <= 14; y++) {
            level.addIceWall(new Location(6, y));
            level.addIceWall(new Location(18, y));
        }
        
        // Asignar jugador (la posición ya fue establecida en el constructor)
        level.setPlayer(player);
        
        // Frutas: Solo Bananas y Uvas (estáticas)
        level.addFruit(new Banana(new Location(22, 2)));
        level.addFruit(new Banana(new Location(2, 15)));
        level.addFruit(new Banana(new Location(22, 15)));
        level.addFruit(new Banana(new Location(12, 2)));
        level.addFruit(new Grapes(new Location(4, 4)));
        level.addFruit(new Grapes(new Location(20, 4)));
        level.addFruit(new Grapes(new Location(4, 13)));
        level.addFruit(new Grapes(new Location(20, 13)));
        
        // Enemigos: 2 Trolls (colocados FUERA de las paredes)
        level.addEnemy(new Troll(new Location(10, 7)));
        level.addEnemy(new Troll(new Location(14, 11)));
        
        // Baldosas calientes (introducción)
        level.getMap().addHotTile(new BaldosaCaliente(new Location(12, 12)));
        level.getMap().addHotTile(new BaldosaCaliente(new Location(12, 13)));
        
        return level;
    }
    
    /**
     * NIVEL 2 - Laberinto simple
     * Más paredes, 5 frutas (1 móvil), 2 enemigos
     */
    private static Level createLevel2(IceCream player) {
        Level level = new Level(2, 25, 18);
        
        addPerimeterWalls(level, 25, 18);
        
        // Laberinto en forma de cruz - Bloques de hielo destruibles
        for (int x = 10; x <= 14; x++) {
            level.addIceWall(new Location(x, 5));
            level.addIceWall(new Location(x, 12));
        }
        
        for (int y = 5; y <= 12; y++) {
            level.addIceWall(new Location(10, y));
            level.addIceWall(new Location(14, y));
        }
        
        // Obstáculos adicionales
        for (int x = 4; x <= 7; x++) {
            level.addIceWall(new Location(x, 8));
            level.addIceWall(new Location(x + 13, 8));
        }
        
        level.setPlayer(player);
        
        // Frutas: 8 Piñas (móviles) y 8 Bananas (estáticas)
        // Bananas
        level.addFruit(new Banana(new Location(3, 2)));
        level.addFruit(new Banana(new Location(22, 2)));
        level.addFruit(new Banana(new Location(3, 15)));
        level.addFruit(new Banana(new Location(22, 15)));
        level.addFruit(new Banana(new Location(7, 3)));
        level.addFruit(new Banana(new Location(17, 3)));
        level.addFruit(new Banana(new Location(7, 14)));
        level.addFruit(new Banana(new Location(17, 14)));
        
        // Piñas (móviles)
        level.addFruit(new Pineapple(new Location(12, 3)));
        level.addFruit(new Pineapple(new Location(6, 10)));
        level.addFruit(new Pineapple(new Location(18, 10)));
        level.addFruit(new Pineapple(new Location(12, 14)));
        level.addFruit(new Pineapple(new Location(8, 7)));
        level.addFruit(new Pineapple(new Location(16, 7)));
        level.addFruit(new Pineapple(new Location(8, 9)));
        level.addFruit(new Pineapple(new Location(16, 9)));
        
        // Cactus (introducción - espinas peligrosas)
        level.addFruit(new Cactus(new Location(12, 10)));
        
        // Enemigo: 1 Maceta (colocada lejos del jugador que está en 2,2)
        level.addEnemy(new Maceta(new Location(22, 15)));
        
        // Fogatas (introducción - se pueden apagar)
        level.getMap().addCampfire(new Fogata(new Location(12, 8)));
        level.getMap().addCampfire(new Fogata(new Location(4, 4)));
        
        return level;
    }
    
    /**
     * NIVEL 3 - Habitaciones
     * Diseño con habitaciones, 6 frutas (2 móviles), 2 enemigos
     */
    private static Level createLevel3(IceCream player) {
        Level level = new Level(3, 25, 18);
        
        addPerimeterWalls(level, 25, 18);
        
        // Crear habitaciones con puertas - Bloques de hielo destruibles
        // Pared horizontal con aberturas
        for (int x = 1; x <= 23; x++) {
            if (x != 8 && x != 12 && x != 16) {
                level.addIceWall(new Location(x, 9));
            }
        }
        
        // Paredes verticales con aberturas
        for (int y = 1; y <= 16; y++) {
            if (y != 5 && y != 13) {
                level.addIceWall(new Location(8, y));
                level.addIceWall(new Location(16, y));
            }
        }
        
        level.setPlayer(player);
        
        // Frutas distribuidas en habitaciones
        level.addFruit(new Banana(new Location(4, 4)));
        level.addFruit(new Grapes(new Location(20, 4)));
        level.addFruit(new Cherry(new Location(4, 13))); // Móvil - teletransporte
        level.addFruit(new Pineapple(new Location(20, 13))); // Móvil - se empuja
        level.addFruit(new Banana(new Location(12, 4)));
        level.addFruit(new Grapes(new Location(12, 13)));
        level.addFruit(new Cactus(new Location(12, 7))); // Peligroso - espinas
        level.addFruit(new Cactus(new Location(12, 11))); // Peligroso - espinas
        
        // Enemigos: Introduce Narval en este nivel
        level.addEnemy(new Troll(new Location(12, 12)));
        level.addEnemy(new Maceta(new Location(18, 6)));
        level.addEnemy(new Narval(new Location(4, 9))); // Narval patrulla horizontalmente
        
        // Baldosas calientes en pasillos
        level.getMap().addHotTile(new BaldosaCaliente(new Location(12, 5)));
        level.getMap().addHotTile(new BaldosaCaliente(new Location(12, 6)));
        level.getMap().addHotTile(new BaldosaCaliente(new Location(12, 13)));
        
        // Fogatas en esquinas de habitaciones
        level.getMap().addCampfire(new Fogata(new Location(2, 2)));
        level.getMap().addCampfire(new Fogata(new Location(22, 15)));
        
        return level;
    }
    
    /**
     * NIVEL 4 - Desafío medio
     * Más complejo, 7 frutas (3 móviles), 3 enemigos
     */
    private static Level createLevel4(IceCream player) {
        Level level = new Level(4, 25, 18);
        
        addPerimeterWalls(level, 25, 18);
        
        // Patrón de espiral - Bloques de hielo destruibles
        for (int x = 3; x <= 21; x++) {
            level.addIceWall(new Location(x, 3));
        }
        for (int y = 3; y <= 14; y++) {
            level.addIceWall(new Location(21, y));
        }
        for (int x = 6; x <= 21; x++) {
            level.addIceWall(new Location(x, 14));
        }
        for (int y = 6; y <= 14; y++) {
            level.addIceWall(new Location(6, y));
        }
        for (int x = 6; x <= 18; x++) {
            level.addIceWall(new Location(x, 6));
        }
        for (int y = 6; y <= 11; y++) {
            level.addIceWall(new Location(18, y));
        }
        
        level.setPlayer(player);
        
        // Frutas variadas
        level.addFruit(new Banana(new Location(10, 2)));
        level.addFruit(new Grapes(new Location(22, 10)));
        level.addFruit(new Cherry(new Location(15, 15))); // Móvil
        level.addFruit(new Pineapple(new Location(8, 10))); // Móvil
        level.addFruit(new Cherry(new Location(12, 8))); // Móvil
        level.addFruit(new Banana(new Location(22, 2)));
        level.addFruit(new Grapes(new Location(2, 15)));
        
        // 4 enemigos: Mayor desafío con Narval
        level.addEnemy(new Maceta(new Location(15, 10)));
        level.addEnemy(new Troll(new Location(10, 12)));
        level.addEnemy(new CalamarNaranja(new Location(20, 8)));
        level.addEnemy(new Narval(new Location(12, 2))); // Narval patrulla verticalmente
        
        return level;
    }
    
    /**
     * NIVEL 5 - Difícil
     * Laberinto complejo, 8 frutas (4 móviles), 4 enemigos
     */
    private static Level createLevel5(IceCream player) {
        Level level = new Level(5, 25, 18);
        
        addPerimeterWalls(level, 25, 18);
        
        // Laberinto complejo - Bloques de hielo destruibles
        // Columnas verticales
        for (int y = 2; y <= 15; y += 3) {
            for (int i = 0; i < 3; i++) {
                if (y + i < 16) {
                    level.addIceWall(new Location(5, y + i));
                    level.addIceWall(new Location(10, y + i));
                    level.addIceWall(new Location(15, y + i));
                    level.addIceWall(new Location(20, y + i));
                }
            }
        }
        
        // Líneas horizontales intercaladas
        for (int x = 3; x <= 7; x++) {
            level.addIceWall(new Location(x, 5));
            level.addIceWall(new Location(x, 12));
        }
        for (int x = 12; x <= 17; x++) {
            level.addIceWall(new Location(x, 5));
            level.addIceWall(new Location(x, 12));
        }
        
        level.setPlayer(player);
        
        // Muchas frutas
        level.addFruit(new Banana(new Location(3, 3)));
        level.addFruit(new Grapes(new Location(22, 3)));
        level.addFruit(new Cherry(new Location(8, 8))); // Móvil
        level.addFruit(new Pineapple(new Location(16, 8))); // Móvil
        level.addFruit(new Cherry(new Location(3, 14))); // Móvil
        level.addFruit(new Pineapple(new Location(22, 14))); // Móvil
        level.addFruit(new Banana(new Location(12, 3)));
        level.addFruit(new Grapes(new Location(12, 14)));
        
        // 6 enemigos: Máximo desafío con 2 Narvales
        level.addEnemy(new Maceta(new Location(8, 10)));
        level.addEnemy(new Troll(new Location(16, 10)));
        level.addEnemy(new CalamarNaranja(new Location(12, 6)));
        level.addEnemy(new Maceta(new Location(12, 13)));
        level.addEnemy(new Narval(new Location(2, 9))); // Narval horizontal izquierdo
        level.addEnemy(new Narval(new Location(23, 9))); // Narval horizontal derecho
        
        return level;
    }
    
    /**
     * Agrega paredes perimetrales al mapa
     */
    private static void addPerimeterWalls(Level level, int width, int height) {
        // Paredes horizontales (arriba y abajo)
        for (int x = 0; x < width; x++) {
            level.addWall(new Location(x, 0));
            level.addWall(new Location(x, height - 1));
        }
        
        // Paredes verticales (izquierda y derecha)
        for (int y = 1; y < height - 1; y++) {
            level.addWall(new Location(0, y));
            level.addWall(new Location(width - 1, y));
        }
    }
}
