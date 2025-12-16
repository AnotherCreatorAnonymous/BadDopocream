package com.duran_jimenez.baddopocream.presentation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MenuPanel extends JPanel{
    private Image backgroundImage;
    private JButton playButton;
    private JButton optionsButton;
    private JButton creditsButton;
    private JButton exitButton;
    
    // Constantes de color
    private static final Color BUTTON_DEFAULT_COLOR = new Color(52, 152, 219);
    private static final Color BUTTON_HOVER_COLOR = new Color(41, 128, 185);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;
    private static final Color BUTTON_BORDER_COLOR = Color.WHITE;
    private static final Color BACKGROUND_FALLBACK = new Color(44, 62, 80);

    public MenuPanel(String imagePath){
        setLayout(new GridBagLayout());
        loadBackgroundImage(imagePath);
        createButtons();
    }

    private void loadBackgroundImage(String imagePath){
        try{
            java.io.InputStream imageStream = getClass().getResourceAsStream("/" + imagePath);
            if (imageStream != null) {
                backgroundImage = ImageIO.read(imageStream);
                imageStream.close();
            } else {
                throw new java.io.IOException("Recurso no encontrado: " + imagePath);
            }
        } catch (java.io.IOException e){
            com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions ex = 
                new com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions(
                    com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions.IMAGE_LOAD_ERROR + ": " + imagePath, e);
            System.err.println("⚠️ " + ex.getMessage());
            setBackground(BACKGROUND_FALLBACK);
        } catch (Exception e){
            com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions ex = 
                new com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions(
                    com.duran_jimenez.baddopocream.domain.BadDopoCream_Exceptions.UI_COMPONENT_INITIALIZATION_ERROR + 
                    ": MenuPanel - Fondo", e);
            System.err.println("⚠️ " + ex.getMessage());
            setBackground(BACKGROUND_FALLBACK);
        }
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if (backgroundImage != null){
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void createButtons(){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;  // Ocupa todo el ancho
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(15, 0, 15, 0);

        JLabel titleLabel = new JLabel("Bad Dopo Cream");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(titleLabel, gbc);

        playButton = createMenuButton("");
        gbc.gridy = 1;
        gbc.insets = new Insets(50, 0, 15, 0);
        add(playButton, gbc);

        // Configurar botones con imágenes usando clase utilitaria global
        ButtonImageConfigurator.configureImageButton(playButton, 
            "Recusos nuevos\\Botones jugar claro.png", 
            "Recusos nuevos\\Botones jugar oscuro.png");

        optionsButton = createMenuButton("");
        ButtonImageConfigurator.configureImageButton(optionsButton, 
            "Recusos nuevos\\Botones opciones claro.png", 
            "Recusos nuevos\\Botones opciones oscuro.png");
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 0, 15, 0);
        add(optionsButton, gbc);

        creditsButton = createMenuButton("");
        ButtonImageConfigurator.configureImageButton(creditsButton, 
            "Recusos nuevos\\Botones creditos claro.png", 
            "Recusos nuevos\\Botones creditos oscuro.png");
        gbc.gridy = 3;
        add(creditsButton, gbc);

        exitButton = createMenuButton("");
        // Nota: No hay imágenes para SALIR, se mantiene con estilo estándar
        // Si deseas agregar imágenes, descomenta y proporciona las rutas:
        ButtonImageConfigurator.configureImageButton(exitButton, 
            "Recusos nuevos\\Boton salir.png", 
            "Recusos nuevos\\Boton salir.png");
        gbc.gridy = 4;
        add(exitButton, gbc);
    }

    private JButton createMenuButton(String text){
        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(300, 60));
        button.setFocusPainted(false);
        button.setBorderPainted(true);

        button.setBackground(BUTTON_DEFAULT_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setBorder(BorderFactory.createLineBorder(BUTTON_BORDER_COLOR, 2));

        addHoverEffect(button);

        return button;
    }

    // Método separado para el efecto hover (más limpio y reutilizable)
    private void addHoverEffect(JButton button){
        button.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseEntered(MouseEvent evt){
                button.setBackground(BUTTON_HOVER_COLOR);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent evt){
                button.setBackground(BUTTON_DEFAULT_COLOR);
            }
        });
    }

    public void setPlayButtonListener(java.awt.event.ActionListener listener){
        if(playButton != null){
            playButton.addActionListener(listener);
        }
    }

    public void setOptionsButtonListener(java.awt.event.ActionListener listener){
        if(optionsButton != null){
            optionsButton.addActionListener(listener);
        }
    }

    public void setCreditsButtonListener(java.awt.event.ActionListener listener){
        if(creditsButton != null){
            creditsButton.addActionListener(listener);
        }
    }

    public void setExitButtonListener(java.awt.event.ActionListener listener){
        if(exitButton != null){
            exitButton.addActionListener(listener);
        }
    }
}





