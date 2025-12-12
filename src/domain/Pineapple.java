package domain;

/**
 * Pineapple - Fruta que se mueve cuando el jugador se mueve
 * Se empuja en la dirección opuesta al movimiento del jugador
 */
public class Pineapple extends Fruit{
    
    private static final int PINEAPPLE_POINTS = 200;
    private Map map;

    public Pineapple(Location location){
        super("Pineapple", PINEAPPLE_POINTS, location, true);
        this.map = null;
    }
    
    /**
     * Establece la referencia al mapa
     */
    public void setMap(Map map) {
        this.map = map;
    }

    @Override
    public int collect(){
        if(!collected){
            collected = true;
            return PINEAPPLE_POINTS;
        }
        return 0;
    }
    
    /**
     * Mueve la piña cuando el jugador se mueve
     * Se empuja en la dirección del movimiento del jugador
     * @param playerDx Dirección X del movimiento del jugador
     * @param playerDy Dirección Y del movimiento del jugador
     * @param playerLocation Posición del jugador
     */
    public void pushByPlayer(int playerDx, int playerDy, Location playerLocation) {
        if (collected || map == null) return;
        
        // Solo empujar si el jugador está adyacente
        int dx = this.location.getX() - playerLocation.getX();
        int dy = this.location.getY() - playerLocation.getY();
        
        // Verificar si está en la dirección del movimiento del jugador
        boolean isInPath = (dx == playerDx && dy == playerDy) ||
                          (Math.abs(dx) <= 1 && Math.abs(dy) <= 1);
        
        if (!isInPath) return;
        
        // Intentar mover en la dirección del jugador
        Location newLocation = this.location.move(playerDx, playerDy);
        
        // Verificar que la nueva posición sea válida
        if (map.isValidPosition(newLocation)) {
            this.location = newLocation;
        }
    }
    
    @Override
    public String getTypeName(){
        return "Pineapple";
    }
    
    @Override
    public boolean requiresMapReference(){
        return true;
    }
    
}
