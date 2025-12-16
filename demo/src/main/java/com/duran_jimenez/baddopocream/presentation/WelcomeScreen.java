package com.duran_jimenez.baddopocream.presentation;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class WelcomeScreen extends JPanel {

    private JLabel titleLabel;
    private JLabel clickLabel;
    private boolean blinkState = true;
    private Timer blinkTimer;

    public WelcomeScreen(Runnable onClickAction){
        setLayout(new GridBagLayout());

        setBackground(new Color(41, 128, 185));

        createComponents();
        setupClickListener(onClickAction);
        startBlinkAnimation();
    }

    private void createComponents(){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 20, 20, 20);

        titleLabel = new JLabel("BAD DOPO CREAM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 64));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        add(titleLabel, gbc);

        JLabel subtitleLabel = new JLabel("VERSION 1.0");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 20, 50, 20);
        add(subtitleLabel, gbc);

        clickLabel = new JLabel("Click para continuar.");
        clickLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        clickLabel.setForeground(Color.WHITE);
        gbc.gridy = 2;
        gbc.insets = new Insets(80, 20, 20, 20);
        add(clickLabel, gbc);
    }
    
    private void setupClickListener(Runnable onClickAction){
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                stopBlinkAnimation();
                if (onClickAction != null) {
                    onClickAction.run();
                }
            }
        });
    
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
    }

    private void startBlinkAnimation(){
        blinkTimer = new Timer(600, e -> {
            blinkState = !blinkState;
            clickLabel.setVisible(blinkState);
        });
        blinkTimer.start();
    }

    private void stopBlinkAnimation(){
        if (blinkTimer != null) {
            blinkTimer.stop();
        }
    }
}
