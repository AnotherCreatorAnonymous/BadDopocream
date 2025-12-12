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
    
    /**
     * Retorna el tipo de fruta para identificación (sin instanceof)
     * Ejemplo: "Cherry", "Cactus", "Banana", "Pineapple", "Grapes"
     */
    public abstract String getTypeName();

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
    
    // Métodos para comportamientos especiales (por defecto no hacen nada)
    
    /**
     * Indica si esta fruta requiere referencia al mapa
     */
    public boolean requiresMapReference(){
        return false;
    }
    
    /**
     * Configura el mapa (solo para frutas que lo necesiten)
     */
    public void setMap(Map map){
        // Por defecto no hace nada, solo Cherry y Pineapple lo sobrescriben
    }
    
    /**
     * Actualiza comportamiento especial de la fruta
     */
    public void update(){
        // Por defecto no hace nada, solo frutas especiales lo sobrescriben
    }
    
    /**
     * Verifica si esta fruta puede matar al jugador en la ubicación dada
     */
    public boolean shouldKillPlayer(Location playerLocation){
        return false; // Por defecto las frutas no matan
    }
    
    /**
     * Empuja la fruta (solo Pineapple)
     */
    public void pushByPlayer(int dx, int dy, Location playerLocation){
        // Por defecto no hace nada
    }
    
    /**
     * Verifica si tiene pinchos (solo Cactus)
     */
    public boolean hasSpikes(){
        return false;
    }
    
    /**
     * Obtiene segundos hasta próximo pincho (solo Cactus)
     */
    public int getSecondsUntilNextSpike(){
        return 0;
    }
}
