package domain;

/**
 * Cactus - Fruta peligrosa que crece espinas cada 30 segundos
 * Cuando tiene espinas, mata al jugador si lo toca
 */
public class Cactus extends Fruit{
    private static final int CACTUS_POINTS = 250;
    private static final long SPIKE_INTERVAL = 30000; // 30 segundos en milisegundos
    private boolean hasSpikes;
    private long lastSpikeTime;

    public Cactus(Location location){
        super("Cactus", CACTUS_POINTS, location, false);
        this.hasSpikes = false;
        this.lastSpikeTime = System.currentTimeMillis();
    }

    @Override
    public int collect(){
        if(!collected){
            collected = true;
            return CACTUS_POINTS;
        }
        return 0;
    }
    
    /**
     * Actualiza el estado de las espinas
     */
    public void update() {
        if (collected) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpikeTime >= SPIKE_INTERVAL) {
            hasSpikes = !hasSpikes; // Alternar espinas
            lastSpikeTime = currentTime;
        }
    }
    
    /**
     * Verifica si el cactus tiene espinas activas
     */
    public boolean hasSpikes() {
        return hasSpikes && !collected;
    }
    
    /**
     * Verifica si debe matar al jugador
     */
    public boolean shouldKillPlayer(Location playerLocation) {
        return hasSpikes && !collected && this.location.equals(playerLocation);
    }
    
    /**
     * Obtiene el tiempo restante hasta el siguiente cambio de espinas (en segundos)
     */
    public int getSecondsUntilNextSpike() {
        if (collected) return 0;
        
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastSpikeTime;
        long remaining = SPIKE_INTERVAL - elapsed;
        
        if (remaining <= 0) return 0;
        return (int) (remaining / 1000);
    }
    
    @Override
    public String getTypeName(){
        return "Cactus";
    }
}
