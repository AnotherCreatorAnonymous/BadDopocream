package com.duran_jimenez.baddopocream.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.duran_jimenez.baddopocream.domain.BadDopoCream;
import com.duran_jimenez.baddopocream.domain.EnemyInfo;
import com.duran_jimenez.baddopocream.domain.FruitInfo;
import com.duran_jimenez.baddopocream.domain.HighScoreManager;
import com.duran_jimenez.baddopocream.domain.IceCreamAI;
import com.duran_jimenez.baddopocream.domain.ObstacleInfo;
import com.duran_jimenez.baddopocream.domain.PlayerInfo;

/**
 * Pantalla principal del juego.
 * 
 * Responsabilidades:
 * - Renderizado del mapa, jugadores, frutas, enemigos y obstáculos
 * - Gestión de controles de teclado (flechas + WASD para 2 jugadores)
 * - Game loop y actualización del estado
 * - Diálogos de victoria, derrota y highscores
 * - Integración con IA para modos Machine-vs-Machine y PvsM
 * 
 * Modos soportados:
 * - Single-Player: Un jugador con controles de flechas
 * - Cooperative: Dos jugadores (flechas + WASD)
 * - Versus (PvsM-Competitivo): Jugador vs IA
 * - Machine-vs-Machine: IA vs IA (espectador)
 * 
 * @author Durán-Jiménez
 */
public class GameScreen extends JPanel {
    
    private final BadDopoCream game;
    private final Runnable onBackAction;
    private final Runnable onRestartAction;
    private final Runnable onNextLevelAction;
    private Timer gameTimer;
    private final String gameMode; // "Single-Player", "Cooperative", "Versus", "Machine-vs-Machine", "PvsM-Competitivo"
    private final IceCreamAI aiController; // Para modo máquina vs máquina
    private final HighScoreManager highScoreManager; // Gestor de highscores
    private String playerColor; // Color del jugador para guardado
    private JFrame parentFrame; // Frame padre para mostrar pantallas
    
    // Constantes de renderizado
    private static final int CELL_SIZE = 30; // Tamaño de cada celda en píxeles
    private static final Color COLOR_BACKGROUND = new Color(20, 20, 40);
    private static final Color COLOR_WALL = new Color(100, 100, 150);
    private static final Color COLOR_ICE_WALL = new Color(150, 200, 255);
    private static final Color COLOR_EMPTY = new Color(40, 40, 60);
    
    // Colores para personajes
    private static final Color COLOR_PLAYER_PINK = new Color(255, 100, 150);
    
    // Colores para enemigos
    private static final Color COLOR_MACETA = new Color(100, 200, 100);
    private static final Color COLOR_TROLL = new Color(200, 100, 200);
    private static final Color COLOR_CALAMAR = new Color(255, 150, 50);
    
    private JPanel gamePanel;
    private JLabel statusLabel;
    private JLabel fruitsLabel;
    private JLabel scoreLabel;
    private JLabel player1ScoreLabel; // Puntaje jugador 1
    private JLabel player2ScoreLabel; // Puntaje jugador 2
    private JLabel timerLabel;
    private JButton pauseButton;
    
    // Estado de movimiento para animaciones
    private long lastMoveTime = 0;
    private long lastMoveTimePlayer2 = 0;
    private static final long MOVE_ANIMATION_DURATION = 200; // ms
    private static final long MOVE_DELAY = 150; // Delay entre movimientos (ms) - Controla la velocidad
    
    // Estado de animación de hielo
    private boolean showingIceAnimation = false;
    private boolean showingIceAnimationP2 = false;
    private long iceAnimationStartTime = 0;
    private long iceAnimationStartTimeP2 = 0;
    private static final long ICE_ANIMATION_DURATION = 400; // ms
    private static final long ICE_ACTION_DELAY = 300; // Delay antes de crear el hielo
    
    public GameScreen(Runnable onBackAction, Runnable onRestartAction, Runnable onNextLevelAction, BadDopoCream game, String gameMode){
        this.onBackAction = onBackAction;
        this.onRestartAction = onRestartAction;
        this.onNextLevelAction = onNextLevelAction;
        this.game = game;
        this.gameMode = gameMode;
        // Crear IA para modo Machine-vs-Machine o PvsM-Competitivo
        this.aiController = ("Machine-vs-Machine".equals(gameMode) || "PvsM-Competitivo".equals(gameMode)) 
            ? new com.duran_jimenez.baddopocream.domain.IceCreamAI() : null;
        // Inicializar gestor de highscores
        this.highScoreManager = new HighScoreManager();
        
        setLayout(new BorderLayout());
        setBackground(COLOR_BACKGROUND);
        setupComponents();
        setupKeyboardControls();
        startGameLoop();
    }
    
    private void setupComponents(){
        // Panel superior con información mejorada
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(44, 62, 80));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Panel izquierdo: Nivel y Frutas
        JPanel leftInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftInfoPanel.setBackground(new Color(44, 62, 80));
        
        statusLabel = new JLabel("Nivel: " + game.getCurrentLevelNumber());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
        statusLabel.setForeground(Color.WHITE);
        
        fruitsLabel = new JLabel("Frutas: 0/0");
        fruitsLabel.setFont(new Font("Arial", Font.BOLD, 20));
        fruitsLabel.setForeground(new Color(255, 215, 0));
        
        scoreLabel = new JLabel("⭐ 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(new Color(255, 223, 0)); // Dorado brillante
        scoreLabel.setToolTipText("Puntaje Total");
        
        // Labels para puntajes individuales (solo visibles en modo 2 jugadores)
        player1ScoreLabel = new JLabel("🍓 P1: 0");
        player1ScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        player1ScoreLabel.setForeground(new Color(255, 150, 180)); // Rosa
        player1ScoreLabel.setToolTipText("Puntaje Jugador 1");
        player1ScoreLabel.setVisible(game.hasTwoPlayers());
        
        player2ScoreLabel = new JLabel("🍨 P2: 0");
        player2ScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        player2ScoreLabel.setForeground(new Color(150, 200, 255)); // Azul claro
        player2ScoreLabel.setToolTipText("Puntaje Jugador 2");
        player2ScoreLabel.setVisible(game.hasTwoPlayers());
        
        leftInfoPanel.add(statusLabel);
        leftInfoPanel.add(fruitsLabel);
        leftInfoPanel.add(scoreLabel);
        leftInfoPanel.add(player1ScoreLabel);
        leftInfoPanel.add(player2ScoreLabel);
        
        // Panel central: Timer con icono de reloj
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        timerPanel.setBackground(new Color(44, 62, 80));
        
        JLabel clockIcon = new JLabel("⏰");
        clockIcon.setFont(new Font("Arial", Font.BOLD, 24));
        clockIcon.setForeground(Color.WHITE);
        
        timerLabel = new JLabel("03:00");
        timerLabel.setFont(new Font("Courier New", Font.BOLD, 28));
        timerLabel.setForeground(new Color(0, 255, 100));
        
        timerPanel.add(clockIcon);
        timerPanel.add(timerLabel);
        
        // Panel derecho: Botón de pausa
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setBackground(new Color(44, 62, 80));
        
        pauseButton = new JButton("⏸ PAUSA");
        pauseButton.setFont(new Font("Arial", Font.BOLD, 16));
        pauseButton.setPreferredSize(new Dimension(150, 40));
        pauseButton.setBackground(new Color(52, 152, 219));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFocusPainted(false);
        pauseButton.setBorderPainted(false);
        pauseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pauseButton.addActionListener(e -> togglePause());
        
        rightPanel.add(pauseButton);
        
        topPanel.add(leftInfoPanel, BorderLayout.WEST);
        topPanel.add(timerPanel, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Panel del juego (centro)
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderGame((Graphics2D) g);
            }
        };
        gamePanel.setBackground(COLOR_BACKGROUND);
        gamePanel.setPreferredSize(new Dimension(800, 600));
        gamePanel.setFocusable(true);
        
        add(gamePanel, BorderLayout.CENTER);
        
        // Panel inferior con controles
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(44, 62, 80));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JLabel controlsLabel = new JLabel("Controles: ←↑→↓ Mover | ESPACIO Hielo | P Pausa");
        controlsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        controlsLabel.setForeground(Color.LIGHT_GRAY);
        
        JButton backButton = new JButton("← MENÚ");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(120, 35));
        backButton.setBackground(new Color(231, 76, 60));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> {
            stopGameLoop();
            if (onBackAction != null) {
                game.resetGame();
                onBackAction.run();
            }
        });
        
        JButton saveButton = new JButton("💾 GUARDAR");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(140, 35));
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e -> openSaveScreen());
        
        JButton loadButton = new JButton("📂 CARGAR");
        loadButton.setFont(new Font("Arial", Font.BOLD, 14));
        loadButton.setPreferredSize(new Dimension(140, 35));
        loadButton.setBackground(new Color(155, 89, 182));
        loadButton.setForeground(Color.WHITE);
        loadButton.setFocusPainted(false);
        loadButton.setBorderPainted(false);
        loadButton.addActionListener(e -> openLoadScreen());
        
        JButton restartButton = new JButton("↻ REINICIAR");
        restartButton.setFont(new Font("Arial", Font.BOLD, 14));
        restartButton.setPreferredSize(new Dimension(140, 35));
        restartButton.setBackground(new Color(46, 204, 113));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.setBorderPainted(false);
        restartButton.addActionListener(e -> {
            if (onRestartAction != null) {
                onRestartAction.run();
            }
        });
        
        bottomPanel.add(controlsLabel);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(saveButton);
        bottomPanel.add(loadButton);
        bottomPanel.add(restartButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Renderiza el juego completo
     */
    private void renderGame(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int[][] grid = game.getCurrentMapGrid();
        int width = game.getMapWidth();
        int height = game.getMapHeight();
        
        if(width == 0 || height == 0) return;
        
        // Renderizar mapa (paredes y espacios vacíos)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int screenX = x * CELL_SIZE;
                int screenY = y * CELL_SIZE;
                int cellType = grid[x][y];
                
                if (cellType == 1) { // WALL
                    // Pared permanente
                    g.setColor(COLOR_WALL);
                    g.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                } else if (cellType == 2) { // ICE
                    // Pared de hielo
                    g.setColor(COLOR_ICE_WALL);
                    g.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.WHITE);
                    g.drawRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                } else {
                    // Espacio vacío
                    g.setColor(COLOR_EMPTY);
                    g.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        
        // Renderizar obstáculos (baldosas calientes y fogatas)
        renderObstacles(g);
        
        // Renderizar frutas
        for (FruitInfo fruit : game.getAllFruitsInfo()) {
            if (!fruit.isCollected) {
                renderFruit(g, fruit);
            }
        }
        
        // Renderizar enemigos
        for (EnemyInfo enemy : game.getAllEnemiesInfo()) {
            renderEnemy(g, enemy);
        }
        
        // Renderizar jugador 1
        PlayerInfo player1 = game.getPlayer1Info();
        if (player1 != null && player1.isAlive) {
            renderPlayer(g, player1, showingIceAnimation, iceAnimationStartTime);
        }
        
        // Renderizar jugador 2 si existe
        if(game.hasTwoPlayers()){
            PlayerInfo player2 = game.getPlayer2Info();
            if(player2 != null && player2.isAlive){
                renderPlayer(g, player2, showingIceAnimationP2, iceAnimationStartTimeP2);
            }
        }
        
        // Actualizar etiquetas
        updateLabels();
        
        // Verificar fin del juego
        checkGameStatus();
    }
    
    /**
     * Renderiza un jugador con su estado de animación
     */
    private void renderPlayer(Graphics2D g, PlayerInfo player, boolean showIce, long iceStartTime) {
        int screenX = player.x * CELL_SIZE;
        int screenY = player.y * CELL_SIZE;
        
        // Determinar el tipo de personaje basado en el color del jugador
        CharacterType characterType = getCharacterTypeFromColor(player.color);
        
        int dx = player.lastDx;
        int dy = player.lastDy;
        
        long currentTime = System.currentTimeMillis();
        Image animation;
        
        // Verificar si debe mostrar animación de hielo
        if (showIce && (currentTime - iceStartTime) < ICE_ANIMATION_DURATION) {
            // Mostrar animación de crear hielo
            animation = CharacterImageConfigurator.getIceAnimation(characterType, dx, dy);
        } else {
            // Determinar si está en movimiento (basado en tiempo desde último movimiento)
            boolean isMoving = (currentTime - lastMoveTime) < MOVE_ANIMATION_DURATION;
            
            // Obtener la animación apropiada según dirección y movimiento
            animation = CharacterImageConfigurator.getAnimationByDirection(
                characterType, dx, dy, isMoving
            );
        }
        
        // Dibujar el personaje con animación (Image optimizado)
        if (animation != null) {
            g.drawImage(animation, screenX, screenY, CELL_SIZE, CELL_SIZE, null);
        } else {
            // Fallback: dibujar círculo simple con el color apropiado
            Color playerFallbackColor = getPlayerFallbackColor(player.color);
            g.setColor(Color.BLACK);
            g.fillOval(screenX + 3, screenY + 3, CELL_SIZE - 6, CELL_SIZE - 6);
            g.setColor(playerFallbackColor);
            g.fillOval(screenX + 2, screenY + 2, CELL_SIZE - 4, CELL_SIZE - 4);
            g.setColor(playerFallbackColor.brighter());
            g.fillOval(screenX + CELL_SIZE/3, screenY + CELL_SIZE/4, CELL_SIZE/4, CELL_SIZE/4);
        }
    }
    
    /**
     * Convierte el color del jugador a CharacterType
     */
    private CharacterType getCharacterTypeFromColor(String color) {
        if (color == null) return CharacterType.FRESA;
        
        String lowerColor = color.toLowerCase();
        if (lowerColor.contains("vanilla") || lowerColor.contains("cream") || lowerColor.contains("vainilla")) {
            return CharacterType.VAINILLA;
        } else if (lowerColor.contains("chocolate") || lowerColor.contains("brown")) {
            return CharacterType.CHOCOLATE;
        } else {
            // pink, fresa, strawberry, etc.
            return CharacterType.FRESA;
        }
    }
    
    /**
     * Obtiene el color de fallback para dibujar el jugador si no hay imagen
     */
    private Color getPlayerFallbackColor(String color) {
        if (color == null) return COLOR_PLAYER_PINK;
        
        String lowerColor = color.toLowerCase();
        if (lowerColor.contains("vanilla") || lowerColor.contains("cream") || lowerColor.contains("vainilla")) {
            return new Color(255, 255, 200); // Crema/Vainilla
        } else if (lowerColor.contains("chocolate") || lowerColor.contains("brown")) {
            return new Color(139, 90, 43); // Marrón chocolate
        } else {
            return COLOR_PLAYER_PINK; // Rosa/Fresa
        }
    }
    
    private void renderFruit(Graphics2D g, FruitInfo fruit) {
        int screenX = fruit.x * CELL_SIZE;
        int screenY = fruit.y * CELL_SIZE;
        
        // Usar FruitImageConfigurator para dibujar la imagen
        FruitImageConfigurator.drawFruit(g, fruit.name, screenX, screenY, CELL_SIZE);
        
        // Indicadores especiales para Cactus
        if ("Cactus".equals(fruit.typeName)) {
            if (fruit.hasSpikes) {
                // Indicador de peligro - borde rojo pulsante
                g.setColor(new Color(255, 0, 0, 150));
                g.setStroke(new java.awt.BasicStroke(3));
                g.drawRect(screenX + 2, screenY + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                
                // Símbolo de advertencia
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("⚠", screenX + CELL_SIZE - 15, screenY + 15);
            }
            
            // Mostrar temporizador
            if (fruit.secondsUntilSpike > 0 && fruit.secondsUntilSpike <= 10) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                g.drawString(String.valueOf(fruit.secondsUntilSpike), screenX + CELL_SIZE/2 - 3, screenY + CELL_SIZE - 3);
            }
        }
    }
    
    /**
     * Renderiza baldosas calientes y fogatas
     */
    private void renderObstacles(Graphics2D g) {
        for (ObstacleInfo obstacle : game.getAllObstaclesInfo()) {
            int screenX = obstacle.x * CELL_SIZE;
            int screenY = obstacle.y * CELL_SIZE;
            
            if ("HotTile".equals(obstacle.type)) {
                Image img = ObstacleImageConfigurator.getHotTileImage();
                if (img != null) {
                    g.drawImage(img, screenX, screenY, CELL_SIZE, CELL_SIZE, null);
                } else {
                    // Fallback: dibujar rectángulo rojo/naranja
                    g.setColor(new Color(255, 100, 0, 150));
                    g.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.ORANGE);
                    g.drawRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                }
            } else if ("Campfire".equals(obstacle.type)) {
                Image img = ObstacleImageConfigurator.getCampfireImage(obstacle.isLit);
                
                if (img != null) {
                    g.drawImage(img, screenX, screenY, CELL_SIZE, CELL_SIZE, null);
                } else {
                    // Fallback: dibujar representación simple
                    if (obstacle.isLit) {
                        // Fogata encendida - círculo rojo/amarillo
                        g.setColor(new Color(255, 150, 0));
                        g.fillOval(screenX + 5, screenY + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                        g.setColor(Color.YELLOW);
                        g.fillOval(screenX + 10, screenY + 10, CELL_SIZE - 20, CELL_SIZE - 20);
                    } else {
                        // Fogata apagada - círculo gris
                        g.setColor(new Color(80, 80, 80));
                        g.fillOval(screenX + 5, screenY + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                        g.setColor(Color.DARK_GRAY);
                        g.fillOval(screenX + 10, screenY + 10, CELL_SIZE - 20, CELL_SIZE - 20);
                    }
                }
                
                // Si está apagada, mostrar tiempo de reencendido
                if (!obstacle.isLit) {
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 10));
                    g.drawString(String.valueOf(obstacle.secondsUntilRelight), screenX + CELL_SIZE/2 - 3, screenY + CELL_SIZE/2 + 3);
                }
            }
        }
    }
    
    private void renderEnemy(Graphics2D g, EnemyInfo enemy) {
        int screenX = enemy.x * CELL_SIZE;
        int screenY = enemy.y * CELL_SIZE;
        
        // Determinar tipo de enemigo
        EnemyType enemyType = EnemyImageConfigurator.getEnemyTypeByClassName(enemy.typeName);
        
        int dx = enemy.directionX;
        int dy = enemy.directionY;
        
        // Obtener animación según dirección
        Image animation = EnemyImageConfigurator.getAnimationByDirection(enemyType, dx, dy);
        
        // Dibujar enemigo con animación
        if (animation != null) {
            g.drawImage(animation, screenX, screenY, CELL_SIZE, CELL_SIZE, null);
        } else {
            // Fallback: usar colores según tipo
            Color enemyColor = COLOR_MACETA;
            if ("Troll".equals(enemy.typeName)) {
                enemyColor = COLOR_TROLL;
            } else if ("CalamarNaranja".equals(enemy.typeName)) {
                enemyColor = COLOR_CALAMAR;
            }
            
            // Dibujar enemigo como cuadrado con X
            g.setColor(enemyColor);
            g.fillRect(screenX + 4, screenY + 4, CELL_SIZE - 8, CELL_SIZE - 8);
            g.setColor(Color.BLACK);
            g.drawLine(screenX + 6, screenY + 6, screenX + CELL_SIZE - 6, screenY + CELL_SIZE - 6);
            g.drawLine(screenX + CELL_SIZE - 6, screenY + 6, screenX + 6, screenY + CELL_SIZE - 6);
        }
    }
    
    private void updateLabels() {
        statusLabel.setText("Nivel: " + game.getCurrentLevelNumber());
        fruitsLabel.setText("Frutas: " + game.countCollectedFruits() + "/" + game.countTotalFruits());
        scoreLabel.setText("⭐ " + game.getCombinedScore());
        
        // Actualizar puntajes individuales si hay dos jugadores
        if (game.hasTwoPlayers()) {
            player1ScoreLabel.setVisible(true);
            player2ScoreLabel.setVisible(true);
            player1ScoreLabel.setText("🍓 P1: " + game.getPlayer1Score());
            player2ScoreLabel.setText("🍨 P2: " + game.getPlayer2Score());
        } else {
            player1ScoreLabel.setVisible(false);
            player2ScoreLabel.setVisible(false);
        }
        
        updateTimerDisplay();
    }
    
    private void checkGameStatus() {
        // Verificar si el tiempo se agotó
        if (game.isTimeExpired()) {
            stopGameLoop();
            showGameOverDialog("⏰ ¡Tiempo Agotado!", 
                "El reloj llegó a 00:00\n\n" +
                "Puntaje obtenido: " + game.getCombinedScore());
            return;
        }
        
        if (game.isGameOver()) {
            stopGameLoop();
            showGameOverDialog("💀 ¡Game Over!", 
                "Has sido atrapado por un enemigo\n\n" +
                "Puntaje obtenido: " + game.getCombinedScore());
        } else if (game.isCurrentLevelCompleted()) {
            stopGameLoop();
            
            if (!game.hasNextLevel()) {
                // Juego completado - todos los niveles ganados
                showVictoryDialog("🏆 ¡VICTORIA TOTAL!", 
                    "¡Felicidades! Has completado todos los niveles\n\n" +
                    "Frutas recolectadas: " + game.countCollectedFruits() + "/" + game.countTotalFruits() + "\n" +
                    "Puntaje final: " + game.getCombinedScore());
            } else {
                // Nivel completado - hay más niveles
                showLevelCompleteDialog();
            }
        }
    }
    
    /**
     * Muestra diálogo de nivel completado con puntaje
     */
    private void showLevelCompleteDialog() {
        // Acumular puntaje antes de avanzar
        int levelScore = game.getCurrentLevelScore();
        int totalScore = game.getCombinedScore();
        
        String message = String.format(
            "🎉 ¡Nivel %d Completado!\n\n" +
            "Frutas recolectadas: %d/%d\n" +
            "Puntaje del nivel: %d\n" +
            "Puntaje total: %d",
            game.getCurrentLevelNumber(),
            game.countCollectedFruits(),
            game.countTotalFruits(),
            levelScore,
            totalScore
        );
        
        Object[] options = {"Siguiente Nivel", "Menu Principal"};
        int choice = JOptionPane.showOptionDialog(this,
            message,
            "Nivel Completado",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == 0) {
            // Siguiente nivel - acumular score y avanzar
            game.addToTotalScore(game.getCurrentLevelScore());
            game.nextLevel();
            
            // Avanzar al siguiente nivel
            if (onNextLevelAction != null) {
                onNextLevelAction.run();
            }
        } else {
            // Volver al menú (choice == 1 o CLOSED_OPTION)
            if (onBackAction != null) {
                game.resetGame();
                onBackAction.run();
            }
        }
    }
    
    /**
     * Muestra diálogo de victoria total
     */
    private void showVictoryDialog(String title, String message) {
        int finalScore = game.getCombinedScore();
        
        // Mostrar puntajes individuales si hay dos jugadores
        String scoreDetails = message;
        if (game.hasTwoPlayers()) {
            scoreDetails += "\n\n📊 Puntajes Individuales:\n" +
                "   🍓 Jugador 1: " + game.getPlayer1Score() + " pts\n" +
                "   🍨 Jugador 2: " + game.getPlayer2Score() + " pts";
        }
        
        // Verificar si es highscore
        if (highScoreManager.isHighScore(finalScore)) {
            int position = highScoreManager.getPositionForScore(finalScore);
            scoreDetails += "\n\n🎉 ¡NUEVO HIGHSCORE! (Posición #" + position + ")";
            
            // Pedir nombre del jugador
            String playerName = promptForPlayerName(finalScore);
            if (playerName != null && !playerName.trim().isEmpty()) {
                highScoreManager.addHighScore(playerName, finalScore, gameMode, game.getCurrentLevelNumber());
            }
        }
        
        Object[] options = {"Ver Highscores", "Jugar de Nuevo", "Menu Principal"};
        int choice = JOptionPane.showOptionDialog(this,
            scoreDetails,
            title,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == 0) {
            // Ver highscores
            showHighScoresDialog();
            // Después de ver highscores, volver al menú
            if (onBackAction != null) {
                game.resetGame();
                onBackAction.run();
            }
        } else if (choice == 1) {
            // Reiniciar desde nivel 1
            if (onRestartAction != null) {
                onRestartAction.run();
            }
        } else {
            // Volver al menú (choice == 2 o CLOSED_OPTION)
            if (onBackAction != null) {
                game.resetGame();
                onBackAction.run();
            }
        }
    }
    
    /**
     * Muestra diálogo de game over con opciones
     */
    private void showGameOverDialog(String title, String message) {
        int finalScore = game.getCombinedScore();
        
        // Agregar puntajes individuales si hay dos jugadores
        String scoreDetails = message;
        if (game.hasTwoPlayers()) {
            scoreDetails += "\n\n📊 Puntajes Individuales:\n" +
                "   🍓 Jugador 1: " + game.getPlayer1Score() + " pts\n" +
                "   🍨 Jugador 2: " + game.getPlayer2Score() + " pts";
        }
        
        // Verificar si califica para highscore
        if (finalScore > 0 && highScoreManager.isHighScore(finalScore)) {
            int position = highScoreManager.getPositionForScore(finalScore);
            scoreDetails += "\n\n🎉 ¡Tu puntaje entra en el TOP 10! (Posición #" + position + ")";
            
            // Pedir nombre del jugador
            String playerName = promptForPlayerName(finalScore);
            if (playerName != null && !playerName.trim().isEmpty()) {
                highScoreManager.addHighScore(playerName, finalScore, gameMode, game.getCurrentLevelNumber());
            }
        }
        
        Object[] options = {"Ver Highscores", "Reiniciar Nivel", "Menu Principal"};
        int choice = JOptionPane.showOptionDialog(this,
            scoreDetails,
            title,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == 0) {
            // Ver highscores
            showHighScoresDialog();
            // Después de ver highscores, volver al menú
            if (onBackAction != null) {
                game.resetGame();
                onBackAction.run();
            }
        } else if (choice == 1) {
            // Reiniciar nivel actual
            if (onRestartAction != null) {
                onRestartAction.run();
            }
        } else {
            // Volver al menú (choice == 2 o CLOSED_OPTION)
            if (onBackAction != null) {
                game.resetGame();
                onBackAction.run();
            }
        }
    }
    
    /**
     * Solicita el nombre del jugador para el highscore
     */
    private String promptForPlayerName(int score) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel messageLabel = new JLabel("<html><center>🏆 ¡Felicidades!<br>Tu puntaje de <b>" + score + "</b> puntos<br>entra en la tabla de récords!</center></html>");
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        javax.swing.JTextField nameField = new javax.swing.JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(new JLabel("Tu nombre:"), BorderLayout.WEST);
        inputPanel.add(nameField, BorderLayout.CENTER);
        
        panel.add(messageLabel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "🎮 Ingresa tu Nombre", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            return nameField.getText().trim();
        }
        return null;
    }
    
    /**
     * Muestra el diálogo con la tabla de highscores
     */
    private void showHighScoresDialog() {
        java.util.List<HighScoreManager.HighScoreEntry> scores = highScoreManager.getHighScores();
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: Arial; padding: 10px; width: 480px;'>");
        sb.append("<h2 style='text-align: center; color: #FFD700;'>🏆 Tabla de Récords 🏆</h2>");
        sb.append("<table style='width: 100%; border-collapse: collapse;'>");
        sb.append("<tr style='background-color: #2c3e50; color: white;'>");
        sb.append("<th style='padding: 8px;'>#</th>");
        sb.append("<th style='padding: 8px;'>Jugador</th>");
        sb.append("<th style='padding: 8px;'>Puntaje</th>");
        sb.append("<th style='padding: 8px;'>Modo</th>");
        sb.append("</tr>");
        
        if (scores.isEmpty()) {
            sb.append("<tr><td colspan='4' style='text-align: center; padding: 20px;'>No hay récords aún. ¡Sé el primero!</td></tr>");
        } else {
            int position = 1;
            for (HighScoreManager.HighScoreEntry entry : scores) {
                String bgColor = position % 2 == 0 ? "#ecf0f1" : "#ffffff";
                String medal = "";
                if (position == 1) medal = "🥇 ";
                else if (position == 2) medal = "🥈 ";
                else if (position == 3) medal = "🥉 ";
                
                sb.append("<tr style='background-color: ").append(bgColor).append(";'>");
                sb.append("<td style='padding: 8px; text-align: center;'>").append(medal).append(position).append("</td>");
                sb.append("<td style='padding: 8px;'>").append(entry.getPlayerName()).append("</td>");
                sb.append("<td style='padding: 8px; text-align: right; font-weight: bold;'>").append(entry.getScore()).append("</td>");
                sb.append("<td style='padding: 8px;'>").append(formatGameMode(entry.getGameMode())).append("</td>");
                sb.append("</tr>");
                position++;
            }
        }
        
        sb.append("</table></body></html>");
        
        JLabel label = new JLabel(sb.toString());
        label.setVerticalAlignment(JLabel.TOP);
        
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(label);
        scrollPane.setPreferredSize(new Dimension(550, 500));
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        JOptionPane.showMessageDialog(this, scrollPane, "Highscores", JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Formatea el nombre del modo de juego para mostrar
     */
    private String formatGameMode(String mode) {
        if (mode == null) return "Normal";
        switch (mode) {
            case "PvP-Cooperativo": return "Cooperativo";
            case "PvsM-Competitivo": return "vs IA";
            case "Machine-vs-Machine": return "IA vs IA";
            default: return mode;
        }
    }
    
    /**
     * Configura los controles del teclado
     */
    private void setupKeyboardControls() {
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        
        // Agregar MouseListener para asegurar foco al hacer clic
        gamePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });
        
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (game.isGameOver()) return;
                
                // Controles del Jugador 1 (Flechas y SPACE)
                long currentTime = System.currentTimeMillis();
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (currentTime - lastMoveTime >= MOVE_DELAY) {
                            game.movePlayer1(0, -1);
                            lastMoveTime = currentTime;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (currentTime - lastMoveTime >= MOVE_DELAY) {
                            game.movePlayer1(0, 1);
                            lastMoveTime = currentTime;
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (currentTime - lastMoveTime >= MOVE_DELAY) {
                            game.movePlayer1(-1, 0);
                            lastMoveTime = currentTime;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (currentTime - lastMoveTime >= MOVE_DELAY) {
                            game.movePlayer1(1, 0);
                            lastMoveTime = currentTime;
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        handleIceAction();
                        break;
                    case KeyEvent.VK_P:
                    case KeyEvent.VK_ESCAPE:
                        togglePause();
                        break;
                }
                
                // Controles del Jugador 2 (WASD y SHIFT) - Solo en modo cooperativo (no en competitivo)
                // En modo PvsM-Competitivo, la IA controla al jugador 2
                if(game.hasTwoPlayers() && !"PvsM-Competitivo".equals(gameMode)){
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_W:
                            if (currentTime - lastMoveTimePlayer2 >= MOVE_DELAY) {
                                game.movePlayer2(0, -1);
                                lastMoveTimePlayer2 = currentTime;
                            }
                            break;
                        case KeyEvent.VK_S:
                            if (currentTime - lastMoveTimePlayer2 >= MOVE_DELAY) {
                                game.movePlayer2(0, 1);
                                lastMoveTimePlayer2 = currentTime;
                            }
                            break;
                        case KeyEvent.VK_A:
                            if (currentTime - lastMoveTimePlayer2 >= MOVE_DELAY) {
                                game.movePlayer2(-1, 0);
                                lastMoveTimePlayer2 = currentTime;
                            }
                            break;
                        case KeyEvent.VK_D:
                            if (currentTime - lastMoveTimePlayer2 >= MOVE_DELAY) {
                                game.movePlayer2(1, 0);
                                lastMoveTimePlayer2 = currentTime;
                            }
                            break;
                        case KeyEvent.VK_SHIFT:
                            handleIceActionPlayer2();
                            break;
                    }
                }
                
                gamePanel.repaint();
            }
        });
    }
    
    /**
     * Maneja la creación/destrucción de paredes de hielo
     */
    private void handleIceAction() {
        PlayerInfo player = game.getPlayer1Info();
        if (player == null) return;
        
        // Evitar spam de hielo
        long currentTime = System.currentTimeMillis();
        if (showingIceAnimation && (currentTime - iceAnimationStartTime) < ICE_ANIMATION_DURATION) {
            return; // Ya está creando hielo
        }
        
        int dx = player.lastDx;
        int dy = player.lastDy;
        
        // Verificar si hay hielo adyacente en la dirección actual para romper
        int[][] grid = game.getCurrentMapGrid();
        int nextX = player.x + dx;
        int nextY = player.y + dy;
        
        if (nextX >= 0 && nextX < grid.length && nextY >= 0 && nextY < grid[0].length) {
            if (grid[nextX][nextY] == 2) { // ICE = 2
                // Romper línea de hielo en esa dirección (inmediato, sin delay)
                game.breakIceLinePlayer1(dx, dy);
                gamePanel.repaint();
                return;
            }
        }
        
        // Iniciar animación de hielo
        showingIceAnimation = true;
        iceAnimationStartTime = currentTime;
        
        // Crear el hielo después del delay (usando un Timer)
        Timer delayTimer = new Timer((int)ICE_ACTION_DELAY, e -> {
            game.createIceLinePlayer1(dx, dy);
            gamePanel.repaint();
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
        
        // Detener la animación después de su duración
        Timer animTimer = new Timer((int)ICE_ANIMATION_DURATION, e -> {
            showingIceAnimation = false;
            gamePanel.repaint();
        });
        animTimer.setRepeats(false);
        animTimer.start();
    }
    
    /**
     * Maneja la creación/destrucción de paredes de hielo para el jugador 2
     */
    private void handleIceActionPlayer2() {
        PlayerInfo player2 = game.getPlayer2Info();
        if(player2 == null) return;
        
        // Evitar spam de hielo
        long currentTime = System.currentTimeMillis();
        if (showingIceAnimationP2 && (currentTime - iceAnimationStartTimeP2) < ICE_ANIMATION_DURATION) {
            return; // Ya está creando hielo
        }
        
        int dx = player2.lastDx;
        int dy = player2.lastDy;
        
        // Verificar si hay hielo adyacente en la dirección actual para romper
        int[][] grid = game.getCurrentMapGrid();
        int nextX = player2.x + dx;
        int nextY = player2.y + dy;
        
        if (nextX >= 0 && nextX < grid.length && nextY >= 0 && nextY < grid[0].length) {
            if (grid[nextX][nextY] == 2) { // ICE = 2
                // Romper línea de hielo en esa dirección (inmediato, sin delay)
                game.breakIceLinePlayer2(dx, dy);
                gamePanel.repaint();
                return;
            }
        }
        
        // Iniciar animación de hielo
        showingIceAnimationP2 = true;
        iceAnimationStartTimeP2 = currentTime;
        
        // Crear el hielo después del delay (usando un Timer)
        Timer delayTimer = new Timer((int)ICE_ACTION_DELAY, e -> {
            game.createIceLinePlayer2(dx, dy);
            gamePanel.repaint();
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
        
        // Detener la animación después de su duración
        Timer animTimer = new Timer((int)ICE_ANIMATION_DURATION, e -> {
            showingIceAnimationP2 = false;
            gamePanel.repaint();
        });
        animTimer.setRepeats(false);
        animTimer.start();
    }
    
    /**
     * Inicia el bucle del juego
     */
    private void startGameLoop() {
        gameTimer = new Timer(500, e -> { // 500ms = movimiento más lento y controlable
            // Si es modo máquina vs máquina, la IA controla al jugador 1
            if("Machine-vs-Machine".equals(gameMode) && aiController != null && !game.isGameOver()){
                PlayerInfo player = game.getPlayer1Info();
                if(player != null){
                    // Crear un objeto temporal IceCream para la IA (necesita acceso a Level)
                    // Por ahora, simplemente mover aleatoriamente
                    int[] move = new int[]{(int)(Math.random()*3)-1, (int)(Math.random()*3)-1, 0};
                    
                    // Aplicar movimiento
                    if(move[0] != 0 || move[1] != 0){
                        game.movePlayer1(move[0], move[1]);
                    }
                }
            }
            
            // Si es modo competitivo (PvsM), la IA controla al jugador 2
            if("PvsM-Competitivo".equals(gameMode) && aiController != null && !game.isGameOver()){
                com.duran_jimenez.baddopocream.domain.Level level = game.getCurrentLevel();
                if(level != null && level.hasTwoPlayers()){
                    com.duran_jimenez.baddopocream.domain.IceCream aiPlayer = level.getPlayer2();
                    if(aiPlayer != null && aiPlayer.isAlive()){
                        // Usar la IA para decidir el movimiento
                        int[] move = aiController.decideMove(level, aiPlayer);
                        
                        // Aplicar movimiento al jugador 2
                        if(move[0] != 0 || move[1] != 0){
                            game.movePlayer2(move[0], move[1]);
                        }
                        
                        // Si la IA decide usar hielo
                        if(move.length > 2 && move[2] == 1){
                            game.createIceLinePlayer2(move[0], move[1]);
                        }
                    }
                }
            }
            
            game.update(); // Actualiza enemigos y frutas móviles
            updateLabels();
            checkGameStatus();
            gamePanel.repaint();
        });
        gameTimer.start();
    }
    
    /**
     * Detiene el bucle del juego
     */
    private void stopGameLoop() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }
    
    /**
     * Alterna entre pausa y reanudación
     */
    private void togglePause() {
        if (game.isPaused()) {
            // Reanudar
            game.resume();
            pauseButton.setText("⏸ PAUSA");
            pauseButton.setBackground(new Color(52, 152, 219));
            gameTimer.start();
        } else {
            // Pausar
            game.pause();
            pauseButton.setText("▶ REANUDAR");
            pauseButton.setBackground(new Color(46, 204, 113));
            gameTimer.stop();
            gamePanel.repaint(); // Mostrar pantalla de pausa
        }
    }
    
    /**
     * Actualiza el display del timer
     */
    private void updateTimerDisplay() {
        int seconds = game.getRemainingSeconds();
        int minutes = seconds / 60;
        int secs = seconds % 60;
        
        String timeStr = String.format("%02d:%02d", minutes, secs);
        timerLabel.setText(timeStr);
        
        // Cambiar color según tiempo restante
        if (seconds <= 30) {
            timerLabel.setForeground(Color.RED); // Rojo cuando quedan 30s o menos
        } else if (seconds <= 60) {
            timerLabel.setForeground(new Color(255, 165, 0)); // Naranja cuando queda 1 minuto
        } else {
            timerLabel.setForeground(new Color(0, 255, 100)); // Verde cuando hay tiempo
        }
    }
    
    /**
     * Abre la pantalla de guardado
     */
    private void openSaveScreen() {
        if (parentFrame == null) {
            findParentFrame();
        }
        
        if (parentFrame == null) return;
        
        // Pausar el juego
        if (game.getCurrentLevel() != null && !game.getCurrentLevel().isPaused()) {
            togglePause();
        }
        
        // Crear y mostrar pantalla de guardado
        SaveLoadScreen saveScreen = new SaveLoadScreen(
            game, playerColor, gameMode, parentFrame, true, 
            () -> {
                // Al cerrar, volver al juego
                parentFrame.setContentPane(this);
                parentFrame.revalidate();
                parentFrame.repaint();
                requestFocus();
            }
        );
        
        parentFrame.setContentPane(saveScreen);
        parentFrame.revalidate();
        parentFrame.repaint();
    }
    
    /**
     * Abre la pantalla de carga
     */
    private void openLoadScreen() {
        if (parentFrame == null) {
            findParentFrame();
        }
        
        if (parentFrame == null) return;
        
        // Detener el game loop antes de cargar
        stopGameLoop();
        
        // Crear y mostrar pantalla de carga
        SaveLoadScreen loadScreen = new SaveLoadScreen(
            game, playerColor, gameMode, parentFrame, false,
            () -> {
                // Al cerrar sin cargar, volver al juego
                parentFrame.setContentPane(this);
                parentFrame.revalidate();
                parentFrame.repaint();
                startGameLoop(); // Reanudar el juego
                requestFocus();
            },
            (state) -> {
                // Al cargar exitosamente, usar BadDopoCreamGUI para recrear todo
                if (parentFrame instanceof BadDopoCreamGUI) {
                    ((BadDopoCreamGUI) parentFrame).loadGameFromState(state);
                } else {
                    // Fallback si no es BadDopoCreamGUI
                    game.restoreFromState(state);
                    parentFrame.setContentPane(this);
                    parentFrame.revalidate();
                    parentFrame.repaint();
                    startGameLoop();
                    requestFocus();
                }
            }
        );
        
        parentFrame.setContentPane(loadScreen);
        parentFrame.revalidate();
        parentFrame.repaint();
    }
    
    /**
     * Busca el JFrame padre
     */
    private void findParentFrame() {
        Container container = getParent();
        while (container != null) {
            if (container instanceof JFrame) {
                parentFrame = (JFrame) container;
                break;
            }
            container = container.getParent();
        }
    }
    
    /**
     * Establece el color del jugador para guardado
     */
    public void setPlayerColor(String color) {
        this.playerColor = color;
    }
    
    /**
     * Renderiza el overlay de pausa
     */
    private void renderPauseOverlay(Graphics2D g) {
        // Fondo semi-transparente
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, gamePanel.getWidth(), gamePanel.getHeight());
        
        // Mensaje de pausa
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        String pauseText = "⏸ PAUSA";
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(pauseText);
        int x = (gamePanel.getWidth() - textWidth) / 2;
        int y = gamePanel.getHeight() / 2 - 50;
        
        // Sombra del texto
        g.setColor(new Color(0, 0, 0, 200));
        g.drawString(pauseText, x + 3, y + 3);
        
        // Texto principal
        g.setColor(Color.WHITE);
        g.drawString(pauseText, x, y);
        
        // Instrucciones
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        String instructions = "Presiona P, ESC o el botón REANUDAR para continuar";
        int instrWidth = g.getFontMetrics().stringWidth(instructions);
        int instrX = (gamePanel.getWidth() - instrWidth) / 2;
        g.drawString(instructions, instrX, y + 80);
    }
}
