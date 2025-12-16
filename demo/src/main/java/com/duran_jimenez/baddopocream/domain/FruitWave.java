package com.duran_jimenez.baddopocream.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una oleada de frutas que aparece en el nivel
 * Las frutas se agrupan por tipo y puntos
 */
public class FruitWave {
    private final String waveType; // Tipo de fruta de esta oleada
    private final int points; // Puntos que vale este tipo de fruta
    private final List<Fruit> fruits; // Frutas de esta oleada
    private boolean isActive; // Si esta oleada está activa
    private boolean isCompleted; // Si todas las frutas fueron recolectadas
    
    public FruitWave(String waveType, int points) {
        this.waveType = waveType;
        this.points = points;
        this.fruits = new ArrayList<>();
        this.isActive = false;
        this.isCompleted = false;
    }
    
    /**
     * Agrega una fruta a esta oleada
     */
    public void addFruit(Fruit fruit) {
        fruits.add(fruit);
    }
    
    /**
     * Activa esta oleada, haciendo que las frutas aparezcan
     */
    public void activate() {
        this.isActive = true;
    }
    
    /**
     * Verifica si todas las frutas de esta oleada han sido recolectadas
     */
    public void checkCompletion() {
        if (!isActive) return;
        
        boolean allCollected = true;
        for (Fruit fruit : fruits) {
            if (!fruit.isCollected()) {
                allCollected = false;
                break;
            }
        }
        
        if (allCollected && !fruits.isEmpty()) {
            isCompleted = true;
        }
    }
    
    /**
     * Obtiene las frutas de esta oleada
     */
    public List<Fruit> getFruits() {
        return fruits;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public String getWaveType() {
        return waveType;
    }
    
    public int getPoints() {
        return points;
    }
    
    public int getTotalFruits() {
        return fruits.size();
    }
    
    public int getCollectedFruits() {
        int collected = 0;
        for (Fruit fruit : fruits) {
            if (fruit.isCollected()) {
                collected++;
            }
        }
        return collected;
    }
}
