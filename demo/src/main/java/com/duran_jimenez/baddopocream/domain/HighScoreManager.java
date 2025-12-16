package com.duran_jimenez.baddopocream.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestor de highscores para Bad Dopo Cream
 * Almacena y recupera los mejores puntajes de los jugadores
 */
public class HighScoreManager {
    
    private static final String HIGHSCORE_FILE = "highscores.dat";
    private static final int MAX_HIGHSCORES = 10;
    
    private List<HighScoreEntry> highScores;
    
    public HighScoreManager() {
        this.highScores = new ArrayList<>();
        loadHighScores();
    }
    
    /**
     * Representa una entrada de highscore
     */
    public static class HighScoreEntry implements Serializable, Comparable<HighScoreEntry> {
        private static final long serialVersionUID = 1L;
        
        private final String playerName;
        private final int score;
        private final String gameMode;
        private final long timestamp;
        private final int levelsCompleted;
        
        public HighScoreEntry(String playerName, int score, String gameMode, int levelsCompleted) {
            this.playerName = playerName;
            this.score = score;
            this.gameMode = gameMode;
            this.levelsCompleted = levelsCompleted;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getPlayerName() { return playerName; }
        public int getScore() { return score; }
        public String getGameMode() { return gameMode; }
        public long getTimestamp() { return timestamp; }
        public int getLevelsCompleted() { return levelsCompleted; }
        
        @Override
        public int compareTo(HighScoreEntry other) {
            // Ordenar de mayor a menor puntaje
            return Integer.compare(other.score, this.score);
        }
        
        @Override
        public String toString() {
            return String.format("%s - %d pts (%s)", playerName, score, gameMode);
        }
    }
    
    /**
     * Agrega un nuevo highscore si califica
     * @return true si el puntaje fue agregado a la lista de highscores
     */
    public boolean addHighScore(String playerName, int score, String gameMode, int levelsCompleted) {
        // Limpiar el nombre
        String cleanName = playerName.trim();
        if (cleanName.isEmpty()) {
            cleanName = "Anónimo";
        }
        if (cleanName.length() > 20) {
            cleanName = cleanName.substring(0, 20);
        }
        
        HighScoreEntry newEntry = new HighScoreEntry(cleanName, score, gameMode, levelsCompleted);
        
        // Verificar si califica para la lista
        if (highScores.size() < MAX_HIGHSCORES || isHighScore(score)) {
            highScores.add(newEntry);
            Collections.sort(highScores);
            
            // Mantener solo los mejores MAX_HIGHSCORES
            while (highScores.size() > MAX_HIGHSCORES) {
                highScores.remove(highScores.size() - 1);
            }
            
            saveHighScores();
            return true;
        }
        
        return false;
    }
    
    /**
     * Verifica si un puntaje califica para la lista de highscores
     */
    public boolean isHighScore(int score) {
        if (score <= 0) return false;
        if (highScores.size() < MAX_HIGHSCORES) return true;
        
        // Verificar si el puntaje es mayor que el menor en la lista
        HighScoreEntry lowest = highScores.get(highScores.size() - 1);
        return score > lowest.getScore();
    }
    
    /**
     * Obtiene la lista de highscores
     */
    public List<HighScoreEntry> getHighScores() {
        return new ArrayList<>(highScores);
    }
    
    /**
     * Obtiene la posición que tendría un puntaje en la lista
     * @return posición (1-10) o -1 si no califica
     */
    public int getPositionForScore(int score) {
        if (!isHighScore(score) && highScores.size() >= MAX_HIGHSCORES) {
            return -1;
        }
        
        int position = 1;
        for (HighScoreEntry entry : highScores) {
            if (score > entry.getScore()) {
                return position;
            }
            position++;
        }
        return position;
    }
    
    /**
     * Guarda los highscores en archivo
     */
    private void saveHighScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(getHighScoreFilePath()))) {
            oos.writeObject(highScores);
        } catch (IOException e) {
            System.err.println("Error guardando highscores: " + e.getMessage());
        }
    }
    
    /**
     * Carga los highscores desde archivo
     */
    @SuppressWarnings("unchecked")
    private void loadHighScores() {
        File file = new File(getHighScoreFilePath());
        if (!file.exists()) {
            highScores = new ArrayList<>();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                highScores = (List<HighScoreEntry>) obj;
                Collections.sort(highScores);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error cargando highscores: " + e.getMessage());
            highScores = new ArrayList<>();
        }
    }
    
    /**
     * Obtiene la ruta del archivo de highscores
     */
    private String getHighScoreFilePath() {
        String userHome = System.getProperty("user.home");
        File gameDir = new File(userHome, ".baddopocream");
        if (!gameDir.exists()) {
            gameDir.mkdirs();
        }
        return new File(gameDir, HIGHSCORE_FILE).getAbsolutePath();
    }
    
    /**
     * Limpia todos los highscores (para testing o reset)
     */
    public void clearHighScores() {
        highScores.clear();
        saveHighScores();
    }
    
    /**
     * Obtiene el highscore más alto
     */
    public int getTopScore() {
        if (highScores.isEmpty()) return 0;
        return highScores.get(0).getScore();
    }
}
