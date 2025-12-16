package com.duran_jimenez.baddopocream.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase Level
 */
@DisplayName("Pruebas de Level")
class LevelTest {
    
    private Level level;
    private IceCream player;
    
    @BeforeEach
    void setUp() {
        level = new Level(1, 10, 10);
        player = new IceCream("TestPlayer", "blue", new Location(5, 5));
        level.setPlayer(player);
        
        // Crear bordes
        for (int i = 0; i < 10; i++) {
            level.addWall(new Location(i, 0));
            level.addWall(new Location(i, 9));
            level.addWall(new Location(0, i));
            level.addWall(new Location(9, i));
        }
    }
    
    @Test
    @DisplayName("El nivel se crea con número correcto")
    void testCrearNivel() {
        assertEquals(1, level.getLevelNumber());
    }
    
    @Test
    @DisplayName("El mapa tiene dimensiones correctas")
    void testDimensionesMapa() {
        Map map = level.getMap();
        assertEquals(10, map.getWidth());
        assertEquals(10, map.getHeight());
    }
    
    @Test
    @DisplayName("Se puede agregar jugador")
    void testSetPlayer() {
        assertNotNull(level.getPlayer());
        assertEquals("TestPlayer", level.getPlayer().getName());
    }
    
    @Test
    @DisplayName("Se puede agregar segundo jugador")
    void testSetPlayer2() {
        IceCream player2 = new IceCream("Player2", "red", new Location(3, 3));
        level.setPlayer2(player2);
        
        assertTrue(level.hasTwoPlayers());
        assertNotNull(level.getPlayer2());
    }
    
    @Test
    @DisplayName("Se pueden agregar frutas")
    void testAgregarFrutas() {
        level.addFruit(new Banana(new Location(2, 2)));
        level.addFruit(new Cherry(new Location(3, 3)));
        
        assertEquals(2, level.getTotalFruits());
        assertEquals(0, level.getCollectedFruits());
    }
    
    @Test
    @DisplayName("Se pueden agregar enemigos")
    void testAgregarEnemigos() {
        level.addEnemy(new Maceta(new Location(7, 7)));
        level.addEnemy(new Troll(new Location(8, 8)));
        
        assertEquals(2, level.getEnemies().size());
    }
    
    @Test
    @DisplayName("movePlayer mueve al jugador correctamente")
    void testMovePlayer() {
        Location inicial = player.getLocation();
        boolean movido = level.movePlayer(0, 1); // Mover abajo
        
        assertTrue(movido);
        assertEquals(inicial.getY() + 1, player.getLocation().getY());
    }
    
    @Test
    @DisplayName("movePlayer no mueve a posición inválida")
    void testMovePlayerPosicionInvalida() {
        // Mover hacia la pared superior varias veces
        for (int i = 0; i < 10; i++) {
            level.movePlayer(0, -1);
        }
        
        // No debería estar en y=0 (pared)
        assertTrue(player.getLocation().getY() >= 1);
    }
    
    @Test
    @DisplayName("movePlayer no mueve jugador muerto")
    void testMovePlayerMuerto() {
        player.die();
        boolean movido = level.movePlayer(0, 1);
        
        assertFalse(movido);
    }
    
    @Test
    @DisplayName("Nivel no está completado inicialmente")
    void testNivelNoCompletado() {
        level.addFruit(new Banana(new Location(2, 2)));
        assertFalse(level.isCompleted());
    }
    
    @Test
    @DisplayName("Game over cuando jugador muere")
    void testGameOver() {
        assertFalse(level.isGameOver());
        player.die();
        assertTrue(level.isGameOver());
    }
    
    @Test
    @DisplayName("Se pueden crear paredes de hielo")
    void testCrearHielo() {
        Location icePos = new Location(6, 5);
        level.createIceWall(icePos);
        
        assertTrue(level.getMap().hasIceWall(icePos));
    }
    
    @Test
    @DisplayName("Se pueden romper paredes de hielo")
    void testRomperHielo() {
        Location icePos = new Location(6, 5);
        level.createIceWall(icePos);
        level.breakIceWall(icePos);
        
        assertFalse(level.getMap().hasIceWall(icePos));
    }
    
    @Test
    @DisplayName("Puntajes iniciales son cero")
    void testPuntajesIniciales() {
        assertEquals(0, level.getCurrentScore());
        assertEquals(0, level.getPlayer1Score());
        assertEquals(0, level.getPlayer2Score());
    }
    
    @Test
    @DisplayName("El nivel puede pausarse y reanudarse")
    void testPausarReanudar() {
        assertFalse(level.isPaused());
        
        level.pause();
        assertTrue(level.isPaused());
        
        level.resume();
        assertFalse(level.isPaused());
    }
    
    @Test
    @DisplayName("El tiempo restante es positivo al inicio")
    void testTiempoRestante() {
        int tiempo = level.getRemainingSeconds();
        assertTrue(tiempo > 0);
    }
}
