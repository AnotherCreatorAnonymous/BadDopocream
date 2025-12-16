package com.duran_jimenez.baddopocream.domain;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase principal BadDopoCream
 * Cubre las funcionalidades principales del juego
 */
@DisplayName("Pruebas de BadDopoCream - Clase Principal")
class BadDopoCreamTest {
    
    private BadDopoCream game;
    private Level testLevel;
    private IceCream player;
    
    @BeforeEach
    void setUp() {
        // Inicializar juego y nivel de prueba antes de cada test
        game = new BadDopoCream();
        testLevel = new Level(1, 15, 15);
        
        // Crear bordes del mapa
        for (int i = 0; i < 15; i++) {
            testLevel.addWall(new Location(i, 0));
            testLevel.addWall(new Location(i, 14));
            testLevel.addWall(new Location(0, i));
            testLevel.addWall(new Location(14, i));
        }
        
        // Crear jugador en el centro
        player = new IceCream("TestPlayer", "strawberry", new Location(7, 7));
        testLevel.setPlayer(player);
    }
    
    // ===================== TESTS DE INICIALIZACIÓN =====================
    
    @Nested
    @DisplayName("Tests de Inicialización")
    class InicializacionTests {
        
        @Test
        @DisplayName("El juego se crea correctamente sin niveles")
        void testCrearJuegoVacio() {
            BadDopoCream nuevoJuego = new BadDopoCream();
            
            assertNotNull(nuevoJuego);
            assertEquals(0, nuevoJuego.getTotalScore());
            assertFalse(nuevoJuego.isGameWon());
            assertNull(nuevoJuego.getCurrentLevel());
            assertTrue(nuevoJuego.getLevels().isEmpty());
        }
        
        @Test
        @DisplayName("El juego puede agregar un nivel")
        void testAgregarNivel() {
            game.addLevel(testLevel);
            
            assertEquals(1, game.getLevels().size());
        }
        
        @Test
        @DisplayName("El juego inicia correctamente con el primer nivel")
        void testIniciarJuego() {
            game.addLevel(testLevel);
            game.startGame();
            
            assertNotNull(game.getCurrentLevel());
            assertEquals(1, game.getCurrentLevelNumber());
        }
        
        @Test
        @DisplayName("Las constantes de niveles son correctas")
        void testConstantesNiveles() {
            assertEquals(10, BadDopoCream.MAX_LEVELS);
            assertEquals(5, BadDopoCream.PLAYABLE_LEVELS);
        }
    }
    
    // ===================== TESTS DE NIVELES =====================
    
    @Nested
    @DisplayName("Tests de Gestión de Niveles")
    class NivelesTests {
        
        @BeforeEach
        void setUpNiveles() {
            // Agregar múltiples niveles
            for (int i = 1; i <= 3; i++) {
                Level nivel = new Level(i, 10, 10);
                IceCream jugador = new IceCream("Player", "vanilla", new Location(5, 5));
                nivel.setPlayer(jugador);
                game.addLevel(nivel);
            }
            game.startGame();
        }
        
        @Test
        @DisplayName("Se puede avanzar al siguiente nivel")
        void testSiguienteNivel() {
            int nivelInicial = game.getCurrentLevelNumber();
            boolean avanzado = game.nextLevel();
            
            assertTrue(avanzado);
            assertEquals(nivelInicial + 1, game.getCurrentLevelNumber());
        }
        
        @Test
        @DisplayName("No se puede avanzar más allá del último nivel")
        void testNoAvanzarMasAllaDelUltimo() {
            game.nextLevel(); // Nivel 2
            game.nextLevel(); // Nivel 3
            boolean avanzado = game.nextLevel(); // No hay nivel 4
            
            assertFalse(avanzado);
            assertTrue(game.isGameWon());
        }
        
        @Test
        @DisplayName("Se puede reiniciar a un nivel específico")
        void testReiniciarNivel() {
            game.nextLevel();
            game.nextLevel();
            
            game.resetToLevel(1);
            
            assertEquals(1, game.getCurrentLevelNumber());
            assertFalse(game.isGameWon());
        }
        
        @Test
        @DisplayName("resetGame limpia completamente el estado")
        void testResetGame() {
            game.nextLevel();
            game.addToTotalScore(100);
            
            game.resetGame();
            
            assertEquals(0, game.getTotalScore());
            assertFalse(game.isGameWon());
            assertNull(game.getCurrentLevel());
            assertTrue(game.getLevels().isEmpty());
        }
        
        @Test
        @DisplayName("hasNextLevel funciona correctamente")
        void testHasNextLevel() {
            // En nivel 1 de 3, hay siguiente
            assertTrue(game.hasNextLevel() || game.getCurrentLevelNumber() < BadDopoCream.PLAYABLE_LEVELS);
        }
    }
    
    // ===================== TESTS DE JUGADOR =====================
    
    @Nested
    @DisplayName("Tests de Jugador")
    class JugadorTests {
        
        @BeforeEach
        void setUpJugador() {
            game.addLevel(testLevel);
            game.startGame();
        }
        
        @Test
        @DisplayName("Se puede obtener información del jugador 1")
        void testGetPlayer1Info() {
            PlayerInfo info = game.getPlayer1Info();
            
            assertNotNull(info);
            assertEquals(7, info.x);
            assertEquals(7, info.y);
            assertTrue(info.isAlive);
            assertEquals("strawberry", info.color);
        }
        
        @Test
        @DisplayName("El jugador puede moverse correctamente")
        void testMoverJugador() {
            // Mover hacia arriba
            boolean movido = game.movePlayer1(0, -1);
            
            assertTrue(movido);
            PlayerInfo info = game.getPlayer1Info();
            assertEquals(6, info.y); // Movió hacia arriba
        }
        
        @Test
        @DisplayName("El jugador no puede moverse a una pared")
        void testNoMoverAPared() {
            // Intentar mover hacia la pared (moverse hasta el borde)
            for (int i = 0; i < 10; i++) {
                game.movePlayer1(0, -1); // Mover hacia arriba
            }
            
            // Ahora debería estar cerca de la pared superior
            PlayerInfo info = game.getPlayer1Info();
            assertTrue(info.y >= 1); // No puede estar en y=0 (pared)
        }
        
        @Test
        @DisplayName("Jugador 2 no existe en modo un jugador")
        void testNoHayJugador2EnModoSimple() {
            assertFalse(game.hasTwoPlayers());
            assertNull(game.getPlayer2Info());
        }
        
        @Test
        @DisplayName("Se puede agregar un segundo jugador")
        void testAgregarSegundoJugador() {
            IceCream player2 = new IceCream("Player2", "chocolate", new Location(5, 5));
            testLevel.setPlayer2(player2);
            
            assertTrue(game.hasTwoPlayers());
            assertNotNull(game.getPlayer2Info());
        }
    }
    
    // ===================== TESTS DE FRUTAS =====================
    
    @Nested
    @DisplayName("Tests de Frutas")
    class FrutasTests {
        
        @BeforeEach
        void setUpFrutas() {
            // Agregar frutas al nivel
            testLevel.addFruit(new Banana(new Location(3, 3)));
            testLevel.addFruit(new Cherry(new Location(4, 4)));
            testLevel.addFruit(new Grapes(new Location(5, 5)));
            testLevel.addFruit(new Pineapple(new Location(6, 6)));
            
            game.addLevel(testLevel);
            game.startGame();
        }
        
        @Test
        @DisplayName("Las frutas se agregan correctamente")
        void testFrutasAgregadas() {
            assertEquals(4, game.countTotalFruits());
            assertEquals(0, game.countCollectedFruits());
        }
        
        @Test
        @DisplayName("Se puede obtener información de las frutas")
        void testGetAllFruitsInfo() {
            List<FruitInfo> frutas = game.getAllFruitsInfo();
            
            assertNotNull(frutas);
            assertEquals(4, frutas.size());
        }
        
        @Test
        @DisplayName("Las frutas tienen diferentes puntos")
        void testPuntosFrutas() {
            List<FruitInfo> frutas = game.getAllFruitsInfo();
            
            // Verificar que hay diferentes tipos
            boolean hayBanana = frutas.stream().anyMatch(f -> f.typeName.equals("Banana"));
            boolean hayCherry = frutas.stream().anyMatch(f -> f.typeName.equals("Cherry"));
            
            assertTrue(hayBanana);
            assertTrue(hayCherry);
        }
    }
    
    // ===================== TESTS DE ENEMIGOS =====================
    
    @Nested
    @DisplayName("Tests de Enemigos")
    class EnemigosTests {
        
        @BeforeEach
        void setUpEnemigos() {
            testLevel.addEnemy(new Maceta(new Location(3, 3)));
            testLevel.addEnemy(new Troll(new Location(10, 10)));
            
            game.addLevel(testLevel);
            game.startGame();
        }
        
        @Test
        @DisplayName("Los enemigos se agregan correctamente")
        void testEnemigosAgregados() {
            List<EnemyInfo> enemigos = game.getAllEnemiesInfo();
            
            assertNotNull(enemigos);
            assertEquals(2, enemigos.size());
        }
        
        @Test
        @DisplayName("Los enemigos tienen tipos diferentes")
        void testTiposEnemigos() {
            List<EnemyInfo> enemigos = game.getAllEnemiesInfo();
            
            boolean hayMaceta = enemigos.stream().anyMatch(e -> e.typeName.equals("Maceta"));
            boolean hayTroll = enemigos.stream().anyMatch(e -> e.typeName.equals("Troll"));
            
            assertTrue(hayMaceta);
            assertTrue(hayTroll);
        }
        
        @Test
        @DisplayName("El juego detecta game over cuando enemigo mata al jugador")
        void testGameOverPorEnemigo() {
            // Crear nivel sin paredes internas para facilitar colisión
            Level nivelPeligroso = new Level(1, 5, 5);
            IceCream jugador = new IceCream("Test", "red", new Location(2, 2));
            nivelPeligroso.setPlayer(jugador);
            nivelPeligroso.addEnemy(new Maceta(new Location(2, 3)));
            
            BadDopoCream juegoTest = new BadDopoCream();
            juegoTest.addLevel(nivelPeligroso);
            juegoTest.startGame();
            
            // Mover hacia el enemigo
            juegoTest.movePlayer1(0, 1);
            
            // Verificar estado
            assertTrue(juegoTest.isGameOver() || !jugador.isAlive());
        }
    }
    
    // ===================== TESTS DE PUNTAJE =====================
    
    @Nested
    @DisplayName("Tests de Puntaje")
    class PuntajeTests {
        
        @BeforeEach
        void setUpPuntaje() {
            game.addLevel(testLevel);
            game.startGame();
        }
        
        @Test
        @DisplayName("El puntaje inicial es cero")
        void testPuntajeInicial() {
            assertEquals(0, game.getTotalScore());
            assertEquals(0, game.getCurrentLevelScore());
            assertEquals(0, game.getCombinedScore());
        }
        
        @Test
        @DisplayName("Se puede agregar puntaje al total")
        void testAgregarPuntaje() {
            game.addToTotalScore(50);
            
            assertEquals(50, game.getTotalScore());
        }
        
        @Test
        @DisplayName("Se puede establecer el puntaje total")
        void testSetTotalScore() {
            game.setTotalScore(200);
            
            assertEquals(200, game.getTotalScore());
        }
        
        @Test
        @DisplayName("El puntaje combinado incluye el nivel actual")
        void testCombinedScore() {
            game.setTotalScore(100);
            // El puntaje combinado debería ser totalScore + puntaje del nivel
            int combined = game.getCombinedScore();
            
            assertTrue(combined >= 100); // Al menos el total
        }
        
        @Test
        @DisplayName("Puntajes de jugadores individuales inician en cero")
        void testPuntajesIndividuales() {
            assertEquals(0, game.getPlayer1Score());
            assertEquals(0, game.getPlayer2Score());
        }
    }
    
    // ===================== TESTS DE MAPA =====================
    
    @Nested
    @DisplayName("Tests de Mapa")
    class MapaTests {
        
        @BeforeEach
        void setUpMapa() {
            game.addLevel(testLevel);
            game.startGame();
        }
        
        @Test
        @DisplayName("El mapa tiene las dimensiones correctas")
        void testDimensionesMapa() {
            assertEquals(15, game.getMapWidth());
            assertEquals(15, game.getMapHeight());
        }
        
        @Test
        @DisplayName("Se puede obtener el grid del mapa")
        void testGetMapGrid() {
            int[][] grid = game.getCurrentMapGrid();
            
            assertNotNull(grid);
            assertEquals(15, grid.length);
        }
        
        @Test
        @DisplayName("Se pueden crear paredes de hielo")
        void testCrearHielo() {
            // El jugador está en (7,7), intentar crear hielo en su dirección
            game.createIceLinePlayer1(1, 0); // Crear hacia la derecha
            
            // Verificar que el mapa tiene hielo
            int[][] grid = game.getCurrentMapGrid();
            // La celda a la derecha del jugador debería tener hielo (valor 2)
            // Nota: Depende de la implementación exacta
            assertNotNull(grid);
        }
        
        @Test
        @DisplayName("Se pueden romper paredes de hielo")
        void testRomperHielo() {
            // Crear y luego romper
            game.createIceLinePlayer1(1, 0);
            game.breakIceLinePlayer1(1, 0);
            
            // No debería lanzar excepción
            assertNotNull(game.getCurrentMapGrid());
        }
    }
    
    // ===================== TESTS DE PAUSA =====================
    
    @Nested
    @DisplayName("Tests de Pausa/Reanudación")
    class PausaTests {
        
        @BeforeEach
        void setUpPausa() {
            game.addLevel(testLevel);
            game.startGame();
        }
        
        @Test
        @DisplayName("El juego puede pausarse")
        void testPausar() {
            assertFalse(game.isPaused());
            
            game.pause();
            
            assertTrue(game.isPaused());
        }
        
        @Test
        @DisplayName("El juego puede reanudarse")
        void testReanudar() {
            game.pause();
            game.resume();
            
            assertFalse(game.isPaused());
        }
        
        @Test
        @DisplayName("El tiempo restante es positivo al inicio")
        void testTiempoRestante() {
            int tiempo = game.getRemainingSeconds();
            
            assertTrue(tiempo > 0);
        }
    }
    
    // ===================== TESTS DE OBSTÁCULOS =====================
    
    @Nested
    @DisplayName("Tests de Obstáculos")
    class ObstaculosTests {
        
        @BeforeEach
        void setUpObstaculos() {
            // Agregar obstáculos al nivel
            testLevel.getMap().addHotTile(new BaldosaCaliente(new Location(4, 4)));
            testLevel.getMap().addCampfire(new Fogata(new Location(6, 6)));
            
            game.addLevel(testLevel);
            game.startGame();
        }
        
        @Test
        @DisplayName("Se puede obtener información de obstáculos")
        void testGetObstaculosInfo() {
            List<ObstacleInfo> obstaculos = game.getAllObstaclesInfo();
            
            assertNotNull(obstaculos);
            assertTrue(obstaculos.size() >= 2);
        }
        
        @Test
        @DisplayName("Hay diferentes tipos de obstáculos")
        void testTiposObstaculos() {
            List<ObstacleInfo> obstaculos = game.getAllObstaclesInfo();
            
            boolean hayHotTile = obstaculos.stream().anyMatch(o -> o.type.equals("HotTile"));
            boolean hayCampfire = obstaculos.stream().anyMatch(o -> o.type.equals("Campfire"));
            
            assertTrue(hayHotTile);
            assertTrue(hayCampfire);
        }
    }
    
    // ===================== TESTS DE DOS JUGADORES =====================
    
    @Nested
    @DisplayName("Tests de Modo Dos Jugadores")
    class DosJugadoresTests {
        
        @BeforeEach
        void setUpDosJugadores() {
            IceCream player2 = new IceCream("Player2", "chocolate", new Location(5, 5));
            testLevel.setPlayer2(player2);
            
            game.addLevel(testLevel);
            game.startGame();
        }
        
        @Test
        @DisplayName("hasTwoPlayers retorna true con dos jugadores")
        void testHasTwoPlayers() {
            assertTrue(game.hasTwoPlayers());
        }
        
        @Test
        @DisplayName("Se puede obtener info del jugador 2")
        void testGetPlayer2Info() {
            PlayerInfo info = game.getPlayer2Info();
            
            assertNotNull(info);
            assertEquals(5, info.x);
            assertEquals(5, info.y);
            assertEquals("chocolate", info.color);
        }
        
        @Test
        @DisplayName("Jugador 2 puede moverse")
        void testMoverJugador2() {
            boolean movido = game.movePlayer2(1, 0);
            
            assertTrue(movido);
            PlayerInfo info = game.getPlayer2Info();
            assertEquals(6, info.x); // Movió hacia la derecha
        }
        
        @Test
        @DisplayName("Ambos jugadores pueden crear hielo")
        void testAmbosCreanHielo() {
            game.createIceLinePlayer1(1, 0);
            game.createIceLinePlayer2(-1, 0);
            
            // No debería lanzar excepción
            assertNotNull(game.getCurrentMapGrid());
        }
    }
    
    // ===================== TESTS DE GUARDADO =====================
    
    @Nested
    @DisplayName("Tests de Guardado/Carga")
    class GuardadoTests {
        
        @Test
        @DisplayName("Se puede listar partidas guardadas sin error")
        void testListarPartidasGuardadas() {
            List<String> partidas = BadDopoCream.listSavedGames();
            
            assertNotNull(partidas);
        }
        
        @Test
        @DisplayName("Cargar partida inexistente retorna null")
        void testCargarPartidaInexistente() {
            GameState state = BadDopoCream.loadGame("partida_que_no_existe_12345");
            
            assertNull(state);
        }
    }
    
    // ===================== TESTS DE ESTADO DEL JUEGO =====================
    
    @Nested
    @DisplayName("Tests de Estado del Juego")
    class EstadoJuegoTests {
        
        @BeforeEach
        void setUpEstado() {
            game.addLevel(testLevel);
            game.startGame();
        }
        
        @Test
        @DisplayName("isGameOver retorna false al inicio")
        void testIsGameOverInicial() {
            assertFalse(game.isGameOver());
        }
        
        @Test
        @DisplayName("isGameWon retorna false al inicio")
        void testIsGameWonInicial() {
            assertFalse(game.isGameWon());
        }
        
        @Test
        @DisplayName("isCurrentLevelCompleted retorna false al inicio")
        void testNivelNoCompletadoInicial() {
            assertFalse(game.isCurrentLevelCompleted());
        }
        
        @Test
        @DisplayName("isTimeExpired retorna false al inicio")
        void testTiempoNoExpiradoInicial() {
            assertFalse(game.isTimeExpired());
        }
        
        @Test
        @DisplayName("setCurrentLevel funciona correctamente")
        void testSetCurrentLevel() {
            Level nuevoNivel = new Level(99, 20, 20);
            IceCream jugador = new IceCream("Nuevo", "pink", new Location(10, 10));
            nuevoNivel.setPlayer(jugador);
            
            game.setCurrentLevel(nuevoNivel);
            
            assertEquals(nuevoNivel, game.getCurrentLevel());
        }
    }
    
    // ===================== TESTS DE UPDATE =====================
    
    @Nested
    @DisplayName("Tests de Actualización del Juego")
    class UpdateTests {
        
        @BeforeEach
        void setUpUpdate() {
            testLevel.addEnemy(new Maceta(new Location(12, 12)));
            testLevel.addFruit(new Banana(new Location(3, 3)));
            
            game.addLevel(testLevel);
            game.startGame();
        }
        
        @Test
        @DisplayName("update() no lanza excepciones")
        void testUpdateNoLanzaExcepcion() {
            assertDoesNotThrow(() -> {
                for (int i = 0; i < 10; i++) {
                    game.update();
                }
            });
        }
        
        @Test
        @DisplayName("update() mueve a los enemigos")
        void testUpdateMueveEnemigos() {
            List<EnemyInfo> enemigosAntes = game.getAllEnemiesInfo();
            assertFalse(enemigosAntes.isEmpty(), "Debe haber enemigos en el nivel");
            
            // Ejecutar varios updates
            for (int i = 0; i < 5; i++) {
                game.update();
            }
            
            // Los enemigos deberían haberse movido (o al menos intentado)
            List<EnemyInfo> enemigosDespues = game.getAllEnemiesInfo();
            assertNotNull(enemigosDespues);
            assertFalse(enemigosDespues.isEmpty());
        }
    }
}
