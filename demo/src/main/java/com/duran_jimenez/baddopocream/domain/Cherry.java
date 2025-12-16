package com.duran_jimenez.baddopocream.domain;

/**
 * Cherry - Fruta que se teletransporta cada 20 segundos
 */
public class Cherry extends Fruit{
    
    private static final int CHERRY_POINTS = 150;
    private static final long TELEPORT_INTERVAL = 20000; // 20 segundos en milisegundos
    private long lastTeleportTime;
    private Map map; // Referencia al mapa para encontrar posiciones válidas

    public Cherry(Location location){
        super("Cherry", CHERRY_POINTS, location, true);
        this.lastTeleportTime = System.currentTimeMillis();
        this.map = null;
    }
    
    /**
     * Establece la referencia al mapa (necesaria para teletransporte)
     */
    @Override
    public void setMap(Map map) {
        this.map = map;
    }

    @Override
    public int collect(){
        if(!collected){
            collected = true;
            return CHERRY_POINTS;
        }
        return 0;
    }
    
    /**
     * Verifica si debe teletransportarse y lo hace
     */
    @Override
    public void update() {
        if (collected || map == null) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTeleportTime >= TELEPORT_INTERVAL) {
            teleport();
            lastTeleportTime = currentTime;
        }
    }
    
    /**
     * Teletransporta la cherry a una posición aleatoria válida
     */
    private void teleport() {
        int maxAttempts = 100;
        int attempts = 0;
        
        while (attempts < maxAttempts) {
            int randomX = (int)(Math.random() * map.getWidth());
            int randomY = (int)(Math.random() * map.getHeight());
            Location newLocation = new Location(randomX, randomY);
            
            // Verificar que la posición sea válida (no pared, no hielo)
            if (map.isValidPosition(newLocation)) {
                this.location = newLocation;
                return;
            }
            attempts++;
        }
        // Si no se encuentra posición después de 100 intentos, mantener la actual
    }
    
    @Override
    public String getTypeName(){
        return "Cherry";
    }
    
    @Override
    public boolean requiresMapReference(){
        return true;
    }
    
}
