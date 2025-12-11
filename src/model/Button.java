package model;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Button {
    private Rectangle buttonRectangle, shadowRectangle;
    int x,y,width,height, shadowOffset, textSize;
    boolean pressed;
    boolean enabled;
    Color color;
    String text;
    Font font;
    BasicStroke stroke;
    FontMetrics fm;
    private Runnable onClick;
    public Button(int x, int y, int width, int height, Color color, String text, int textSize) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.text = text;
        this.textSize = textSize;
        this.pressed = false;
        this.enabled = true;
        this.font = new Font("Comic Sans MS", Font.BOLD, this.textSize);
        this.shadowOffset = 10;
        this.stroke = new BasicStroke(3f);
        this.buttonRectangle = new Rectangle(x,y,width,height);
        this.shadowRectangle = new Rectangle(x+shadowOffset,y+shadowOffset,width,height);
    }
    public void show(Graphics2D g2d) {
        g2d.setColor(new Color(35,35,35,255));
        g2d.fill(this.shadowRectangle);
        if(pressed) buttonRectangle.setLocation(x+shadowOffset,y+shadowOffset);
        else buttonRectangle.setLocation(x,y);
        g2d.setColor(color);
        g2d.fill(buttonRectangle);
        g2d.setStroke(this.stroke);
        g2d.setColor(new Color(35,35,35,255));
        g2d.draw(buttonRectangle);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        int playButtonTextX = x + width/2 - fm.stringWidth(text)/2;
        int playButtonTextY = y + height/2 + fm.getAscent()/4;
        if(pressed) {
            playButtonTextX += this.shadowOffset;
            playButtonTextY += this.shadowOffset;
        }
        g2d.drawString(text,playButtonTextX,playButtonTextY);
    }
    public Point getLocation(){
        Point location = new Point(x,y);
        return location;
    }
    public void setLocation(int x, int y){
        this.x = x;
        this.y = y;
        buttonRectangle.setLocation(x,y);
        shadowRectangle.setLocation(x+shadowOffset,y+shadowOffset);
    }
    public Point getSize(){
        Point size = new Point(width,height);
        return size;
    }
    public boolean isPressed(){
        return pressed;
    }
    public void setPressed(boolean pressed){
        this.pressed = pressed;
    }
    public void setOnClick(Runnable onClick){
        this.onClick = onClick;
    }
    public void run() {
        if(onClick == null) return;
        onClick.run();
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
