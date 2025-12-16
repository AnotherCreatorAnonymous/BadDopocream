package com.duran_jimenez.baddopocream.domain;

/**
 * Clase de datos para representar información de un enemigo
 * (Data Transfer Object)
 */
public class EnemyInfo {
    public final int x;
    public final int y;
    public final String typeName; // "Troll", "Maceta", "CalamarNaranja", "Narval"
    public final int directionX;
    public final int directionY;
    
    public EnemyInfo(int x, int y, String typeName, int directionX, int directionY){
        this.x = x;
        this.y = y;
        this.typeName = typeName;
        this.directionX = directionX;
        this.directionY = directionY;
    }
}
