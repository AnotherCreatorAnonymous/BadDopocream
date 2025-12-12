package domain;

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

    public double distanceTo(Location other){
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean isAbjacent(Location other){
        int dx = Math.abs(this.x - other.x);
        int dy = Math.abs(this.y - other.y);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

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
}
