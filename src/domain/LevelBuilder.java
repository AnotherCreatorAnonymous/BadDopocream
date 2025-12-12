package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Patrón Builder para construir niveles personalizados
 * Permite configurar frutas, enemigos y obstáculos antes de crear el nivel
 */
public class LevelBuilder {
    
    // Atributos básicos del nivel
    private int levelNumber;
    private int width;
    private int height;
    private int timeLimit;
    private IceCream player1;
    private IceCream player2;
    
    // Configuración de frutas (tipo y cantidad)
    private int bananaCount = 0;
    private int cherryCount = 0;
    private int grapesCount = 0;
    private int pineappleCount = 0;
    private int cactusCount = 0;
    
    // Configuración de enemigos (tipo y cantidad)
    private int trollCount = 0;
    private int macetaCount = 0;
    private int calamarCount = 0;
    private int narvalCount = 0;
    
    // Configuración de obstáculos
    private int hotTileCount = 0;
    private int campfireCount = 0;
    
    // Configuración de estructura
    private boolean addPerimeterWalls = true;
    private int iceWallCount = 0;
    
    private Random random = new Random();
    
    /**
     * Constructor privado - usar métodos estáticos para crear instancias
     */
    private LevelBuilder() {
        this.width = 25;
        this.height = 18;
        this.timeLimit = 120;
    }
    
    /**
     * Crea un nuevo builder para un nivel personalizado
     */
    public static LevelBuilder createCustomLevel(int levelNumber) {
        LevelBuilder builder = new LevelBuilder();
        builder.levelNumber = levelNumber;
        return builder;
    }
    
    /**
     * Crea un builder pre-configurado con valores por defecto
     */
    public static LevelBuilder createDefaultLevel(int levelNumber) {
        LevelBuilder builder = new LevelBuilder();
        builder.levelNumber = levelNumber;
        // Configuración por defecto basada en el número de nivel
        builder.bananaCount = 4 + levelNumber;
        builder.grapesCount = 3 + levelNumber;
        builder.trollCount = 1 + (levelNumber / 2);
        builder.macetaCount = levelNumber / 3;
        builder.iceWallCount = 10 + (levelNumber * 5);
        return builder;
    }
    
    // ==================== CONFIGURACIÓN DE DIMENSIONES ====================
    
    public LevelBuilder setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }
    
    public LevelBuilder setTimeLimit(int seconds) {
        this.timeLimit = seconds;
        return this;
    }
    
    // ==================== CONFIGURACIÓN DE JUGADORES ====================
    
    public LevelBuilder setPlayer1(IceCream player) {
        this.player1 = player;
        return this;
    }
    
    public LevelBuilder setPlayer2(IceCream player) {
        this.player2 = player;
        return this;
    }
    
    // ==================== CONFIGURACIÓN DE FRUTAS ====================
    
    public LevelBuilder addBananas(int count) {
        this.bananaCount = Math.max(0, count);
        return this;
    }
    
    public LevelBuilder addCherries(int count) {
        this.cherryCount = Math.max(0, count);
        return this;
    }
    
    public LevelBuilder addGrapes(int count) {
        this.grapesCount = Math.max(0, count);
        return this;
    }
    
    public LevelBuilder addPineapples(int count) {
        this.pineappleCount = Math.max(0, count);
        return this;
    }
    
    public LevelBuilder addCactus(int count) {
        this.cactusCount = Math.max(0, count);
        return this;
    }
    
    // ==================== CONFIGURACIÓN DE ENEMIGOS ====================
    
    public LevelBuilder addTrolls(int count) {
        this.trollCount = Math.max(0, count);
        return this;
    }
    
    public LevelBuilder addMacetas(int count) {
        this.macetaCount = Math.max(0, count);
        return this;
    }
    
    public LevelBuilder addCalamares(int count) {
        this.calamarCount = Math.max(0, count);
        return this;
    }
    
    public LevelBuilder addNarvales(int count) {
        this.narvalCount = Math.max(0, count);
        return this;
    }
    
    // ==================== CONFIGURACIÓN DE OBSTÁCULOS ====================
    
    public LevelBuilder addHotTiles(int count) {
        this.hotTileCount = Math.max(0, count);
        return this;
    }
    
    public LevelBuilder addCampfires(int count) {
        this.campfireCount = Math.max(0, count);
        return this;
    }
    
    public LevelBuilder addIceWalls(int count) {
        this.iceWallCount = Math.max(0, count);
        return this;
    }
    
    public LevelBuilder setPerimeterWalls(boolean add) {
        this.addPerimeterWalls = add;
        return this;
    }
    
    // ==================== CONSTRUCCIÓN DEL NIVEL ====================
    
    /**
     * Construye el nivel con la configuración especificada
     * @return Nivel completamente configurado y listo para jugar
     */
    public Level build() {
        // Validar que hay al menos un jugador
        if (player1 == null) {
            throw new IllegalStateException("Debe haber al menos un jugador configurado");
        }
        
        // Validar que hay al menos una fruta
        int totalFruits = bananaCount + cherryCount + grapesCount + pineappleCount + cactusCount;
        if (totalFruits == 0) {
            throw new IllegalStateException("Debe haber al menos una fruta en el nivel");
        }
        
        // Crear el nivel
        Level level = new Level(levelNumber, width, height);
        
        // Configurar jugadores
        level.setPlayer(player1);
        if (player2 != null) {
            level.setPlayer2(player2);
        }
        
        // Generar estructura del mapa
        if (addPerimeterWalls) {
            addPerimeterWallsToLevel(level);
        }
        
        // Generar posiciones válidas para colocar elementos
        List<Location> availablePositions = generateAvailablePositions(level);
        
        // Agregar paredes de hielo
        addIceWallsToLevel(level, availablePositions);
        
        // Agregar frutas
        addFruitsToLevel(level, availablePositions);
        
        // Agregar enemigos
        addEnemiesToLevel(level, availablePositions);
        
        // Agregar obstáculos
        addObstaclesToLevel(level, availablePositions);
        
        return level;
    }
    
    // ==================== MÉTODOS AUXILIARES PRIVADOS ====================
    
    private void addPerimeterWallsToLevel(Level level) {
        Map map = level.getMap();
        // Paredes horizontales (arriba y abajo)
        for (int x = 0; x < width; x++) {
            map.addWall(new Location(x, 0));
            map.addWall(new Location(x, height - 1));
        }
        // Paredes verticales (izquierda y derecha)
        for (int y = 0; y < height; y++) {
            map.addWall(new Location(0, y));
            map.addWall(new Location(width - 1, y));
        }
    }
    
    private List<Location> generateAvailablePositions(Level level) {
        List<Location> positions = new ArrayList<>();
        Map map = level.getMap();
        
        // Generar todas las posiciones válidas (no paredes, no jugadores)
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                Location loc = new Location(x, y);
                
                // Excluir posiciones de jugadores (con margen de seguridad)
                if (player1 != null && isNearLocation(loc, player1.getLocation(), 2)) {
                    continue;
                }
                if (player2 != null && isNearLocation(loc, player2.getLocation(), 2)) {
                    continue;
                }
                
                // Excluir si ya es pared
                if (!map.isWall(loc)) {
                    positions.add(loc);
                }
            }
        }
        
        return positions;
    }
    
    private boolean isNearLocation(Location loc1, Location loc2, int distance) {
        int dx = Math.abs(loc1.getX() - loc2.getX());
        int dy = Math.abs(loc1.getY() - loc2.getY());
        return dx <= distance && dy <= distance;
    }
    
    private void addIceWallsToLevel(Level level, List<Location> availablePositions) {
        for (int i = 0; i < iceWallCount && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            Location loc = availablePositions.remove(index);
            level.addIceWall(loc);
        }
    }
    
    private void addFruitsToLevel(Level level, List<Location> availablePositions) {
        // Agregar Bananas
        for (int i = 0; i < bananaCount && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            Location loc = availablePositions.remove(index);
            level.addFruit(new Banana(loc));
        }
        
        // Agregar Cherries
        for (int i = 0; i < cherryCount && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            Location loc = availablePositions.remove(index);
            level.addFruit(new Cherry(loc));
        }
        
        // Agregar Grapes
        for (int i = 0; i < grapesCount && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            Location loc = availablePositions.remove(index);
            level.addFruit(new Grapes(loc));
        }
        
        // Agregar Pineapples
        for (int i = 0; i < pineappleCount && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            Location loc = availablePositions.remove(index);
            level.addFruit(new Pineapple(loc));
        }
        
        // Agregar Cactus
        for (int i = 0; i < cactusCount && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            Location loc = availablePositions.remove(index);
            level.addFruit(new Cactus(loc));
        }
    }
    
    private void addEnemiesToLevel(Level level, List<Location> availablePositions) {
        // Agregar Trolls
        for (int i = 0; i < trollCount && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            Location loc = availablePositions.remove(index);
            level.addEnemy(new Troll(loc));
        }
        
        // Agregar Macetas
        for (int i = 0; i < macetaCount && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            Location loc = availablePositions.remove(index);
            level.addEnemy(new Maceta(loc));
        }
        
        // Agregar Calamares
        for (int i = 0; i < calamarCount && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            Location loc = availablePositions.remove(index);
            level.addEnemy(new CalamarNaranja(loc));
        }
        
        // Agregar Narvales
        for (int i = 0; i < narvalCount && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            Location loc = availablePositions.remove(index);
            level.addEnemy(new Narval(loc));
        }
    }
    
    private void addObstaclesToLevel(Level level, List<Location> availablePositions) {
        Map map = level.getMap();
        
        // Agregar Baldosas Calientes
        for (int i = 0; i < hotTileCount && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            Location loc = availablePositions.remove(index);
            map.addHotTile(new BaldosaCaliente(loc));
        }
        
        // Agregar Fogatas
        for (int i = 0; i < campfireCount && !availablePositions.isEmpty(); i++) {
            int index = random.nextInt(availablePositions.size());
            Location loc = availablePositions.remove(index);
            map.addCampfire(new Fogata(loc));
        }
    }
    
    // ==================== GETTERS PARA VALIDACIÓN ====================
    
    public int getTotalFruits() {
        return bananaCount + cherryCount + grapesCount + pineappleCount + cactusCount;
    }
    
    public int getTotalEnemies() {
        return trollCount + macetaCount + calamarCount + narvalCount;
    }
    
    public int getBananaCount() { return bananaCount; }
    public int getCherryCount() { return cherryCount; }
    public int getGrapesCount() { return grapesCount; }
    public int getPineappleCount() { return pineappleCount; }
    public int getCactusCount() { return cactusCount; }
    
    public int getTrollCount() { return trollCount; }
    public int getMacetaCount() { return macetaCount; }
    public int getCalamarCount() { return calamarCount; }
    public int getNarvalCount() { return narvalCount; }
}
