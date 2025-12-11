package view;

import controller.ButtonManager;
import controller.GameManager;
import controller.MouseHandler;
import enums.AppleType;
import enums.GameStatus;
import model.*;
import model.Button;
import model.Point;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class PlayPanel extends JPanel {
    private GameManager gameManager;
    private MouseHandler mouseHandler;
    private BufferedImage appleImg, goldenImg, bombImg;
    private BufferedImage backgroundImg;
    private FontMetrics fm = null;
    private Button playButton, restartButton,resumeButton;
    private Set<Button> enabledButtons;
    private ButtonManager buttonManager;
    public PlayPanel(GameManager gameManager, MouseHandler mouseHandler, ButtonManager buttonManager) {
        this.gameManager = gameManager;
        this.mouseHandler = mouseHandler;
        this.buttonManager = buttonManager;
        this.playButton = null;
        this.restartButton = null;
        this.resumeButton = null;
        this.enabledButtons = new HashSet<>();
        try {
            this.appleImg = ImageIO.read(new File("assets/apple.png"));
            this.goldenImg = ImageIO.read(new File("assets/goldenApple.png"));
            this.bombImg = ImageIO.read(new File("assets/bombApple.png"));
            this.backgroundImg = ImageIO.read(new File("assets/back.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLayout(null);
        setVisible(true);
    }
    public void drawApples(List<Apple> apples, Graphics2D g2d) {
        if (apples == null || apples.isEmpty()) return;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        BufferedImage img;
        Graphics2D g2;
        for (Apple apple : apples) {
            if(apple.isUsed())continue;
            Point point = apple.getPosition();
            double size = apple.getSize();
            if (apple.getIcon() == null || apple.getSelectedIcon() == null) {
                BufferedImage srcImg = appleImg;
                if (apple.getAppleType() == AppleType.GOLDEN) srcImg = goldenImg;
                else if (apple.getAppleType() == AppleType.BOMB) srcImg = bombImg;
                img = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
                g2 = img.createGraphics();
                g2.drawImage(srcImg, 0, 0, srcImg.getWidth(), srcImg.getHeight(), null);
                if (apple.getAppleType() != AppleType.BOMB) g2.setColor(Color.BLACK);
                else g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 500));

                fm = g2.getFontMetrics();
                int ascent = fm.getAscent();
                String text = Integer.toString(apple.getValue());
                g2.drawString(text, (img.getWidth()  - fm.stringWidth(text)) / 2 ,(img.getHeight() + ascent) / 2);  // <-- 여기
                g2.dispose();
                apple.setIcon(img);
                img = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
                g2 = img.createGraphics();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2.drawImage(apple.getIcon(), 0, 0, apple.getIcon().getWidth(), apple.getIcon().getHeight(), null);
                g2.dispose();
                apple.setSelectedIcon(img);
            }
            else {
                img = apple.isSelected() ? apple.getSelectedIcon() : apple.getIcon();
            }

            AffineTransform at = new AffineTransform();
            at.translate(point.getX(), point.getY());
            at.rotate(apple.getRotation(), size/2.0, size/2.0);
            at.scale(size/img.getWidth(), size/img.getHeight());
            g2d.drawImage(img, at, null);
        }
    }
    public void drawSparkles(List<List<Sparkle>> sparkles, Graphics2D g2d) {
        Random rand = new Random();
        for(List<Sparkle> sparkle : sparkles) {
            for(Sparkle sparkle1 : sparkle) {
                if(sparkle1.getDone()) continue;
                Vector v = sparkle1.getPosition();
                Point p = v.getPoint();
                g2d.setColor(sparkle1.getColor());
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sparkle1.getOpacity()));
                int randFrom = 1, randTo = 10;
                if (sparkle1.getApple().getAppleType() == AppleType.BOMB) {
                    randFrom = 20;
                    randTo = 40;
                }
                g2d.fill(new Ellipse2D.Double(p.getX(),p.getY(), rand.nextInt(randFrom,randTo),rand.nextInt(randFrom,randTo)));
            }
        }
    }
    public void drawBackground(Graphics2D g2d) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
    }
    public void drawBlurOverlay(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    public void drawIntroOverlay(Graphics2D g2d) {
        buttonManager.disableAllButtons();
        String title = "Fruits Box!";
        g2d.setColor(new Color(35,35,35,255));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        fm = g2d.getFontMetrics();
        int titleX = getWidth()/2 - fm.stringWidth(title)/2;
        int titleY = getHeight()/4;
        g2d.drawString(title,titleX,titleY);
        int playButtonWidth = 225;
        int playButtonHeight = 75;
        int playButtonX = getWidth()/2 -  playButtonWidth/2;
        int playButtonY = getHeight()/4*3 - playButtonHeight;
        if(playButton == null) {
            playButton = new Button(playButtonX,playButtonY,playButtonWidth,playButtonHeight,new Color(192,99,72,255),"Play",30);
            playButton.setOnClick(() -> {
                gameManager.setGameStatus(GameStatus.PLAYING);
                gameManager.startGame();
            });
            mouseHandler.addSamplingButton(playButton);
        }
        buttonManager.enableButton(playButton);
        playButton.setLocation(playButtonX,playButtonY);
        playButton.show(g2d);
    }
    public void drawPauseOverlay(Graphics2D g2d) {
        buttonManager.disableAllButtons();
        String title = "Paused";
        g2d.setColor(new Color(35,35,35,255));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        fm = g2d.getFontMetrics();
        int titleX = getWidth()/2 - fm.stringWidth(title)/2;
        int titleY = getHeight()/4;
        g2d.drawString(title,titleX,titleY);
        int buttonWidth = 225;
        int buttonHeight = 50;
        int resumeButtonX = getWidth()/2 -  buttonWidth/2;
        int resumeButtonY = getHeight()/4*3 - buttonHeight;
        if (resumeButton == null) {
            resumeButton = new Button(resumeButtonX,resumeButtonY,buttonWidth,buttonHeight,new Color(192,99,72,255),"Resume",30);
            resumeButton.setOnClick(() -> {
                gameManager.setGameStatus(GameStatus.PLAYING);
            });
            mouseHandler.addSamplingButton(resumeButton);
        }
        int restartButtonX = resumeButtonX;
        int restartButtonY = resumeButtonY + buttonHeight;
        if(restartButton == null) {
            restartButton = new Button(restartButtonX,restartButtonY,buttonWidth,buttonHeight,new Color(108, 192,72,255),"Restart",30);
            restartButton.setOnClick(()-> {
                gameManager.setGameStatus(GameStatus.PLAYING);
                gameManager.startGame();
            });
            mouseHandler.addSamplingButton(restartButton);
        }
        buttonManager.enableButton(resumeButton);
        buttonManager.enableButton(restartButton);
        resumeButton.setLocation(resumeButtonX,resumeButtonY);
        restartButton.setLocation(restartButtonX,restartButtonY);

        resumeButton.show(g2d);
        restartButton.show(g2d);

        g2d.dispose();
    }
    public void drawResultOverlay(Graphics2D g2d) {
        buttonManager.disableAllButtons();
        String title = "Game Over!";
        g2d.setColor(new Color(35,35,35,255));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 50));
        fm = g2d.getFontMetrics();
        int titleX = getWidth()/2 - fm.stringWidth(title)/2;
        int titleY = getHeight()/4;
        g2d.drawString(title,titleX,titleY);
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 30));
        int scoreX = titleX + fm.stringWidth(title)/4;
        int scoreY = titleY + fm.getAscent();
        String scoreText = "Score : "+ gameManager.getScore();
        g2d.drawString(scoreText,scoreX,scoreY);
        int buttonWidth = 225;
        int buttonHeight = 50;
        int restartButtonX = getWidth()/2 -  buttonWidth/2;
        int restartButtonY = getHeight()/4*3 - buttonHeight;
        if(restartButton == null) {
            restartButton = new Button(restartButtonX,restartButtonY,buttonWidth,buttonHeight,new Color(108, 192,72,255),"Restart",30);
            restartButton.setOnClick(()-> {
                gameManager.setGameStatus(GameStatus.PLAYING);
                gameManager.startGame();
            });
            mouseHandler.addSamplingButton(restartButton);
        }
        buttonManager.enableButton(restartButton);
        restartButton.setLocation(restartButtonX,restartButtonY);
        restartButton.show(g2d);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        drawBackground(g2d);
        if (gameManager.getGameStatus() != GameStatus.PLAYING) {
            drawBlurOverlay(g2d);
            if(gameManager.getGameStatus() == GameStatus.STANDBY) {
                drawIntroOverlay(g2d);
            }
            else if(gameManager.getGameStatus() == GameStatus.PAUSED) {
                drawPauseOverlay(g2d);
            }
            else if(gameManager.getGameStatus() == GameStatus.ENDED) {
                drawResultOverlay(g2d);
            }
            else{
                buttonManager.disableAllButtons();
            }

        }else {
            drawApples(gameManager.getApples(), g2d);
            drawSparkles(gameManager.getSparkles(), g2d);
        }
    }
}
