package model;

import enums.AppleType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Apple {
    private Point position;
    private double size;
    private double vx,vy,ax,ay;
    private double prevVy, prevVx;
    private Point prevPosition;
    private int value;
    private boolean used = false;
    private double rotation=0.0;
    private boolean selected = false;
    private List<Sparkle> sparkles;
    private BufferedImage icon, selectedIcon;
    private AppleType appleType;
    private HashMap<AppleType, Color[]> sparkleColors;
    public Apple(int value, double size, int x,double vx, AppleType appleType) {
        this.position = new Point(x,-size);
        this.prevPosition = new Point(x,-size);
        this.vx = vx;
        this.vy = 0.0;
        this.prevVx = this.vx;
        this.prevVy = this.vy;
        this.ax = 0.0;
        this.ay = 0.0;
        this.size = size;
        this.value = value;
        this.sparkles = new ArrayList<>();
        this.appleType = appleType;
        sparkleColors = new HashMap<>();
        sparkleColors.put(AppleType.DEFAULT, new Color[] {
                new Color(192,99,72,255),
                new Color(192, 140,72,255),
                new Color(192, 182,72,255)
        });
        sparkleColors.put(AppleType.GOLDEN, new Color[] {
                new Color(220,185,21,255),
                new Color(192, 140,72,255),
                new Color(211, 206, 158,255)
        });
        sparkleColors.put(AppleType.BOMB, new Color[] {
                new Color(218, 13, 13,255),
                new Color(248, 174, 66,255),
                new Color(50, 50, 50,255),
        });
        generateSparkles();
    }
    private void generateSparkles() {
        Random rand = new Random();
        for(int i=0; i<100; i++) {
            double dx = Math.cos(Math.toRadians(rand.nextInt(360)));
            double dy = Math.sin(Math.toRadians(rand.nextInt(360)));
            sparkles.add(new Sparkle(this,sparkleColors.get(appleType)[rand.nextInt(sparkleColors.size()-1)],new Vector(dx,dy)));
        }
    }
    public List<Sparkle> getSparkles() {
        return sparkles;
    }
    public AppleType getAppleType() {
        return appleType;
    }
    public Point getPosition() {
        return position;
    }
    public double getSize() {
        return size;
    }
    public double getVx() {
        return vx;
    }
    public double getPrevVy() {
        return prevVy;
    }
    public void setPrevVy(double prevVy) {
        this.prevVy = prevVy;
    }
    public double getPrevVx() {
        return prevVx;
    }
    public void setPrevVx(double prevVx) {
        this.prevVx = prevVx;
    }
    public double getVy() { return vy; }
    public void setVx(double vx) { this.vx = vx; }
    public void setVy(double vy) { this.vy = vy; }
    public void setAy(double ay) { this.ay = ay; }
    public double getAy() { return ay; }
    public void setPosition(Point position) {
        this.position = position;
    }
    public int getValue() {
        return value;
    }
    public double getRotation() {
        return rotation;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public void setUsed() {
        this.used = true;
    }
    public boolean isUsed() {
        return used;
    }
    public void setIcon(BufferedImage icon) {
        this.icon = icon;
    }
    public BufferedImage getIcon() {
        return icon;
    }
    public void setSelectedIcon(BufferedImage selectedIcon) {
        this.selectedIcon = selectedIcon;
    }
    public BufferedImage getSelectedIcon() {
        return selectedIcon;
    }
    public void update(int samplingRate) {
        double div = 1.0 / samplingRate;
        rotation += (position.getX() - prevPosition.getX())*0.02;
        this.prevPosition.setX(this.position.getX());
        this.prevPosition.setY(this.position.getY());
        vx += ax / div;
        vy += ay / div;
        position.move(vx/div, vy/div);
    }
}
