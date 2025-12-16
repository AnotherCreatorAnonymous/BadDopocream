package com.duran_jimenez.baddopocream.domain;

/**
 * Representa una posición inmutable en el mapa del juego.
 * 
 * Esta clase es inmutable (thread-safe) - el método move() retorna
 * una nueva instancia en lugar de modificar la actual.
 * 
 * Implementa equals() y hashCode() para permitir uso en colecciones.
 * 
 * @author Durán-Jiménez
 */
public class Location {
    
    private final int x;
    private final int y;

    public Location(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    /**
     * Calcula la distancia euclidiana a otra ubicación.
     * Útil para determinar qué jugador está más cerca de un enemigo.
     */
    public double distanceTo(Location other){
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Calcula la distancia Manhattan (sin diagonales) a otra ubicación.
     * Más eficiente que distanceTo() cuando no se necesita precisión.
     */
    public int manhattanDistanceTo(Location other){
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    /**
     * Verifica si otra ubicación está adyacente (arriba, abajo, izquierda, derecha).
     * No incluye diagonales.
     */
    public boolean isAdjacent(Location other){
        int dx = Math.abs(this.x - other.x);
        int dy = Math.abs(this.y - other.y);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    /**
     * Retorna una nueva ubicación desplazada por (dx, dy).
     * No modifica la ubicación actual (inmutabilidad).
     */
    public Location move(int dx, int dy){
        return new Location(this.x + dx, this.y + dy);
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(!(obj instanceof Location)) return false;
        Location other = (Location) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode(){
        return 31 * x + y;
    }
    
    @Override
    public String toString(){
        return "(" + x + ", " + y + ")";
    }
}
