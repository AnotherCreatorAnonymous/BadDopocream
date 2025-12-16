package com.duran_jimenez.baddopocream.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Pantalla para seleccionar el nivel del juego
 * Muestra los niveles disponibles en forma de cuadrícula
 */
public class LevelSelectionScreen extends JPanel {
    
    private static final int MAX_LEVELS = com.duran_jimenez.baddopocream.domain.BadDopoCream.MAX_LEVELS; // Cantidad máxima de niveles
    private static final int UNLOCKED_LEVELS = com.duran_jimenez.baddopocream.domain.BadDopoCream.PLAYABLE_LEVELS; // Niveles desbloqueados inicialmente
    
    private Consumer<Integer> onLevelSelected; // Callback que recibe el número de nivel
    private Runnable onBack;
    private Image backgroundImage;
    
    private JLabel titleLabel;
    private JPanel levelsPanel;
    private JButton backButton;
    
    /**
     * Constructor
     * @param onBack Acción al presionar volver
     */
    public LevelSelectionScreen(Runnable onBack) {
        this.onBack = onBack;
        setupPanel();
        createComponents();
        layoutComponents();
    }
    
    /**
     * Configura las propiedades básicas del panel
     */
    private void setupPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        loadBackgroundImage("Fondo.png");
    }
    
    /**
     * Carga la imagen de fondo
     */
    private void loadBackgroundImage(String imagePath) {
        try {
            java.io.InputStream imageStream = getClass().getResourceAsStream("/" + imagePath);
            if (imageStream != null) {
                backgroundImage = javax.imageio.ImageIO.read(imageStream);
                imageStream.close();
            } else {
                throw new java.io.IOException("Recurso no encontrado: " + imagePath);
            }
        } catch (java.io.IOException e) {
            com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions ex = 
                new com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions(
                    com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions.IMAGE_LOAD_ERROR + ": " + imagePath, e);
            System.err.println("⚠️ " + ex.getMessage());
        } catch (Exception e) {
            com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions ex = 
                new com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions(
                    com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions.UI_COMPONENT_INITIALIZATION_ERROR + 
                    ": LevelSelectionScreen - Fondo", e);
            System.err.println("⚠️ " + ex.getMessage());
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            setBackground(new Color(20, 20, 40));
        }
    }
    
    /**
     * Crea todos los componentes visuales
     */
    private void createComponents() {
        // Título
        titleLabel = new JLabel("SELECCIONA UN NIVEL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(100, 200, 255));
        
        // Panel de niveles (cuadrícula)
        levelsPanel = new JPanel(new GridLayout(2, 5, 20, 20)); // 2 filas, 5 columnas
        levelsPanel.setOpaque(false);
        
        // Crear botones de nivel
        for (int i = 1; i <= MAX_LEVELS; i++) {
            JButton levelButton = createLevelButton(i, i <= UNLOCKED_LEVELS);
            levelsPanel.add(levelButton);
        }
        
        // Botón de volver
        backButton = new JButton("← VOLVER");
        styleBackButton(backButton);
        backButton.addActionListener(e -> onBack.run());
    }
    
    /**
     * Crea un botón de nivel individual
     * @param levelNumber Número del nivel
     * @param isUnlocked Si el nivel está desbloqueado
     * @return Botón configurado
     */
    private JButton createLevelButton(int levelNumber, boolean isUnlocked) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(120, 120));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        
        if (isUnlocked) {
            // Nivel desbloqueado
            button.setBackground(new Color(50, 100, 150));
            button.setBorder(BorderFactory.createLineBorder(new Color(100, 200, 255), 3));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Número del nivel (grande)
            JLabel numberLabel = new JLabel(String.valueOf(levelNumber), SwingConstants.CENTER);
            numberLabel.setFont(new Font("Arial", Font.BOLD, 48));
            numberLabel.setForeground(Color.WHITE);
            
            // Texto "NIVEL" (pequeño arriba)
            JLabel textLabel = new JLabel("NIVEL", SwingConstants.CENTER);
            textLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            textLabel.setForeground(new Color(200, 200, 200));
            
            // Estrellas o indicador de progreso (opcional)
            JLabel starsLabel = new JLabel("★ ★ ★", SwingConstants.CENTER);
            starsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            starsLabel.setForeground(new Color(255, 215, 0)); // Dorado
            
            button.add(textLabel, BorderLayout.NORTH);
            button.add(numberLabel, BorderLayout.CENTER);
            button.add(starsLabel, BorderLayout.SOUTH);
            
            // Listener para seleccionar nivel
            button.addActionListener(e -> {
                if (onLevelSelected != null) {
                    onLevelSelected.accept(levelNumber);
                }
            });
            
            // Efecto hover
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(70, 130, 180));
                    button.setBorder(BorderFactory.createLineBorder(new Color(150, 220, 255), 4));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(50, 100, 150));
                    button.setBorder(BorderFactory.createLineBorder(new Color(100, 200, 255), 3));
                }
            });
            
        } else {
            // Nivel bloqueado
            button.setBackground(new Color(60, 60, 60));
            button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 3));
            button.setEnabled(false);
            
            // Icono de candado
            JLabel lockLabel = new JLabel("🔒", SwingConstants.CENTER);
            lockLabel.setFont(new Font("Arial", Font.PLAIN, 48));
            lockLabel.setForeground(new Color(150, 150, 150));
            
            // Texto "BLOQUEADO"
            JLabel textLabel = new JLabel("BLOQUEADO", SwingConstants.CENTER);
            textLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            textLabel.setForeground(new Color(150, 150, 150));
            
            button.add(textLabel, BorderLayout.NORTH);
            button.add(lockLabel, BorderLayout.CENTER);
        }
        
        return button;
    }
    
    /**
     * Aplica estilo al botón de volver
     */
    private void styleBackButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(150, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(180, 70, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(150, 50, 50));
            }
        });
    }
    
    /**
     * Organiza los componentes en el panel
     */
    private void layoutComponents() {
        // Panel superior con título
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Panel central con niveles
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(levelsPanel);
        
        // Panel inferior con botón de volver
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(backButton);
        
        // Agregar todos los paneles
        add(topPanel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Establece el listener para cuando se selecciona un nivel
     * @param onLevelSelected Consumer que recibe el número de nivel seleccionado
     */
    public void setLevelSelectedListener(Consumer<Integer> onLevelSelected) {
        this.onLevelSelected = onLevelSelected;
    }
    
    /**
     * Actualiza qué niveles están desbloqueados
     * @param unlockedLevels Cantidad de niveles desbloqueados
     */
    public void setUnlockedLevels(int unlockedLevels) {
        // Remover todos los componentes actuales
        levelsPanel.removeAll();
        
        // Recrear botones con el nuevo estado
        for (int i = 1; i <= MAX_LEVELS; i++) {
            JButton levelButton = createLevelButton(i, i <= unlockedLevels);
            levelsPanel.add(levelButton);
        }
        
        // Refrescar la interfaz
        levelsPanel.revalidate();
        levelsPanel.repaint();
    }
}
