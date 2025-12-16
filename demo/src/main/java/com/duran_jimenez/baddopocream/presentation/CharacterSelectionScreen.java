package com.duran_jimenez.baddopocream.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CharacterSelectionScreen extends JPanel{
    
    private int playersCount;
    private ArrayList<String> selectedCharacters;
    private Image backgroundImage;

    private JButton startGameButton;
    private JButton strawberryIceCreamButton;
    private JButton vanillaIceCreamButton;
    private JButton chocolateIceCreamButton;

    private JButton confirmButton;
    private JButton backButton;

    private JLabel instructionLabel;
    private JLabel selectedLabel;

    private Runnable onStartGame;
    private Runnable onBack;
    private Runnable onConfirm;

    public CharacterSelectionScreen(int playersCount, Runnable onBack){
        this.playersCount = playersCount;
        this.onBack = onBack;
        this.selectedCharacters = new ArrayList<>();
        setupPanel();
        createComponents();
        layoutComponents();
        updateInstructions(); // Llamar después de crear los componentes
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
                    ": CharacterSelectionScreen - Fondo", e);
            System.err.println("⚠️ " + ex.getMessage());
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            setBackground(new Color(220, 20, 40));
        }
    }

    private void createComponents(){
        strawberryIceCreamButton = createCharacterButton("", new Color(255, 100, 150), "strawberry");
        vanillaIceCreamButton = createCharacterButton("", new Color(255, 255, 150), "vanilla");
        chocolateIceCreamButton = createCharacterButton("", new Color(150, 75, 0), "chocolate");

        // configuraion de imagenes 
        ButtonImageConfigurator.configureImageButton(strawberryIceCreamButton, 
            "Recusos nuevos\\Fresa.png", 
            "Recusos nuevos\\Fresa Feliz.png", 
            60,80);
        ButtonImageConfigurator.configureImageButton(vanillaIceCreamButton, 
            "Recusos nuevos\\Vainilla.png", 
            "Recusos nuevos\\Vainilla Feliz.png", 
            60,80);
        ButtonImageConfigurator.configureImageButton(chocolateIceCreamButton, 
            "Recusos nuevos\\Cocholate.png", 
            "Recusos nuevos\\Chocolate Feliz.png", 
            60,80);

        instructionLabel = new JLabel();
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 24));
        instructionLabel.setForeground(Color.WHITE);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        selectedLabel = new JLabel();
        selectedLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        selectedLabel.setForeground(new Color(200, 200, 200));
        selectedLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //Botones de accion
        confirmButton = new JButton ("CONFIRMAR SELECCION");
        styleActionButton(confirmButton, new Color(100, 200, 100));
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(e -> {
            if(onConfirm != null && selectedCharacters.size() == playersCount){
                onConfirm.run();
            }
        });

        backButton = new JButton("VOLVER");
        styleActionButton(backButton, new Color(80, 80, 100));
        backButton.addActionListener(e -> {
            if(onBack != null){
                onBack.run();
            }
        });
    }

    private JButton createCharacterButton(String text, Color color, String character){
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

        button.addActionListener(e -> toggleCharacterSelection(character, button, color));
        return button;
    }

    private void toggleCharacterSelection(String character, JButton button, Color color){
        if(selectedCharacters.contains(character)){
            // Deseleccionar
            selectedCharacters.remove(character);
            button.setBackground(color);
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        } else {
            // Verificar si ya se alcanzó el máximo
            if(selectedCharacters.size() >= playersCount){
                JOptionPane.showMessageDialog(this, "Ya has seleccionado el numero maximo de personajes.", 
                    "Seleccion Completa", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // En modo cooperativo (2 jugadores), no permitir seleccionar el mismo personaje
            // (El personaje solo puede estar en la lista una vez, lo cual ya está manejado)
            selectedCharacters.add(character);
            button.setBackground(color.darker());
            button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        }
    
        updateInstructions();
    }

    private void updateInstructions(){
        int remaining = playersCount - selectedCharacters.size();

        if(remaining > 0){
            instructionLabel.setText("Selecciona " + remaining + " helado(s).");
            selectedLabel.setText("Helados seleccionados: " + selectedCharacters.size() + "/" + playersCount);
            confirmButton.setEnabled(false);
        } else {
            instructionLabel.setText("Seleccion completa. Pulsa 'Confirmar Seleccion' para continuar.");
            selectedLabel.setText("Helados seleccionados:" + String.join(", ", selectedCharacters));
            confirmButton.setEnabled(true);
        }
    }

    private void styleActionButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 50));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = color;
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(color.brighter());
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
    }

    private void layoutComponents() {
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("SELECCIÓN DE PERSONAJES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(instructionLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(selectedLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Center - Grid de personajes
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 3, 20, 20));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
        
        centerPanel.add(chocolateIceCreamButton);
        centerPanel.add(strawberryIceCreamButton);
        centerPanel.add(vanillaIceCreamButton);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Footer - Botones de acción
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        footerPanel.setOpaque(false);
        footerPanel.add(backButton);
        footerPanel.add(confirmButton);
        
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Obtiene los personajes seleccionados
     */
    public ArrayList<String> getSelectedCharacters() {
        return new ArrayList<>(selectedCharacters);
    }

    /**
     * Configura la acción al confirmar
     */
    public void setConfirmListener(Runnable action) {
        this.onConfirm = action;
    }

    public void setBackListener(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBackListener'");
    }
}
