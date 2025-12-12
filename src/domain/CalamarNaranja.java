package domain;
import java.util.*;

public class CalamarNaranja extends Calamar {
    
    private static final int SPEED = 2;
    private static final int DETECTION_RANGE = 7;

    private Random random;
    private int movementCounter;
    private int currentDirection;

    public CalamarNaranja(Location location){
        super("Calamar Naranja", location, SPEED, DETECTION_RANGE);
        this.random = new Random();
        this.movementCounter = 0;
        this.currentDirection = random.nextInt(4);
    }

    @Override
    public void move(Location playerLocation){
        movementCounter++;
        if(movementCounter >= 5){
            currentDirection = random.nextInt(4);
            movementCounter = 0;
        }

        int dx = 0, dy = 0;
        switch(currentDirection){
            case 0: dy = -1; break; // Arriba
            case 1: dy = 1; break;  // Abajo
            case 2: dx = -1; break; // Izquierda
            case 3: dx = 1; break;  // Derecha
        }
        this.location = this.location.move(dx, dy);
    }
    
    /**
     * Intenta moverse validando contra el mapa
     * Persigue al jugador si está en rango, si no, se mueve aleatoriamente
     * Retorna true si logró moverse, false si no pudo
     */
    public boolean tryMove(Location playerLocation, Map map){
        // Verificar si el jugador está en rango de detección
        if(detectPlayer(playerLocation)){
            // Perseguir al jugador con pathfinding inteligente
            return chasePlayer(playerLocation, map);
        }
        
        // Movimiento aleatorio cuando no detecta al jugador
        movementCounter++;
        if(movementCounter >= 5){
            currentDirection = random.nextInt(4);
            movementCounter = 0;
        }

        int dx = 0, dy = 0;
        switch(currentDirection){
            case 0: dy = -1; break; // Arriba
            case 1: dy = 1; break;  // Abajo
            case 2: dx = -1; break; // Izquierda
            case 3: dx = 1; break;  // Derecha
        }
        
        Location newLocation = this.location.move(dx, dy);
        
        if(map.isValidPosition(newLocation)){
            this.location = newLocation;
            return true;
        }
        
        // Si choca con algo, cambiar dirección inmediatamente
        currentDirection = random.nextInt(4);
        movementCounter = 0;
        
        return false;
    }
    
    /**
     * Persigue al jugador de forma inteligente evitando obstáculos
     */
    private boolean chasePlayer(Location playerLocation, Map map){
        int dx = Integer.compare(playerLocation.getX(), location.getX());
        int dy = Integer.compare(playerLocation.getY(), location.getY());
        
        // Intentar movimiento horizontal primero
        if(dx != 0){
            Location nextPos = location.move(dx, 0);
            if(map.isValidPosition(nextPos)){
                location = nextPos;
                return true;
            } else if(map.hasIceWall(nextPos)){
                // Romper el hielo si puede
                breakIce(map, nextPos);
                location = nextPos;
                return true;
            }
        }
        
        // Intentar movimiento vertical
        if(dy != 0){
            Location nextPos = location.move(0, dy);
            if(map.isValidPosition(nextPos)){
                location = nextPos;
                return true;
            } else if(map.hasIceWall(nextPos)){
                // Romper el hielo si puede
                breakIce(map, nextPos);
                location = nextPos;
                return true;
            }
        }
        
        // No pudo moverse directamente, buscar alternativa
        return findAlternativePath(playerLocation, map);
    }
    
    /**
     * Busca un camino alternativo cuando el camino directo está bloqueado
     */
    private boolean findAlternativePath(Location target, Map map){
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        Location bestMove = null;
        double bestDistance = Double.MAX_VALUE;
        boolean canBreakIce = false;
        
        for(int[] dir : directions){
            Location nextPos = location.move(dir[0], dir[1]);
            
            if(map.isValidPosition(nextPos)){
                double distance = nextPos.distanceTo(target);
                if(distance < bestDistance){
                    bestDistance = distance;
                    bestMove = nextPos;
                    canBreakIce = false;
                }
            } else if(map.hasIceWall(nextPos)){
                double distance = nextPos.distanceTo(target);
                if(distance < bestDistance){
                    bestDistance = distance;
                    bestMove = nextPos;
                    canBreakIce = true;
                }
            }
        }
        
        if(bestMove != null){
            if(canBreakIce){
                breakIce(map, bestMove);
            }
            location = bestMove;
            return true;
        }
        
        return false;
    }
    
    /**
     * Rompe el hielo en una posición específica
     */
    private void breakIce(Map map, Location iceLocation){
        map.removeIceWall(iceLocation);
    }
    
    /**
     * Cambia de dirección aleatoriamente (usado cuando colisiona con otro enemigo)
     */
    public void changeDirectionRandomly(){
        currentDirection = random.nextInt(4);
        movementCounter = 0;
    }
    
    @Override
    public String getTypeName(){
        return "CalamarNaranja";
    }
}
