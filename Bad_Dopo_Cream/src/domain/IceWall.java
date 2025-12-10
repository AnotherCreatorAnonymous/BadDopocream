package domain;

public class IceWall {
    
    private final Location location;
    private boolean isBroken;

    public IceWall(Location location){
        this.location = location;
        this.isBroken = false;
    }

    public void breakWall(){
        this.isBroken = true;
    }

    public boolean isBroken(){
        return this.isBroken;
    }

    public Location getLocation(){
        return this.location;
    }

    public boolean isAt(Location location){
        return this.location.equals(location);
    }
}
