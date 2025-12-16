package com.duran_jimenez.baddopocream.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Algoritmo A* para pathfinding inteligente
 * Encuentra el camino más corto entre dos puntos evitando obstáculos
 */
public class PathFinder {
    
    private static class Node implements Comparable<Node> {
        Location location;
        Node parent;
        double gCost; // Distancia desde el inicio
        double hCost; // Heurística (distancia estimada al objetivo)
        double fCost; // gCost + hCost
        
        Node(Location location, Node parent, double gCost, double hCost) {
            this.location = location;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
        }
        
        @Override
        public int compareTo(Node other) {
            return Double.compare(this.fCost, other.fCost);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) return false;
            Node other = (Node) obj;
            return this.location.equals(other.location);
        }
        
        @Override
        public int hashCode() {
            return location.hashCode();
        }
    }
    
    /**
     * Encuentra el camino más corto usando A*
     * @param start Posición inicial
     * @param goal Posición objetivo
     * @param map Mapa del nivel
     * @param avoidLocations Localizaciones a evitar (enemigos, obstáculos peligrosos)
     * @return Lista de movimientos [dx, dy] o lista vacía si no hay camino
     */
    public static List<int[]> findPath(Location start, Location goal, Map map, List<Location> avoidLocations) {
        if (start.equals(goal)) {
            return new ArrayList<>();
        }
        
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Location> closedSet = new HashSet<>();
        Set<Location> avoidSet = avoidLocations != null ? new HashSet<>(avoidLocations) : new HashSet<>();
        
        openSet.add(new Node(start, null, 0, heuristic(start, goal)));
        
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            
            if (current.location.equals(goal)) {
                return reconstructPath(current);
            }
            
            closedSet.add(current.location);
            
            // Explorar vecinos (4 direcciones)
            for (int[] dir : new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}}) {
                Location neighbor = current.location.move(dir[0], dir[1]);
                
                if (closedSet.contains(neighbor)) continue;
                if (!map.isValidPosition(neighbor)) continue;
                if (avoidSet.contains(neighbor)) continue;
                
                double tentativeGCost = current.gCost + 1;
                
                Node neighborNode = new Node(neighbor, current, tentativeGCost, heuristic(neighbor, goal));
                
                // Si ya está en openSet con mejor costo, no lo agregamos
                boolean shouldAdd = true;
                for (Node node : openSet) {
                    if (node.location.equals(neighbor) && node.gCost <= tentativeGCost) {
                        shouldAdd = false;
                        break;
                    }
                }
                
                if (shouldAdd) {
                    openSet.add(neighborNode);
                }
            }
        }
        
        return new ArrayList<>(); // No se encontró camino
    }
    
    /**
     * Encuentra el camino más corto considerando que puede romper hielo
     * @param start Posición inicial
     * @param goal Posición objetivo
     * @param map Mapa del nivel
     * @param level Nivel para detectar hielo
     * @param avoidLocations Localizaciones a evitar
     * @return Lista de movimientos [dx, dy, useIce] donde useIce=1 significa romper hielo
     */
    public static List<int[]> findPathWithIceBreaking(Location start, Location goal, Map map, 
                                                       Level level, List<Location> avoidLocations) {
        if (start.equals(goal)) {
            return new ArrayList<>();
        }
        
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Location> closedSet = new HashSet<>();
        Set<Location> avoidSet = avoidLocations != null ? new HashSet<>(avoidLocations) : new HashSet<>();
        
        openSet.add(new Node(start, null, 0, heuristic(start, goal)));
        
        HashMap<Location, Boolean> iceBreakMap = new HashMap<>();
        
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            
            if (current.location.equals(goal)) {
                return reconstructPathWithIce(current, iceBreakMap);
            }
            
            closedSet.add(current.location);
            
            // Explorar vecinos (4 direcciones)
            for (int[] dir : new int[][]{{0, 1}, {0, -1}, {1, 0}, {-1, 0}}) {
                Location neighbor = current.location.move(dir[0], dir[1]);
                
                if (closedSet.contains(neighbor)) continue;
                if (avoidSet.contains(neighbor)) continue;
                
                boolean isIce = false;
                double moveCost = 1;
                
                // Verificar si es hielo (costo mayor para romperlo)
                if (!map.isValidPosition(neighbor)) {
                    IceWall ice = level.getIceWallAt(neighbor);
                    if (ice != null) {
                        isIce = true;
                        moveCost = 3; // Costo mayor para romper hielo
                    } else {
                        continue; // Es pared sólida, no se puede pasar
                    }
                }
                
                double tentativeGCost = current.gCost + moveCost;
                
                Node neighborNode = new Node(neighbor, current, tentativeGCost, heuristic(neighbor, goal));
                
                if (isIce) {
                    iceBreakMap.put(neighbor, true);
                }
                
                boolean shouldAdd = true;
                for (Node node : openSet) {
                    if (node.location.equals(neighbor) && node.gCost <= tentativeGCost) {
                        shouldAdd = false;
                        break;
                    }
                }
                
                if (shouldAdd) {
                    openSet.add(neighborNode);
                }
            }
        }
        
        return new ArrayList<>();
    }
    
    /**
     * Encuentra la dirección general hacia un objetivo (sin pathfinding completo)
     * Útil para enemigos simples
     */
    public static int[] getDirectionTowards(Location from, Location to, Map map, List<Location> avoid) {
        int dx = Integer.compare(to.getX(), from.getX());
        int dy = Integer.compare(to.getY(), from.getY());
        
        Set<Location> avoidSet = avoid != null ? new HashSet<>(avoid) : new HashSet<>();
        
        // Intentar movimiento hacia el objetivo
        if (dx != 0) {
            Location next = from.move(dx, 0);
            if (map.isValidPosition(next) && !avoidSet.contains(next)) {
                return new int[]{dx, 0};
            }
        }
        
        if (dy != 0) {
            Location next = from.move(0, dy);
            if (map.isValidPosition(next) && !avoidSet.contains(next)) {
                return new int[]{0, dy};
            }
        }
        
        // Intentar direcciones alternativas
        if (dx != 0) {
            Location next = from.move(dx, 0);
            if (map.isValidPosition(next)) {
                return new int[]{dx, 0};
            }
        }
        
        if (dy != 0) {
            Location next = from.move(0, dy);
            if (map.isValidPosition(next)) {
                return new int[]{0, dy};
            }
        }
        
        return new int[]{0, 0};
    }
    
    /**
     * Calcula la distancia Manhattan (heurística para A*)
     */
    private static double heuristic(Location a, Location b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }
    
    /**
     * Reconstruye el camino desde el nodo final
     */
    private static List<int[]> reconstructPath(Node end) {
        List<int[]> path = new ArrayList<>();
        Node current = end;
        
        while (current.parent != null) {
            Location from = current.parent.location;
            Location to = current.location;
            int dx = to.getX() - from.getX();
            int dy = to.getY() - from.getY();
            path.add(0, new int[]{dx, dy});
            current = current.parent;
        }
        
        return path;
    }
    
    /**
     * Reconstruye el camino incluyendo información de romper hielo
     */
    private static List<int[]> reconstructPathWithIce(Node end, HashMap<Location, Boolean> iceBreakMap) {
        List<int[]> path = new ArrayList<>();
        Node current = end;
        
        while (current.parent != null) {
            Location from = current.parent.location;
            Location to = current.location;
            int dx = to.getX() - from.getX();
            int dy = to.getY() - from.getY();
            int useIce = iceBreakMap.containsKey(to) && iceBreakMap.get(to) ? 1 : 0;
            path.add(0, new int[]{dx, dy, useIce});
            current = current.parent;
        }
        
        return path;
    }
}
