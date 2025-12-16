package com.duran_jimenez.baddopocream.domain;

/**
 * Narval - Enemigo con comportamiento de embestida
 * 
 * COMPORTAMIENTO:
 * - Recorre el mapa en líneas rectas sin perseguir directamente al jugador
 * - Si un helado se alinea con él (horizontal o vertical), embiste rápidamente
 * - Durante la embestida, destruye todos los bloques de hielo en su camino
 * - Tiene animaciones de "drill" (perforación) cuando rompe hielo
 */
public class Narval extends Enemy implements IceBreaker {
    
    // Estados del Narval
    private enum State {
        PATROLLING,     // Patrullando normalmente
        CHARGING,       // Embistiendo al jugador
        BREAKING_ICE    // Rompiendo hielo durante embestida
    }
    
    private State currentState;
    
    // Patrulla
    private int directionX;
    private int directionY;
    private static final int PATROL_SPEED = 1;
    
    // Embestida
    private static final int CHARGE_SPEED = 2;
    private static final int DETECTION_RANGE = 10; // Rango para detectar alineación con jugador
    private int chargeDirectionX;
    private int chargeDirectionY;
    private int chargeStepsRemaining;
    private static final int MAX_CHARGE_STEPS = 8; // Máximo de pasos en una embestida
    
    public Narval(Location location) {
        super("Narval", location, PATROL_SPEED);
        this.currentState = State.PATROLLING;
        // Dirección inicial de patrulla (hacia la derecha)
        this.directionX = 1;
        this.directionY = 0;
        this.chargeStepsRemaining = 0;
    }
    
    @Override
    public void move(Location playerLocation) {
        switch (currentState) {
            case PATROLLING:
                movePatrol(playerLocation);
                break;
            case CHARGING:
            case BREAKING_ICE:
                moveCharge();
                break;
        }
    }
    
    /**
     * Movimiento de patrulla: línea recta sin perseguir
     */
    private void movePatrol(Location playerLocation) {
        // Verificar si el jugador está alineado
        if (isPlayerAligned(playerLocation)) {
            // Iniciar embestida
            startCharge(playerLocation);
            return;
        }
        
        // Movimiento normal de patrulla
        this.location = this.location.move(directionX, directionY);
    }
    
    /**
     * Movimiento de embestida: rápido y en línea recta
     */
    private void moveCharge() {
        if (chargeStepsRemaining <= 0) {
            // Terminar embestida, volver a patrulla
            currentState = State.PATROLLING;
            return;
        }
        
        // Moverse en la dirección de embestida (puede ser múltiples celdas)
        for (int i = 0; i < CHARGE_SPEED && chargeStepsRemaining > 0; i++) {
            this.location = this.location.move(chargeDirectionX, chargeDirectionY);
            chargeStepsRemaining--;
        }
    }
    
    /**
     * Verifica si el jugador está alineado horizontal o verticalmente
     */
    private boolean isPlayerAligned(Location playerLocation) {
        int dx = playerLocation.getX() - this.location.getX();
        int dy = playerLocation.getY() - this.location.getY();
        
        // Alineado horizontalmente (misma fila Y)
        if (dy == 0 && Math.abs(dx) > 0 && Math.abs(dx) <= DETECTION_RANGE) {
            return true;
        }
        
        // Alineado verticalmente (misma columna X)
        if (dx == 0 && Math.abs(dy) > 0 && Math.abs(dy) <= DETECTION_RANGE) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Inicia la embestida hacia el jugador
     */
    private void startCharge(Location playerLocation) {
        currentState = State.CHARGING;
        
        int dx = playerLocation.getX() - this.location.getX();
        int dy = playerLocation.getY() - this.location.getY();
        
        // Establecer dirección de embestida
        if (dx != 0) {
            // Embestida horizontal
            chargeDirectionX = Integer.signum(dx);
            chargeDirectionY = 0;
            chargeStepsRemaining = Math.abs(dx);
        } else {
            // Embestida vertical
            chargeDirectionX = 0;
            chargeDirectionY = Integer.signum(dy);
            chargeStepsRemaining = Math.abs(dy);
        }
        
        // Limitar pasos de embestida
        chargeStepsRemaining = Math.min(chargeStepsRemaining, MAX_CHARGE_STEPS);
    }
    
    @Override
    public boolean tryMove(Location playerLocation, Map map) {
        Location currentLoc = this.location;
        
        // Guardar estado anterior
        State previousState = this.currentState;
        
        // Intentar movimiento
        move(playerLocation);
        
        Location newLoc = this.location;
        
        // Verificar si la nueva posición es válida
        if (!map.isValidPosition(newLoc)) {
            // Si está cargando y hay un obstáculo
            if (currentState == State.CHARGING || currentState == State.BREAKING_ICE) {
                // Verificar si es hielo
                if (map.isIceWall(newLoc)) {
                    // Romper el hielo y continuar
                    currentState = State.BREAKING_ICE;
                    breakIceAt(newLoc, map);
                    return true; // El hielo fue destruido, movimiento válido
                } else {
                    // Es una pared sólida, terminar embestida
                    this.location = currentLoc;
                    currentState = State.PATROLLING;
                    changePatrolDirection(map);
                    return false;
                }
            } else {
                // Patrullando y chocó con algo
                this.location = currentLoc;
                changePatrolDirection(map);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Rompe un bloque de hielo en la posición especificada
     */
    private void breakIceAt(Location iceLocation, Map map) {
        if (map.isIceWall(iceLocation)) {
            map.removeIceWall(iceLocation);
        }
    }
    
    @Override
    public void breakIce(IceWall wall) {
        // Implementación de IceBreaker
        if (currentState == State.CHARGING || currentState == State.BREAKING_ICE) {
            wall.breakWall();
        }
    }
    
    /**
     * Cambia la dirección de patrulla cuando choca con algo
     */
    private void changePatrolDirection(Map map) {
        Location currentLoc = this.location;
        
        // Verificar direcciones posibles
        boolean canMoveUp = map.isValidPosition(currentLoc.move(0, -1));
        boolean canMoveDown = map.isValidPosition(currentLoc.move(0, 1));
        boolean canMoveLeft = map.isValidPosition(currentLoc.move(-1, 0));
        boolean canMoveRight = map.isValidPosition(currentLoc.move(1, 0));
        
        // Invertir dirección actual primero
        if (directionX != 0) {
            // Estaba horizontal, intentar invertir
            directionX = -directionX;
            if (map.isValidPosition(currentLoc.move(directionX, 0))) {
                return; // Dirección invertida es válida
            }
            // Si no, cambiar a vertical
            directionX = 0;
            if (canMoveUp) {
                directionY = -1;
            } else if (canMoveDown) {
                directionY = 1;
            }
        } else {
            // Estaba vertical, intentar invertir
            directionY = -directionY;
            if (map.isValidPosition(currentLoc.move(0, directionY))) {
                return; // Dirección invertida es válida
            }
            // Si no, cambiar a horizontal
            directionY = 0;
            if (canMoveRight) {
                directionX = 1;
            } else if (canMoveLeft) {
                directionX = -1;
            }
        }
    }
    
    @Override
    public boolean detectPlayer(Location playerLocation) {
        // El Narval detecta al jugador si está alineado
        return isPlayerAligned(playerLocation);
    }
    
    /**
     * Retorna true si está en estado de embestida
     */
    public boolean isCharging() {
        return currentState == State.CHARGING || currentState == State.BREAKING_ICE;
    }
    
    /**
     * Retorna true si está rompiendo hielo
     */
    public boolean isBreakingIce() {
        return currentState == State.BREAKING_ICE;
    }
    
    /**
     * Obtiene la dirección actual (para animaciones)
     */
    public int getDirectionX() {
        return currentState == State.PATROLLING ? directionX : chargeDirectionX;
    }
    
    public int getDirectionY() {
        return currentState == State.PATROLLING ? directionY : chargeDirectionY;
    }
    
    @Override
    public String getTypeName(){
        return "Narval";
    }
}
