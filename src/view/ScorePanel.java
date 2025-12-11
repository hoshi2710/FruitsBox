package view;

import controller.GameManager;

import javax.swing.*;
import java.awt.*;

public class ScorePanel extends JPanel {
    private GameManager gameManager;
    private int marginTop, marginRight;
    private HUDPanel hudPanel;
    public ScorePanel(GameManager gameManager, HUDPanel hudPanel) {
        this.gameManager = gameManager;
        this.hudPanel = hudPanel;
        this.marginTop = 10;
        this.marginRight = 10;
        setBackground(new Color(61, 138, 93));
        setLayout(null);
        setVisible(true);
    }
    public void drawScore(Graphics2D g2d) {
        int score = this.gameManager.getScore();
        g2d.setColor(Color.white);
        g2d.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int x = hudPanel.getWidth()/2 - (int)(fontMetrics.stringWidth(Integer.toString(score))) - marginRight;
        int y = hudPanel.getHeight() - fontMetrics.getAscent() + marginTop;

        g2d.drawString(Integer.toString(score), x,y);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawScore((Graphics2D) g);
    }
}
