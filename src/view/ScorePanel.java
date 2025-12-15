package view;

import controller.GameManager;

import javax.swing.*;
import java.awt.*;
/**
 *  <b>[🔍view] - ScorePanel.java</b><br>
 *  점수 패널 객체를 정의하는 View 클래스<br>
 */
public class ScorePanel extends JPanel {
    private GameManager gameManager; // 게임 매니저 객체
    private int marginTop, marginRight; // 상단 여백, 우측 여백
    private HUDPanel hudPanel; // HUD 패널 객체

    /**
     * @param gameManager 게임 매니저 객체
     * @param hudPanel HUD 패널 객체
     */
    public ScorePanel(GameManager gameManager, HUDPanel hudPanel) {
        this.gameManager = gameManager; // 게임 매니저 객체 설정
        this.hudPanel = hudPanel; // HUD 패널 객체 설정
        this.marginTop = 10; // 상단 여백 설정
        this.marginRight = 10; // 우측 여백 설정
        setBackground(new Color(61, 138, 93)); // HUD 패널과 동일한 배경 설정
        setLayout(null); // 레이아웃 설정하지 않음
        setVisible(true); // 패널 보이기
    }

    /**
     * 점수 값을 그립니다.
     * @param g2d G2D 객체
     */
    public void drawScore(Graphics2D g2d) {
        int score = this.gameManager.getScore(); // 점수값을 게임 매니저로 부터 가져오기
        g2d.setColor(Color.white); // 글자 색은 흰색으로 설정
        g2d.setFont(new Font("Arial", Font.BOLD, 40)); // 폰트는 Arial, Bold, 40 으로 설정
        FontMetrics fontMetrics = g2d.getFontMetrics(); // 폰트 매트릭스 가져오기
        // 폰트 매트릭스를 활용하여 텍스트의 x,y 위치 값을 계산하기
        int x = hudPanel.getWidth()/2 - (int)(fontMetrics.stringWidth(Integer.toString(score))) - marginRight;
        int y = hudPanel.getHeight() - fontMetrics.getAscent() + marginTop;
        // 계산한 좌표에 텍스트 그리기
        g2d.drawString(Integer.toString(score), x,y);
    }

    /**
     * paint 작업이 일어날때 발생하는 작업
     * @param g 그래픽 객체
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawScore((Graphics2D) g); // 점수 텍스트 그리기
    }
}
