package com.duran_jimenez.baddopocream.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Sistema de IA para enemigos
 * Proporciona comportamientos inteligentes para diferentes tipos de enemigos
 */
public class EnemyAI {
    
    /**
     * Perfiles de comportamiento para enemigos
     */
    public enum AIProfile {
        PATROL,         // Patrulla en línea recta, cambia dirección al chocar
        CHASE,          // Persigue al jugador directamente
        SMART_CHASE,    // Persigue al jugador usando pathfinding
        AMBUSH,         // Se mueve aleatoriamente hasta detectar al jugador, luego persigue
        TERRITORIAL     // Persigue al jugador solo si entra en su territorio
    }
    
    private Random random;
    
    public EnemyAI() {
        this.random = new Random();
    }
    
    /**
     * Calcula el siguiente movimiento para un enemigo con perfil PATROL
     * @return Array [dx, dy] con la dirección del movimiento
     */
    public int[] getPatrolMove(Enemy enemy, Map map, int currentDirX, int currentDirY) {
        Location nextPos = enemy.getLocation().move(currentDirX, currentDirY);
        
        // Si puede moverse en la dirección actual, continuar
        if (map.isValidPosition(nextPos)) {
            return new int[]{currentDirX, currentDirY};
        }
        
        // Si no puede, cambiar de dirección
        return getNewPatrolDirection(enemy, map, currentDirX, currentDirY);
    }
    
    /**
     * Encuentra una nueva dirección de patrulla cuando se encuentra un obstáculo
     */
    private int[] getNewPatrolDirection(Enemy enemy, Map map, int currentDirX, int currentDirY) {
        Location currentLoc = enemy.getLocation();
        
        // Verificar direcciones disponibles
        boolean canMoveUp = map.isValidPosition(currentLoc.move(0, -1));
        boolean canMoveDown = map.isValidPosition(currentLoc.move(0, 1));
        boolean canMoveLeft = map.isValidPosition(currentLoc.move(-1, 0));
        boolean canMoveRight = map.isValidPosition(currentLoc.move(1, 0));
        
        // Preferir dirección perpendicular
        if (currentDirX != 0) {
            // Estaba en horizontal, intentar vertical
            if (canMoveUp) return new int[]{0, -1};
            if (canMoveDown) return new int[]{0, 1};
            if (canMoveLeft) return new int[]{-1, 0};
            if (canMoveRight) return new int[]{1, 0};
        } else {
            // Estaba en vertical, intentar horizontal
            if (canMoveRight) return new int[]{1, 0};
            if (canMoveLeft) return new int[]{-1, 0};
            if (canMoveUp) return new int[]{0, -1};
            if (canMoveDown) return new int[]{0, 1};
        }
        
        return new int[]{0, 0}; // No puede moverse
    }
    
    /**
     * Calcula el siguiente movimiento para un enemigo con perfil CHASE
     * Persigue directamente al jugador sin pathfinding
     */
    public int[] getChaseMove(Enemy enemy, Location playerLocation, Map map) {
        Location enemyLoc = enemy.getLocation();
        int dx = Integer.compare(playerLocation.getX(), enemyLoc.getX());
        int dy = Integer.compare(playerLocation.getY(), enemyLoc.getY());
        
        // Intentar movimiento horizontal primero
        if (dx != 0) {
            Location nextPos = enemyLoc.move(dx, 0);
            if (map.isValidPosition(nextPos)) {
                return new int[]{dx, 0};
            }
        }
        
        // Si no puede horizontal, intentar vertical
        if (dy != 0) {
            Location nextPos = enemyLoc.move(0, dy);
            if (map.isValidPosition(nextPos)) {
                return new int[]{0, dy};
            }
        }
        
        // Intentar movimiento horizontal forzado
        if (dx != 0) {
            Location nextPos = enemyLoc.move(dx, 0);
            if (map.isValidPosition(nextPos)) {
                return new int[]{dx, 0};
            }
        }
        
        return new int[]{0, 0}; // No puede acercarse
    }
    
    /**
     * Calcula el siguiente movimiento para un enemigo con perfil SMART_CHASE
     * Usa pathfinding para perseguir al jugador de forma inteligente
     */
    public int[] getSmartChaseMove(Enemy enemy, Location playerLocation, Map map) {
        Location enemyLoc = enemy.getLocation();
        
        // Usar PathFinder para calcular la ruta
        List<int[]> path = PathFinder.findPath(enemyLoc, playerLocation, map, new ArrayList<>());
        
        if (!path.isEmpty()) {
            return path.get(0);
        }
        
        // Fallback a chase simple si no hay camino
        return getChaseMove(enemy, playerLocation, map);
    }
    
    /**
     * Calcula el siguiente movimiento para un enemigo con perfil AMBUSH
     * Se mueve aleatoriamente hasta detectar al jugador, luego persigue
     */
    public int[] getAmbushMove(Enemy enemy, Location playerLocation, Map map, 
                               int detectionRange, boolean isChasing) {
        Location enemyLoc = enemy.getLocation();
        double distanceToPlayer = enemyLoc.distanceTo(playerLocation);
        
        // Si detectó al jugador, perseguir
        if (distanceToPlayer <= detectionRange || isChasing) {
            return getSmartChaseMove(enemy, playerLocation, map);
        }
        
        // Movimiento aleatorio
        return getRandomMove(enemy, map);
    }
    
    /**
     * Calcula el siguiente movimiento para un enemigo con perfil TERRITORIAL
     * Solo persigue si el jugador entra en su territorio
     */
    public int[] getTerritorialMove(Enemy enemy, Location playerLocation, Location territoryCenter,
                                    int territoryRadius, Map map) {
        Location enemyLoc = enemy.getLocation();
        
        // Verificar si el jugador está en el territorio
        boolean playerInTerritory = playerLocation.distanceTo(territoryCenter) <= territoryRadius;
        
        if (playerInTerritory) {
            // Perseguir al jugador
            return getSmartChaseMove(enemy, playerLocation, map);
        } else {
            // Volver al centro del territorio
            if (enemyLoc.distanceTo(territoryCenter) > 2) {
                return getSmartChaseMove(enemy, territoryCenter, map);
            }
            // Patrullar alrededor del centro
            return getPatrolMove(enemy, map, 1, 0);
        }
    }
    
    /**
     * Obtiene un movimiento aleatorio válido
     */
    private int[] getRandomMove(Enemy enemy, Map map) {
        Location currentLoc = enemy.getLocation();
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        
        // Barajar direcciones
        for (int i = directions.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int[] temp = directions[i];
            directions[i] = directions[j];
            directions[j] = temp;
        }
        
        // Probar cada dirección
        for (int[] dir : directions) {
            Location nextPos = currentLoc.move(dir[0], dir[1]);
            if (map.isValidPosition(nextPos)) {
                return dir;
            }
        }
        
        return new int[]{0, 0}; // No puede moverse
    }
    
    /**
     * Calcula movimiento evasivo (alejarse del jugador)
     * Útil para enemigos que huyen o mantienen distancia
     */
    public int[] getEvadeMove(Enemy enemy, Location playerLocation, Map map) {
        Location enemyLoc = enemy.getLocation();
        
        // Calcular dirección opuesta al jugador
        int dx = Integer.compare(enemyLoc.getX(), playerLocation.getX());
        int dy = Integer.compare(enemyLoc.getY(), playerLocation.getY());
        
        // Intentar alejarse horizontalmente
        if (dx != 0) {
            Location nextPos = enemyLoc.move(dx, 0);
            if (map.isValidPosition(nextPos)) {
                return new int[]{dx, 0};
            }
        }
        
        // Intentar alejarse verticalmente
        if (dy != 0) {
            Location nextPos = enemyLoc.move(0, dy);
            if (map.isValidPosition(nextPos)) {
                return new int[]{0, dy};
            }
        }
        
        // Buscar cualquier dirección que aumente la distancia
        Location bestMove = null;
        double maxDistance = enemyLoc.distanceTo(playerLocation);
        int bestDx = 0, bestDy = 0;
        
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        for (int[] dir : directions) {
            Location nextPos = enemyLoc.move(dir[0], dir[1]);
            if (map.isValidPosition(nextPos)) {
                double dist = nextPos.distanceTo(playerLocation);
                if (dist > maxDistance) {
                    maxDistance = dist;
                    bestDx = dir[0];
                    bestDy = dir[1];
                }
            }
        }
        
        return new int[]{bestDx, bestDy};
    }
    
    /**
     * Verifica si el enemigo puede ver al jugador (línea de visión directa)
     */
    public boolean hasLineOfSight(Location enemyLoc, Location playerLoc, Map map) {
        int dx = playerLoc.getX() - enemyLoc.getX();
        int dy = playerLoc.getY() - enemyLoc.getY();
        
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) return true;
        
        double stepX = (double) dx / steps;
        double stepY = (double) dy / steps;
        
        // Verificar cada celda en el camino
        for (int i = 1; i < steps; i++) {
            int x = enemyLoc.getX() + (int) Math.round(stepX * i);
            int y = enemyLoc.getY() + (int) Math.round(stepY * i);
            Location checkLoc = new Location(x, y);
            
            if (!map.isValidPosition(checkLoc)) {
                return false; // Hay un obstáculo
            }
        }
        
        return true; // Línea de visión clara
    }
}
