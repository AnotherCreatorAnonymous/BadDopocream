package com.duran_jimenez.baddopocream.presentation;
import java.awt.event.*;

public class KeyboardListener extends KeyAdapter{
    
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean spacePressed;
    private boolean escapePressed;

    @Override
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        switch(key){
            case KeyEvent.VK_UP:
                upPressed = true;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = true;
                break;
            case KeyEvent.VK_LEFT:
                leftPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = true;
                break;
            case KeyEvent.VK_SPACE:
                spacePressed = true;
                break;
            case KeyEvent.VK_ESCAPE:
                escapePressed = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        switch(key){
            case KeyEvent.VK_UP:
                upPressed = false;
                break;
            case KeyEvent.VK_DOWN:
                downPressed = false;
                break;
            case KeyEvent.VK_LEFT:
                leftPressed = false;
                break;
            case KeyEvent.VK_RIGHT:
                rightPressed = false;
                break;
            case KeyEvent.VK_SPACE:
                spacePressed = false;
                break;
            case KeyEvent.VK_ESCAPE:
                escapePressed = false;
                break;
        }
    }

    public boolean isUpPressed(){
        return this.upPressed;
    }

    public boolean isDownPressed(){
        return this.downPressed;
    }

    public boolean isLeftPressed(){
        return this.leftPressed;
    }

    public boolean isRightPressed(){
        return this.rightPressed;
    }

    public boolean isSpacePressed(){
        return this.spacePressed;
    }

    public boolean isEscapePressed(){
        return this.escapePressed;
    }

    public void reset(){
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        spacePressed = false;
        escapePressed = false;
    }
}
