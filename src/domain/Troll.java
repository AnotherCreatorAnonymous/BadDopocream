package domain;

public class Troll extends Enemy{
    
    private static final double DETECTION_RANGE = 5;
    private int directionX;
    private int directionY;
    
    public Troll(Location location){
        super("Troll", location, 1);
        // Dirección inicial (puede ser aleatoria o predefinida)
        this.directionX = 1;
        this.directionY = 0;
    }

    @Override
    public void move(Location playerLocation){
        // El troll se mueve en su dirección actual
        Location newLocation = this.location.move(directionX, directionY);
        
        // Aquí se debe verificar si la nueva posición es válida (no hay pared/hielo)
        // Por ahora, asumimos que el movimiento es válido
        this.location = newLocation;
    }
    
    /**
     * Intenta moverse validando contra el mapa
     * Retorna true si logró moverse, false si no pudo
     */
    public boolean tryMove(Location playerLocation, Map map){
        // Verificar si puede moverse en la dirección actual
        Location newLocation = this.location.move(directionX, directionY);
        
        if(map.isValidPosition(newLocation)){
            this.location = newLocation;
            return true;
        }
        
        // Si no puede, cambiar dirección
        changeDirectionOnCollision(map);
        
        // Intentar moverse en la nueva dirección
        newLocation = this.location.move(directionX, directionY);
        if(map.isValidPosition(newLocation)){
            this.location = newLocation;
            return true;
        }
        
        return false; // No pudo moverse
    }
    
    /**
     * Cambia de dirección cuando encuentra un obstáculo
     */
    private void changeDirectionOnCollision(Map map){
        Location currentLoc = this.location;
        
        // Verificar qué direcciones son válidas
        boolean canMoveUp = map.isValidPosition(currentLoc.move(0, -1));
        boolean canMoveDown = map.isValidPosition(currentLoc.move(0, 1));
        boolean canMoveLeft = map.isValidPosition(currentLoc.move(-1, 0));
        boolean canMoveRight = map.isValidPosition(currentLoc.move(1, 0));
        
        // Cambiar a una dirección perpendicular preferentemente
        if(directionX != 0){
            // Estaba moviéndose horizontalmente, intenta vertical
            if(canMoveUp){
                directionX = 0;
                directionY = -1;
            } else if(canMoveDown){
                directionX = 0;
                directionY = 1;
            } else if(canMoveLeft){
                directionX = -1;
                directionY = 0;
            } else if(canMoveRight){
                directionX = 1;
                directionY = 0;
            } else {
                // Atrapado, se queda quieto
                directionX = 0;
                directionY = 0;
            }
        } else {
            // Estaba moviéndose verticalmente, intenta horizontal
            if(canMoveRight){
                directionX = 1;
                directionY = 0;
            } else if(canMoveLeft){
                directionX = -1;
                directionY = 0;
            } else if(canMoveUp){
                directionX = 0;
                directionY = -1;
            } else if(canMoveDown){
                directionX = 0;
                directionY = 1;
            } else {
                // Atrapado, se queda quieto
                directionX = 0;
                directionY = 0;
            }
        }
    }
    
    /**
     * Cambia de dirección cuando colisiona con otro enemigo
     */
    public void changeDirectionOnEnemyCollision(Map map){
        changeDirectionOnCollision(map);
    }
    
    
    public void changeDirection(boolean canMoveUp, boolean canMoveDown, boolean canMoveLeft, boolean canMoveRight){
        // Si no puede moverse en la dirección actual, busca una nueva dirección
        if((directionX == 1 && !canMoveRight) || (directionX == -1 && !canMoveLeft) || (directionY == 1 && !canMoveDown) || (directionY == -1 && !canMoveUp)){
            // Intenta cambiar a una dirección perpendicular
            if(directionX != 0){
                // Estaba moviéndose horizontalmente, intenta vertical
                if(canMoveUp){
                    directionX = 0;
                    directionY = -1;
                } else if(canMoveDown){
                    directionX = 0;
                    directionY = 1;
                } else {
                    // No puede moverse, se queda quieto
                    directionX = 0;
                    directionY = 0;
                }
            } else {
                // Estaba moviéndose verticalmente, intenta horizontal
                if(canMoveRight){
                    directionX = 1;
                    directionY = 0;
                } else if(canMoveLeft){
                    directionX = -1;
                    directionY = 0;
                } else {
                    // No puede moverse, se queda quieto
                    directionX = 0;
                    directionY = 0;
                }
            }
        }
    }
    
    public int getDirectionX(){
        return directionX;
    }
    
    public int getDirectionY(){
        return directionY;
    }

    @Override
    public boolean detectPlayer(Location playerLocation){
        double distance = this.location.distanceTo(playerLocation);
        return distance <= DETECTION_RANGE;
    }
    
    @Override
    public String getTypeName(){
        return "Troll";
    }
}