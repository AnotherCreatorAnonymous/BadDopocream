package presentation;

import java.awt.*;
import javax.swing.*;

/**
 * Pantalla de carga inicial del juego
 * Muestra el logo, título y barra de progreso
 */
public class LoadingScreen extends JPanel {
    
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    
    /**
     * Constructor - Inicializa la pantalla de carga
     */
    public LoadingScreen() {
        setLayout(new GridBagLayout());
        setBackground(new Color(41, 128, 185)); // Azul
        
        createComponents();
        layoutComponents();
    }
    
    /**
     * Crea los componentes visuales
     */
    private void createComponents() {
        // Título principal
        titleLabel = new JLabel("BAD DOPO CREAM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        
        // Subtítulo
        subtitleLabel = new JLabel("Cargando recursos del juego...");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.WHITE);
        
        // Barra de progreso
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(400, 30));
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(46, 204, 113)); // Verde
        
        // Estado de carga
        statusLabel = new JLabel("Inicializando...");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        statusLabel.setForeground(Color.WHITE);
    }
    
    /**
     * Organiza los componentes en el panel
     */
    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridy = 0;
        add(titleLabel, gbc);
        
        gbc.gridy = 1;
        add(subtitleLabel, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        add(progressBar, gbc);
        
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 10, 10, 10);
        add(statusLabel, gbc);
    }
    
    /**
     * Actualiza el progreso de carga
     * @param progress Valor entre 0 y 100
     * @param status Mensaje de estado
     */
    public void updateProgress(int progress, String status) {
        progressBar.setValue(progress);
        statusLabel.setText(status);
    }
    
    /**
     * Simula la carga de recursos
     * @param onComplete Callback que se ejecuta al terminar
     */
    public void startLoading(Runnable onComplete) {
        new Thread(() -> {
            try {
                String[] loadingSteps = {
                    "Cargando texturas...",
                    "Cargando sonidos...",
                    "Inicializando motor de juego...",
                    "Preparando escenarios...",
                    "Cargando enemigos...",
                    "¡Listo para jugar!"
                };
                
                for (int i = 0; i < loadingSteps.length; i++) {
                    final int progress = (i + 1) * 100 / loadingSteps.length;
                    final String status = loadingSteps[i];
                    
                    SwingUtilities.invokeLater(() -> {
                        updateProgress(progress, status);
                    });
                    
                    try {
                        Thread.sleep(500); // Simula tiempo de carga
                    } catch (InterruptedException e) {
                        throw new domain.BadDopoCream_Exceptions.GameInterruptedException(
                            "Pantalla de carga", e);
                    }
                }
                
                // Al terminar, ejecutar el callback
                SwingUtilities.invokeLater(onComplete);
                
            } catch (domain.BadDopoCream_Exceptions.GameInterruptedException e) {
                System.err.println("⚠️ " + e.getMessage());
                // El hilo se termina pero la aplicación continúa
            }
        }).start();
    }
}
