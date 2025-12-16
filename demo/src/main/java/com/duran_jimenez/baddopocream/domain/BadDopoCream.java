package com.duran_jimenez.baddopocream.domain;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal del juego Bad Dopo Cream.
 * 
 * Implementa el patrón Facade para encapsular toda la lógica del dominio
 * y exponer una API limpia para la capa de presentación.
 * 
 * Responsabilidades:
 * - Gestión del ciclo de vida del juego (inicio, pausa, fin)
 * - Control de niveles y progresión
 * - Sistema de puntaje y highscores
 * - Guardado y carga de partidas
 * - Interfaz unificada para la presentación (sin exponer clases internas)
 * 
 * @author Durán-Jiménez
 * @version 2.0
 */
public class BadDopoCream implements FruitCounter {
    
    /** Número máximo de niveles planeados para el juego */
    public static final int MAX_LEVELS = 10;
    
    /** Número de niveles actualmente implementados y jugables */
    public static final int PLAYABLE_LEVELS = 5;
    
    private final ArrayList<Level> levels;
    private int currentLevelIndex;
    private Level currentLevel;
    private int totalScore;
    private boolean gameWon;

    public BadDopoCream(){
        this.levels = new ArrayList<>();
        this.currentLevelIndex = 0;
        this.totalScore = 0;
        this.gameWon = false;
    }

    public void addLevel(Level level){
        this.levels.add(level);
    }

    public void startGame(){
        if(!levels.isEmpty()){
            currentLevelIndex = 0;
            currentLevel = levels.get(currentLevelIndex);
        }
    }

    public boolean nextLevel(){
        if(currentLevelIndex + 1 < levels.size()){
            currentLevelIndex++;
            currentLevel = levels.get(currentLevelIndex);
            return true;
        } else {
            gameWon = true; 
            return false;
        }
    }

    @Override
    public int countCollectedFruits(){
        if(currentLevel != null){
            return currentLevel.getCollectedFruits();
        }
        return 0;
    }

    @Override
    public int countTotalFruits(){
        if(currentLevel != null){
            return currentLevel.getTotalFruits();
        }
        return 0;
    }
    
    public void update(){
        if(currentLevel != null && !currentLevel.isCompleted() && !currentLevel.isGameOver()){
            currentLevel.moveEnemies();
            currentLevel.moveFruits();
            if(currentLevel.isCompleted()){
                // Acumular puntaje del nivel al total
                totalScore += currentLevel.getCurrentScore();
                nextLevel();
            }
        }
    }

    public boolean isGameOver(){
        return currentLevel != null && currentLevel.isGameOver();
    }

    public boolean isGameWon(){
        return this.gameWon;
    }

    public Level getCurrentLevel(){
        return this.currentLevel;
    }
    
    /**
     * Establece el nivel actual (usado cuando se construye con LevelBuilder)
     * @param level Nivel construido externamente
     */
    public void setCurrentLevel(Level level){
        this.currentLevel = level;
        // Agregar a la lista si no está
        if(!levels.contains(level)){
            levels.add(level);
        }
        // Actualizar el índice al último nivel agregado
        this.currentLevelIndex = levels.size() - 1;
    }

    public int getCurrentLevelNumber(){
        return currentLevelIndex + 1;
    }

    public int getTotalScore(){
        return this.totalScore;
    }
    
    public int getCurrentLevelScore(){
        if(currentLevel != null){
            return currentLevel.getCurrentScore();
        }
        return 0;
    }
    
    public int getCombinedScore(){
        // Retorna puntaje total acumulado + puntaje del nivel actual
        return totalScore + getCurrentLevelScore();
    }
    
    /**
     * Obtiene el puntaje del jugador 1 en el nivel actual
     */
    public int getPlayer1Score(){
        if(currentLevel != null){
            return currentLevel.getPlayer1Score();
        }
        return 0;
    }
    
    /**
     * Obtiene el puntaje del jugador 2 en el nivel actual
     */
    public int getPlayer2Score(){
        if(currentLevel != null){
            return currentLevel.getPlayer2Score();
        }
        return 0;
    }

    public List<Level> getLevels(){
        return new ArrayList<>(this.levels);
    }
    
    /**
     * Verifica si hay un siguiente nivel disponible
     * @return true si el siguiente nivel existe y está implementado
     */
    public boolean hasNextLevel(){
        int nextLevelNumber = currentLevelIndex + 2; // +2 porque index es 0-based y queremos el siguiente
        return nextLevelNumber <= PLAYABLE_LEVELS;
    }
    
    public void resetToLevel(int levelNumber){
        if(levelNumber > 0 && levelNumber <= levels.size()){
            currentLevelIndex = levelNumber - 1;
            currentLevel = levels.get(currentLevelIndex);
            gameWon = false;
        }
    }
    
    /**
     * Reinicia completamente el juego, limpiando todos los niveles y estados
     * Útil cuando se vuelve al menú principal para empezar una nueva partida
     */
    public void resetGame(){
        this.levels.clear();
        this.currentLevelIndex = 0;
        this.currentLevel = null;
        this.totalScore = 0;
        this.gameWon = false;
    }
    
    /**
     * Restaura el juego desde un estado guardado
     */
    public void restoreFromState(GameState state){
        if(state == null) return;
        
        // Restaurar nivel actual
        this.currentLevelIndex = state.getCurrentLevelIndex();
        if(currentLevelIndex >= 0 && currentLevelIndex < levels.size()){
            this.currentLevel = levels.get(currentLevelIndex);
            
            // Restaurar totalScore
            this.totalScore = state.getTotalScore();
            this.gameWon = false;
            
            // Restaurar estado del nivel
            if(currentLevel != null){
                currentLevel.restoreFromState(state);
            }
        }
    }
    
    public void setTotalScore(int score){
        this.totalScore = score;
    }
    
    /**
     * Agrega puntaje al total acumulado
     */
    public void addToTotalScore(int score){
            this.totalScore += score;
        }
    
    // ==================== MÉTODOS DE FACHADA PARA PRESENTACIÓN ====================
    // Estos métodos encapsulan la lógica del dominio para que la presentación
    // NO tenga que importar ni usar directamente las clases del dominio
    
    /**
     * Obtiene información del jugador 1
     */
    public PlayerInfo getPlayer1Info(){ 
        if(currentLevel == null) return null;
        IceCream player = currentLevel.getPlayer();
        if(player == null) return null;
        
        Location loc = player.getLocation();
        return new PlayerInfo(loc.getX(), loc.getY(), player.isAlive(), player.getLastDx(), player.getLastDy(), player.getName(), player.getColor());
    }
    
    /**
     * Obtiene información del jugador 2 (si existe)
     */
    public PlayerInfo getPlayer2Info(){
        if(currentLevel == null || !currentLevel.hasTwoPlayers()) return null;
        IceCream player2 = currentLevel.getPlayer2();
        if(player2 == null) return null;
        
        Location loc = player2.getLocation();
        return new PlayerInfo(loc.getX(), loc.getY(), player2.isAlive(), player2.getLastDx(), player2.getLastDy(), player2.getName(), player2.getColor());
    }
    
    /**
     * Obtiene lista de información de todas las frutas
     */
    public List<FruitInfo> getAllFruitsInfo(){
        if(currentLevel == null) return new ArrayList<>();
        
        List<FruitInfo> fruitsInfo = new ArrayList<>();
        for(Fruit fruit : currentLevel.getFruits()){
            Location loc = fruit.getLocation();
            fruitsInfo.add(new FruitInfo(
                loc.getX(),
                loc.getY(),
                fruit.getTypeName(),
                fruit.getName(),
                fruit.isCollected(),
                fruit.hasSpikes(),
                fruit.getSecondsUntilNextSpike()
            ));
        }
        return fruitsInfo;
    }
    
    /**
     * Obtiene lista de información de todos los enemigos
     */
    public List<EnemyInfo> getAllEnemiesInfo(){
        if(currentLevel == null) return new ArrayList<>();
        
        List<EnemyInfo> enemiesInfo = new ArrayList<>();
        for(Enemy enemy : currentLevel.getEnemies()){
            Location loc = enemy.getLocation();
            enemiesInfo.add(new EnemyInfo(
                loc.getX(),
                loc.getY(),
                enemy.getTypeName(),
                enemy.getDirectionX(),
                enemy.getDirectionY()
            ));
        }
        return enemiesInfo;
    }
    
    /**
     * Obtiene lista de información de todos los obstáculos
     */
    public List<ObstacleInfo> getAllObstaclesInfo(){
        if(currentLevel == null) return new ArrayList<>();
        
        Map map = currentLevel.getMap();
        if(map == null) return new ArrayList<>();
        
        List<ObstacleInfo> obstaclesInfo = new ArrayList<>();
        
        // Baldosas calientes
        for(BaldosaCaliente hotTile : map.getHotTiles()){
            Location loc = hotTile.getLocation();
            obstaclesInfo.add(new ObstacleInfo(loc.getX(), loc.getY(), "HotTile", false, 0));
        }
        
        // Fogatas
        for(Fogata campfire : map.getCampfires()){
            Location loc = campfire.getLocation();
            obstaclesInfo.add(new ObstacleInfo(
                loc.getX(),
                loc.getY(),
                "Campfire",
                campfire.isLit(),
                campfire.getSecondsUntilRelight()
            ));
        }
        
        return obstaclesInfo;
    }
    
    /**
     * Obtiene el mapa del nivel actual (grid de enteros)
     */
    public int[][] getCurrentMapGrid(){
        if(currentLevel == null) return new int[0][0];
        Map map = currentLevel.getMap();
        if(map == null) return new int[0][0];
        return map.getGrid();
    }
    
    /**
     * Obtiene el ancho del mapa
     */
    public int getMapWidth(){
        if(currentLevel == null) return 0;
        Map map = currentLevel.getMap();
        return map != null ? map.getWidth() : 0;
    }
    
    /**
     * Obtiene el alto del mapa
     */
    public int getMapHeight(){
        if(currentLevel == null) return 0;
        Map map = currentLevel.getMap();
        return map != null ? map.getHeight() : 0;
    }
    
    /**
     * Mueve el jugador 1
     */
    public boolean movePlayer1(int dx, int dy){
        if(currentLevel != null){
            return currentLevel.movePlayer(dx, dy);
        }
        return false;
    }
    
    /**
     * Mueve el jugador 2
     */
    public boolean movePlayer2(int dx, int dy){
        if(currentLevel != null){
            return currentLevel.movePlayer2(dx, dy);
        }
        return false;
    }
    
    /**
     * Crea línea de hielo para jugador 1
     */
    public void createIceLinePlayer1(int dx, int dy){
        if(currentLevel != null){
            currentLevel.createIceLine(dx, dy);
        }
    }
    
    /**
     * Crea línea de hielo para jugador 2
     */
    public void createIceLinePlayer2(int dx, int dy){
        if(currentLevel != null){
            currentLevel.createIceLinePlayer2(dx, dy);
        }
    }
    
    /**
     * Rompe línea de hielo para jugador 1
     */
    public void breakIceLinePlayer1(int dx, int dy){
        if(currentLevel != null){
            currentLevel.breakIceLine(dx, dy);
        }
    }
    
    /**
     * Rompe línea de hielo para jugador 2
     */
    public void breakIceLinePlayer2(int dx, int dy){
        if(currentLevel != null){
            currentLevel.breakIceLinePlayer2(dx, dy);
        }
    }
    
    /**
     * Verifica si hay dos jugadores en el nivel actual
     */
    public boolean hasTwoPlayers(){
        return currentLevel != null && currentLevel.hasTwoPlayers();
    }
    
    /**
     * Verifica si el nivel actual está completado
     */
    public boolean isCurrentLevelCompleted(){
        return currentLevel != null && currentLevel.isCompleted();
    }
    
    /**
     * Verifica si el tiempo del nivel se agotó
     */
    public boolean isTimeExpired(){
        return currentLevel != null && currentLevel.isTimeExpired();
    }
    
    /**
     * Obtiene los segundos restantes del nivel actual
     */
    public int getRemainingSeconds(){
        if(currentLevel != null){
            return currentLevel.getRemainingSeconds();
        }
        return 0;
    }
    
    /**
     * Pausa el nivel actual
     */
    public void pause(){
        if(currentLevel != null){
            currentLevel.pause();
        }
    }
    
    /**
     * Reanuda el nivel actual
     */
    public void resume(){
        if(currentLevel != null){
            currentLevel.resume();
        }
    }
    
    /**
     * Verifica si el nivel está pausado
     */
    public boolean isPaused(){
        return currentLevel != null && currentLevel.isPaused();
    }
    
    // ==================== MÉTODOS DE FACHADA PARA GUARDADO/CARGA ====================
    
    /**
     * Guarda la partida actual con el nombre especificado
     */
    public boolean saveGame(String playerColor, String gameMode, String saveName){
        return GameState.saveGameSafe(this, playerColor, gameMode, saveName);
    }
    
    /**
     * Lista todos los juegos guardados
     */
    public static List<String> listSavedGames(){
        return GameState.listSavedGames();
    }
    
    /**
     * Carga un juego guardado y retorna el GameState
     */
    public static GameState loadGame(String saveName){
        return GameState.loadGameSafe(saveName);
    }
    
    /**
     * Elimina un juego guardado
     */
    public static boolean deleteSavedGame(String saveName){
        return GameState.deleteSave(saveName);
    }
}
