package com.duran_jimenez.baddopocream.presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Pantalla para seleccionar 1 helado y 1 enemigo (modo confrontación PvP)
 * Jugador 1 controla el helado, Jugador 2 controla el enemigo
 */
public class PvPCharacterSelectionScreen extends JPanel {
    private String selectedIceCream;
    private String selectedEnemy;
    
    // Botones para helados
    private JButton pinkIceCreamButton;
    private JButton vanillaIceCreamButton;
    private JButton chocolateIceCreamButton;
    
    // Botones para enemigos
    private JButton macetaButton;
    private JButton trollButton;
    private JButton calamarButton;
    
    private JLabel instructionsLabel;
    private JLabel selectedLabel;
    private JButton confirmButton;
    private JButton backButton;
    
    public PvPCharacterSelectionScreen() {
        setupPanel();
        createComponents();
        layoutComponents();
        updateInstructions();
    }
    
    /**
     * Configura las propiedades básicas del panel
     */
    private void setupPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(20, 20, 40));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
    }
    
    /**
     * Crea todos los componentes visuales
     */
    private void createComponents() {
        // Título
        instructionsLabel = new JLabel("MODO CONFRONTACIÓN", SwingConstants.CENTER);
        instructionsLabel.setFont(new Font("Arial", Font.BOLD, 32));
        instructionsLabel.setForeground(new Color(255, 100, 100));
        
        // Label para mostrar selecciones actuales
        selectedLabel = new JLabel();
        selectedLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        selectedLabel.setForeground(new Color(200, 200, 200));
        selectedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Crear botones de helados
        pinkIceCreamButton = createCharacterButton("Helado Rosa", new Color(255, 100, 150), true);
        vanillaIceCreamButton = createCharacterButton("Helado Vainilla", new Color(255, 255, 150), true);
        chocolateIceCreamButton = createCharacterButton("Helado Chocolate", new Color(150, 75, 0), true);
        //Configurar imagenes de los botones de helados
        ButtonImageConfigurator.configureImageButton(pinkIceCreamButton, 
            "Recusos nuevos\\Fresa.png", 
            "Personajes\\Fresa\\Victoria animation.gif", 60, 80);
        ButtonImageConfigurator.configureImageButton(vanillaIceCreamButton, 
            "Recusos nuevos\\Vainilla.png", 
            "Personajes\\Vainilla\\Victoria animation.gif", 60, 80);
        ButtonImageConfigurator.configureImageButton(chocolateIceCreamButton, 
            "Recusos nuevos\\Cocholate.png", 
            "Personajes\\Chocolate\\Victoria animation.gif", 60, 80);
        

        // Crear botones de enemigos
        macetaButton = createCharacterButton("Maceta", new Color(100, 200, 100), false);
        trollButton = createCharacterButton("Troll", new Color(200, 100, 200), false);
        calamarButton = createCharacterButton("Calamar", new Color(255, 150, 50), false);
        
        //Configurar imagenes de los botones de enemigos
        ButtonImageConfigurator.configureImageButton(macetaButton, 
            "Recusos nuevos\\Maceta enemigo.png", 
            "Enemigos\\Maceta\\Ataque animation.gif", 80, 80);
        ButtonImageConfigurator.configureImageButton(trollButton, 
            "Recusos nuevos\\Troll.png", 
            "Troll\\Caminar Abajo animation.gif", 80, 70);
        ButtonImageConfigurator.configureImageButton(calamarButton, 
            "Recusos nuevos\\Calamar Amarillo.png", 
            "Calamar Amarillo\\Caminando Abajo animation.gif", 60, 80);
        // Botón de volver
        backButton = new JButton("VOLVER");
        styleActionButton(backButton, new Color(80, 80, 100));
        backButton.setEnabled(true);

        // Botón de confirmar (inicialmente deshabilitado)
        confirmButton = new JButton("CONFIRMAR SELECCIÓN");
        styleActionButton(confirmButton, new Color(50, 150, 50));
        confirmButton.setEnabled(false);
        
    }
    
    /**
     * Crea un botón de personaje con estilo consistente
     * @param text Texto del botón
     * @param color Color del personaje
     * @param isIceCream true si es helado, false si es enemigo
     * @return Botón configurado
     */
    private JButton createCharacterButton(String text, Color color, boolean isIceCream) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        button.setPreferredSize(new Dimension(200, 80));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Agregar listener según tipo
        if (isIceCream) {
            button.addActionListener(e -> toggleIceCreamSelection(text, button, color));
        } else {
            button.addActionListener(e -> toggleEnemySelection(text, button, color));
        }
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!isSelected(button)) {
                    button.setBackground(color.brighter());
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!isSelected(button)) {
                    button.setBackground(color);
                }
            }
        });
        
        return button;
    }
    
    /**
     * Maneja la selección de helados
     */
    private void toggleIceCreamSelection(String character, JButton button, Color originalColor) {
        // Si ya está seleccionado, deseleccionar
        if (selectedIceCream != null && selectedIceCream.equals(character)) {
            selectedIceCream = null;
            button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        } else {
            // Deseleccionar helado anterior
            if (selectedIceCream != null) {
                resetIceCreamBorder(selectedIceCream);
            }
            // Seleccionar nuevo helado
            selectedIceCream = character;
            button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
        }
        updateInstructions();
    }
    
    /**
     * Maneja la selección de enemigos
     */
    private void toggleEnemySelection(String character, JButton button, Color originalColor) {
        // Si ya está seleccionado, deseleccionar
        if (selectedEnemy != null && selectedEnemy.equals(character)) {
            selectedEnemy = null;
            button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        } else {
            // Deseleccionar enemigo anterior
            if (selectedEnemy != null) {
                resetEnemyBorder(selectedEnemy);
            }
            // Seleccionar nuevo enemigo
            selectedEnemy = character;
            button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5));
        }
        updateInstructions();
    }
    
    /**
     * Resetea el borde de un botón de helado
     */
    private void resetIceCreamBorder(String character) {
        JButton button = getIceCreamButton(character);
        if (button != null) {
            button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        }
    }
    
    /**
     * Resetea el borde de un botón de enemigo
     */
    private void resetEnemyBorder(String character) {
        JButton button = getEnemyButton(character);
        if (button != null) {
            button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
        }
    }
    
    /**
     * Obtiene el botón correspondiente a un helado
     */
    private JButton getIceCreamButton(String character) {
        if (character.contains("Rosa")) return pinkIceCreamButton;
        if (character.contains("Vainilla")) return vanillaIceCreamButton;
        if (character.contains("Chocolate")) return chocolateIceCreamButton;
        return null;
    }
    
    /**
     * Obtiene el botón correspondiente a un enemigo
     */
    private JButton getEnemyButton(String character) {
        if (character.contains("Maceta")) return macetaButton;
        if (character.contains("Troll")) return trollButton;
        if (character.contains("Calamar")) return calamarButton;
        return null;
    }
    
    /**
     * Verifica si un botón está seleccionado
     */
    private boolean isSelected(JButton button) {
        return button.getBorder() instanceof javax.swing.border.LineBorder &&
                ((javax.swing.border.LineBorder)button.getBorder()).getLineColor().equals(Color.YELLOW);
    }
    
    /**
     * Actualiza las instrucciones y habilita/deshabilita el botón de confirmar
     */
    private void updateInstructions() {
        StringBuilder sb = new StringBuilder();
        
        if (selectedIceCream == null) {
            sb.append("Jugador 1: Selecciona tu helado");
        } else {
            sb.append("Jugador 1: ").append(selectedIceCream);
        }
        
        sb.append("  |  ");
        
        if (selectedEnemy == null) {
            sb.append("Jugador 2: Selecciona tu enemigo");
        } else {
            sb.append("Jugador 2: ").append(selectedEnemy);
        }
        
        selectedLabel.setText(sb.toString());
        
        // Habilitar botón solo si ambos están seleccionados
        confirmButton.setEnabled(selectedIceCream != null && selectedEnemy != null);
    }
    
    /**
     * Organiza los componentes en el panel
     */
    private void layoutComponents() {
        // Panel superior con título e instrucciones
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        topPanel.setOpaque(false);
        topPanel.add(instructionsLabel);
        topPanel.add(selectedLabel);
        
        // Panel central con dos secciones
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        centerPanel.setOpaque(false);
        
        // Sección de helados
        JPanel iceCreamPanel = new JPanel();
        iceCreamPanel.setLayout(new BoxLayout(iceCreamPanel, BoxLayout.Y_AXIS));
        iceCreamPanel.setOpaque(false);
        
        JLabel iceCreamTitle = new JLabel("HELADOS (Jugador 1)", SwingConstants.CENTER);
        iceCreamTitle.setFont(new Font("Arial", Font.BOLD, 20));
        iceCreamTitle.setForeground(new Color(100, 200, 255));
        iceCreamTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        iceCreamPanel.add(iceCreamTitle);
        iceCreamPanel.add(Box.createVerticalStrut(20));
        
        pinkIceCreamButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        vanillaIceCreamButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        chocolateIceCreamButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        iceCreamPanel.add(pinkIceCreamButton);
        iceCreamPanel.add(Box.createVerticalStrut(15));
        iceCreamPanel.add(vanillaIceCreamButton);
        iceCreamPanel.add(Box.createVerticalStrut(15));
        iceCreamPanel.add(chocolateIceCreamButton);
        
        // Sección de enemigos
        JPanel enemyPanel = new JPanel();
        enemyPanel.setLayout(new BoxLayout(enemyPanel, BoxLayout.Y_AXIS));
        enemyPanel.setOpaque(false);
        
        JLabel enemyTitle = new JLabel("ENEMIGOS (Jugador 2)", SwingConstants.CENTER);
        enemyTitle.setFont(new Font("Arial", Font.BOLD, 20));
        enemyTitle.setForeground(new Color(255, 100, 100));
        enemyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        enemyPanel.add(enemyTitle);
        enemyPanel.add(Box.createVerticalStrut(20));
        
        macetaButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        trollButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calamarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        enemyPanel.add(macetaButton);
        enemyPanel.add(Box.createVerticalStrut(15));
        enemyPanel.add(trollButton);
        enemyPanel.add(Box.createVerticalStrut(15));
        enemyPanel.add(calamarButton);
        
        centerPanel.add(iceCreamPanel);
        centerPanel.add(enemyPanel);
        
        // Panel inferior con botones de confirmar y volver
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.add(confirmButton);
        bottomPanel.add(backButton);
        
        // Agregar todos los paneles
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Aplica estilo a los botones de acción
     */
    private void styleActionButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(300, 60));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }
    
    /**
     * Establece el listener para el botón de confirmar
     */
    public void setConfirmListener(ActionListener listener) {
        confirmButton.addActionListener(listener);
    }
    
    /**
     * Establece el listener para el botón de volver
     */
    public void setBackListener(ActionListener listener) {
        backButton.addActionListener(listener);
    }
    
    /**
     * Obtiene el helado seleccionado
     */
    public String getSelectedIceCream() {
        return selectedIceCream;
    }
    
    /**
     * Obtiene el enemigo seleccionado
     */
    public String getSelectedEnemy() {
        return selectedEnemy;
    }
}




