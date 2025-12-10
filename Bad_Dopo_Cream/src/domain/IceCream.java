package domain;

public class IceCream implements IceBreaker{
    
    private String name;
    private String color;
    private Location location;
    private boolean isAlive;
    private int speed;
    
    // Dirección actual del jugador (para creación de hielo)
    private int lastDx = 0;
    private int lastDy = 1; // Por defecto mirando hacia abajo

    public IceCream(String name, String color, Location location){
        this.name = name;
        this.color = color;
        this.location = location;
        this.isAlive = true;
        this.speed = 1;
    }

    public void move(int dx, int dy){
        this.location = this.location.move(dx, dy);
        // Actualizar dirección solo si hay movimiento
        if(dx != 0 || dy != 0){
            this.lastDx = dx;
            this.lastDy = dy;
        }
    }
    
    public int getLastDx(){
        return this.lastDx;
    }
    
    public int getLastDy(){
        return this.lastDy;
    }

    public boolean isAlive(){
        return this.isAlive;
    }

    public void die(){
        this.isAlive = false;
    }

    @Override
    public void breakIce(IceWall wall){
        wall.breakWall();
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

    public String getColor(){
        return this.color;
    }

    public int getSpeed(){
        return this.speed;
    }

}
