package com.duran_jimenez.baddopocream.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.duran_jimenez.baddopocream.domain.BadDopoCream;
import com.duran_jimenez.baddopocream.domain.IceCream;
import com.duran_jimenez.baddopocream.domain.Level;
import com.duran_jimenez.baddopocream.domain.LevelBuilder;
import com.duran_jimenez.baddopocream.domain.Location;

/**
 * Pantalla de configuración de nivel
 * Permite al jugador seleccionar qué frutas y enemigos aparecerán y en qué cantidad
 */
public class LevelConfigurationScreen extends JPanel {
    
    private Runnable onBackAction;
    private LevelStartCallback onStartLevel;
    private int levelNumber;
    private String player1Character;
    private String player2Character;
    private String gameMode;
    private BadDopoCream game;
    
    // Spinners para frutas
    private JSpinner bananaSpinner;
    private JSpinner cherrySpinner;
    private JSpinner grapesSpinner;
    private JSpinner pineappleSpinner;
    private JSpinner cactusSpinner;
    
    // Spinners para enemigos
    private JSpinner trollSpinner;
    private JSpinner macetaSpinner;
    private JSpinner calamarSpinner;
    private JSpinner narvalSpinner;
    
    // Spinners para obstáculos
    private JSpinner hotTileSpinner;
    private JSpinner campfireSpinner;
    
    // Labels informativos
    private JLabel totalFruitsLabel;
    private JLabel totalEnemiesLabel;
    private JLabel difficultyLabel;
    
    /**
     * Interface funcional para callback cuando se inicia el nivel
     */
    @FunctionalInterface
    public interface LevelStartCallback {
        void onLevelStart(Level level);
    }
    
    public LevelConfigurationScreen(BadDopoCream game, int levelNumber, 
                                   String player1Character, String player2Character,
                                   String gameMode, Runnable onBackAction,
                                   LevelStartCallback onStartLevel) {
        this.game = game;
        this.levelNumber = levelNumber;
        this.player1Character = player1Character;
        this.player2Character = player2Character;
        this.gameMode = gameMode;
        this.onBackAction = onBackAction;
        this.onStartLevel = onStartLevel;
        
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 40));
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Panel superior - Título
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Panel central - Configuración
        JPanel configPanel = createConfigurationPanel();
        add(configPanel, BorderLayout.CENTER);
        
        // Panel inferior - Botones
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(30, 30, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Configuración de Nivel " + levelNumber);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);
        
        return panel;
    }
    
    private JPanel createConfigurationPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(new Color(20, 20, 40));
        
        // Panel principal con scroll
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(20, 20, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // COLUMNA IZQUIERDA (x=0,1) - FRUTAS Y ENEMIGOS
        int leftRow = 0;
        
        // Sección: Frutas
        gbc.gridx = 0; gbc.gridy = leftRow++; gbc.gridwidth = 2;
        JLabel fruitsHeader = new JLabel("🍎 FRUTAS");
        fruitsHeader.setFont(new Font("Arial", Font.BOLD, 18));
        fruitsHeader.setForeground(new Color(255, 200, 100));
        mainPanel.add(fruitsHeader, gbc);
        
        gbc.gridwidth = 1;
        
        // Bananas
        leftRow = addSpinnerRowCompact(mainPanel, leftRow, "🍌 Bananas:", 0, 20, 4, gbc, 0);
        bananaSpinner = (JSpinner) mainPanel.getComponent(mainPanel.getComponentCount() - 1);
        
        // Cherries
        leftRow = addSpinnerRowCompact(mainPanel, leftRow, "🍒 Cerezas:", 0, 20, 3, gbc, 0);
        cherrySpinner = (JSpinner) mainPanel.getComponent(mainPanel.getComponentCount() - 1);
        
        // Grapes
        leftRow = addSpinnerRowCompact(mainPanel, leftRow, "🍇 Uvas:", 0, 20, 3, gbc, 0);
        grapesSpinner = (JSpinner) mainPanel.getComponent(mainPanel.getComponentCount() - 1);
        
        // Pineapples
        leftRow = addSpinnerRowCompact(mainPanel, leftRow, "🍍 Piñas:", 0, 15, 2, gbc, 0);
        pineappleSpinner = (JSpinner) mainPanel.getComponent(mainPanel.getComponentCount() - 1);
        
        // Cactus
        leftRow = addSpinnerRowCompact(mainPanel, leftRow, "🌵 Cactus:", 0, 10, 1, gbc, 0);
        cactusSpinner = (JSpinner) mainPanel.getComponent(mainPanel.getComponentCount() - 1);
        
        leftRow++; // Espacio
        
        // Sección: Enemigos
        gbc.gridx = 0; gbc.gridy = leftRow++; gbc.gridwidth = 2;
        JLabel enemiesHeader = new JLabel("👹 ENEMIGOS");
        enemiesHeader.setFont(new Font("Arial", Font.BOLD, 18));
        enemiesHeader.setForeground(new Color(255, 100, 100));
        mainPanel.add(enemiesHeader, gbc);
        
        gbc.gridwidth = 1;
        
        // Trolls
        leftRow = addSpinnerRowCompact(mainPanel, leftRow, "👹 Trolls:", 0, 10, 2, gbc, 0);
        trollSpinner = (JSpinner) mainPanel.getComponent(mainPanel.getComponentCount() - 1);
        
        // Macetas
        leftRow = addSpinnerRowCompact(mainPanel, leftRow, "🪴 Macetas:", 0, 10, 1, gbc, 0);
        macetaSpinner = (JSpinner) mainPanel.getComponent(mainPanel.getComponentCount() - 1);
        
        // Calamares
        leftRow = addSpinnerRowCompact(mainPanel, leftRow, "🦑 Calamares:", 0, 10, 1, gbc, 0);
        calamarSpinner = (JSpinner) mainPanel.getComponent(mainPanel.getComponentCount() - 1);
        
        // Narvales
        leftRow = addSpinnerRowCompact(mainPanel, leftRow, "🐋 Narvales:", 0, 8, 0, gbc, 0);
        narvalSpinner = (JSpinner) mainPanel.getComponent(mainPanel.getComponentCount() - 1);
        
        // COLUMNA DERECHA (x=3,4) - OBSTÁCULOS Y RESUMEN
        int rightRow = 0;
        
        // Sección: Obstáculos
        gbc.gridx = 3; gbc.gridy = rightRow++; gbc.gridwidth = 2;
        JLabel obstaclesHeader = new JLabel("🔥 OBSTÁCULOS");
        obstaclesHeader.setFont(new Font("Arial", Font.BOLD, 18));
        obstaclesHeader.setForeground(new Color(255, 150, 50));
        mainPanel.add(obstaclesHeader, gbc);
        
        gbc.gridwidth = 1;
        
        // Baldosas Calientes
        rightRow = addSpinnerRowCompact(mainPanel, rightRow, "🟥 Baldosas:", 0, 15, 3, gbc, 3);
        hotTileSpinner = (JSpinner) mainPanel.getComponent(mainPanel.getComponentCount() - 1);
        
        // Fogatas
        rightRow = addSpinnerRowCompact(mainPanel, rightRow, "🔥 Fogatas:", 0, 10, 2, gbc, 3);
        campfireSpinner = (JSpinner) mainPanel.getComponent(mainPanel.getComponentCount() - 1);
        
        rightRow++; // Espacio
        
        // Panel de resumen
        gbc.gridx = 3; gbc.gridy = rightRow++; gbc.gridwidth = 2;
        JPanel summaryPanel = createSummaryPanel();
        mainPanel.add(summaryPanel, gbc);
        
        // Agregar listeners para actualizar el resumen
        addChangeListeners();
        
        // Envolver en scroll
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBackground(new Color(20, 20, 40));
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        outerPanel.add(scrollPane, BorderLayout.CENTER);
        
        return outerPanel;
    }
    
    private int addSpinnerRow(JPanel panel, int row, String labelText, 
                             int min, int max, int initial, GridBagConstraints gbc) {
        // Label
        gbc.gridx = 0; gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(Color.WHITE);
        panel.add(label, gbc);
        
        // Spinner
        gbc.gridx = 1; gbc.gridy = row;
        SpinnerNumberModel model = new SpinnerNumberModel(initial, min, max, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(new Font("Arial", Font.PLAIN, 16));
        spinner.setPreferredSize(new Dimension(100, 30));
        panel.add(spinner, gbc);
        
        return row + 1;
    }
    
    private int addSpinnerRowCompact(JPanel panel, int row, String labelText, 
                             int min, int max, int initial, GridBagConstraints gbc, int startX) {
        // Label
        gbc.gridx = startX; gbc.gridy = row;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        panel.add(label, gbc);
        
        // Spinner
        gbc.gridx = startX + 1; gbc.gridy = row;
        SpinnerNumberModel model = new SpinnerNumberModel(initial, min, max, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(new Font("Arial", Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(80, 25));
        panel.add(spinner, gbc);
        
        return row + 1;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBackground(new Color(40, 40, 80));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 200), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        totalFruitsLabel = new JLabel("Total de Frutas: 13");
        totalFruitsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalFruitsLabel.setForeground(new Color(255, 255, 150));
        
        totalEnemiesLabel = new JLabel("Total de Enemigos: 4");
        totalEnemiesLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalEnemiesLabel.setForeground(new Color(255, 150, 150));
        
        difficultyLabel = new JLabel("Dificultad: Media");
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        difficultyLabel.setForeground(new Color(150, 255, 150));
        
        panel.add(totalFruitsLabel);
        panel.add(totalEnemiesLabel);
        panel.add(difficultyLabel);
        
        return panel;
    }
    
    private void addChangeListeners() {
        ActionListener updateListener = e -> updateSummary();
        
        // Agregar listeners a todos los spinners
        bananaSpinner.addChangeListener(e -> updateSummary());
        cherrySpinner.addChangeListener(e -> updateSummary());
        grapesSpinner.addChangeListener(e -> updateSummary());
        pineappleSpinner.addChangeListener(e -> updateSummary());
        cactusSpinner.addChangeListener(e -> updateSummary());
        
        trollSpinner.addChangeListener(e -> updateSummary());
        macetaSpinner.addChangeListener(e -> updateSummary());
        calamarSpinner.addChangeListener(e -> updateSummary());
        narvalSpinner.addChangeListener(e -> updateSummary());
        
        hotTileSpinner.addChangeListener(e -> updateSummary());
        campfireSpinner.addChangeListener(e -> updateSummary());
        
        // Actualizar resumen inicial
        updateSummary();
    }
    
    private void updateSummary() {
        int totalFruits = (int)bananaSpinner.getValue() + (int)cherrySpinner.getValue() +
                         (int)grapesSpinner.getValue() + (int)pineappleSpinner.getValue() +
                         (int)cactusSpinner.getValue();
        
        int totalEnemies = (int)trollSpinner.getValue() + (int)macetaSpinner.getValue() +
                          (int)calamarSpinner.getValue() + (int)narvalSpinner.getValue();
        
        totalFruitsLabel.setText("Total de Frutas: " + totalFruits);
        totalEnemiesLabel.setText("Total de Enemigos: " + totalEnemies);
        
        // Calcular dificultad
        String difficulty;
        Color difficultyColor;
        if (totalEnemies == 0) {
            difficulty = "Muy Fácil";
            difficultyColor = new Color(150, 255, 150);
        } else if (totalEnemies <= 3) {
            difficulty = "Fácil";
            difficultyColor = new Color(200, 255, 150);
        } else if (totalEnemies <= 6) {
            difficulty = "Media";
            difficultyColor = new Color(255, 255, 150);
        } else if (totalEnemies <= 10) {
            difficulty = "Difícil";
            difficultyColor = new Color(255, 200, 100);
        } else {
            difficulty = "Muy Difícil";
            difficultyColor = new Color(255, 100, 100);
        }
        
        difficultyLabel.setText("Dificultad: " + difficulty);
        difficultyLabel.setForeground(difficultyColor);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(new Color(30, 30, 60));
        
        // Botón Volver
        JButton backButton = createStyledButton("Volver", new Color(100, 100, 150));
        backButton.addActionListener(e -> {
            if (onBackAction != null) {
                onBackAction.run();
            }
        });
        panel.add(backButton);
        
        // Botón Iniciar Nivel
        JButton startButton = createStyledButton("Iniciar Nivel", new Color(100, 200, 100));
        startButton.addActionListener(e -> buildAndStartLevel());
        panel.add(startButton);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(200, 50));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void buildAndStartLevel() {
        try {
            // Validar que hay al menos una fruta
            int totalFruits = (int)bananaSpinner.getValue() + (int)cherrySpinner.getValue() +
                             (int)grapesSpinner.getValue() + (int)pineappleSpinner.getValue() +
                             (int)cactusSpinner.getValue();
            
            if (totalFruits == 0) {
                JOptionPane.showMessageDialog(this,
                    "Debe haber al menos una fruta en el nivel",
                    "Error de Configuración",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear jugadores con posiciones iniciales
            IceCream player1 = createIceCream(player1Character, new Location(2, 2));
            IceCream player2 = null;
            if (player2Character != null && !player2Character.isEmpty()) {
                player2 = createIceCream(player2Character, new Location(4, 2));
            }
            
            // Construir nivel usando el Builder
            LevelBuilder builder = LevelBuilder.createCustomLevel(levelNumber)
                .setDimensions(25, 18)
                .setTimeLimit(120)
                .setPlayer1(player1)
                .addBananas((int)bananaSpinner.getValue())
                .addCherries((int)cherrySpinner.getValue())
                .addGrapes((int)grapesSpinner.getValue())
                .addPineapples((int)pineappleSpinner.getValue())
                .addCactus((int)cactusSpinner.getValue())
                .addTrolls((int)trollSpinner.getValue())
                .addMacetas((int)macetaSpinner.getValue())
                .addCalamares((int)calamarSpinner.getValue())
                .addNarvales((int)narvalSpinner.getValue())
                .addHotTiles((int)hotTileSpinner.getValue())
                .addCampfires((int)campfireSpinner.getValue())
                .addIceWalls(15 + levelNumber * 5)
                .setPerimeterWalls(true)
                .enableWaveSystem();  // Sistema de oleadas siempre activo
            
            if (player2 != null) {
                builder.setPlayer2(player2);
            }
            
            Level level = builder.build();
            
            // Notificar que el nivel está listo
            if (onStartLevel != null) {
                onStartLevel.onLevelStart(level);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al construir el nivel: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private IceCream createIceCream(String characterName, Location startLocation) {
        String name = "";
        String color = "";
        
        switch (characterName.toLowerCase()) {
            case "fresa":
                name = "Fresa";
                color = "Pink";
                break;
            case "vainilla":
                name = "Vainilla";
                color = "Cream";
                break;
            case "chocolate":
                name = "Chocolate";
                color = "Brown";
                break;
            default:
                name = "Vainilla";
                color = "Cream";
        }
        
        return new IceCream(name, color, startLocation);
    }
}
