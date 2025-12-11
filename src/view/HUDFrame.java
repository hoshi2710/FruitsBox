package view;

import controller.GameManager;

import javax.swing.*;

public class HUDFrame extends JFrame {
    private GameManager gameManager;
    private HUDPanel hudPanel;
    public HUDFrame(GameManager gameManager) {
        this.gameManager = gameManager;
        this.hudPanel = new HUDPanel(gameManager);
        setSize(350,100);
        add(hudPanel);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
