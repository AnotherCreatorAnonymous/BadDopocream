package domain;
import java.util.*;

/**
 * Inteligencia artificial para controlar un IceCream de forma autónoma
 * Soporta tres perfiles: Hungry, Fearful y Expert
 */
public class IceCreamAI {
    
    /**
     * Perfiles de comportamiento de la IA
     */
    public enum AIProfile {
        HUNGRY,   // Prioriza recoger frutas por el camino más corto
        FEARFUL,  // Prioriza seguridad, evita enemigos a toda costa
        EXPERT    // Balance perfecto entre eficiencia y seguridad
    }
    
    private static final double ENEMY_DETECTION_RANGE = 4.0; // Rango de detección de enemigos
    private static final double ENEMY_DANGER_RANGE = 2.0; // Rango crítico de peligro
    private static final double FEARFUL_SAFE_DISTANCE = 6.0; // Distancia segura para perfil Fearful
    
    private AIProfile profile;
    private Random random;
    private int moveCounter;
    private int lastDx;
    private int lastDy;
    
    public IceCreamAI(){
        this(AIProfile.EXPERT); // Por defecto usa perfil Expert
    }
    
    public IceCreamAI(AIProfile profile){
        this.profile = profile;
        this.random = new Random();
        this.moveCounter = 0;
        this.lastDx = 1;
        this.lastDy = 0;
    }
    
    /**
     * Cambia el perfil de la IA
     */
    public void setProfile(AIProfile profile){
        this.profile = profile;
    }
    
    /**
     * Obtiene el perfil actual
     */
    public AIProfile getProfile(){
        return this.profile;
    }
    
    /**
     * Decide el próximo movimiento del helado según el perfil seleccionado
     * Retorna un array [dx, dy, useIce] donde useIce es 1 para crear/romper hielo, 0 para no hacerlo
     */
    public int[] decideMove(Level level, IceCream player){
        moveCounter++;
        
        switch(profile){
            case HUNGRY:
                return decideHungryMove(level, player);
            case FEARFUL:
                return decideFearfulMove(level, player);
            case EXPERT:
                return decideExpertMove(level, player);
            default:
                return decideExpertMove(level, player);
        }
    }
    
    /**
     * PERFIL HUNGRY: Prioriza recoger frutas por el camino más corto
     * Solo evita enemigos si están justo en el camino
     */
    private int[] decideHungryMove(Level level, IceCream player){
        Location playerLoc = player.getLocation();
        
        // Buscar la fruta más cercana sin considerar enemigos
        Fruit closestFruit = findClosestFruit(level, playerLoc);
        
        if(closestFruit != null && !closestFruit.isCollected()){
            Location fruitLoc = closestFruit.getLocation();
            int dx = Integer.compare(fruitLoc.getX(), playerLoc.getX());
            int dy = Integer.compare(fruitLoc.getY(), playerLoc.getY());
            
            // Intentar movimiento horizontal
            if(dx != 0){
                Location nextPos = playerLoc.move(dx, 0);
                
                // Solo evitar si hay enemigo JUSTO en el siguiente paso
                if(isEnemyAt(level, nextPos)){
                    if(dy != 0){
                        Location altPos = playerLoc.move(0, dy);
                        if(level.getMap().isValidPosition(altPos) && !isEnemyAt(level, altPos)){
                            lastDx = 0; lastDy = dy;
                            return new int[]{0, dy, 0};
                        }
                    }
                }
                
                // Romper hielo si es necesario para avanzar
                if(!level.getMap().isValidPosition(nextPos)){
                    IceWall iceWall = level.getIceWallAt(nextPos);
                    if(iceWall != null && player instanceof IceBreaker){
                        lastDx = dx; lastDy = 0;
                        return new int[]{dx, 0, 1};
                    }
                    if(dy != 0){
                        Location altPos = playerLoc.move(0, dy);
                        if(level.getMap().isValidPosition(altPos)){
                            lastDx = 0; lastDy = dy;
                            return new int[]{0, dy, 0};
                        }
                    }
                    return findAlternativePath(level, playerLoc, fruitLoc, player);
                }
                
                lastDx = dx; lastDy = 0;
                return new int[]{dx, 0, 0};
                
            } else if(dy != 0){
                Location nextPos = playerLoc.move(0, dy);
                
                if(isEnemyAt(level, nextPos)){
                    return findAlternativePath(level, playerLoc, fruitLoc, player);
                }
                
                if(!level.getMap().isValidPosition(nextPos)){
                    IceWall iceWall = level.getIceWallAt(nextPos);
                    if(iceWall != null && player instanceof IceBreaker){
                        lastDx = 0; lastDy = dy;
                        return new int[]{0, dy, 1};
                    }
                    return findAlternativePath(level, playerLoc, fruitLoc, player);
                }
                
                lastDx = 0; lastDy = dy;
                return new int[]{0, dy, 0};
            }
        }
        
        // Movimiento aleatorio si no hay frutas
        return getRandomMove();
    }
    
    /**
     * PERFIL FEARFUL: Prioriza seguridad, evita enemigos a toda costa
     * Busca frutas alejadas de enemigos
     */
    private int[] decideFearfulMove(Level level, IceCream player){
        Location playerLoc = player.getLocation();
        Enemy nearestEnemy = findNearestEnemy(level, playerLoc);
        
        // SIEMPRE verificar enemigos cercanos
        if(nearestEnemy != null){
            double distToEnemy = playerLoc.distanceTo(nearestEnemy.getLocation());
            
            // Si hay enemigo muy cerca, HUIR inmediatamente
            if(distToEnemy <= ENEMY_DANGER_RANGE){
                // Intentar crear barrera de hielo defensiva
                if(player instanceof IceBreaker && random.nextInt(100) < 70){
                    int dx = Integer.compare(nearestEnemy.getLocation().getX(), playerLoc.getX());
                    int dy = Integer.compare(nearestEnemy.getLocation().getY(), playerLoc.getY());
                    // Crear hielo en dirección al enemigo para bloquearlo
                    lastDx = dx; lastDy = dy;
                    return new int[]{dx, dy, 1};
                }
                return escapeFromEnemy(level, playerLoc, nearestEnemy.getLocation(), player);
            }
            
            // Si está en rango de detección, buscar fruta SEGURA
            if(distToEnemy <= FEARFUL_SAFE_DISTANCE){
                Fruit safeFruit = findSafestFruit(level, playerLoc);
                if(safeFruit != null){
                    return moveTowardsFruitSafely(level, playerLoc, safeFruit.getLocation(), player);
                }
            }
        }
        
        // Buscar fruta más segura (lejos de todos los enemigos)
        Fruit safeFruit = findSafestFruit(level, playerLoc);
        if(safeFruit != null){
            return moveTowardsFruitSafely(level, playerLoc, safeFruit.getLocation(), player);
        }
        
        return getRandomMove();
    }
    
    /**
     * PERFIL EXPERT: Balance perfecto entre eficiencia y seguridad
     * Encuentra caminos más cortos que sean seguros
     */
    private int[] decideExpertMove(Level level, IceCream player){
        Location playerLoc = player.getLocation();
        Enemy nearestEnemy = findNearestEnemy(level, playerLoc);
        
        // Evaluar amenaza
        if(nearestEnemy != null){
            double distanceToEnemy = playerLoc.distanceTo(nearestEnemy.getLocation());
            
            // Peligro crítico: escapar
            if(distanceToEnemy <= ENEMY_DANGER_RANGE){
                // Expert usa hielo estratégicamente
                if(player instanceof IceBreaker && canBlockEnemyWithIce(level, playerLoc, nearestEnemy.getLocation())){
                    int dx = Integer.compare(nearestEnemy.getLocation().getX(), playerLoc.getX());
                    int dy = Integer.compare(nearestEnemy.getLocation().getY(), playerLoc.getY());
                    lastDx = dx; lastDy = dy;
                    return new int[]{dx, dy, 1}; // Bloquear enemigo
                }
                return escapeFromEnemy(level, playerLoc, nearestEnemy.getLocation(), player);
            }
            
            // Rango medio: considerar amenaza al elegir fruta
            if(distanceToEnemy <= ENEMY_DETECTION_RANGE){
                Fruit optimalFruit = findOptimalFruit(level, playerLoc);
                if(optimalFruit != null){
                    return moveTowardsFruitWithAwareness(level, playerLoc, optimalFruit.getLocation(), player);
                }
            }
        }
        
        // Sin amenazas cercanas: ir por fruta más cercana eficientemente
        Fruit closestFruit = findClosestFruit(level, playerLoc);
        if(closestFruit != null){
            return moveTowardsFruitWithAwareness(level, playerLoc, closestFruit.getLocation(), player);
        }
        
        return getRandomMove();
    }
    
    /**
     * Movimiento aleatorio cuando no hay objetivo claro
     */
    private int[] getRandomMove(){
        if(moveCounter % 3 == 0){
            int direction = random.nextInt(4);
            switch(direction){
                case 0: lastDx = 0; lastDy = -1; break;
                case 1: lastDx = 0; lastDy = 1; break;
                case 2: lastDx = -1; lastDy = 0; break;
                case 3: lastDx = 1; lastDy = 0; break;
            }
        }
        return new int[]{lastDx, lastDy, 0};
    }
    
    /**
     * Verifica si puede bloquear a un enemigo con hielo
     */
    private boolean canBlockEnemyWithIce(Level level, Location playerLoc, Location enemyLoc){
        int dx = Integer.compare(enemyLoc.getX(), playerLoc.getX());
        int dy = Integer.compare(enemyLoc.getY(), playerLoc.getY());
        Location blockPos = playerLoc.move(dx, dy);
        return level.getMap().isValidPosition(blockPos);
    }
    
    /**
     * Encuentra la fruta más segura (más alejada de todos los enemigos)
     */
    private Fruit findSafestFruit(Level level, Location playerLoc){
        Fruit safest = null;
        double maxMinEnemyDist = -1;
        
        for(Fruit fruit : level.getFruits()){
            if(!fruit.isCollected()){
                double minEnemyDist = Double.MAX_VALUE;
                for(Enemy enemy : level.getEnemies()){
                    double dist = fruit.getLocation().distanceTo(enemy.getLocation());
                    if(dist < minEnemyDist) minEnemyDist = dist;
                }
                
                if(minEnemyDist > maxMinEnemyDist){
                    maxMinEnemyDist = minEnemyDist;
                    safest = fruit;
                }
            }
        }
        return safest;
    }
    
    /**
     * Encuentra la fruta óptima (balance entre distancia y seguridad)
     */
    private Fruit findOptimalFruit(Level level, Location playerLoc){
        Fruit optimal = null;
        double bestScore = Double.MAX_VALUE;
        
        for(Fruit fruit : level.getFruits()){
            if(!fruit.isCollected()){
                double distToFruit = playerLoc.distanceTo(fruit.getLocation());
                double minEnemyDist = Double.MAX_VALUE;
                
                for(Enemy enemy : level.getEnemies()){
                    double dist = fruit.getLocation().distanceTo(enemy.getLocation());
                    if(dist < minEnemyDist) minEnemyDist = dist;
                }
                
                // Score: priorizar cercanía pero penalizar si hay enemigos muy cerca
                double score = distToFruit + (minEnemyDist < ENEMY_DETECTION_RANGE ? 10 : 0);
                
                if(score < bestScore){
                    bestScore = score;
                    optimal = fruit;
                }
            }
        }
        return optimal;
    }
    
    /**
     * Mueve hacia la fruta evitando completamente a los enemigos
     */
    private int[] moveTowardsFruitSafely(Level level, Location from, Location to, IceCream player){
        int dx = Integer.compare(to.getX(), from.getX());
        int dy = Integer.compare(to.getY(), from.getY());
        
        // Intentar horizontal
        if(dx != 0){
            Location nextPos = from.move(dx, 0);
            if(!isEnemyAt(level, nextPos) && level.getMap().isValidPosition(nextPos)){
                // Verificar que no nos acerque peligrosamente a algún enemigo
                if(isSafePosition(level, nextPos)){
                    lastDx = dx; lastDy = 0;
                    return new int[]{dx, 0, 0};
                }
            }
        }
        
        // Intentar vertical
        if(dy != 0){
            Location nextPos = from.move(0, dy);
            if(!isEnemyAt(level, nextPos) && level.getMap().isValidPosition(nextPos)){
                if(isSafePosition(level, nextPos)){
                    lastDx = 0; lastDy = dy;
                    return new int[]{0, dy, 0};
                }
            }
        }
        
        // Buscar cualquier movimiento seguro
        return findSafestMove(level, from, player);
    }
    
    /**
     * Mueve hacia la fruta con consciencia de enemigos (Expert)
     */
    private int[] moveTowardsFruitWithAwareness(Level level, Location from, Location to, IceCream player){
        int dx = Integer.compare(to.getX(), from.getX());
        int dy = Integer.compare(to.getY(), from.getY());
        
        if(dx != 0){
            Location nextPos = from.move(dx, 0);
            if(!isEnemyAt(level, nextPos)){
                if(!level.getMap().isValidPosition(nextPos)){
                    IceWall iceWall = level.getIceWallAt(nextPos);
                    if(iceWall != null && player instanceof IceBreaker){
                        lastDx = dx; lastDy = 0;
                        return new int[]{dx, 0, 1};
                    }
                } else {
                    lastDx = dx; lastDy = 0;
                    return new int[]{dx, 0, 0};
                }
            }
        }
        
        if(dy != 0){
            Location nextPos = from.move(0, dy);
            if(!isEnemyAt(level, nextPos)){
                if(!level.getMap().isValidPosition(nextPos)){
                    IceWall iceWall = level.getIceWallAt(nextPos);
                    if(iceWall != null && player instanceof IceBreaker){
                        lastDx = 0; lastDy = dy;
                        return new int[]{0, dy, 1};
                    }
                } else {
                    lastDx = 0; lastDy = dy;
                    return new int[]{0, dy, 0};
                }
            }
        }
        
        return findSafeAlternativePath(level, from, to, player);
    }
    
    /**
     * Verifica si una posición es segura (lejos de enemigos)
     */
    private boolean isSafePosition(Level level, Location pos){
        for(Enemy enemy : level.getEnemies()){
            if(pos.distanceTo(enemy.getLocation()) < ENEMY_DANGER_RANGE){
                return false;
            }
        }
        return true;
    }
    
    /**
     * Encuentra el movimiento más seguro disponible
     */
    private int[] findSafestMove(Level level, Location from, IceCream player){
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        Location safestPos = null;
        double maxMinEnemyDist = -1;
        
        for(int[] dir : directions){
            Location nextPos = from.move(dir[0], dir[1]);
            if(level.getMap().isValidPosition(nextPos) && !isEnemyAt(level, nextPos)){
                double minEnemyDist = Double.MAX_VALUE;
                for(Enemy enemy : level.getEnemies()){
                    double dist = nextPos.distanceTo(enemy.getLocation());
                    if(dist < minEnemyDist) minEnemyDist = dist;
                }
                if(minEnemyDist > maxMinEnemyDist){
                    maxMinEnemyDist = minEnemyDist;
                    safestPos = nextPos;
                }
            }
        }
        
        if(safestPos != null){
            int dx = safestPos.getX() - from.getX();
            int dy = safestPos.getY() - from.getY();
            lastDx = dx; lastDy = dy;
            return new int[]{dx, dy, 0};
        }
        
        return new int[]{0, 0, 0};
    }
    
    /**
     * Encuentra la fruta no recolectada más cercana al jugador
     */
    private Fruit findClosestFruit(Level level, Location playerLoc){
        Fruit closest = null;
        double minDistance = Double.MAX_VALUE;
        
        for(Fruit fruit : level.getFruits()){
            if(!fruit.isCollected()){
                double distance = playerLoc.distanceTo(fruit.getLocation());
                if(distance < minDistance){
                    minDistance = distance;
                    closest = fruit;
                }
            }
        }
        
        return closest;
    }
    
    /**
     * Busca un camino alternativo cuando el camino directo está bloqueado
     */
    private int[] findAlternativePath(Level level, Location from, Location to, IceCream player){
        Map map = level.getMap();
        
        // Probar las 4 direcciones posibles
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // Arriba, Abajo, Izquierda, Derecha
        
        Location bestMove = null;
        double bestDistance = Double.MAX_VALUE;
        boolean canBreakIce = false;
        
        for(int[] dir : directions){
            Location nextPos = from.move(dir[0], dir[1]);
            
            // Verificar si la posición es válida
            if(map.isValidPosition(nextPos)){
                double distance = nextPos.distanceTo(to);
                if(distance < bestDistance){
                    bestDistance = distance;
                    bestMove = nextPos;
                    canBreakIce = false;
                }
            } else {
                // Verificar si hay hielo que puede romper
                IceWall iceWall = level.getIceWallAt(nextPos);
                if(iceWall != null && player instanceof IceBreaker){
                    double distance = nextPos.distanceTo(to);
                    if(distance < bestDistance){
                        bestDistance = distance;
                        bestMove = nextPos;
                        canBreakIce = true;
                    }
                }
            }
        }
        
        if(bestMove != null){
            int dx = bestMove.getX() - from.getX();
            int dy = bestMove.getY() - from.getY();
            lastDx = dx;
            lastDy = dy;
            return new int[]{dx, dy, canBreakIce ? 1 : 0};
        }
        
        // No hay camino, quedarse quieto
        return new int[]{0, 0, 0};
    }
    
    /**
     * Encuentra el enemigo más cercano al jugador
     */
    private Enemy findNearestEnemy(Level level, Location playerLoc){
        Enemy nearest = null;
        double minDistance = Double.MAX_VALUE;
        
        for(Enemy enemy : level.getEnemies()){
            double distance = playerLoc.distanceTo(enemy.getLocation());
            if(distance < minDistance){
                minDistance = distance;
                nearest = enemy;
            }
        }
        
        return nearest;
    }
    
    /**
     * Verifica si hay un enemigo en una ubicación específica
     */
    private boolean isEnemyAt(Level level, Location location){
        for(Enemy enemy : level.getEnemies()){
            if(enemy.getLocation().equals(location)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Calcula un movimiento para escapar de un enemigo
     */
    private int[] escapeFromEnemy(Level level, Location playerLoc, Location enemyLoc, IceCream player){
        Map map = level.getMap();
        
        // Calcular dirección opuesta al enemigo
        int dx = Integer.compare(playerLoc.getX(), enemyLoc.getX());
        int dy = Integer.compare(playerLoc.getY(), enemyLoc.getY());
        
        // Intentar alejarse horizontalmente
        if(dx != 0){
            Location escapePos = playerLoc.move(dx, 0);
            if(map.isValidPosition(escapePos) && !isEnemyAt(level, escapePos)){
                lastDx = dx;
                lastDy = 0;
                return new int[]{dx, 0, 0};
            }
        }
        
        // Intentar alejarse verticalmente
        if(dy != 0){
            Location escapePos = playerLoc.move(0, dy);
            if(map.isValidPosition(escapePos) && !isEnemyAt(level, escapePos)){
                lastDx = 0;
                lastDy = dy;
                return new int[]{0, dy, 0};
            }
        }
        
        // Buscar cualquier dirección segura
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        Location safestMove = null;
        double maxDistance = -1;
        
        for(int[] dir : directions){
            Location nextPos = playerLoc.move(dir[0], dir[1]);
            if(map.isValidPosition(nextPos) && !isEnemyAt(level, nextPos)){
                double distToEnemy = nextPos.distanceTo(enemyLoc);
                if(distToEnemy > maxDistance){
                    maxDistance = distToEnemy;
                    safestMove = nextPos;
                }
            }
        }
        
        if(safestMove != null){
            int moveDx = safestMove.getX() - playerLoc.getX();
            int moveDy = safestMove.getY() - playerLoc.getY();
            lastDx = moveDx;
            lastDy = moveDy;
            return new int[]{moveDx, moveDy, 0};
        }
        
        // No puede escapar
        return new int[]{0, 0, 0};
    }
    
    /**
     * Busca una fruta segura (lejos de enemigos)
     */
    private Fruit findSafeFruit(Level level, Location playerLoc, Location enemyLoc){
        Fruit safest = null;
        double maxEnemyDist = -1;
        
        for(Fruit fruit : level.getFruits()){
            if(!fruit.isCollected()){
                double distToEnemy = fruit.getLocation().distanceTo(enemyLoc);
                if(distToEnemy > ENEMY_DETECTION_RANGE){
                    double distToPlayer = playerLoc.distanceTo(fruit.getLocation());
                    if(safest == null || distToPlayer < playerLoc.distanceTo(safest.getLocation())){
                        safest = fruit;
                        maxEnemyDist = distToEnemy;
                    }
                }
            }
        }
        
        return safest != null ? safest : findClosestFruit(level, playerLoc);
    }
    
    /**
     * Busca un camino alternativo evitando enemigos
     */
    private int[] findSafeAlternativePath(Level level, Location from, Location to, IceCream player){
        Map map = level.getMap();
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        
        Location bestMove = null;
        double bestScore = Double.MAX_VALUE;
        boolean canBreakIce = false;
        
        for(int[] dir : directions){
            Location nextPos = from.move(dir[0], dir[1]);
            
            // Verificar si hay enemigo
            if(isEnemyAt(level, nextPos)){
                continue; // Saltar esta dirección
            }
            
            if(map.isValidPosition(nextPos)){
                // Calcular score: distancia a objetivo - distancia a enemigos
                double distToTarget = nextPos.distanceTo(to);
                double distToNearestEnemy = Double.MAX_VALUE;
                
                for(Enemy enemy : level.getEnemies()){
                    double d = nextPos.distanceTo(enemy.getLocation());
                    if(d < distToNearestEnemy){
                        distToNearestEnemy = d;
                    }
                }
                
                // Penalizar movimientos cerca de enemigos
                double score = distToTarget - (distToNearestEnemy * 0.5);
                
                if(score < bestScore){
                    bestScore = score;
                    bestMove = nextPos;
                    canBreakIce = false;
                }
            } else {
                IceWall iceWall = level.getIceWallAt(nextPos);
                if(iceWall != null && player instanceof IceBreaker){
                    double distToTarget = nextPos.distanceTo(to);
                    if(distToTarget < bestScore){
                        bestScore = distToTarget;
                        bestMove = nextPos;
                        canBreakIce = true;
                    }
                }
            }
        }
        
        if(bestMove != null){
            int dx = bestMove.getX() - from.getX();
            int dy = bestMove.getY() - from.getY();
            lastDx = dx;
            lastDy = dy;
            return new int[]{dx, dy, canBreakIce ? 1 : 0};
        }
        
        return new int[]{0, 0, 0};
    }
}
