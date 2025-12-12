package domain;

/**
 * BaldosaCaliente (Hot Tile) - Obstáculo que derrite bloques de hielo
 * 
 * COMPORTAMIENTO:
 * - Los bloques de hielo creados sobre baldosas calientes se derriten inmediatamente
 * - Si el helado crea una línea de bloques, solo se derriten los que están sobre baldosas
 * - Los demás bloques permanecen intactos
 * - Los jugadores y enemigos pueden pasar normalmente sobre las baldosas
 */
public class BaldosaCaliente {
    
    private Location location;
    private boolean active;
    
    /**
     * Constructor
     * @param location Posición de la baldosa caliente
     */
    public BaldosaCaliente(Location location) {
        this.location = location;
        this.active = true;
    }
    
    /**
     * Obtiene la ubicación de la baldosa
     * @return Location de la baldosa
     */
    public Location getLocation() {
        return location;
    }
    
    /**
     * Verifica si la baldosa está activa (siempre true para baldosas)
     * @return true si está activa
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Verifica si un bloque de hielo en esta posición se derrite
     * @param iceLocation Posición del bloque de hielo
     * @return true si se debe derretir
     */
    public boolean shouldMeltIce(Location iceLocation) {
        return active && location.equals(iceLocation);
    }
    
    /**
     * Procesa el derretimiento de un bloque de hielo si está sobre esta baldosa
     * @param iceLocation Posición del bloque de hielo
     * @return true si el hielo fue derretido
     */
    public boolean meltIce(Location iceLocation) {
        if (shouldMeltIce(iceLocation)) {
            // El hielo se derrite instantáneamente
            return true;
        }
        return false;
    }
}
