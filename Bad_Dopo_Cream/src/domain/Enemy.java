package domain;

public abstract class Enemy {
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

    public Location getLocation(){
        return this.location;
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public int getSpeed(){
        return this.speed;
    }
}
