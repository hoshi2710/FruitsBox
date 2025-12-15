package view;

import controller.GameManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
/**
 *  <b>[🔍view] - HUDPanel.java</b><br>
 *  HUD 패널 객체를 정의하는 View 클래스<br>
 */
public class HUDPanel extends JPanel {
    private TimerPanel timerPanel; // 타이머 패널 객체
    private ScorePanel scorePanel; // 점수 패널 객체

    /**
     * @param gameManager 게임 매니저 객체
     */
    public HUDPanel(GameManager gameManager) {
        this.timerPanel = new TimerPanel(gameManager); // 타이머 패널 객체 설정
        this.scorePanel = new ScorePanel(gameManager,this); // 점수 패널 객체 설정
        setBackground(new Color(61, 138, 93)); // 배경을 녹색으로 설정
        setLayout(new GridLayout(1, 2)); // 레이아웃을 1,2인 GridLayout으로 설정
        add(timerPanel); // 타이머 패널을 추가
        add(scorePanel); // 점수 패널을 추가
        setVisible(true); // 창 표시
    }
}
