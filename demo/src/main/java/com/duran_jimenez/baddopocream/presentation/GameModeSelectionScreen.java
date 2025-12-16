package com.duran_jimenez.baddopocream.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameModeSelectionScreen extends JPanel {

    private Image backgroundImage;
    private JButton normalModeButton;
    private JButton pvpModeButton;
    private JButton playerVsMachineButton;
    private JButton machineVsMachineButton;
    private JButton backButton;

    private Runnable onNormalMode;
    private Runnable onPvPMode;
    private Runnable onPlayerVsMachine;
    private Runnable onMachineVsMachine;
    private Runnable onBack;

    public GameModeSelectionScreen(Runnable onBack){
        this.onBack = onBack;
        setupPanel();
        createComponents();
        layoutComponents();
    }

    private void setupPanel(){
        setLayout(new BorderLayout());
        loadBackgroundImage("Fondo.png");
    }
    
    private void loadBackgroundImage(String imagePath) {
        try {
            java.io.InputStream imageStream = getClass().getResourceAsStream("/" + imagePath);
            if (imageStream != null) {
                backgroundImage = ImageIO.read(imageStream);
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
                    ": GameModeSelectionScreen - Fondo", e);
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

    private void createComponents(){
        normalModeButton = new JButton("");
        ButtonImageConfigurator.configureImageButton(normalModeButton, 
            "Recusos nuevos\\Versus.png", 
            "Recusos nuevos\\Versus.png", 
            300, 150);

        pvpModeButton = new JButton("");
        ButtonImageConfigurator.configureImageButton(pvpModeButton, 
            "Recusos nuevos\\versus jugador.png", 
            "Recusos nuevos\\versus jugador.png", 
            300, 150);

        playerVsMachineButton = new JButton("VS IA");
        styleCompetitiveButton(playerVsMachineButton);

        machineVsMachineButton = new JButton("");
        ButtonImageConfigurator.configureImageButton(machineVsMachineButton, 
            "Recusos nuevos\\versus maquina.png", 
            "Recusos nuevos\\versus maquina.png", 
            300, 150);

        backButton = new JButton("VOLVER");
        styleButton(backButton, new Color(80, 80, 100));

        backButton.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });
    }

    private void styleButton(JButton button, Color color){
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter(){
            Color originalColor = color;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt){
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt){
                button.setBackground(originalColor);
            }
        });
    }

    /**
     * Estilo especial para el botón de modo competitivo
     */
    private void styleCompetitiveButton(JButton button){
        button.setFont(new Font("Arial", Font.BOLD, 28));
        button.setBackground(new Color(200, 60, 60));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 100, 100), 3),
            BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 150));

        button.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt){
                button.setBackground(new Color(220, 80, 80));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt){
                button.setBackground(new Color(200, 60, 60));
            }
        });
    }

    private void layoutComponents(){
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel titleLabel = new JLabel("SELECCIONA EL MODO DE JUEGO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 2, 20, 20));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        centerPanel.add(normalModeButton);
        centerPanel.add(pvpModeButton);
        centerPanel.add(playerVsMachineButton);
        centerPanel.add(machineVsMachineButton);

        add(centerPanel, BorderLayout.CENTER);


        JPanel footerPanel = new JPanel( new FlowLayout(FlowLayout.CENTER, 0, 20));
        footerPanel.setOpaque(false);
        footerPanel.add(backButton);

        add(footerPanel, BorderLayout.SOUTH);
    }
    
    public void setNormalModeListener(Runnable action){
        this.onNormalMode = action;
        normalModeButton.addActionListener(e -> {
            if(action != null){
                action.run();
            }
        });
    }

    public void setPvPModeListener(Runnable action){
        this.onPvPMode = action;
        pvpModeButton.addActionListener(e -> {
            if(action != null){
                action.run();
            }
        });
    }

    public void setPlayerVsMachineListener(Runnable action){
        this.onPlayerVsMachine = action;
        playerVsMachineButton.addActionListener(e -> {
            if(action != null){
                action.run();
            }
        });
    }

    public void setMachineVsMachineListener(Runnable action){
        this.onMachineVsMachine = action;
        machineVsMachineButton.addActionListener(e -> {
            if(action != null){
                action.run();
            }
        });
    }
}