package controller;

import enums.GameStatus;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardHandler implements KeyListener {
    private GameManager gameManager;
    public KeyboardHandler(GameManager gm) {
        this.gameManager = gm;
    }
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
            if(gameManager.getGameStatus() == GameStatus.PAUSED)
                gameManager.setGameStatus(GameStatus.PLAYING);
            else
                gameManager.setGameStatus(GameStatus.PAUSED);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
