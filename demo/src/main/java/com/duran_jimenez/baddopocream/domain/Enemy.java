package com.duran_jimenez.baddopocream.domain;

/**
 * Clase base abstracta para todos los enemigos del juego.
 * 
 * Los enemigos persiguen o patrullan el mapa y matan al jugador
 * al hacer contacto. Cada tipo de enemigo tiene un comportamiento
 * de movimiento diferente.
 * 
 * Tipos de enemigos implementados:
 * - Troll: Patrulla en línea recta, cambia de dirección al chocar
 * - Maceta: Persigue directamente al jugador más cercano
 * - CalamarNaranja: Movimiento aleatorio caótico
 * - Narval: Comportamiento similar al troll pero más rápido
 * 
 * @author Durán-Jiménez
 */
public abstract class Enemy implements Movable {
    
    protected String name;
    protected Location location;
    protected int speed;
    
    protected Enemy(String name, Location location, int speed){
        this.name = name;
        this.location = location;
        this.speed = speed;
    }

    public abstract void move(Location playerLocation);
    
    /**
     * Retorna el tipo de enemigo para identificación (sin instanceof)
     * Ejemplo: "Troll", "Maceta", "CalamarNaranja", "Narval"
     */
    public abstract String getTypeName();
    
    /**
     * Obtiene la dirección X del movimiento (para animaciones)
     * Por defecto 0, subclases que lo necesiten lo sobrescriben
     */
    public int getDirectionX(){
        return 0;
    }
    
    /**
     * Obtiene la dirección Y del movimiento (para animaciones)
     * Por defecto 1, subclases que lo necesiten lo sobrescriben
     */
    public int getDirectionY(){
        return 1;
    }
    
    /**
     * Cambia dirección al colisionar con otro enemigo
     * Solo implementado en enemigos que lo necesiten
     */
    public void changeDirectionOnEnemyCollision(Map map){
        // Por defecto no hace nada
    }
    
    /**
     * Cambia dirección aleatoriamente
     * Solo implementado en enemigos que lo necesiten
     */
    public void changeDirectionRandomly(){
        // Por defecto no hace nada
    }
    
    /**
     * Intenta moverse validando contra el mapa
     * Por defecto usa el método move() estándar con validación
     * Las subclases pueden sobrescribir este método para comportamientos más inteligentes
     */
    public boolean tryMove(Location playerLocation, Map map){
        Location currentLoc = this.location;
        move(playerLocation);
        Location newLoc = this.location;
        
        if(map.isValidPosition(newLoc)){
            return true;
        } else {
            // Revertir movimiento
            this.location = currentLoc;
            return false;
        }
    }

    public abstract boolean detectPlayer(Location playerLocation);

    public boolean collidesWithPlayer(Location playerLocation) {
        return this.location.equals(playerLocation);
    }

    public String getName(){
        return this.name;
    }

    @Override
    public Location getLocation(){
        return this.location;
    }

    @Override
    public void setLocation(Location location){
        this.location = location;
    }

    public int getSpeed(){
        return this.speed;
    }
}
