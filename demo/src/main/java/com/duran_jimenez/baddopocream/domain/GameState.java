package com.duran_jimenez.baddopocream.domain;

import java.io.*;
import java.util.*;

/**
 * Clase para guardar y cargar el estado del juego
 * Implementa Serializable para persistencia
 */
public class GameState implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final String SAVE_DIRECTORY = "saves";
    private static final String SAVE_EXTENSION = ".bdcsave";
    
    // Datos del juego a guardar
    private int currentLevelIndex;
    private int totalScore;
    private String playerColor;
    private String gameMode;
    private long saveTimestamp;
    
    // Datos del nivel actual
    private int levelScore;
    private int collectedFruits;
    private long remainingTime;
    
    // Posiciones
    private SerializableLocation playerLocation;
    private SerializableLocation player2Location;
    private List<SerializableFruit> fruits;
    private List<SerializableEnemy> enemies;
    
    public GameState() {
        this.saveTimestamp = System.currentTimeMillis();
        this.fruits = new ArrayList<>();
        this.enemies = new ArrayList<>();
    }
    
    /**
     * Guarda el estado actual del juego en un archivo
     * @throws BadDopoCream_Exceptions si falla el guardado
     */
    public static boolean saveGame(BadDopoCream game, String playerColor, String gameMode, String saveName) 
            throws BadDopoCream_Exceptions {
        try {
            // Crear directorio de guardado si no existe
            File saveDir = new File(SAVE_DIRECTORY);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            
            GameState state = new GameState();
            Level currentLevel = game.getCurrentLevel();
            
            if (currentLevel == null) {
                return false;
            }
            
            // Guardar datos generales
            state.currentLevelIndex = game.getCurrentLevelNumber() - 1;
            state.totalScore = game.getTotalScore();
            state.playerColor = playerColor;
            state.gameMode = gameMode;
            
            // Guardar datos del nivel
            state.levelScore = currentLevel.getCurrentScore();
            state.collectedFruits = currentLevel.getCollectedFruits();
            state.remainingTime = currentLevel.getRemainingTime();
            
            // Guardar posición del jugador
            IceCream player = currentLevel.getPlayer();
            if (player != null) {
                state.playerLocation = new SerializableLocation(player.getLocation());
            }
            
            // Guardar posición del jugador 2 si existe
            if (currentLevel.hasTwoPlayers()) {
                IceCream player2 = currentLevel.getPlayer2();
                if (player2 != null) {
                    state.player2Location = new SerializableLocation(player2.getLocation());
                }
            }
            
            guardarFrutas(currentLevel.getFruits(), state);
            guardarEnemigos(currentLevel.getEnemies(), state);
            
            // Escribir archivo
            String fileName = SAVE_DIRECTORY + "/" + saveName + SAVE_EXTENSION;
            FileOutputStream fileOut = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(state);
            out.close();
            fileOut.close();
            
            return true;
            
        } catch (IOException e) {
            throw new BadDopoCream_Exceptions(
                BadDopoCream_Exceptions.SAVE_GAME_ERROR + " '" + saveName + "'", e);
        }
    }
    

    private static void guardarFrutas(ArrayList<Fruit> frutas, GameState state) {
        for (Fruit fruta : frutas) {
            state.fruits.add(new SerializableFruit(fruta));
        }
    }

    private static void guardarEnemigos(ArrayList<Enemy> enemigos, GameState state) {
        for (Enemy enemigo : enemigos) {
            state.enemies.add(new SerializableEnemy(enemigo));
        }
    }

    /**
     * Guarda el juego sin lanzar excepciones (wrapper seguro)
     */
    public static boolean saveGameSafe(BadDopoCream game, String playerColor, String gameMode, String saveName) {
        try {
            saveGame(game, playerColor, gameMode, saveName);
            return true;
        } catch (BadDopoCream_Exceptions e) {
            System.err.println("Error al guardar partida: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Carga un juego guardado desde un archivo
     * @throws BadDopoCream_Exceptions si falla la carga o el archivo está corrupto
     */
    public static GameState loadGame(String saveName) 
            throws BadDopoCream_Exceptions {
        try {
            String fileName = SAVE_DIRECTORY + "/" + saveName + SAVE_EXTENSION;
            FileInputStream fileIn = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            GameState state = (GameState) in.readObject();
            in.close();
            fileIn.close();
            return state;
            
        } catch (ClassNotFoundException e) {
            throw new BadDopoCream_Exceptions(
                BadDopoCream_Exceptions.CORRUPTED_SAVE_FILE + " '" + saveName + "'", e);
        } catch (IOException e) {
            throw new BadDopoCream_Exceptions(
                BadDopoCream_Exceptions.LOAD_GAME_ERROR + " '" + saveName + "'", e);
        }
    }
    
    /**
     * Carga un juego sin lanzar excepciones (wrapper seguro)
     */
    public static GameState loadGameSafe(String saveName) {
        try {
            return loadGame(saveName);
        } catch (BadDopoCream_Exceptions e) {
            System.err.println("Error al cargar partida: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lista todos los archivos de guardado disponibles
     */
    public static List<String> listSavedGames() {
        List<String> saves = new ArrayList<>();
        File saveDir = new File(SAVE_DIRECTORY);
        
        if (saveDir.exists() && saveDir.isDirectory()) {
            File[] files = saveDir.listFiles((dir, name) -> name.endsWith(SAVE_EXTENSION));
            if (files != null) {
                for (File file : files) {
                    String name = file.getName().replace(SAVE_EXTENSION, "");
                    saves.add(name);
                }
            }
        }
        
        return saves;
    }
    
    /**
     * Elimina un archivo de guardado
     */
    public static boolean deleteSave(String saveName) {
        String fileName = SAVE_DIRECTORY + "/" + saveName + SAVE_EXTENSION;
        File file = new File(fileName);
        return file.exists() && file.delete();
    }
    
    // Getters
    public int getCurrentLevelIndex() { return currentLevelIndex; }
    public int getTotalScore() { return totalScore; }
    public String getPlayerColor() { return playerColor; }
    public String getGameMode() { return gameMode; }
    public long getSaveTimestamp() { return saveTimestamp; }
    public int getLevelScore() { return levelScore; }
    public int getCollectedFruits() { return collectedFruits; }
    public long getRemainingTime() { return remainingTime; }
    public SerializableLocation getPlayerLocation() { return playerLocation; }
    public SerializableLocation getPlayer2Location() { return player2Location; }
    public List<SerializableFruit> getFruits() { return fruits; }
    public List<SerializableEnemy> getEnemies() { return enemies; }
    
    // Clases internas para serialización
    static class SerializableLocation implements Serializable {
        private static final long serialVersionUID = 1L;
        int x, y;
        
        SerializableLocation(Location loc) {
            this.x = loc.getX();
            this.y = loc.getY();
        }
        
        Location toLocation() {
            return new Location(x, y);
        }
    }
    
    static class SerializableFruit implements Serializable {
        private static final long serialVersionUID = 1L;
        String name;
        SerializableLocation location;
        boolean collected;
        
        SerializableFruit(Fruit fruit) {
            this.name = fruit.getName();
            this.location = new SerializableLocation(fruit.getLocation());
            this.collected = fruit.isCollected();
        }
    }
    
    static class SerializableEnemy implements Serializable {
        private static final long serialVersionUID = 1L;
        String type;
        SerializableLocation location;
        
        SerializableEnemy(Enemy enemy) {
            this.type = enemy.getName();
            this.location = new SerializableLocation(enemy.getLocation());
        }
    }
}
