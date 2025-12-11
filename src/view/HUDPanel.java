package view;

import controller.GameManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class HUDPanel extends JPanel {
    private GameManager gameManager;
    private TimerPanel timerPanel;
    private ScorePanel scorePanel;
    public HUDPanel(GameManager gameManager) {
        this.gameManager = gameManager;
        this.timerPanel = new TimerPanel(gameManager);
        this.scorePanel = new ScorePanel(gameManager,this);
        setBackground(new Color(61, 138, 93));
        setLayout(new GridLayout(1, 2));
        add(timerPanel);
        add(scorePanel);
        setVisible(true);
    }
}
