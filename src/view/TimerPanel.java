package view;

import controller.GameManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
/**
 *  <b>[🔍view] - TimerPanel.java</b><br>
 *  타이머 패널 객체를 정의하는 View 클래스<br>
 */
public class TimerPanel extends JPanel {
    private GameManager gameManager; // 게임 매니저 객체
    private double width; // 가로 길이
    private double height; // 세로 길이
    private double marginTop; // 상단 여백
    private double marginLeft; // 왼쪽 여백

    /**
     * @param gameManager 게임 매니저 객체
     */
    public TimerPanel(GameManager gameManager) {
        this.gameManager = gameManager; // 게임 매니저 객체 저장
        this.marginTop = 10.0; // 상단 여백 설정
        this.marginLeft = 10.0; // 왼쪽 여백 설정
        this.width = 50.0; // 가로 길이 설정
        this.height = 50.0; // 세로 길이 설정
        setBackground(new Color(61, 138, 93)); // 배경을 HUD 패널과 같게 하기
        setLayout(new FlowLayout()); // 플로우 레이아웃으로 설정
        setVisible(true); // 패널 표시
    }

    /**
     * 타이머를 그립니다.
     * @param g2d G2D 객체
     */
    public void drawTimer(Graphics2D g2d) {
        double remains =  360 * ((double) gameManager.getRemainTime() / gameManager.getInitTime()); // 남은 시간 퍼센트지를 전체 360으로 스케일을 맞춥니다.
        g2d.setStroke(new BasicStroke(2f)); // 외부 외곽선 설정
        g2d.setColor(Color.LIGHT_GRAY); // 연한 회색으로 외곽선 설정
        Ellipse2D.Double outerCircle = new Ellipse2D.Double(marginLeft,marginTop,width, height); // 외부 원 외곽선
        Ellipse2D.Double innerCircle = new Ellipse2D.Double(marginLeft + width/4,marginTop + height/4,width/2,height/2); // 내부 원 외곽선
        g2d.draw(outerCircle); // 외부 원 외곽선 그리기
        g2d.draw(innerCircle); // 내부 원 외곽선 그리기
        g2d.setColor(Color.WHITE); // 내부 실린더 색깔은 흰색으로 설정
        for(int i=0; i<remains; i+=1) { // 남은 게이지 만큼 원을 그려넣기. 중심점을 기준으로 원형으로 여러개의 원을 찍어 도넛 형태의 타이머처럼 보이게 하기
            double angle = i*Math.PI/180;
            double size = width/4;
            double x = marginLeft + (width/2) - (size/2) - (width/2.8)*Math.sin(angle);
            double y = marginTop + (height/2) - (size/2) - (height/2.8)*Math.cos(angle);
            g2d.fill(new Ellipse2D.Double(x,y,size,size));
        }
        // 폰트는 Arial, 볼드, 40 으로 설정
        g2d.setFont(new Font("Arial",Font.BOLD,40));
        FontMetrics fm = g2d.getFontMetrics(); // 폰트 매트릭스 가져오기
        g2d.drawString(Integer.toString(gameManager.getRemainTime()),(int)(marginLeft*2 + width), (int)(marginTop+fm.getAscent())); // 타이머 텍스트 그리기
    }

    /**
     * paint 작업이 일어날때 발생하는 작업
     * @param g 그래픽 객체
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawTimer((Graphics2D) g); // 타이머 그리기
    }
}
