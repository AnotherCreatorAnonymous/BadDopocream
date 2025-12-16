package com.duran_jimenez.baddopocream.domain;

import java.util.Random;

/**
 * Pineapple - Fruta que se mueve aleatoriamente por el mapa
 * Usa la interfaz Movable para gestionar su movimiento
 */
public class Pineapple extends Fruit implements Movable {
    
    private static final int PINEAPPLE_POINTS = 200;
    private Map map;
    private final Random random;
    private int currentDx;
    private int currentDy;

    public Pineapple(Location location){
        super("Pineapple", PINEAPPLE_POINTS, location, true);
        this.map = null;
        this.random = new Random();
        // Inicializar con dirección aleatoria
        chooseRandomDirection();
    }
    
    /**
     * Establece la referencia al mapa
     */
    @Override
    public void setMap(Map map) {
        this.map = map;
    }

    @Override
    public int collect(){
        if(!collected){
            collected = true;
            return PINEAPPLE_POINTS;
        }
        return 0;
    }
    
    /**
     * Mueve la piña de forma aleatoria en el mapa
     * Usa la interfaz Movable para el movimiento validado
     */
    @Override
    public void move() {
        if (collected || map == null) return;
        
        // Intentar mover en la dirección actual
        boolean moved = tryMove(currentDx, currentDy, map);
        
        // Si no pudo moverse (chocó con algo), elegir nueva dirección
        if (!moved) {
            chooseRandomDirection();
        } else {
            // Ocasionalmente cambiar de dirección aleatoriamente (20% de probabilidad)
            if (random.nextDouble() < 0.2) {
                chooseRandomDirection();
            }
        }
    }
    
    /**
     * Elige una dirección aleatoria para moverse
     */
    private void chooseRandomDirection() {
        int direction = random.nextInt(4);
        switch (direction) {
            case 0 -> { // Arriba
                currentDx = 0;
                currentDy = -1;
            }
            case 1 -> { // Abajo
                currentDx = 0;
                currentDy = 1;
            }
            case 2 -> { // Izquierda
                currentDx = -1;
                currentDy = 0;
            }
            default -> { // Derecha (case 3)
                currentDx = 1;
                currentDy = 0;
            }
        }
    }
    
    /**
     * Este método mantiene compatibilidad con el sistema anterior
     * pero ahora simplemente llama a move() para movimiento aleatorio
     */
    @Override
    public void pushByPlayer(int playerDx, int playerDy, Location playerLocation) {
        // Mantener por compatibilidad, pero la piña ahora se mueve aleatoriamente
        // Se puede dejar vacío o eliminar si no se usa
    }
    
    @Override
    public String getTypeName(){
        return "Pineapple";
    }
    
    @Override
    public boolean requiresMapReference(){
        return true;
    }
    
}
