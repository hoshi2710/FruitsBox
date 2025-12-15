package model;

import java.awt.*;
/**
 *  <b>[🗿model] - Button.java</b><br>
 *  버튼 객체를 정의하는 model 클래스<br>
 */
public class Button {
    private Rectangle buttonRectangle, shadowRectangle; // 버튼 사각형, 그림자 사격형
    int x,y,width,height, shadowOffset, textSize; // x,y 좌표, 가로 / 세로 길이, 그림자 오프셋, 텍스트 크기
    boolean pressed; // 버튼 눌림 여부
    boolean enabled; // 버튼 활성화 여부
    Color color; // 버튼 색
    String text; // 버튼 텍스트
    Font font; // 버튼 폰트
    BasicStroke stroke; // 버튼 외곽선
    FontMetrics fm; // 버튼 폰트 메트릭스
    private Runnable onClick; // 버튼 온클릭 함수

    /**
     * @param x x좌표
     * @param y y좌표
     * @param width 가로 길이
     * @param height 세로 길이
     * @param color 버튼 색상
     * @param text 버튼 텍스트
     * @param textSize 버튼 텍스트 사이즈
     */
    public Button(int x, int y, int width, int height, Color color, String text, int textSize) {
        this.x = x; // x 좌표
        this.y = y; // y 좌표
        this.width = width; // 가로 길이
        this.height = height; // 세로 길이
        this.color = color; // 버튼 색상
        this.text = text; // 버튼 텍스트
        this.textSize = textSize; // 버튼 텍스트 사이즈
        this.pressed = false; // 버튼 눌림 여부
        this.enabled = true; // 버튼 활성화 여부
        this.font = new Font("Comic Sans MS", Font.BOLD, this.textSize); // 버튼 폰트
        this.shadowOffset = 10; // 버튼 그림자 오프셋
        this.stroke = new BasicStroke(3f); // 버튼 외곽 윤관선
        this.buttonRectangle = new Rectangle(x,y,width,height); // 버튼 사각형
        this.shadowRectangle = new Rectangle(x+shadowOffset,y+shadowOffset,width,height); // 버튼 그림자 사각형
    }

    /**
     * 버튼을 표시합니다.
     * @param g2d G2D 객체
     */
    public void show(Graphics2D g2d) {
        g2d.setColor(new Color(35,35,35,255)); // 색상을 진한 회색 정도로 설정
        g2d.fill(this.shadowRectangle); // 버튼 그림자 사각형 그리기 / 채우기
        //버튼이 눌렸을때와 눌리지 않았을때 버튼 사각형 위치를 다르게 설
        if(pressed) buttonRectangle.setLocation(x+shadowOffset,y+shadowOffset);
        else buttonRectangle.setLocation(x,y);
        g2d.setColor(color); // 색상을 사전에 설정한 색상으로 설정
        g2d.fill(buttonRectangle); // 버튼 사각형을 그리기 / 채우기
        g2d.setStroke(this.stroke); // 버튼 사각형 외곽선 설정
        g2d.setColor(new Color(35,35,35,255)); // 버튼 사각형 외관선 진한 회색 정도로 설정
        g2d.draw(buttonRectangle); // 외곽선 그리기
        g2d.setFont(font); // 사전에 지정한 폰트 설정
        fm = g2d.getFontMetrics(); // 폰트 매트릭스 변수에 업데이트
        //텍스트 가운데 정렬 하여 그리기
        int playButtonTextX = x + width/2 - fm.stringWidth(text)/2;
        int playButtonTextY = y + height/2 + fm.getAscent()/4;
        if(pressed) {
            playButtonTextX += this.shadowOffset;
            playButtonTextY += this.shadowOffset;
        }
        g2d.drawString(text,playButtonTextX,playButtonTextY);
    }

    /**
     * 버튼의 위치 객체를 반환합니다.
     * @return 위치 객체
     */
    public Point getLocation(){
        Point location = new Point(x,y);
        return location;
    }

    /**
     * 버튼의 위치를 설정합니다.
     * @param x x 좌표
     * @param y y 좌표
     */
    public void setLocation(int x, int y){
        this.x = x;
        this.y = y;
        buttonRectangle.setLocation(x,y);
        shadowRectangle.setLocation(x+shadowOffset,y+shadowOffset);
    }

    /**
     * 버튼의 사이즈 객체를 가져옵니다.
     * @return 사이즈 객체
     */
    public Point getSize(){
        Point size = new Point(width,height);
        return size;
    }

    /**
     * 버튼의 눌림 여부를 가져옵니다.
     * @return 버튼의 눌림 여부
     */
    public boolean isPressed(){
        return pressed;
    }

    /**
     * 버튼의 눌림 여부를 설정합니다.
     * @param pressed 변경할 버튼의 눌림 여부
     */
    public void setPressed(boolean pressed){
        this.pressed = pressed;
    }

    /**
     * 버튼을 눌렀을때 진행할 작업 함수를 설정합니다.
     * @param onClick 작업 함수
     */
    public void setOnClick(Runnable onClick){
        this.onClick = onClick;
    }

    /**
     * 지정한 작업 함수를 실행합니다.
     */
    public void run() {
        if(onClick == null) return;
        onClick.run();
    }

    /**
     * 버튼이 활성화 되어 있는지 여부를 가져옵니다.
     * @return 활성화 여부
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 버튼의 활성화 여부를 변경 시킵니다.
     * @param enabled 변경할 버튼의 활성화 여부
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
