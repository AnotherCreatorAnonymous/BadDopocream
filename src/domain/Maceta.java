package domain;

public class Maceta extends Enemy{
    
    public Maceta(Location location){
        super("Maceta", location, 1);
    }

    @Override
    public void move(Location playerLocation){
        // La maceta se mueve hacia el jugador, pero solo en una dirección (sin diagonales)
        int dx = Integer.compare(playerLocation.getX(), this.location.getX());
        int dy = Integer.compare(playerLocation.getY(), this.location.getY());
        
        // Priorizar movimiento: primero horizontal, luego vertical
        // Solo se mueve en UNA dirección por turno
        if (dx != 0) {
            // Moverse horizontalmente
            this.location = this.location.move(dx, 0);
        } else if (dy != 0) {
            // Moverse verticalmente solo si ya está alineado horizontalmente
            this.location = this.location.move(0, dy);
        }
    }
    
    /**
     * Intenta moverse validando contra el mapa
     * Retorna true si logró moverse, false si no pudo
     */
    public boolean tryMove(Location playerLocation, Map map){
        // La maceta se mueve hacia el jugador, pero solo en una dirección (sin diagonales)
        int dx = Integer.compare(playerLocation.getX(), this.location.getX());
        int dy = Integer.compare(playerLocation.getY(), this.location.getY());
        
        // Intentar movimiento horizontal primero
        if (dx != 0) {
            Location newLoc = this.location.move(dx, 0);
            if(map.isValidPosition(newLoc)){
                this.location = newLoc;
                return true;
            }
        }
        
        // Si no pudo horizontal, intentar vertical
        if (dy != 0) {
            Location newLoc = this.location.move(0, dy);
            if(map.isValidPosition(newLoc)){
                this.location = newLoc;
                return true;
            }
        }
        
        return false; // No pudo moverse en ninguna dirección
    }

    @Override
    public boolean detectPlayer(Location playerLocation){
        return this.location.equals(playerLocation);
    }
    
    @Override
    public String getTypeName(){
        return "Maceta";
    }
}
