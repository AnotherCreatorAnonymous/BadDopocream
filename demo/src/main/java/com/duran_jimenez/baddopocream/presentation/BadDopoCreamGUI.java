package com.duran_jimenez.baddopocream.presentation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.duran_jimenez.baddopocream.domain.BadDopoCream;
import com.duran_jimenez.baddopocream.domain.GameState;

/**
 * Ventana principal del juego - Controlador en MVC
 * Coordina la comunicación entre la vista y el modelo
 */
public class BadDopoCreamGUI extends JFrame {
    
    // Instancia única del dominio (Backend)
    private BadDopoCream game;
    
    // Configuración actual del juego
    private String currentGameMode;
    private String currentPlayer1Character;
    private String currentPlayer2Character;
    private String currentEnemyCharacter;
    private int currentLevelNumber;
    
    // Componentes de la interfaz
    private KeyboardListener keyboardListener;
    private JPanel currentPanel;
    private JMenuBar menuBar;
    
    /**
     * Constructor - Inicializa la ventana en pantalla completa
     */
    public BadDopoCreamGUI() {
        // Crear instancia única del juego (Backend)
        this.game = new BadDopoCream();
        
        initializeFrame();
        createMenuBar();
        setupKeyboardListener();
        showWelcomeScreen();  // Primero mostrar pantalla de bienvenida
    }
    
    /**
     * Configura la ventana principal
     */
    private void initializeFrame() {
        setTitle("Bad Dopo Cream");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Obtener dimensiones de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Usar el 80% del tamaño de la pantalla
        int width = (int)(screenSize.width * 0.8);
        int height = (int)(screenSize.height * 0.8);
        setSize(width, height);
        
        // Centrar la ventana
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        setResizable(true); // Permitir redimensionar
    }
    
    /**
     * Configura el listener del teclado
     */
    private void setupKeyboardListener() {
        keyboardListener = new KeyboardListener();
        addKeyFocusable(keyboardListener);
    }
    
    /**
     * Añade el KeyListener y configura el foco
     */
    private void addKeyFocusable(KeyboardListener listener) {
        addKeyListener(listener);
        setFocusable(true);
        requestFocusInWindow();
    }
    
    /**
     * Muestra el menú principal (temporal)
     */
    private void showMainMenu() {
        MenuPanel menuPanel = new MenuPanel("Fondo Inicio.png");
        
        // Configurar listeners de los botones
        menuPanel.setPlayButtonListener(e -> showGameModeSelection());
        menuPanel.setOptionsButtonListener(e -> showOptionsScreen());
        menuPanel.setCreditsButtonListener(e -> showCreditsScreen());
        menuPanel.setExitButtonListener(e -> exitGame());
        
        changePanel(menuPanel);
    }
    
    /**
     * Muestra la pantalla de selección de modo de juego
     * Reinicia el juego para permitir comenzar desde el nivel 1
     */
    private void showGameModeSelection() {
        // Reiniciar el juego completamente para poder comenzar desde el nivel 1
        game.resetGame();
        
        GameModeSelectionScreen modeScreen = new GameModeSelectionScreen(() -> showMainMenu());
        
        // Configurar listeners para cada modo
        modeScreen.setNormalModeListener(() -> showNormalCharacterSelection());
        modeScreen.setPvPModeListener(() -> showCooperativeCharacterSelection());
        modeScreen.setMachineVsMachineListener(() -> showMachineVsMachineScreen());
        
        changePanel(modeScreen);
    }
    
    /**
     * Cambia el panel actual por uno nuevo
     * @param newPanel Nuevo panel a mostrar
     */
    public void changePanel(JPanel newPanel) {
        if (currentPanel != null) {
            remove(currentPanel);
        }
        
        currentPanel = newPanel;
        add(currentPanel, BorderLayout.CENTER);
        
        revalidate();
        repaint();
        requestFocusInWindow(); // Mantener el foco para el teclado
    }
    
    /**
     * Obtiene el listener del teclado
     * @return KeyboardListener
     */
    public KeyboardListener getKeyboardListener() {
        return keyboardListener;
    }
    
    /**
     * Crea el menú de juego
     */
    private void createMenuBar() {
        menuBar = new GameMenuBar(this);
        setJMenuBar(menuBar);
    }
    
    /**
     * Método main - Punto de entrada
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | 
                 IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e) {
            System.err.println("⚠️ No se pudo configurar el Look and Feel del sistema: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            BadDopoCreamGUI game = new BadDopoCreamGUI();
            game.setVisible(true);
        });
    }


    //═══════════════════════════════════════════════════════════════════════════
    // FLUJO DE NAVEGACIÓN - SELECCIÓN DE PERSONAJES Y MODOS
    //═══════════════════════════════════════════════════════════════════════════

    /**
     * Muestra selección de personaje para modo Normal (1 helado)
     */
    private void showNormalCharacterSelection() {
        CharacterSelectionScreen characterScreen = new CharacterSelectionScreen(1, () -> showGameModeSelection());
        
        characterScreen.setConfirmListener(() -> {
            ArrayList<String> selected = characterScreen.getSelectedCharacters();
            String player1Character = selected.isEmpty() ? "pink" : selected.get(0);
            showLevelSelection("Normal", player1Character, null, null);
        });
        
        changePanel(characterScreen);
    }
    
    /**
     * Muestra selección de personajes para modo Cooperativo (2 helados)
     */
    private void showCooperativeCharacterSelection() {
        CharacterSelectionScreen characterScreen = new CharacterSelectionScreen(2, () -> showGameModeSelection());
        
        characterScreen.setConfirmListener(() -> {
            ArrayList<String> selected = characterScreen.getSelectedCharacters();
            String player1Character = selected.size() > 0 ? selected.get(0) : "pink";
            String player2Character = selected.size() > 1 ? selected.get(1) : "vanilla";
            showLevelSelection("PvP-Cooperativo", player1Character, player2Character, null);
        });
        // Configurar botón de volver
        /*
        characterScreen.setBackListener(e -> {
            System.out.println("✓ Botón VOLVER presionado - regresando a PvP Mode Selection");
            showGameModeSelection();
        });
        */
        changePanel(characterScreen);
    }
    
    /**
     * Muestra la pantalla de selección de nivel
     * @param gameMode Modo de juego
     * @param player1Character Personaje del jugador 1
     * @param player2Character Personaje del jugador 2 (puede ser null)
     * @param enemyCharacter Enemigo controlado (puede ser null)
     */
    private void showLevelSelection(String gameMode, String player1Character, String player2Character, String enemyCharacter) {
        LevelSelectionScreen levelScreen = new LevelSelectionScreen(() -> {
            // Volver a la selección de personajes según el modo
            if (gameMode.equals("Normal")) {
                showNormalCharacterSelection();
            } else if (gameMode.equals("PvP-Cooperativo")) {
                showCooperativeCharacterSelection();
            } else if (gameMode.equals("Machine-vs-Machine")) {
                showGameModeSelection();
            }
        });
        
        // Cuando se selecciona un nivel, ir a la CONFIGURACIÓN del nivel (no directamente al juego)
        levelScreen.setLevelSelectedListener(levelNumber -> {
            showLevelConfigurationScreen(gameMode, player1Character, player2Character, enemyCharacter, levelNumber);
        });
        
        changePanel(levelScreen);
    }
    
    /**
     * Muestra la pantalla de configuración del nivel
     * Permite al jugador elegir frutas y enemigos antes de iniciar
     */
    private void showLevelConfigurationScreen(String gameMode, String player1Character, 
                                             String player2Character, String enemyCharacter, 
                                             int levelNumber) {
        LevelConfigurationScreen configScreen = new LevelConfigurationScreen(
            game,
            levelNumber,
            player1Character,
            player2Character,
            gameMode,
            // Callback para volver
            () -> showLevelSelection(gameMode, player1Character, player2Character, enemyCharacter),
            // Callback cuando el nivel está construido
            (level) -> {
                // Asignar el nivel construido al juego
                game.setCurrentLevel(level);
                
                // Mostrar la pantalla de juego
                showGameScreenWithLevel(gameMode, player1Character, player2Character, enemyCharacter, levelNumber, level);
            }
        );
        
        changePanel(configScreen);
    }
    
    /**
     * Muestra la pantalla de juego con el nivel ya construido
     * @param gameMode Modo de juego
     * @param player1Character Personaje del jugador 1
     * @param player2Character Personaje del jugador 2 (puede ser null)
     * @param enemyCharacter Personaje enemigo (puede ser null)
     * @param levelNumber Número del nivel
     * @param level Nivel ya construido con la configuración del usuario
     */
    private void showGameScreenWithLevel(String gameMode, String player1Character, 
                                        String player2Character, String enemyCharacter, 
                                        int levelNumber, com.duran_jimenez.baddopocream.domain.Level level) {
        // Guardar configuración actual
        this.currentGameMode = gameMode;
        this.currentPlayer1Character = player1Character;
        this.currentPlayer2Character = player2Character;
        this.currentEnemyCharacter = enemyCharacter;
        this.currentLevelNumber = levelNumber;
        
        // Información de depuración
        System.out.println("\n══════════════════════════════════");
        System.out.println("Iniciando juego:");
        System.out.println("Modo: " + gameMode);
        System.out.println("Nivel: " + levelNumber);
        System.out.println("Jugador 1: " + player1Character);
        if (player2Character != null) {
            System.out.println("Jugador 2: " + player2Character);
        }
        if (enemyCharacter != null) {
            System.out.println("Enemigo controlado: " + enemyCharacter);
        }
        System.out.println("Backend inicializado: " + (game.getCurrentLevel() != null));
        System.out.println("Frutas en nivel: " + level.getFruits().size());
        System.out.println("Enemigos en nivel: " + level.getEnemies().size());
        System.out.println("═══════════════════════════════════\n");
        
        // Crear y mostrar GameScreen pasando la instancia del juego
        GameScreen gameScreen = new GameScreen(
            () -> showGameModeSelection(),  // onBack
            () -> restartLevel(),            // onRestart (reiniciar solo muestra la config de nuevo)
            () -> continueToNextLevel(),     // onNextLevel (avanzar al siguiente nivel)
            game,
            currentGameMode                   // gameMode
        );
        gameScreen.setPlayerColor(player1Character);
        changePanel(gameScreen);
    }
    
    /**
     * Continúa al siguiente nivel después de completar el actual
     * Genera el siguiente nivel usando la configuración anterior
     */
    private void continueToNextLevel() {
        int nextLevelNumber = game.getCurrentLevelNumber();
        showLevelConfigurationScreen(currentGameMode, currentPlayer1Character, 
                                     currentPlayer2Character, currentEnemyCharacter, 
                                     nextLevelNumber);
    }
    
    /**
     * Reinicia el nivel actual mostrando la pantalla de configuración
     */
    private void restartLevel() {
        showLevelConfigurationScreen(currentGameMode, currentPlayer1Character, 
                                     currentPlayer2Character, currentEnemyCharacter, 
                                     currentLevelNumber);
    }
    
    /**
     * Reinicia el juego completo desde el nivel 1
     */
    private void restartGame() {
        // Obtener el nivel actual del backend antes de recrear
        int currentLevel = game.getCurrentLevelNumber();
        int currentScore = game.getTotalScore();
        boolean gameWon = game.isGameWon();
        
        // Si el juego fue ganado completamente, reiniciar desde nivel 1
        // Si fue game over, reiniciar el nivel actual
        int levelToRestart = gameWon ? 1 : currentLevel;
        
        // Reinicializar el backend con la misma configuración DESDE EL NIVEL 1
        game = new com.duran_jimenez.baddopocream.domain.BadDopoCream();  // Crear nueva instancia
        initializeGameInBackend(currentGameMode, currentPlayer1Character, 
                                currentPlayer2Character, currentEnemyCharacter, 
                                1); // Siempre empezar desde nivel 1
        
        // Avanzar hasta el nivel objetivo
        for(int i = 1; i < levelToRestart; i++){
            if(game.hasNextLevel()){
                game.nextLevel();
            }
        }
        
        // Restaurar el puntaje acumulado SOLO si no es victoria total
        if(!gameWon){
            game.setTotalScore(currentScore);
        }
        
        // Mostrar nueva pantalla de juego
        GameScreen gameScreen = new GameScreen(
            () -> showGameModeSelection(),  // onBack
            () -> restartGame(),              // onRestart
            () -> continueToNextLevel(),      // onNextLevel
            game,
            currentGameMode                   // gameMode
        );
        gameScreen.setPlayerColor(currentPlayer1Character);
        changePanel(gameScreen);
    }
    
    /**
     * Carga una partida desde un GameState
     * Recrea completamente el juego como si se iniciara desde el menú
     */
    public void loadGameFromState(GameState state) {
        if (state == null) return;
        
        // Actualizar configuración actual con los datos del save
        this.currentGameMode = state.getGameMode();
        this.currentPlayer1Character = state.getPlayerColor();
        this.currentPlayer2Character = null; // Por ahora solo single player
        this.currentEnemyCharacter = null;
        this.currentLevelNumber = state.getCurrentLevelIndex() + 1;
        
        // Crear nueva instancia del juego
        game = new com.duran_jimenez.baddopocream.domain.BadDopoCream();
        
        // Inicializar el backend desde el nivel 1
        initializeGameInBackend(currentGameMode, currentPlayer1Character, 
                                currentPlayer2Character, currentEnemyCharacter, 1);
        
        // Restaurar el estado completo desde el save
        game.restoreFromState(state);
        
        // Crear y mostrar GameScreen con el estado cargado
        GameScreen gameScreen = new GameScreen(
            () -> showGameModeSelection(),  // onBack
            () -> restartGame(),              // onRestart
            () -> continueToNextLevel(),      // onNextLevel
            game,
            currentGameMode
        );
        gameScreen.setPlayerColor(currentPlayer1Character);
        changePanel(gameScreen);
    }
    
    /**
     * Muestra la pantalla de juego - Modo Máquina vs Máquina
     */
    private void showMachineVsMachineScreen() {
        // En este modo no se seleccionan personajes, la máquina elige automáticamente
        // Pero sí se selecciona el nivel
        showLevelSelection("Machine-vs-Machine", "Auto", "Auto", null);
    }
    
    //══════════════════════════════════════════════════════════════════════════════
    // COMUNICACIÓN CON EL BACKEND (DOMINIO)
    //══════════════════════════════════════════════════════════════════════════════
    
    /**
     * Inicializa el juego en el backend con la configuración seleccionada
     * Este es el ÚNICO punto de comunicación GUI -> Dominio
     */
    private void initializeGameInBackend(String gameMode, String player1Character, 
                                        String player2Character, String enemyCharacter, 
                                        int levelNumber) {
        // Crear el(los) jugador(es) según el modo
        com.duran_jimenez.baddopocream.domain.IceCream player1 = createIceCream(player1Character, new com.duran_jimenez.baddopocream.domain.Location(2, 2));
        
        // Usar LevelConfigurator para crear el nivel "quemado"
        com.duran_jimenez.baddopocream.domain.Level level = com.duran_jimenez.baddopocream.domain.LevelConfigurator.createLevel(levelNumber, player1);
        
        // En modo cooperativo, agregar segundo jugador
        if("Cooperative".equals(gameMode) && player2Character != null){
            com.duran_jimenez.baddopocream.domain.IceCream player2 = createIceCream(player2Character, new com.duran_jimenez.baddopocream.domain.Location(4, 2));
            level.setPlayer2(player2);
        }
        
        // TODO: En modo versus, el enemigo seleccionado será controlado por jugador 2
        
        // Agregar el nivel al juego
        game.addLevel(level);
        
        // Iniciar el juego
        game.startGame();
    }
    
    /**
     * Crea un nivel según el número especificado
     * @param levelNumber Número del nivel (1-10)
     * @return Nivel configurado
     */
    private com.duran_jimenez.baddopocream.domain.Level createLevel(int levelNumber) {
        // Por ahora, crear un nivel de prueba
        // TODO: Implementar configuraciones específicas para cada nivel
        com.duran_jimenez.baddopocream.domain.Level level = new com.duran_jimenez.baddopocream.domain.Level(levelNumber, 20, 15);
        
        // Agregar paredes de ejemplo
        for (int x = 0; x < 20; x++) {
            level.addWall(new com.duran_jimenez.baddopocream.domain.Location(x, 0));
            level.addWall(new com.duran_jimenez.baddopocream.domain.Location(x, 14));
        }
        for (int y = 1; y < 14; y++) {
            level.addWall(new com.duran_jimenez.baddopocream.domain.Location(0, y));
            level.addWall(new com.duran_jimenez.baddopocream.domain.Location(19, y));
        }
        
        // Agregar algunas paredes internas
        for (int x = 5; x < 10; x++) {
            level.addWall(new com.duran_jimenez.baddopocream.domain.Location(x, 7));
        }
        
        // Agregar frutas de ejemplo
        level.addFruit(new com.duran_jimenez.baddopocream.domain.Banana(new com.duran_jimenez.baddopocream.domain.Location(3, 3)));
        level.addFruit(new com.duran_jimenez.baddopocream.domain.Cherry(new com.duran_jimenez.baddopocream.domain.Location(16, 3)));
        level.addFruit(new com.duran_jimenez.baddopocream.domain.Grapes(new com.duran_jimenez.baddopocream.domain.Location(3, 11)));
        level.addFruit(new com.duran_jimenez.baddopocream.domain.Pineapple(new com.duran_jimenez.baddopocream.domain.Location(16, 11)));
        
        // Agregar enemigos de ejemplo
        level.addEnemy(new com.duran_jimenez.baddopocream.domain.Maceta(new com.duran_jimenez.baddopocream.domain.Location(10, 5)));
        level.addEnemy(new com.duran_jimenez.baddopocream.domain.Troll(new com.duran_jimenez.baddopocream.domain.Location(10, 10)));
        
        return level;
    }
    
    /**
     * Crea una instancia de IceCream según el color seleccionado
     * @param characterName Nombre del personaje ("pink", "vanilla", "chocolate")
     * @param startLocation Ubicación inicial
     * @return Instancia de IceCream
     */
    private com.duran_jimenez.baddopocream.domain.IceCream createIceCream(String characterName, com.duran_jimenez.baddopocream.domain.Location startLocation) {
        // Mapear nombres de personajes a colores
        String name;
        String color;
        
        if (characterName != null) {
            if (characterName.toLowerCase().contains("rosa") || characterName.toLowerCase().contains("pink")) {
                name = "IceCream_Pink";
                color = "pink";
            } else if (characterName.toLowerCase().contains("vainilla") || characterName.toLowerCase().contains("vanilla")) {
                name = "IceCream_Vanilla";
                color = "vanilla";
            } else if (characterName.toLowerCase().contains("chocolate")) {
                name = "IceCream_Chocolate";
                color = "chocolate";
            } else {
                name = "IceCream_Pink";
                color = "pink"; // Por defecto
            }
        } else {
            name = "IceCream_Pink";
            color = "pink";
        }
        
        return new com.duran_jimenez.baddopocream.domain.IceCream(name, color, startLocation);
    }
    
    /**
     * Obtiene la instancia única del juego (Backend)
     * @return Instancia de BadDopoCream
     */
    public com.duran_jimenez.baddopocream.domain.BadDopoCream getGame() {
        return this.game;
    }

    /**
     * Muestra la pantalla de opciones
     */
    private void showOptionsScreen() {
        OptionsScreen optionsScreen = new OptionsScreen(() -> showMainMenu());
        changePanel(optionsScreen);
    }

    /**
     * Muestra los créditos del juego mediante un popup
     */
    private void showCreditsScreen() {
        String credits = "═══════════════════════════\n" +
                        "      BAD DOPO CREAM\n" +
                        "═══════════════════════════\n\n" +
                        "Desarrollado por:\n\n" +
                        "ROGER DURAN\n" +
                        "CARLOS JIMENEZ\n\n" +
                        "Versión 1.0\n\n" +
                        "ECI - 2025";
        
        JOptionPane.showMessageDialog(
            this,
            credits,
            "Créditos",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Confirma y cierra el juego
     */
    private void exitGame() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea salir del juego?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Muestra la pantalla de bienvenida inicial
     */
    private void showWelcomeScreen() {
        WelcomeScreen welcomeScreen = new WelcomeScreen(() -> {
            // Cuando el usuario haga clic en cualquier parte, ir al menú principal
            showMainMenu();
        });
        
        changePanel(welcomeScreen);
    }
}

