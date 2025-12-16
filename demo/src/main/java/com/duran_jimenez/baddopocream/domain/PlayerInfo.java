package com.duran_jimenez.baddopocream.domain;

/**
 * Clase de datos para representar información de un jugador
 * (Data Transfer Object)
 */
public class PlayerInfo {
    public final int x;
    public final int y;
    public final boolean isAlive;
    public final int lastDx;
    public final int lastDy;
    public final String name;
    public final String color;
    
    public PlayerInfo(int x, int y, boolean isAlive, int lastDx, int lastDy, String name, String color){
        this.x = x;
        this.y = y;
        this.isAlive = isAlive;
        this.lastDx = lastDx;
        this.lastDy = lastDy;
        this.name = name;
        this.color = color;
    }
}
