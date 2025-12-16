package com.duran_jimenez.baddopocream.domain;

public abstract class Calamar extends Enemy implements IceBreaker {
    
    protected int detectionRange;

    protected Calamar(String name, Location location, int speed, int detectionRange){
        super(name, location, speed);
        this.detectionRange = detectionRange;
    }

    @Override
    public void breakIce(IceWall wall){
        // El calamar puede romper hielo si está adyacente
        if(this.location.isAbjacent(wall.getLocation())){
            wall.breakWall();
        }
    }

    @Override
    public boolean detectPlayer(Location playerLocation){
        double distance = this.location.distanceTo(playerLocation);
        return distance <= this.detectionRange;
    }
    
    @Override
    public String getTypeName(){
        return "Calamar"; // Las subclases sobrescriben este método
    }
}
