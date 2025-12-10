package presentation;
import java.awt.*;
import javax.swing.*;

// Deprecado
public class PvPModeSelectionScreen extends JPanel {
    
    private JButton cooperativeButton;
    private JButton backButton;

    private Runnable onCooperativeMode;
    private Runnable onBack;

    public PvPModeSelectionScreen(Runnable onBack){
        this.onBack = onBack;
        setupPanel();
        createComponents();
        layoutComponents();
    }

    private void setupPanel(){
        setLayout(new BorderLayout());
        setBackground(new Color(20, 20, 40));
    }

    private void createComponents(){
        cooperativeButton = new JButton("MODO COOPERATIVO");
        styleButton(cooperativeButton, new Color(100, 200, 100));

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
        headerPanel.setBackground(new Color(30, 30, 50));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel titleLabel = new JLabel("PLAYER VS PLAYER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Selecciona el modo de juego");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(200, 200, 200));
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 1, 20, 20));
        centerPanel.setBackground(new Color(20, 20, 40));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
        centerPanel.add(cooperativeButton);

        add(centerPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel (new FlowLayout(FlowLayout.CENTER, 0, 20));
        footerPanel.setBackground(new Color(20, 20, 40));
        footerPanel.add(backButton);

        add(footerPanel, BorderLayout.SOUTH);
    }

    public void setCooperativeListener(Runnable action){
        this.onCooperativeMode = action;
        cooperativeButton.addActionListener(e -> {
            if(action != null){
                action.run();
            }
        });
    }

}
