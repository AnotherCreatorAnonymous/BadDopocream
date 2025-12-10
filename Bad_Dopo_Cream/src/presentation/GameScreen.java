package presentation;

import domain.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import presentation.CharacterImageConfigurator.CharacterType;
import presentation.EnemyImageConfigurator.EnemyType;

/**
 * Pantalla del juego - Renderiza y gestiona el juego en tiempo real
 */
public class GameScreen extends JPanel {
    
    private BadDopoCream game;
    private Runnable onBackAction;
    private Runnable onRestartAction;
    private Timer gameTimer;
    private String gameMode; // "Single-Player", "Cooperative", "Versus", "Machine-vs-Machine"
    private IceCreamAI aiController; // Para modo máquina vs máquina
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
    private static final Color COLOR_PLAYER_VANILLA = new Color(255, 255, 150);
    private static final Color COLOR_PLAYER_CHOCOLATE = new Color(150, 75, 0);
    
    // Colores para frutas
    private static final Color COLOR_BANANA = new Color(255, 255, 0);
    private static final Color COLOR_CHERRY = new Color(255, 0, 0);
    private static final Color COLOR_GRAPES = new Color(128, 0, 128);
    private static final Color COLOR_PINEAPPLE = new Color(255, 165, 0);
    
    // Colores para enemigos
    private static final Color COLOR_MACETA = new Color(100, 200, 100);
    private static final Color COLOR_TROLL = new Color(200, 100, 200);
    private static final Color COLOR_CALAMAR = new Color(255, 150, 50);
    
    private JPanel gamePanel;
    private JLabel statusLabel;
    private JLabel fruitsLabel;
    private JLabel scoreLabel;
    private JLabel timerLabel;
    private JButton pauseButton;
    
    // Estado de movimiento para animaciones
    private boolean player1Moving = false;
    private boolean player2Moving = false;
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
    
    public GameScreen(Runnable onBackAction, Runnable onRestartAction, BadDopoCream game, String gameMode){
        this.onBackAction = onBackAction;
        this.onRestartAction = onRestartAction;
        this.game = game;
        this.gameMode = gameMode;
        this.aiController = "Machine-vs-Machine".equals(gameMode) ? new domain.IceCreamAI() : null;
        
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
        scoreLabel.setToolTipText("Puntaje");
        
        leftInfoPanel.add(statusLabel);
        leftInfoPanel.add(fruitsLabel);
        leftInfoPanel.add(scoreLabel);
        
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
        Level currentLevel = game.getCurrentLevel();
        if (currentLevel == null) return;
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Map map = currentLevel.getMap();
        int width = map.getWidth();
        int height = map.getHeight();
        
        // Renderizar mapa (paredes y espacios vacíos)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Location loc = new Location(x, y);
                int screenX = x * CELL_SIZE;
                int screenY = y * CELL_SIZE;
                
                if (map.isWall(loc)) {
                    // Pared permanente
                    g.setColor(COLOR_WALL);
                    g.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                } else if (map.hasIceWall(loc)) {
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
        renderObstacles(g, map);
        
        // Renderizar frutas
        for (Fruit fruit : currentLevel.getFruits()) {
            if (!fruit.isCollected()) {
                renderFruit(g, fruit);
            }
        }
        
        // Renderizar enemigos
        for (Enemy enemy : currentLevel.getEnemies()) {
            renderEnemy(g, enemy);
        }
        
        // Renderizar jugador
        IceCream player = currentLevel.getPlayer();
        if (player != null && player.isAlive()) {
            renderPlayer(g, player);
        }
        
        // Renderizar jugador 2 si existe
        if(currentLevel.hasTwoPlayers()){
            IceCream player2 = currentLevel.getPlayer2();
            if(player2 != null && player2.isAlive()){
                renderPlayerWithState(g, player2, showingIceAnimationP2, iceAnimationStartTimeP2);
            }
        }
        
        // Actualizar etiquetas
        updateLabels();
        
        // Verificar fin del juego
        checkGameStatus();
        
        // Renderizar overlay de pausa si está pausado
        if (currentLevel.isPaused()) {
            renderPauseOverlay(g);
        }
    }
    
    private void renderPlayer(Graphics2D g, IceCream player) {
        renderPlayerWithState(g, player, showingIceAnimation, iceAnimationStartTime);
    }
    
    /**
     * Renderiza un jugador con su estado de animación
     */
    private void renderPlayerWithState(Graphics2D g, IceCream player, boolean showIce, long iceStartTime) {
        Location loc = player.getLocation();
        int screenX = loc.getX() * CELL_SIZE;
        int screenY = loc.getY() * CELL_SIZE;
        
        // Convertir color del jugador a CharacterType
        CharacterType characterType = CharacterImageConfigurator.getCharacterTypeByColor(
            player.getColor()
        );
        
        // Obtener dirección actual del jugador
        int dx = player.getLastDx();
        int dy = player.getLastDy();
        
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
            // Fallback: usar el método anterior si no hay animación
            renderPlayerFallback(g, player, screenX, screenY);
        }
    }
    
    /**
     * Método de fallback para dibujar el jugador sin animaciones
     */
    private void renderPlayerFallback(Graphics2D g, IceCream player, int screenX, int screenY) {
        // Determinar color según el tipo
        Color playerColor = COLOR_PLAYER_PINK;
        String color = player.getColor();
        if (color.contains("vanilla")) {
            playerColor = COLOR_PLAYER_VANILLA;
        } else if (color.contains("chocolate")) {
            playerColor = COLOR_PLAYER_CHOCOLATE;
        }
        
        // Dibujar helado como círculo con sombra
        g.setColor(Color.BLACK);
        g.fillOval(screenX + 3, screenY + 3, CELL_SIZE - 6, CELL_SIZE - 6);
        g.setColor(playerColor);
        g.fillOval(screenX + 2, screenY + 2, CELL_SIZE - 4, CELL_SIZE - 4);
        g.setColor(playerColor.brighter());
        g.fillOval(screenX + CELL_SIZE/3, screenY + CELL_SIZE/4, CELL_SIZE/4, CELL_SIZE/4);
    }
    
    private void renderFruit(Graphics2D g, Fruit fruit) {
        Location loc = fruit.getLocation();
        int screenX = loc.getX() * CELL_SIZE;
        int screenY = loc.getY() * CELL_SIZE;
        
        // Obtener nombre de la fruta desde el objeto
        String fruitName = fruit.getName();
        
        // Usar FruitImageConfigurator para dibujar la imagen
        FruitImageConfigurator.drawFruit(g, fruitName, screenX, screenY, CELL_SIZE);
        
        // Indicadores especiales para frutas con comportamientos
        if (fruit instanceof Cactus) {
            Cactus cactus = (Cactus)fruit;
            if (cactus.hasSpikes()) {
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
            int seconds = cactus.getSecondsUntilNextSpike();
            if (seconds > 0 && seconds <= 10) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                g.drawString(String.valueOf(seconds), screenX + CELL_SIZE/2 - 3, screenY + CELL_SIZE - 3);
            }
        }
    }
    
    /**
     * Renderiza baldosas calientes y fogatas
     */
    private void renderObstacles(Graphics2D g, Map map) {
        // Renderizar baldosas calientes
        for (BaldosaCaliente hotTile : map.getHotTiles()) {
            Location loc = hotTile.getLocation();
            int screenX = loc.getX() * CELL_SIZE;
            int screenY = loc.getY() * CELL_SIZE;
            
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
        }
        
        // Renderizar fogatas
        for (Fogata campfire : map.getCampfires()) {
            Location loc = campfire.getLocation();
            int screenX = loc.getX() * CELL_SIZE;
            int screenY = loc.getY() * CELL_SIZE;
            
            boolean isLit = campfire.isLit();
            Image img = ObstacleImageConfigurator.getCampfireImage(isLit);
            
            if (img != null) {
                g.drawImage(img, screenX, screenY, CELL_SIZE, CELL_SIZE, null);
            } else {
                // Fallback: dibujar representación simple
                if (isLit) {
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
            if (!isLit) {
                int seconds = campfire.getSecondsUntilRelight();
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                g.drawString(String.valueOf(seconds), screenX + CELL_SIZE/2 - 3, screenY + CELL_SIZE/2 + 3);
            }
        }
    }
    
    private void renderEnemy(Graphics2D g, Enemy enemy) {
        Location loc = enemy.getLocation();
        int screenX = loc.getX() * CELL_SIZE;
        int screenY = loc.getY() * CELL_SIZE;
        
        // Determinar tipo de enemigo
        EnemyType enemyType = EnemyImageConfigurator.getEnemyTypeByClassName(
            enemy.getClass().getSimpleName()
        );
        
        // Obtener dirección del enemigo
        int dx = 0;
        int dy = 0;
        
        if (enemy instanceof Troll) {
            Troll troll = (Troll) enemy;
            dx = troll.getDirectionX();
            dy = troll.getDirectionY();
        } else if (enemy instanceof Maceta) {
            // Maceta se mueve hacia el jugador, usar última dirección conocida
            dx = 0;
            dy = 1; // Default abajo
        } else if (enemy instanceof CalamarNaranja) {
            // Calamar también se mueve, default
            dx = 1;
            dy = 0;
        }
        
        // Obtener animación según dirección
        Image animation = EnemyImageConfigurator.getAnimationByDirection(enemyType, dx, dy);
        
        // Dibujar enemigo con animación
        if (animation != null) {
            g.drawImage(animation, screenX, screenY, CELL_SIZE, CELL_SIZE, null);
        } else {
            // Fallback: usar el método anterior si no hay animación
            renderEnemyFallback(g, enemy, screenX, screenY);
        }
    }
    
    /**
     * Método de fallback para dibujar enemigos sin animaciones
     */
    private void renderEnemyFallback(Graphics2D g, Enemy enemy, int screenX, int screenY) {
        // Determinar color según tipo
        Color enemyColor = COLOR_MACETA;
        if (enemy instanceof Troll) {
            enemyColor = COLOR_TROLL;
        } else if (enemy instanceof CalamarNaranja) {
            enemyColor = COLOR_CALAMAR;
        }
        
        // Dibujar enemigo como cuadrado con X
        g.setColor(enemyColor);
        g.fillRect(screenX + 4, screenY + 4, CELL_SIZE - 8, CELL_SIZE - 8);
        g.setColor(Color.BLACK);
        g.drawLine(screenX + 6, screenY + 6, screenX + CELL_SIZE - 6, screenY + CELL_SIZE - 6);
        g.drawLine(screenX + CELL_SIZE - 6, screenY + 6, screenX + 6, screenY + CELL_SIZE - 6);
    }
    
    private void updateLabels() {
        statusLabel.setText("Nivel: " + game.getCurrentLevelNumber());
        fruitsLabel.setText("Frutas: " + game.countCollectedFruits() + "/" + game.countTotalFruits());
        scoreLabel.setText("⭐ " + game.getCombinedScore());
        updateTimerDisplay();
    }
    
    private void checkGameStatus() {
        Level currentLevel = game.getCurrentLevel();
        
        // Verificar si el tiempo se agotó
        if (currentLevel != null && currentLevel.isTimeExpired()) {
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
        } else if (currentLevel != null && currentLevel.isCompleted()) {
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
            Level currentLevel = game.getCurrentLevel();
            if (currentLevel != null) {
                game.addToTotalScore(currentLevel.getCurrentScore());
            }
            game.nextLevel();
            
            // Reiniciar la pantalla con el nuevo nivel
            if (onRestartAction != null) {
                onRestartAction.run();
            }
        } else {
            // Volver al menú (choice == 1 o CLOSED_OPTION)
            if (onBackAction != null) {
                onBackAction.run();
            }
        }
    }
    
    /**
     * Muestra diálogo de victoria total
     */
    private void showVictoryDialog(String title, String message) {
        Object[] options = {"Jugar de Nuevo", "Menu Principal"};
        int choice = JOptionPane.showOptionDialog(this,
            message,
            title,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == 0) {
            // Reiniciar desde nivel 1
            // NO llamar a game.resetToLevel(1) aquí porque onRestartAction
            // ya se encarga de crear un nuevo juego y avanzar al nivel correcto
            if (onRestartAction != null) {
                onRestartAction.run();
            }
        } else {
            // Volver al menú (choice == 1 o CLOSED_OPTION)
            if (onBackAction != null) {
                onBackAction.run();
            }
        }
    }
    
    /**
     * Muestra diálogo de game over con opciones
     */
    private void showGameOverDialog(String title, String message) {
        Object[] options = {"Reiniciar Nivel", "Menu Principal"};
        int choice = JOptionPane.showOptionDialog(this,
            message,
            title,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == 0) {
            // Reiniciar nivel actual
            if (onRestartAction != null) {
                onRestartAction.run();
            }
        } else {
            // Volver al menú (choice == 1 o CLOSED_OPTION)
            if (onBackAction != null) {
                onBackAction.run();
            }
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
                Level currentLevel = game.getCurrentLevel();
                if (currentLevel == null || game.isGameOver()) return;
                
                // Controles del Jugador 1 (Flechas y SPACE)
                long currentTime = System.currentTimeMillis();
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (currentTime - lastMoveTime >= MOVE_DELAY) {
                            currentLevel.movePlayer(0, -1);
                            lastMoveTime = currentTime;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (currentTime - lastMoveTime >= MOVE_DELAY) {
                            currentLevel.movePlayer(0, 1);
                            lastMoveTime = currentTime;
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (currentTime - lastMoveTime >= MOVE_DELAY) {
                            currentLevel.movePlayer(-1, 0);
                            lastMoveTime = currentTime;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (currentTime - lastMoveTime >= MOVE_DELAY) {
                            currentLevel.movePlayer(1, 0);
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
                
                // Controles del Jugador 2 (WASD y SHIFT) - Solo en modo cooperativo
                if(currentLevel.hasTwoPlayers()){
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_W:
                            if (currentTime - lastMoveTimePlayer2 >= MOVE_DELAY) {
                                currentLevel.movePlayer2(0, -1);
                                lastMoveTimePlayer2 = currentTime;
                            }
                            break;
                        case KeyEvent.VK_S:
                            if (currentTime - lastMoveTimePlayer2 >= MOVE_DELAY) {
                                currentLevel.movePlayer2(0, 1);
                                lastMoveTimePlayer2 = currentTime;
                            }
                            break;
                        case KeyEvent.VK_A:
                            if (currentTime - lastMoveTimePlayer2 >= MOVE_DELAY) {
                                currentLevel.movePlayer2(-1, 0);
                                lastMoveTimePlayer2 = currentTime;
                            }
                            break;
                        case KeyEvent.VK_D:
                            if (currentTime - lastMoveTimePlayer2 >= MOVE_DELAY) {
                                currentLevel.movePlayer2(1, 0);
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
        Level currentLevel = game.getCurrentLevel();
        if (currentLevel == null) return;
        
        // Evitar spam de hielo
        long currentTime = System.currentTimeMillis();
        if (showingIceAnimation && (currentTime - iceAnimationStartTime) < ICE_ANIMATION_DURATION) {
            return; // Ya está creando hielo
        }
        
        IceCream player = currentLevel.getPlayer();
        Location playerLoc = player.getLocation();
        int dx = player.getLastDx();
        int dy = player.getLastDy();
        
        // Verificar si hay hielo adyacente en la dirección actual para romper
        Location targetLoc = playerLoc.move(dx, dy);
        if (currentLevel.getMap().hasIceWall(targetLoc)) {
            // Romper línea de hielo en esa dirección (inmediato, sin delay)
            Location checkLoc = targetLoc;
            while(currentLevel.getMap().hasIceWall(checkLoc)){
                currentLevel.breakIceWall(checkLoc);
                checkLoc = checkLoc.move(dx, dy);
            }
            return;
        }
        
        // Iniciar animación de hielo
        showingIceAnimation = true;
        iceAnimationStartTime = currentTime;
        
        // Crear el hielo después del delay (usando un Timer)
        Timer delayTimer = new Timer((int)ICE_ACTION_DELAY, e -> {
            currentLevel.createIceLine(dx, dy);
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
        Level currentLevel = game.getCurrentLevel();
        if (currentLevel == null) return;
        
        // Evitar spam de hielo
        long currentTime = System.currentTimeMillis();
        if (showingIceAnimationP2 && (currentTime - iceAnimationStartTimeP2) < ICE_ANIMATION_DURATION) {
            return; // Ya está creando hielo
        }
        
        IceCream player2 = currentLevel.getPlayer2();
        if(player2 == null) return;
        
        Location playerLoc = player2.getLocation();
        int dx = player2.getLastDx();
        int dy = player2.getLastDy();
        
        // Verificar si hay hielo adyacente en la dirección actual para romper
        Location targetLoc = playerLoc.move(dx, dy);
        if (currentLevel.getMap().hasIceWall(targetLoc)) {
            // Romper línea de hielo en esa dirección (inmediato, sin delay)
            Location checkLoc = targetLoc;
            while(currentLevel.getMap().hasIceWall(checkLoc)){
                currentLevel.breakIceWall(checkLoc);
                checkLoc = checkLoc.move(dx, dy);
            }
            return;
        }
        
        // Iniciar animación de hielo
        showingIceAnimationP2 = true;
        iceAnimationStartTimeP2 = currentTime;
        
        // Crear el hielo después del delay (usando un Timer)
        Timer delayTimer = new Timer((int)ICE_ACTION_DELAY, e -> {
            currentLevel.createIceLinePlayer2(dx, dy);
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
            // Si es modo máquina vs máquina, ejecutar IA
            if("Machine-vs-Machine".equals(gameMode) && aiController != null){
                Level currentLevel = game.getCurrentLevel();
                if(currentLevel != null && !game.isGameOver()){
                    IceCream player = currentLevel.getPlayer();
                    int[] move = aiController.decideMove(currentLevel, player);
                    
                    // Aplicar movimiento
                    if(move[0] != 0 || move[1] != 0){
                        currentLevel.movePlayer(move[0], move[1]);
                    }
                    
                    // Usar hielo si la IA lo decide
                    if(move[2] == 1){
                        handleIceAction();
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
     * Reinicia el nivel actual
     */
    private void restartCurrentLevel() {
        if (onRestartAction != null) {
            onRestartAction.run();
        }
    }
    
    /**
     * Reinicia el juego desde el nivel 1
     */
    private void restartFromLevel1() {
        if (onRestartAction != null) {
            onRestartAction.run();
        }
    }
    
    /**
     * Alterna entre pausa y reanudación
     */
    private void togglePause() {
        Level currentLevel = game.getCurrentLevel();
        if (currentLevel == null) return;
        
        if (currentLevel.isPaused()) {
            // Reanudar
            currentLevel.resume();
            pauseButton.setText("⏸ PAUSA");
            pauseButton.setBackground(new Color(52, 152, 219));
            gameTimer.start();
        } else {
            // Pausar
            currentLevel.pause();
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
        Level currentLevel = game.getCurrentLevel();
        if (currentLevel == null) return;
        
        int seconds = currentLevel.getRemainingSeconds();
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
