package com.duran_jimenez.baddopocream.domain;

/**
 * Clase de datos para representar información de un obstáculo
 * (Data Transfer Object)
 */
public class ObstacleInfo {
    public final int x;
    public final int y;
    public final String type; // "HotTile" o "Campfire"
    public final boolean isLit; // Solo para Campfire
    public final int secondsUntilRelight; // Solo para Campfire
    
    public ObstacleInfo(int x, int y, String type, boolean isLit, int secondsUntilRelight){
        this.x = x;
        this.y = y;
        this.type = type;
        this.isLit = isLit;
        this.secondsUntilRelight = secondsUntilRelight;
    }
}
