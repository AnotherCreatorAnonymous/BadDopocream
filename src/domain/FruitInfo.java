package domain;

/**
 * Clase de datos para representar información de una fruta
 * (Data Transfer Object)
 */
public class FruitInfo {
    public final int x;
    public final int y;
    public final String typeName;  // "Banana", "Cherry", "Cactus", etc.
    public final String name;      // Nombre de la fruta
    public final boolean isCollected;
    public final boolean hasSpikes; // Solo para Cactus
    public final int secondsUntilSpike; // Solo para Cactus
    
    public FruitInfo(int x, int y, String typeName, String name, boolean isCollected, boolean hasSpikes, int secondsUntilSpike){
        this.x = x;
        this.y = y;
        this.typeName = typeName;
        this.name = name;
        this.isCollected = isCollected;
        this.hasSpikes = hasSpikes;
        this.secondsUntilSpike = secondsUntilSpike;
    }
}
