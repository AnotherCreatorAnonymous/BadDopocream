package domain;

/**
 * Fogata (Campfire) - Obstáculo que elimina al jugador
 * 
 * COMPORTAMIENTO:
 * - Elimina al helado inmediatamente si entra en contacto
 * - Los enemigos no sufren daño al tocar el fuego
 * - Puede apagarse temporalmente creando un bloque de hielo sobre ella y rompiéndolo
 * - Después de 10 segundos, el fuego vuelve a encenderse
 */
public class Fogata {
    
    private Location location;
    private boolean lit; // true = encendida, false = apagada
    private long extinguishTime; // Tiempo cuando fue apagada
    private static final long RELIGHT_DELAY = 10000; // 10 segundos en milisegundos
    
    /**
     * Constructor
     * @param location Posición de la fogata
     */
    public Fogata(Location location) {
        this.location = location;
        this.lit = true;
        this.extinguishTime = 0;
    }
    
    /**
     * Obtiene la ubicación de la fogata
     * @return Location de la fogata
     */
    public Location getLocation() {
        return location;
    }
    
    /**
     * Verifica si la fogata está encendida
     * Actualiza automáticamente el estado si pasó el tiempo de reencendido
     * @return true si está encendida
     */
    public boolean isLit() {
        // Si está apagada, verificar si ya pasó el tiempo de reencendido
        if (!lit && extinguishTime > 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - extinguishTime >= RELIGHT_DELAY) {
                relight();
            }
        }
        return lit;
    }
    
    /**
     * Apaga la fogata temporalmente
     * Se llama cuando se crea y destruye un bloque de hielo sobre ella
     */
    public void extinguish() {
        if (lit) {
            lit = false;
            extinguishTime = System.currentTimeMillis();
        }
    }
    
    /**
     * Reenciende la fogata
     * Se llama automáticamente después de 10 segundos
     */
    private void relight() {
        lit = true;
        extinguishTime = 0;
    }
    
    /**
     * Fuerza el reencendido de la fogata (para testing o reset de nivel)
     */
    public void forceRelight() {
        relight();
    }
    
    /**
     * Verifica si un jugador en esta posición debería ser eliminado
     * @param playerLocation Posición del jugador
     * @return true si el jugador debe morir
     */
    public boolean shouldKillPlayer(Location playerLocation) {
        return lit && location.equals(playerLocation);
    }
    
    /**
     * Verifica si se puede crear un bloque de hielo sobre la fogata
     * Solo si está encendida (para poder apagarla)
     * @return true si se puede crear hielo sobre ella
     */
    public boolean canPlaceIceBlock() {
        return lit;
    }
    
    /**
     * Obtiene el tiempo restante hasta que se reencienda (en segundos)
     * @return Segundos restantes, o 0 si ya está encendida
     */
    public int getSecondsUntilRelight() {
        if (lit) return 0;
        
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - extinguishTime;
        long remaining = RELIGHT_DELAY - elapsed;
        
        if (remaining <= 0) {
            relight();
            return 0;
        }
        
        return (int) (remaining / 1000);
    }
}
