package view;

import controller.ButtonManager;
import controller.GameManager;
import controller.KeyboardHandler;
import controller.MouseHandler;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private GameManager gameManager;
    private PlayPanel playPanel;
    private double prevX, prevY;
    private double prevVx=0, prevVy=0;
    private double vx=0, vy=0,ax=0,ay=0;
    public MainFrame(GameManager gameManager, ButtonManager buttonManager) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.gameManager = gameManager;
        MouseHandler mouseHandler = gameManager.getMouseHandler();
        KeyboardHandler keyboardHandler = gameManager.getKeyboardHandler();
        this.playPanel = new PlayPanel(gameManager,mouseHandler, buttonManager);
        gameManager.setPlayPanel(playPanel);
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        this.addKeyListener(keyboardHandler);
        add(playPanel);
        setSize(500,400);
        setLocation(screenSize.width/2-this.getWidth()/2,screenSize.height/2-this.getHeight()/2);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.prevX = this.getLocationOnScreen().getX();
        this.prevY = this.getLocationOnScreen().getY();
        this.setVisible(true);
    }
    public PlayPanel getPlayPanel() {
        return playPanel;
    }
    public int getWidth() {
        return this.getSize().width;
    }
    public int getHeight() {
        return this.getSize().height;
    }
    public double getVx() {
        return vx;
    }
    public double getVy() {
        return vy;
    }
    public void setVx(double vx) {
        this.vx = vx;
    }
    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getPrevX() {
        return prevX;
    }
    public double getPrevY() {
        return prevY;
    }
    public void setPrevX(double prevX) {
        this.prevX = prevX;
    }
    public void setPrevY(double prevY) {
        this.prevY = prevY;
    }
    public double getPrevVx() {
        return prevVx;
    }
    public double getPrevVy() {
        return prevVy;
    }
    public void setPrevVx(double prevVx) {
        this.prevVx = prevVx;
    }
    public void setPrevVy(double prevVy) {
        this.prevVy = prevVy;
    }
    public void setAx(double ax) {
        this.ax = ax;
    }
    public void setAy(double ay) {
        this.ay = ay;
    }
    public double getAx() {
        return ax;
    }
    public double getAy() {
        return ay;
    }
}
