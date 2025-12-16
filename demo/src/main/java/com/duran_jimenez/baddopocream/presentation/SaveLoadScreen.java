package com.duran_jimenez.baddopocream.presentation;

import com.duran_jimenez.baddopocream.domain.BadDopoCream;
import com.duran_jimenez.baddopocream.domain.GameState;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;

/**
 * Pantalla para guardar y cargar partidas
 */
public class SaveLoadScreen extends JPanel {
    
    private BadDopoCream game;
    private String playerColor;
    private String gameMode;
    private JFrame parentFrame;
    private Runnable onClose;
    private java.util.function.Consumer<GameState> onLoadSuccess; // Callback con el GameState cargado
    private boolean isSaveMode; // true = guardar, false = cargar
    
    private JPanel savesPanel;
    private JTextField saveNameField;
    
    public SaveLoadScreen(BadDopoCream game, String playerColor, String gameMode, 
                         JFrame parentFrame, boolean isSaveMode, Runnable onClose) {
        this(game, playerColor, gameMode, parentFrame, isSaveMode, onClose, null);
    }
    
    public SaveLoadScreen(BadDopoCream game, String playerColor, String gameMode, 
                         JFrame parentFrame, boolean isSaveMode, Runnable onClose, 
                         java.util.function.Consumer<GameState> onLoadSuccess) {
        this.game = game;
        this.playerColor = playerColor;
        this.gameMode = gameMode;
        this.parentFrame = parentFrame;
        this.isSaveMode = isSaveMode;
        this.onClose = onClose;
        this.onLoadSuccess = onLoadSuccess;
        
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(70, 130, 180));
        
        setupUI();
    }
    
    private void setupUI() {
        // Título
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel(isSaveMode ? "💾 GUARDAR PARTIDA" : "📂 CARGAR PARTIDA");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Panel central con lista de guardados
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        if (isSaveMode) {
            // Campo de texto para nombre de guardado
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            namePanel.setOpaque(false);
            JLabel nameLabel = new JLabel("Nombre:");
            nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            nameLabel.setForeground(Color.WHITE);
            saveNameField = new JTextField(20);
            saveNameField.setFont(new Font("Arial", Font.PLAIN, 18));
            namePanel.add(nameLabel);
            namePanel.add(saveNameField);
            centerPanel.add(namePanel, BorderLayout.NORTH);
        }
        
        // Lista de guardados existentes
        savesPanel = new JPanel();
        savesPanel.setLayout(new BoxLayout(savesPanel, BoxLayout.Y_AXIS));
        savesPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(savesPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Botones inferiores
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPanel.setOpaque(false);
        
        if (isSaveMode) {
            JButton saveButton = createStyledButton("💾 Guardar");
            saveButton.addActionListener(e -> saveGame());
            bottomPanel.add(saveButton);
        }
        
        JButton backButton = createStyledButton("⬅ Volver");
        backButton.addActionListener(e -> close());
        bottomPanel.add(backButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Cargar lista de guardados
        refreshSavesList();
    }
    
    private void refreshSavesList() {
        savesPanel.removeAll();
        
        List<String> saves = BadDopoCream.listSavedGames();
        
        if (saves.isEmpty()) {
            JLabel noSavesLabel = new JLabel("No hay partidas guardadas");
            noSavesLabel.setFont(new Font("Arial", Font.ITALIC, 18));
            noSavesLabel.setForeground(Color.WHITE);
            noSavesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            savesPanel.add(Box.createVerticalGlue());
            savesPanel.add(noSavesLabel);
            savesPanel.add(Box.createVerticalGlue());
        } else {
            for (String saveName : saves) {
                JPanel saveItemPanel = createSaveItem(saveName);
                savesPanel.add(saveItemPanel);
                savesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        savesPanel.revalidate();
        savesPanel.repaint();
    }
    
    private JPanel createSaveItem(String saveName) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(255, 255, 255, 200));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50), 2),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setMaximumSize(new Dimension(800, 80));
        
        // Información del guardado
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel("📁 " + saveName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameLabel.setForeground(new Color(30, 30, 30));
        
        // Intentar cargar información adicional
        GameState state = BadDopoCream.loadGame(saveName);
        String infoText = "Partida guardada";
        if (state != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dateStr = sdf.format(new Date(state.getSaveTimestamp()));
            infoText = String.format("Nivel %d | Puntaje: %d | %s", 
                state.getCurrentLevelIndex() + 1, 
                state.getTotalScore(),
                dateStr);
        }
        
        JLabel infoLabel = new JLabel(infoText);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(60, 60, 60));
        
        infoPanel.add(nameLabel);
        infoPanel.add(infoLabel);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Botones de acción
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsPanel.setOpaque(false);
        
        if (!isSaveMode) {
            JButton loadButton = createSmallButton("▶ Cargar");
            loadButton.addActionListener(e -> loadGame(saveName));
            actionsPanel.add(loadButton);
        }
        
        JButton deleteButton = createSmallButton("🗑 Eliminar");
        deleteButton.setBackground(new Color(220, 50, 50));
        deleteButton.addActionListener(e -> deleteSave(saveName));
        actionsPanel.add(deleteButton);
        
        panel.add(actionsPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void saveGame() {
        String saveName = saveNameField.getText().trim();
        
        if (saveName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor ingresa un nombre para el guardado", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirmar si ya existe
        List<String> saves = BadDopoCream.listSavedGames();
        if (saves.contains(saveName)) {
            int result = JOptionPane.showConfirmDialog(this,
                "Ya existe un guardado con ese nombre. ¿Deseas sobrescribirlo?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        boolean success = game.saveGame(playerColor, gameMode, saveName);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Partida guardada exitosamente", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refreshSavesList();
            saveNameField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al guardar la partida", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadGame(String saveName) {
        int result = JOptionPane.showConfirmDialog(this,
            "¿Deseas cargar esta partida? El progreso actual se perderá.",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        
        GameState state = BadDopoCream.loadGame(saveName);
        
        if (state != null) {
            JOptionPane.showMessageDialog(this, 
                "Partida cargada exitosamente", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            // Llamar al callback de carga exitosa pasando el GameState
            if (onLoadSuccess != null) {
                onLoadSuccess.accept(state);
            } else {
                // Fallback: restaurar en el juego actual y cerrar
                game.restoreFromState(state);
                close();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar la partida", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSave(String saveName) {
        int result = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro de eliminar esta partida guardada?",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            boolean success = BadDopoCream.deleteSavedGame(saveName);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Partida eliminada exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                refreshSavesList();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar la partida", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 150, 50));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(60, 180, 60));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(50, 150, 50));
            }
        });
        
        return button;
    }
    
    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                Color current = button.getBackground();
                button.setBackground(current.brighter());
            }
            public void mouseExited(MouseEvent e) {
                if (text.contains("Eliminar")) {
                    button.setBackground(new Color(220, 50, 50));
                } else {
                    button.setBackground(new Color(70, 130, 180));
                }
            }
        });
        
        return button;
    }
    
    private void close() {
        if (onClose != null) {
            onClose.run();
        }
    }
}
