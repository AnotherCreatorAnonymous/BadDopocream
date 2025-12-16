package com.duran_jimenez.baddopocream.presentation;

import java.awt.event.*;
import javax.swing.*;

public class GameMenuBar extends JMenuBar{
    private JFrame parentFrame;

    public GameMenuBar(JFrame parentFrame){
        this.parentFrame = parentFrame;
        createMenus();
    }

    private void createMenus(){
        add(createFileMenu());
        add(createOptionsMenu());
    }

    private JMenu createFileMenu(){
        JMenu fileMenu = new JMenu("Archivo");
        fileMenu.setMnemonic('A');

        JMenuItem newGameItem = new JMenuItem("Nuevo Juego");
        newGameItem.setAccelerator(KeyStroke.getKeyStroke("Control N"));
        newGameItem.addActionListener(e -> onNewGame());


        JMenuItem loadGameItem = new JMenuItem("Cargar Juego");
        loadGameItem.setAccelerator(KeyStroke.getKeyStroke("Control O"));
        loadGameItem.addActionListener(e -> onLoadGame());

        JMenuItem saveGameItem = new JMenuItem("Guardar Juego");
        saveGameItem.setAccelerator(KeyStroke.getKeyStroke("Control S"));
        saveGameItem.addActionListener(e -> onSaveGame());

        JMenuItem exitItem = new JMenuItem("Salir");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("alt F4"));
        exitItem.addActionListener(e -> onExit());

        fileMenu.add(newGameItem);
        fileMenu.add(loadGameItem);
        fileMenu.add(saveGameItem);
        fileMenu.add(exitItem);

        return fileMenu;
    }

    private JMenu createOptionsMenu(){
        JMenu helpMenu = new JMenu("Opciones");
        helpMenu.setMnemonic('O');

        JMenuItem instructionsItem = new JMenuItem("Ayuda");
        instructionsItem.addActionListener(e -> onInstructions());

        JMenuItem aboutItem = new JMenuItem("Acerca de");
        aboutItem.addActionListener(e -> onAbout());

        helpMenu.add(instructionsItem);
        helpMenu.add(aboutItem);

        return helpMenu;
    }

    private void onNewGame(){

    }

    private void  onLoadGame(){
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(parentFrame);
        if(result == JFileChooser.APPROVE_OPTION){
            // Cargar el juego desde el archivo seleccionado
        }
    }

    private void onSaveGame(){
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(parentFrame);
        if(result == JFileChooser.APPROVE_OPTION){
            // Guardar el juego en el archivo seleccionado
        }
    }

    private void onSettings(){

    }

    private void onInstructions(){
        String instructions = "Instrucciones del juego:\n\n" +
                "1. Usa las flechas del teclado para moverte.\n" +
                "2. Presiona Espacio para crear o romper bloques.\n" +
                "3. Presiona Escape para pausar el juego.\n" +
                "4. Completa los niveles para avanzar.\n\n" +
                "¡Diviértete jugando!";
        JOptionPane.showMessageDialog(parentFrame, instructions, "Instrucciones", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onAbout(){
        String aboutMessage = "Bad Dopo Cream v1.0\n" +
                "Desarrollado por El Mejor Equipo\n" +
                "© 2024 Todos los derechos reservados.";
        JOptionPane.showMessageDialog(parentFrame, aboutMessage, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onExit(){
        int confirm = JOptionPane.showConfirmDialog(parentFrame, "¿Estás seguro de que deseas salir?", "Confirmar salida", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION){
            System.exit(0);
        }
    }

    public void setNewGameListener(ActionListener listener){
        // Implementar si es necesario
    }

    public void setLoadGameListener(ActionListener listener){
        // Implementar si es necesario
    }

    public void setSaveGameListener(ActionListener listener){
        // Implementar si es necesario
    }
}
