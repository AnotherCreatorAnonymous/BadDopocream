package domain;

public class Banana extends Fruit{
    private static final int POINTS = 100;

    public Banana(Location location){
        super("Banana", POINTS, location, false);
    }

    @Override
    public int collect(){
        if(!collected){
            collected = true;
            return POINTS;
        }
        return 0;
    }
    
    @Override
    public String getTypeName(){
        return "Banana";
    }
}
