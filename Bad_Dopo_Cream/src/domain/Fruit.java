package domain;

public abstract class Fruit {
    protected String name;
    protected int points;
    protected Location location;
    protected boolean collected;
    protected boolean canMove;

    protected Fruit(String name, int points, Location location, boolean canMove){
        this.name = name;
        this.points = points;
        this.location = location;
        this.collected = false;
        this.canMove = canMove;
    }

    public abstract int collect();

    public void move() {/* Por defecto no se mueve, las subclases móviles lo sobrescriben*/}

    public boolean canMove() {
        return this.canMove;
    }

    public boolean isCollected(){
        return this.collected;
    }

    protected void setCollecter(boolean collected){
        this.collected = collected;
    }

    public String getName(){
        return this.name;
    }

    public int getPoint(){
        return this.points;
    }

    public Location getLocation(){
        return this.location;
    }

    public void setLocation(Location location){
        this.location = location;
    }
}
