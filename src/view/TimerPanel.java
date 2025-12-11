package view;

import controller.GameManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class TimerPanel extends JPanel {
    private GameManager gameManager;
    private double width;
    private double height;
    private double marginTop;
    private double marginLeft;
    public TimerPanel(GameManager gameManager) {
        this.gameManager = gameManager;
        this.marginTop = 10.0;
        this.marginLeft = 10.0;
        this.width = 50.0;
        this.height = 50.0;
        setBackground(new Color(61, 138, 93));
        setLayout(new FlowLayout());
        setVisible(true);
    }
    public void drawTimer(Graphics2D g2d) {
        double remains =  360 * ((double) gameManager.getRemainTime() / gameManager.getInitTime());
        g2d.setStroke(new BasicStroke(2f));
        g2d.setColor(Color.LIGHT_GRAY);
        Ellipse2D.Double outerCircle = new Ellipse2D.Double(marginLeft,marginTop,width, height);
        Ellipse2D.Double innerCircle = new Ellipse2D.Double(marginLeft + width/4,marginTop + height/4,width/2,height/2);
        g2d.draw(outerCircle);
        g2d.draw(innerCircle);
        g2d.setColor(Color.WHITE);
        for(int i=0; i<remains; i+=1) {
            double angle = i*Math.PI/180;
            double size = width/4;
            double x = marginLeft + (width/2) - (size/2) - (width/2.8)*Math.sin(angle);
            double y = marginTop + (height/2) - (size/2) - (height/2.8)*Math.cos(angle);
            g2d.fill(new Ellipse2D.Double(x,y,size,size));
        }
        g2d.setFont(new Font("Arial",Font.BOLD,40));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(Integer.toString(gameManager.getRemainTime()),(int)(marginLeft*2 + width), (int)(marginTop+fm.getAscent()));
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTimer((Graphics2D) g);
    }
}
