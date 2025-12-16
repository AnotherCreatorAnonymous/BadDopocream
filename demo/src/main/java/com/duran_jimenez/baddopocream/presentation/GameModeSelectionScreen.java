package com.duran_jimenez.baddopocream.presentation;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GameModeSelectionScreen extends JPanel {

    private Image backgroundImage;
    private JButton normalModeButton;
    private JButton pvpModeButton;
    private JButton machineVsMachineButton;
    private JButton backButton;

    private Runnable onNormalMode;
    private Runnable onPvPMode;
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
        centerPanel.setLayout(new GridLayout(1, 3, 20, 20));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 40));

        centerPanel.add(normalModeButton);
        centerPanel.add(pvpModeButton);
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

    public void setMachineVsMachineListener(Runnable action){
        this.onMachineVsMachine = action;
        machineVsMachineButton.addActionListener(e -> {
            if(action != null){
                action.run();
            }
        });
    }
}