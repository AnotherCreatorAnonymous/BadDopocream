package com.duran_jimenez.baddopocream.domain;

import java.util.ArrayList;

/**
 * Representa el mapa de un nivel del juego.
 * 
 * Gestiona:
 * - Grid bidimensional con tipos de celda (vacío, pared, hielo)
 * - Obstáculos especiales (baldosas calientes, fogatas)
 * - Validación de posiciones para movimiento
 * 
 * Tipos de celda:
 * - EMPTY (0): Espacio transitable
 * - WALL (1): Pared sólida (intransitable)
 * - ICE (2): Pared de hielo (intransitable, puede romperse)
 * 
 * @author Durán-Jiménez
 */
public class Map {
    
    /** Celda vacía - transitable */
    public static final int EMPTY = 0;
    
    /** Pared sólida - intransitable */
    public static final int WALL = 1;
    
    /** Pared de hielo - intransitable, puede romperse */
    public static final int ICE = 2;
    
    private final int width;
    private final int height;
    private final int[][] grid;
    private final ArrayList<BaldosaCaliente> hotTiles;
    private final ArrayList<Fogata> campfires;
    
    /**
     * Crea un nuevo mapa con las dimensiones especificadas.
     * Todas las celdas se inicializan como vacías (EMPTY).
     */
    public Map(int width, int height){
        this.width = width;
        this.height = height;
        this.grid = new int[width][height];
        this.hotTiles = new ArrayList<>();
        this.campfires = new ArrayList<>();
        // El grid se inicializa automáticamente a 0 (EMPTY) en Java
    }
    
    public void addWall(Location location){
        if(isInBounds(location)){
            grid[location.getX()][location.getY()] = WALL;
        }
    }
    
    public boolean isWall(Location location){
        if(!isInBounds(location)) return false;
        return grid[location.getX()][location.getY()] == WALL;
    }
    
    public void addIceWall(Location location){
        if(isInBounds(location)){
            grid[location.getX()][location.getY()] = ICE;
        }
    }
    
    public boolean hasIceWall(Location location){
        if(!isInBounds(location)) return false;
        return grid[location.getX()][location.getY()] == ICE;
    }
    
    public boolean isIceWall(Location location){
        return hasIceWall(location);
    }
    
    public void removeIceWall(Location location){
        if(isInBounds(location)){
            grid[location.getX()][location.getY()] = EMPTY;
        }
    }
    
    public boolean isValidPosition(Location location){
        if(!isInBounds(location)) return false;
        return grid[location.getX()][location.getY()] == EMPTY;
    }
    
    private boolean isInBounds(Location location){
        int x = location.getX();
        int y = location.getY();
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    public int getCellType(Location location){
        if(!isInBounds(location)) return -1;
        return grid[location.getX()][location.getY()];
    }
    
    public int getWidth(){
        return this.width;
    }
    
    public int getHeight(){
        return this.height;
    }
    
    public void addHotTile(BaldosaCaliente hotTile){
        hotTiles.add(hotTile);
    }
    
    public void addCampfire(Fogata campfire){
        campfires.add(campfire);
    }
    
    public ArrayList<BaldosaCaliente> getHotTiles(){
        return hotTiles;
    }
    
    public ArrayList<Fogata> getCampfires(){
        return campfires;
    }
    
    public boolean isHotTile(Location location){
        for(BaldosaCaliente tile : hotTiles){
            if(tile.getLocation().equals(location)){
                return true;
            }
        }
        return false;
    }
    
    public Fogata getCampfireAt(Location location){
        for(Fogata campfire : campfires){
            if(campfire.getLocation().equals(location)){
                return campfire;
            }
        }
        return null;
    }
    
    /**
     * Obtiene la pared de hielo en una ubicación específica
     * Retorna un objeto IceWall temporal si existe hielo en esa posición
     */
    public IceWall getIceWallAt(Location location){
        if(hasIceWall(location)){
            return new IceWall(location);
        }
        return null;
    }
    
    /**
     * Obtiene el grid completo del mapa (para renderizado)
     */
    public int[][] getGrid(){
        return this.grid;
    }
}
