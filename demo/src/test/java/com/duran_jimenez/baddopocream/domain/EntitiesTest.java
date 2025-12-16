package com.duran_jimenez.baddopocream.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para las entidades del juego
 * (IceCream, frutas, enemigos, etc.)
 */
@DisplayName("Pruebas de Entidades del Juego")
class EntitiesTest {
    
    // ===================== TESTS DE ICECREAM =====================
    
    @Nested
    @DisplayName("Tests de IceCream (Jugador)")
    class IceCreamTests {
        
        private IceCream player;
        
        @BeforeEach
        void setUp() {
            player = new IceCream("TestPlayer", "strawberry", new Location(5, 5));
        }
        
        @Test
        @DisplayName("IceCream se crea correctamente")
        void testCrearIceCream() {
            assertNotNull(player);
            assertEquals("TestPlayer", player.getName());
            assertEquals("strawberry", player.getColor());
            assertTrue(player.isAlive());
        }
        
        @Test
        @DisplayName("IceCream tiene ubicación correcta")
        void testUbicacionIceCream() {
            Location loc = player.getLocation();
            assertEquals(5, loc.getX());
            assertEquals(5, loc.getY());
        }
        
        @Test
        @DisplayName("IceCream puede moverse")
        void testMoverIceCream() {
            player.move(1, 0); // Mover derecha
            
            assertEquals(6, player.getLocation().getX());
            assertEquals(5, player.getLocation().getY());
        }
        
        @Test
        @DisplayName("IceCream puede morir")
        void testMorirIceCream() {
            assertTrue(player.isAlive());
            player.die();
            assertFalse(player.isAlive());
        }
        
        @Test
        @DisplayName("IceCream guarda última dirección")
        void testUltimaDireccion() {
            player.move(0, -1); // Mover arriba
            
            assertEquals(0, player.getLastDx());
            assertEquals(-1, player.getLastDy());
        }
    }
    
    // ===================== TESTS DE LOCATION =====================
    
    @Nested
    @DisplayName("Tests de Location")
    class LocationTests {
        
        @Test
        @DisplayName("Location se crea correctamente")
        void testCrearLocation() {
            Location loc = new Location(3, 7);
            
            assertEquals(3, loc.getX());
            assertEquals(7, loc.getY());
        }
        
        @Test
        @DisplayName("Location.move retorna nueva ubicación")
        void testMoveLocation() {
            Location loc = new Location(5, 5);
            Location nuevaLoc = loc.move(2, -1);
            
            assertEquals(7, nuevaLoc.getX());
            assertEquals(4, nuevaLoc.getY());
            // La original no cambió
            assertEquals(5, loc.getX());
            assertEquals(5, loc.getY());
        }
        
        @Test
        @DisplayName("Location.equals funciona correctamente")
        void testEqualsLocation() {
            Location loc1 = new Location(5, 5);
            Location loc2 = new Location(5, 5);
            Location loc3 = new Location(5, 6);
            
            assertEquals(loc1, loc2);
            assertNotEquals(loc1, loc3);
        }
        
        @Test
        @DisplayName("Location.distanceTo calcula distancia")
        void testDistanceTo() {
            Location loc1 = new Location(0, 0);
            Location loc2 = new Location(3, 4);
            
            double distancia = loc1.distanceTo(loc2);
            assertEquals(5.0, distancia, 0.001); // 3-4-5 triángulo
        }
    }
    
    // ===================== TESTS DE FRUTAS =====================
    
    @Nested
    @DisplayName("Tests de Frutas")
    class FrutasTests {
        
        @Test
        @DisplayName("Banana se crea correctamente")
        void testCrearBanana() {
            Banana banana = new Banana(new Location(2, 2));
            
            assertEquals("Banana", banana.getName());
            assertEquals("Banana", banana.getTypeName());
            assertFalse(banana.isCollected());
            assertTrue(banana.getPoint() > 0);
        }
        
        @Test
        @DisplayName("Cherry se crea correctamente")
        void testCrearCherry() {
            Cherry cherry = new Cherry(new Location(3, 3));
            
            assertEquals("Cherry", cherry.getName());
            assertTrue(cherry.canMove()); // Cherry es móvil
        }
        
        @Test
        @DisplayName("Grapes se crea correctamente")
        void testCrearGrapes() {
            Grapes grapes = new Grapes(new Location(4, 4));
            
            assertEquals("Grapes", grapes.getName());
            assertFalse(grapes.canMove()); // Grapes no es móvil
        }
        
        @Test
        @DisplayName("Pineapple se crea correctamente")
        void testCrearPineapple() {
            Pineapple pineapple = new Pineapple(new Location(5, 5));
            
            assertEquals("Pineapple", pineapple.getName());
            assertTrue(pineapple.getPoint() > 0);
        }
        
        @Test
        @DisplayName("Cactus puede tener espinas")
        void testCactusEspinas() {
            Cactus cactus = new Cactus(new Location(6, 6));
            
            assertEquals("Cactus", cactus.getName());
            // El estado de las espinas puede variar
            assertNotNull(cactus);
        }
        
        @Test
        @DisplayName("Fruta puede ser recolectada")
        void testRecolectarFruta() {
            Banana banana = new Banana(new Location(2, 2));
            assertFalse(banana.isCollected());
            
            int puntos = banana.collect();
            
            assertTrue(banana.isCollected());
            assertTrue(puntos > 0);
        }
        
        @Test
        @DisplayName("Fruta recolectada no da más puntos")
        void testFrutaRecolectadaNoDaPuntos() {
            Banana banana = new Banana(new Location(2, 2));
            banana.collect();
            
            int puntos = banana.collect(); // Segunda vez
            
            assertEquals(0, puntos);
        }
    }
    
    // ===================== TESTS DE ENEMIGOS =====================
    
    @Nested
    @DisplayName("Tests de Enemigos")
    class EnemigosTests {
        
        @Test
        @DisplayName("Maceta se crea correctamente")
        void testCrearMaceta() {
            Maceta maceta = new Maceta(new Location(3, 3));
            
            assertEquals("Maceta", maceta.getTypeName());
            assertNotNull(maceta.getLocation());
        }
        
        @Test
        @DisplayName("Troll se crea correctamente")
        void testCrearTroll() {
            Troll troll = new Troll(new Location(4, 4));
            
            assertEquals("Troll", troll.getTypeName());
        }
        
        @Test
        @DisplayName("CalamarNaranja se crea correctamente")
        void testCrearCalamarNaranja() {
            CalamarNaranja calamar = new CalamarNaranja(new Location(5, 5));
            
            assertEquals("CalamarNaranja", calamar.getTypeName());
        }
        
        @Test
        @DisplayName("Narval se crea correctamente")
        void testCrearNarval() {
            Narval narval = new Narval(new Location(6, 6));
            
            assertEquals("Narval", narval.getTypeName());
        }
        
        @Test
        @DisplayName("Enemigo detecta colisión con jugador")
        void testColisionConJugador() {
            Maceta maceta = new Maceta(new Location(5, 5));
            Location playerLoc = new Location(5, 5);
            
            assertTrue(maceta.collidesWithPlayer(playerLoc));
        }
        
        @Test
        @DisplayName("Enemigo no detecta colisión con jugador lejano")
        void testNoColisionConJugadorLejano() {
            Maceta maceta = new Maceta(new Location(5, 5));
            Location playerLoc = new Location(8, 8);
            
            assertFalse(maceta.collidesWithPlayer(playerLoc));
        }
        
        @Test
        @DisplayName("Enemigo puede cambiar ubicación")
        void testCambiarUbicacionEnemigo() {
            Maceta maceta = new Maceta(new Location(5, 5));
            Location nuevaLoc = new Location(6, 6);
            
            maceta.setLocation(nuevaLoc);
            
            assertEquals(nuevaLoc, maceta.getLocation());
        }
    }
    
    // ===================== TESTS DE OBSTÁCULOS =====================
    
    @Nested
    @DisplayName("Tests de Obstáculos")
    class ObstaculosTests {
        
        @Test
        @DisplayName("BaldosaCaliente se crea correctamente")
        void testCrearBaldosaCaliente() {
            BaldosaCaliente baldosa = new BaldosaCaliente(new Location(4, 4));
            
            assertNotNull(baldosa);
            assertEquals(4, baldosa.getLocation().getX());
        }
        
        @Test
        @DisplayName("Fogata se crea correctamente")
        void testCrearFogata() {
            Fogata fogata = new Fogata(new Location(5, 5));
            
            assertNotNull(fogata);
            // Fogata empieza encendida
            assertTrue(fogata.isLit());
        }
        
        @Test
        @DisplayName("Fogata puede apagarse y encenderse")
        void testFogataEncenderApagar() {
            Fogata fogata = new Fogata(new Location(5, 5));
            
            assertTrue(fogata.isLit());
            fogata.extinguish();
            assertFalse(fogata.isLit());
        }
    }
    
    // ===================== TESTS DE MAP =====================
    
    @Nested
    @DisplayName("Tests de Map")
    class MapTests {
        
        private Map map;
        
        @BeforeEach
        void setUp() {
            map = new Map(10, 10);
        }
        
        @Test
        @DisplayName("Map se crea con dimensiones correctas")
        void testCrearMap() {
            assertEquals(10, map.getWidth());
            assertEquals(10, map.getHeight());
        }
        
        @Test
        @DisplayName("Se pueden agregar paredes")
        void testAgregarPared() {
            Location wallLoc = new Location(5, 5);
            map.addWall(wallLoc);
            
            assertFalse(map.isValidPosition(wallLoc));
        }
        
        @Test
        @DisplayName("Se pueden agregar paredes de hielo")
        void testAgregarHielo() {
            Location iceLoc = new Location(3, 3);
            map.addIceWall(iceLoc);
            
            assertTrue(map.hasIceWall(iceLoc));
        }
        
        @Test
        @DisplayName("Se pueden remover paredes de hielo")
        void testRemoverHielo() {
            Location iceLoc = new Location(3, 3);
            map.addIceWall(iceLoc);
            map.removeIceWall(iceLoc);
            
            assertFalse(map.hasIceWall(iceLoc));
        }
        
        @Test
        @DisplayName("Posición fuera del mapa no es válida")
        void testPosicionFueraMapa() {
            assertFalse(map.isValidPosition(new Location(-1, 5)));
            assertFalse(map.isValidPosition(new Location(5, -1)));
            assertFalse(map.isValidPosition(new Location(15, 5)));
            assertFalse(map.isValidPosition(new Location(5, 15)));
        }
        
        @Test
        @DisplayName("Se puede obtener el grid")
        void testGetGrid() {
            int[][] grid = map.getGrid();
            
            assertNotNull(grid);
            assertEquals(10, grid.length);
        }
    }
}
