package test;

import presentation.CharacterImageConfigurator;
import presentation.CharacterImageConfigurator.CharacterType;
import presentation.CharacterImageConfigurator.AnimationType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Demo visual de las animaciones de personajes
 * Muestra un personaje con diferentes animaciones
 */
public class CharacterAnimationDemo extends JFrame {
    
    private CharacterType currentCharacter = CharacterType.FRESA;
    private AnimationType currentAnimation = AnimationType.IDLE_DOWN;
    private JPanel displayPanel;
    private JLabel animationLabel;
    
    public CharacterAnimationDemo() {
        setTitle("Demo de Animaciones - Bad Dopo Cream");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setupUI();
        updateAnimation();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior con controles
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(new Color(44, 62, 80));
        
        // Selector de personaje
        JLabel charLabel = new JLabel("Personaje:");
        charLabel.setForeground(Color.WHITE);
        controlPanel.add(charLabel);
        
        JComboBox<CharacterType> characterCombo = new JComboBox<>(CharacterType.values());
        characterCombo.addActionListener(e -> {
            currentCharacter = (CharacterType) characterCombo.getSelectedItem();
            updateAnimation();
        });
        controlPanel.add(characterCombo);
        
        // Selector de animación
        JLabel animLabel = new JLabel("Animación:");
        animLabel.setForeground(Color.WHITE);
        controlPanel.add(animLabel);
        
        JComboBox<AnimationType> animationCombo = new JComboBox<>(AnimationType.values());
        animationCombo.addActionListener(e -> {
            currentAnimation = (AnimationType) animationCombo.getSelectedItem();
            updateAnimation();
        });
        controlPanel.add(animationCombo);
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Panel de visualización
        displayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dibujar el personaje en el centro
                int centerX = getWidth() / 2 - 60;
                int centerY = getHeight() / 2 - 60;
                
                CharacterImageConfigurator.drawCharacter(
                    g2d, currentCharacter, currentAnimation, 
                    centerX, centerY, 120, 120
                );
                
                // Dibujar información
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                String info = currentCharacter.getFolderName() + " - " + currentAnimation.name();
                g2d.drawString(info, 20, getHeight() - 20);
            }
        };
        displayPanel.setBackground(new Color(20, 20, 40));
        add(displayPanel, BorderLayout.CENTER);
        
        // Panel inferior con botones de dirección
        JPanel directionPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        directionPanel.setBackground(new Color(44, 62, 80));
        directionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Botones de dirección
        directionPanel.add(new JLabel()); // Espacio
        addDirectionButton(directionPanel, "↑ Arriba", 0, -1);
        directionPanel.add(new JLabel()); // Espacio
        
        addDirectionButton(directionPanel, "← Izquierda", -1, 0);
        addDirectionButton(directionPanel, "Idle", 0, 0);
        addDirectionButton(directionPanel, "→ Derecha", 1, 0);
        
        directionPanel.add(new JLabel()); // Espacio
        addDirectionButton(directionPanel, "↓ Abajo", 0, 1);
        addDirectionButton(directionPanel, "❄ Hielo", 0, 0, true);
        
        add(directionPanel, BorderLayout.SOUTH);
    }
    
    private void addDirectionButton(JPanel panel, String text, int dx, int dy) {
        addDirectionButton(panel, text, dx, dy, false);
    }
    
    private void addDirectionButton(JPanel panel, String text, int dx, int dy, boolean isIce) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        
        button.addActionListener(e -> {
            if (isIce) {
                currentAnimation = CharacterImageConfigurator.getAnimationByDirection(
                    currentCharacter, dx, dy, false
                ) != null ? getIceAnimationType(dx, dy) : AnimationType.ICE_DOWN;
            } else {
                boolean isMoving = (dx != 0 || dy != 0);
                ImageIcon icon = CharacterImageConfigurator.getAnimationByDirection(
                    currentCharacter, dx, dy, isMoving
                );
                
                // Encontrar el tipo de animación correspondiente
                currentAnimation = getAnimationTypeFromDirection(dx, dy, isMoving);
            }
            updateAnimation();
        });
        
        panel.add(button);
    }
    
    private AnimationType getAnimationTypeFromDirection(int dx, int dy, boolean isMoving) {
        if (isMoving) {
            if (dy > 0) return AnimationType.WALK_DOWN;
            if (dy < 0) return AnimationType.WALK_UP;
            if (dx > 0) return AnimationType.WALK_RIGHT;
            if (dx < 0) return AnimationType.WALK_LEFT;
        } else {
            if (dy > 0) return AnimationType.IDLE_DOWN;
            if (dy < 0) return AnimationType.IDLE_UP;
            if (dx > 0) return AnimationType.IDLE_RIGHT;
            if (dx < 0) return AnimationType.IDLE_LEFT;
        }
        return AnimationType.IDLE_DOWN;
    }
    
    private AnimationType getIceAnimationType(int dx, int dy) {
        if (dy > 0) return AnimationType.ICE_DOWN;
        if (dy < 0) return AnimationType.ICE_UP;
        if (dx > 0) return AnimationType.ICE_RIGHT;
        if (dx < 0) return AnimationType.ICE_LEFT;
        return AnimationType.ICE_DOWN;
    }
    
    private void updateAnimation() {
        displayPanel.repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CharacterAnimationDemo demo = new CharacterAnimationDemo();
            demo.setVisible(true);
            
            // Imprimir reporte de carga
            CharacterImageConfigurator.printLoadingReport();
        });
    }
}
