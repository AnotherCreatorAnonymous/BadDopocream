package com.duran_jimenez.baddopocream.domain;

public class Grapes extends Fruit{
    
    private static final int GRAPES_POINTS = 50;

    public Grapes(Location location){
        super("Grapes", GRAPES_POINTS, location, false);
    }

    @Override
    public int collect(){
        if(!collected){
            collected = true;
            return GRAPES_POINTS;
        }
        return 0;
    }
    
    @Override
    public String getTypeName(){
        return "Grapes";
    }
}
