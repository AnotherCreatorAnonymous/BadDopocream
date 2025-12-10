package domain;
import java.util.*;

public class BadDopoCream implements FruitCounter {
    
    private ArrayList<Level> levels;
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

    public List<Level> getLevels(){
        return new ArrayList<>(this.levels);
    }
    
    public boolean hasNextLevel(){
        return currentLevelIndex + 1 < levels.size();
    }
    
    public void resetToLevel(int levelNumber){
        if(levelNumber > 0 && levelNumber <= levels.size()){
            currentLevelIndex = levelNumber - 1;
            currentLevel = levels.get(currentLevelIndex);
            gameWon = false;
        }
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
}
