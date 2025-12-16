package com.duran_jimenez.baddopocream.presentation;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class OptionsScreen extends JPanel {
    
    public OptionsScreen(Runnable onBackAction){
        setLayout(new BorderLayout());
        setBackground(new Color(44, 62, 80));

        createComponents(onBackAction);
    }

    private void createComponents(Runnable onBackAction){
        
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(44, 62, 80));
        centerPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel titleLabel = new JLabel("OPCIONES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        centerPanel.add(titleLabel, gbc);

        JPanel optionsPanel = createOptionsPanel();
        gbc.gridy = 1;
        gbc.insets = new Insets(30, 15, 15, 15);
        centerPanel.add(optionsPanel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(52, 73, 94));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton backButton = new JButton("VOLVER AL MENU");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setPreferredSize(new Dimension(200, 40));
        backButton.addActionListener(e ->{
            if (onBackAction != null) {
                onBackAction.run();
            }
        });

        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createOptionsPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 20, 20));
        panel.setBackground(new Color(44, 62, 80));

        JLabel volumeLabel = new JLabel("VOLUMEN");
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        volumeLabel.setForeground(Color.WHITE);

        JSlider volumeSlider = new JSlider(0, 100, 50);
        volumeSlider.setBackground(new Color(44, 62, 80));
        volumeSlider.setForeground(Color.WHITE);

        JLabel fullscreenLabel = new JLabel ("PANTALLA COMPLETA");
        fullscreenLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        fullscreenLabel.setForeground(Color.WHITE);

        JCheckBox fullscreenCheck = new JCheckBox();
        fullscreenCheck.setBackground(new Color(44, 62, 80));

        panel.add(volumeLabel);
        panel.add(volumeSlider);
        panel.add(fullscreenLabel);
        panel.add(fullscreenCheck);

        return panel;
    }
}
