package com.duran_jimenez.baddopocream.domain;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un nivel del juego Bad Dopo Cream.
 * 
 * Contiene toda la lógica de un nivel individual:
 * - Mapa con paredes, hielo y obstáculos
 * - Jugador(es), frutas y enemigos
 * - Sistema de puntuación y timer
 * - Mecánicas de hielo (crear/romper)
 * - Detección de colisiones
 * - Sistema de oleadas de frutas (opcional)
 * 
 * Soporta modo un jugador y dos jugadores (cooperativo/competitivo)
 * 
 * @author Durán-Jiménez
 * @version 2.0
 */
public class Level {

    private int levelNumber;
    private Map map;
    private IceCream player;
    private IceCream player2;
    private ArrayList<Fruit> fruits;
    private ArrayList<Enemy> enemies;
    private boolean isCompleted;
    private int totalFruits;
    private int collectedFruits;
    private int currentScore;
    private int player1Score;
    private int player2Score;
    
    // Sistema de oleadas
    private List<FruitWave> fruitWaves;
    private int currentWaveIndex;
    private boolean useWaveSystem;
    
    // Timer del nivel
    private static final long LEVEL_TIME_LIMIT = 180000; // 3 minutos
    private long levelStartTime;
    private long pausedTime;
    private long lastPauseStart;
    private boolean isPaused;
    private boolean timeExpired;

    public Level(int levelNumber, int width, int height){
        this.levelNumber = levelNumber;
        this.map = new Map(width, height);
        this.fruits = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.isCompleted = false;
        this.collectedFruits = 0;
        this.totalFruits = 0;
        this.currentScore = 0;
        this.player1Score = 0;
        this.player2Score = 0;
        this.player2 = null; // Inicialmente sin segundo jugador
        
        // Inicializar sistema de oleadas
        this.fruitWaves = new ArrayList<>();
        this.currentWaveIndex = 0;
        this.useWaveSystem = false; // Por defecto desactivado
        
        // Inicializar timer
        this.levelStartTime = System.currentTimeMillis();
        this.pausedTime = 0;
        this.lastPauseStart = 0;
        this.isPaused = false;
        this.timeExpired = false;
    }

    public void setPlayer(IceCream player){
        this.player = player;
    }
    
    public void setPlayer2(IceCream player2){
        this.player2 = player2;
    }
    
    public IceCream getPlayer2(){
        return this.player2;
    }
    
    public boolean hasTwoPlayers(){
        return this.player2 != null;
    }
    
    /**
     * Restaura el estado del nivel desde un guardado
     */
    public void restoreFromState(GameState state){
        if(state == null) return;
        
        // Restaurar puntaje y frutas recolectadas
        this.currentScore = state.getLevelScore();
        this.collectedFruits = state.getCollectedFruits();
        
        // Restaurar tiempo
        long elapsed = LEVEL_TIME_LIMIT - state.getRemainingTime();
        this.levelStartTime = System.currentTimeMillis() - elapsed;
        this.pausedTime = 0;
        this.isPaused = false;
        this.timeExpired = false;
        
        // Restaurar posición del jugador
        if(state.getPlayerLocation() != null && player != null){
            player.setLocation(state.getPlayerLocation().toLocation());
        }
        
        // Restaurar posición del jugador 2 si existe
        if(state.getPlayer2Location() != null && player2 != null){
            player2.setLocation(state.getPlayer2Location().toLocation());
        }
        
        // Marcar frutas como recolectadas según el estado guardado
        List<GameState.SerializableFruit> savedFruits = state.getFruits();
        for(GameState.SerializableFruit savedFruit : savedFruits){
            for(Fruit fruit : fruits){
                if(fruit.getName().equals(savedFruit.name) && 
                    fruit.getLocation().equals(savedFruit.location.toLocation())){
                    if(savedFruit.collected){
                        fruit.collect();
                    }
                    break;
                }
            }
        }
        
        // Restaurar posiciones de enemigos
        List<GameState.SerializableEnemy> savedEnemies = state.getEnemies();
        for(int i = 0; i < savedEnemies.size() && i < enemies.size(); i++){
            Enemy enemy = enemies.get(i);
            GameState.SerializableEnemy savedEnemy = savedEnemies.get(i);
            if(enemy.getName().equals(savedEnemy.type)){
                enemy.setLocation(savedEnemy.location.toLocation());
            }
        }
    }

    public void addFruit(Fruit fruit){
        this.fruits.add(fruit);
        
        // Si no se usa el sistema de oleadas, incrementar totalFruits normalmente
        if (!useWaveSystem) {
            this.totalFruits++;
        }
        
        // Configurar referencias al mapa para frutas especiales
        if (fruit instanceof Cherry) {
            ((Cherry)fruit).setMap(this.map);
        } else if (fruit instanceof Pineapple) {
            ((Pineapple)fruit).setMap(this.map);
        }
    }
    
    /**
     * Activa el sistema de oleadas de frutas
     */
    public void enableWaveSystem() {
        this.useWaveSystem = true;
    }
    
    /**
     * Agrega una oleada de frutas al sistema
     */
    public void addWave(FruitWave wave) {
        fruitWaves.add(wave);
        
        // Si es la primera oleada, activarla automáticamente
        if (fruitWaves.size() == 1) {
            activateWave(0);
        }
    }
    
    /**
     * Activa una oleada específica
     */
    private void activateWave(int waveIndex) {
        if (waveIndex < 0 || waveIndex >= fruitWaves.size()) return;
        
        FruitWave wave = fruitWaves.get(waveIndex);
        wave.activate();
        currentWaveIndex = waveIndex;
        
        // Agregar las frutas de esta oleada al nivel
        for (Fruit fruit : wave.getFruits()) {
            addFruit(fruit);
        }
        
        // Actualizar totalFruits solo para esta oleada
        if (useWaveSystem) {
            totalFruits = wave.getTotalFruits();
            collectedFruits = 0;
        }
    }
    
    /**
     * Verifica si la oleada actual está completada y activa la siguiente
     */
    private void checkWaveCompletion() {
        if (!useWaveSystem || fruitWaves.isEmpty()) return;
        
        FruitWave currentWave = fruitWaves.get(currentWaveIndex);
        currentWave.checkCompletion();
        
        if (currentWave.isCompleted()) {
            // Si hay más oleadas, activar la siguiente
            if (currentWaveIndex + 1 < fruitWaves.size()) {
                activateNextWave();
            } else {
                // Todas las oleadas completadas, nivel completado
                this.isCompleted = true;
            }
        }
    }
    
    /**
     * Activa la siguiente oleada
     */
    private void activateNextWave() {
        int nextWaveIndex = currentWaveIndex + 1;
        if (nextWaveIndex < fruitWaves.size()) {
            activateWave(nextWaveIndex);
        }
    }
    
    /**
     * Obtiene la oleada actual
     */
    public FruitWave getCurrentWave() {
        if (useWaveSystem && currentWaveIndex < fruitWaves.size()) {
            return fruitWaves.get(currentWaveIndex);
        }
        return null;
    }
    
    /**
     * Obtiene el número total de oleadas
     */
    public int getTotalWaves() {
        return fruitWaves.size();
    }
    
    /**
     * Obtiene el índice de la oleada actual (1-based para mostrar)
     */
    public int getCurrentWaveNumber() {
        return currentWaveIndex + 1;
    }
    
    public boolean isUsingWaveSystem() {
        return useWaveSystem;
    }

    public void addEnemy(Enemy enemy){
        this.enemies.add(enemy);
    }

    public void addWall(Location location){
        this.map.addWall(location);
    }
    
    public void addIceWall(Location location){
        this.map.addIceWall(location);
    }

    public boolean movePlayer(int dx, int dy){
        if(player == null || !player.isAlive()) return false;
        
        Location currentLocation = player.getLocation();
        Location newLocation = currentLocation.move(dx, dy);

        if(!map.isValidPosition(newLocation)){
            return false;  // No puede moverse (posición inválida)
        }
        
        player.move(dx, dy);
        
        checkFruitCollection(newLocation, 1); // Jugador 1 recolecta

        checkEnemyCollisions();
        
        checkCampfireCollisions();
        
        checkCactusCollisions();

        return true;  // Movimiento exitoso
    }
    
    public boolean movePlayer2(int dx, int dy){
        if(player2 == null || !player2.isAlive()) return false;
        
        Location currentLocation = player2.getLocation();
        Location newLocation = currentLocation.move(dx, dy);

        if(!map.isValidPosition(newLocation)){
            return false;  // No puede moverse (posición inválida)
        }
        
        player2.move(dx, dy);
        
        checkFruitCollection(newLocation, 2); // Jugador 2 recolecta

        checkEnemyCollisions();
        
        checkCampfireCollisions();
        
        checkCactusCollisions();

        return true;  // Movimiento exitoso
    }

    /**
     * Verifica y recolecta frutas en la ubicación dada
     * @param location Ubicación a verificar
     * @param playerNumber Número del jugador que recolecta (1 o 2)
     */
    private void checkFruitCollection(Location location, int playerNumber){
        for(Fruit fruit :fruits){
            if(fruit.getLocation().equals(location) && !fruit.isCollected()){
                // Si es un Cactus con espinas, no se recolecta (será manejado por checkCactusCollisions)
                if(fruit instanceof Cactus && ((Cactus)fruit).hasSpikes()){
                    continue; // Saltar este cactus, será manejado por checkCactusCollisions
                }
                
                int points = fruit.collect();
                if(points > 0){
                    this.currentScore += points; // Puntaje combinado
                    this.collectedFruits++;
                    
                    // Asignar puntos al jugador correspondiente
                    if (playerNumber == 1) {
                        this.player1Score += points;
                    } else if (playerNumber == 2) {
                        this.player2Score += points;
                    }
                }
            }
            
            // Verificar completitud según el modo
            if (useWaveSystem) {
                // En modo oleadas, verificar si la oleada actual está completa
                checkWaveCompletion();
            } else {
                // Modo normal, verificar si todas las frutas están recolectadas
                if(collectedFruits >= totalFruits){
                    this.isCompleted = true;
                }
            }
        }
    }
    
    private void checkCampfireCollisions(){
        for(Fogata campfire : map.getCampfires()){
            if(campfire.shouldKillPlayer(player.getLocation())){
                player.die();
                return;
            }
            if(player2 != null && campfire.shouldKillPlayer(player2.getLocation())){
                player2.die();
                return;
            }
        }
    }
    
    private void checkCactusCollisions(){
        for(Fruit fruit : fruits){
            if(fruit instanceof Cactus && !fruit.isCollected()){
                Cactus cactus = (Cactus)fruit;
                if(cactus.shouldKillPlayer(player.getLocation())){
                    player.die();
                    return;
                }
                if(player2 != null && cactus.shouldKillPlayer(player2.getLocation())){
                    player2.die();
                    return;
                }
            }
        }
    }
    


    private void checkEnemyCollisions(){
        // Verificar colisiones solo si el jugador 1 está vivo
        if(player != null && player.isAlive()){
            Location playerLocation = player.getLocation();
            for(Enemy enemy : enemies){
                if(enemy.collidesWithPlayer(playerLocation)){
                    player.die();  // Muerte instantánea al tocar enemigo
                    break;  // No necesita seguir verificando para este jugador
                }
            }
        }
        
        // Verificar colisiones para el jugador 2 si existe y está vivo
        if(player2 != null && player2.isAlive()){
            Location player2Location = player2.getLocation();
            for(Enemy enemy : enemies){
                if(enemy.collidesWithPlayer(player2Location)){
                    player2.die();
                    break;
                }
            }
        }
    }

    public void moveEnemies(){
        for(Enemy enemy : enemies){
            Location currentLoc = enemy.getLocation();
            
            // Determinar qué jugador perseguir (el más cercano que esté vivo)
            Location targetLocation = getClosestPlayerLocation(enemy.getLocation());
            
            // Usar tryMove que maneja validación y cambio de dirección
            enemy.tryMove(targetLocation, map);
            
            Location newLoc = enemy.getLocation();
            
            // Verificar colisión con otros enemigos
            boolean collisionWithEnemy = false;
            for(Enemy otherEnemy : enemies){
                if(otherEnemy != enemy && otherEnemy.getLocation().equals(newLoc)){
                    collisionWithEnemy = true;
                    break;
                }
            }
            
            // Si hay colisión con otro enemigo, revertir movimiento
            if(collisionWithEnemy){
                enemy.setLocation(currentLoc);
                
                // Si es Troll, cambiar dirección al colisionar
                if(enemy instanceof Troll){
                    ((Troll)enemy).changeDirectionOnEnemyCollision(map);
                }
                // Si es Calamar, cambiar dirección aleatoriamente
                if(enemy instanceof CalamarNaranja){
                    ((CalamarNaranja)enemy).changeDirectionRandomly();
                }
            }

            // Verificar colisión con jugador 1
            if(player != null && player.isAlive() && enemy.collidesWithPlayer(player.getLocation())){
                player.die();
            }
            
            // Verificar colisión con jugador 2
            if(player2 != null && player2.isAlive() && enemy.collidesWithPlayer(player2.getLocation())){
                player2.die();
            }
        }
    }
    
    /**
     * Obtiene la ubicación del jugador más cercano que esté vivo
     * @param fromLocation Ubicación desde la cual calcular la distancia
     * @return Ubicación del jugador más cercano, o del jugador 1 si no hay ninguno vivo
     */
    private Location getClosestPlayerLocation(Location fromLocation) {
        Location player1Loc = (player != null && player.isAlive()) ? player.getLocation() : null;
        Location player2Loc = (player2 != null && player2.isAlive()) ? player2.getLocation() : null;
        
        // Si solo hay un jugador vivo, retornarlo
        if (player1Loc == null && player2Loc != null) {
            return player2Loc;
        }
        if (player2Loc == null && player1Loc != null) {
            return player1Loc;
        }
        if (player1Loc == null && player2Loc == null) {
            // Ningún jugador vivo, retornar posición por defecto
            return new Location(0, 0);
        }
        
        // Ambos jugadores vivos, calcular cuál está más cerca
        double dist1 = fromLocation.distanceTo(player1Loc);
        double dist2 = fromLocation.distanceTo(player2Loc);
        
        return (dist1 <= dist2) ? player1Loc : player2Loc;
    }

    public void moveFruits(){
        for(Fruit fruit : fruits){
            if(fruit.canMove() && !fruit.isCollected()){
                fruit.move();
            }
            
            // Actualizar comportamientos especiales
            if(fruit instanceof Cherry){
                ((Cherry)fruit).update();
            } else if(fruit instanceof Cactus){
                ((Cactus)fruit).update();
            }
        }
    }

    public void createIceWall(Location location){
        if(map.isValidPosition(location)){
            map.addIceWall(location);
        }
    }
    
    /**
     * Crea una línea de hielo en la dirección especificada desde el jugador 1
     * Se detiene al encontrar una pared, otro hielo, un enemigo, o el límite del mapa
     * Los bloques sobre baldosas calientes se derriten inmediatamente
     */
    public void createIceLine(int dx, int dy){
        if(player == null) return;
        createIceLineFromPlayer(player.getLocation(), dx, dy);
    }
    
    /**
     * Crea una línea de hielo en la dirección especificada desde el jugador 2
     */
    public void createIceLinePlayer2(int dx, int dy){
        if(player2 == null) return;
        createIceLineFromPlayer(player2.getLocation(), dx, dy);
    }
    
    /**
     * Método auxiliar que crea una línea de hielo desde una posición inicial
     * Elimina código duplicado entre createIceLine y createIceLinePlayer2
     */
    private void createIceLineFromPlayer(Location startLocation, int dx, int dy) {
        Location currentLoc = startLocation;
        
        while(true){
            currentLoc = currentLoc.move(dx, dy);
            
            // Detener si encontramos pared o límite
            if(!map.isValidPosition(currentLoc)){
                break;
            }
            
            // Detener si ya hay hielo
            if(map.hasIceWall(currentLoc)){
                break;
            }
            
            // Detener si hay un enemigo en esta posición
            if(hasEnemyAt(currentLoc)){
                break;
            }
            
            // No crear hielo donde está algún jugador
            if(isPlayerAt(currentLoc)){
                break;
            }
            
            // Crear hielo en esta posición
            map.addIceWall(currentLoc);
            
            // Si hay baldosa caliente, el hielo se derrite inmediatamente
            if(map.isHotTile(currentLoc)){
                map.removeIceWall(currentLoc);
            }
        }
    }
    
    /**
     * Verifica si hay un enemigo en la ubicación dada
     */
    private boolean hasEnemyAt(Location location) {
        for(Enemy enemy : enemies){
            if(enemy.getLocation().equals(location)){
                return true;
            }
        }
        return false;
    }
    
    /**
     * Verifica si hay algún jugador en la ubicación dada
     */
    private boolean isPlayerAt(Location location) {
        if(player != null && player.getLocation().equals(location)){
            return true;
        }
        if(player2 != null && player2.getLocation().equals(location)){
            return true;
        }
        return false;
    }

    public void breakIceWall(Location location){
        if(map.hasIceWall(location)){
            map.removeIceWall(location);
            
            // Si había una fogata en esta posición, apagarla temporalmente
            Fogata campfire = map.getCampfireAt(location);
            if(campfire != null && campfire.isLit()){
                campfire.extinguish();
            }
        }
    }
    
    /**
     * Rompe una línea de hielo en la dirección especificada desde la posición del jugador 1
     * @param dx Dirección X (-1, 0, 1)
     * @param dy Dirección Y (-1, 0, 1)
     */
    public void breakIceLine(int dx, int dy){
        if(player == null) return;
        breakIceLineFromPosition(player.getLocation(), dx, dy);
    }
    
    /**
     * Rompe una línea de hielo en la dirección especificada desde la posición del jugador 2
     * @param dx Dirección X (-1, 0, 1)
     * @param dy Dirección Y (-1, 0, 1)
     */
    public void breakIceLinePlayer2(int dx, int dy){
        if(player2 == null) return;
        breakIceLineFromPosition(player2.getLocation(), dx, dy);
    }
    
    /**
     * Rompe una línea continua de hielo desde una posición en la dirección especificada
     * @param startPos Posición inicial del jugador
     * @param dx Dirección X (-1, 0, 1)
     * @param dy Dirección Y (-1, 0, 1)
     */
    private void breakIceLineFromPosition(Location startPos, int dx, int dy){
        if(dx == 0 && dy == 0) return;
        
        Location currentPos = startPos.move(dx, dy);
        
        while(map.hasIceWall(currentPos) || map.isValidPosition(currentPos)){
            if(map.hasIceWall(currentPos)){
                breakIceWall(currentPos);
                currentPos = currentPos.move(dx, dy);
            } else {
                break;
            }
        }
    }

    public boolean isCompleted(){
        return this.isCompleted;
    }

    public boolean isGameOver(){
        // En modo cooperativo, el juego termina si ambos jugadores mueren
        if(player2 != null){
            return !player.isAlive() && !player2.isAlive();
        }
        return !player.isAlive();
    }

    public int getLevelNumber(){
        return this.levelNumber;
    }

    public Map getMap(){
        return this.map;
    }

    public IceCream getPlayer(){
        return this.player;
    }

    public ArrayList<Fruit> getFruits(){
        return this.fruits;
    }

    public ArrayList<Enemy> getEnemies(){
        return this.enemies;
    }

    public int getCollectedFruits(){
        return this.collectedFruits;
    }

    public int getTotalFruits(){
        return this.totalFruits;
    }
    
    public int getCurrentScore(){
        return this.currentScore;
    }
    
    /**
     * Obtiene el puntaje del jugador 1 en este nivel
     */
    public int getPlayer1Score(){
        return this.player1Score;
    }
    
    /**
     * Obtiene el puntaje del jugador 2 en este nivel
     */
    public int getPlayer2Score(){
        return this.player2Score;
    }
    
    /**
     * Obtiene la pared de hielo en una ubicación específica
     * @return IceWall si existe, null si no
     */
    public IceWall getIceWallAt(Location location){
        return this.map.getIceWallAt(location);
    }
    
    // ========== Métodos de Timer ==========
    
    /**
     * Obtiene el tiempo restante en milisegundos
     */
    public long getRemainingTime() {
        if (timeExpired) return 0;
        
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - levelStartTime) - pausedTime;
        
        // Si está en pausa, no contar el tiempo desde que se pausó
        if (isPaused) {
            elapsedTime -= (currentTime - lastPauseStart);
        }
        
        long remaining = LEVEL_TIME_LIMIT - elapsedTime;
        
        if (remaining <= 0) {
            timeExpired = true;
            return 0;
        }
        
        return remaining;
    }
    
    /**
     * Obtiene el tiempo restante en segundos
     */
    public int getRemainingSeconds() {
        return (int) (getRemainingTime() / 1000);
    }
    
    /**
     * Verifica si el tiempo se agotó
     */
    public boolean isTimeExpired() {
        getRemainingTime(); // Actualizar estado
        return timeExpired;
    }
    
    /**
     * Pausa el timer
     */
    public void pause() {
        if (!isPaused) {
            isPaused = true;
            lastPauseStart = System.currentTimeMillis();
        }
    }
    
    /**
     * Reanuda el timer
     */
    public void resume() {
        if (isPaused) {
            long currentTime = System.currentTimeMillis();
            pausedTime += (currentTime - lastPauseStart);
            isPaused = false;
            lastPauseStart = 0;
        }
    }
    
    /**
     * Verifica si está en pausa
     */
    public boolean isPaused() {
        return isPaused;
    }
    
    /**
     * Reinicia el timer (para restart de nivel)
     */
    public void resetTimer() {
        this.levelStartTime = System.currentTimeMillis();
        this.pausedTime = 0;
        this.lastPauseStart = 0;
        this.isPaused = false;
        this.timeExpired = false;
    }
}
