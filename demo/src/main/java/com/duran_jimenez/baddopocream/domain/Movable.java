package com.duran_jimenez.baddopocream.domain;

/**
 * Interfaz para entidades que pueden moverse en el juego
 * Proporciona comportamiento básico de movimiento y validación
 */
public interface Movable {
    
    /**
     * Obtiene la ubicación actual de la entidad
     * @return Location actual
     */
    Location getLocation();
    
    /**
     * Establece una nueva ubicación para la entidad
     * @param location Nueva ubicación
     */
    void setLocation(Location location);
    
    /**
     * Intenta mover la entidad a una nueva posición
     * @param dx Desplazamiento en X
     * @param dy Desplazamiento en Y
     * @param map Mapa para validar el movimiento
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    default boolean tryMove(int dx, int dy, Map map) {
        Location currentLocation = getLocation();
        Location newLocation = currentLocation.move(dx, dy);
        
        if (map.isValidPosition(newLocation)) {
            setLocation(newLocation);
            return true;
        }
        return false;
    }
    
    /**
     * Mueve la entidad sin validación
     * @param dx Desplazamiento en X
     * @param dy Desplazamiento en Y
     */
    default void move(int dx, int dy) {
        Location currentLocation = getLocation();
        Location newLocation = currentLocation.move(dx, dy);
        setLocation(newLocation);
    }
}
