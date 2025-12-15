package view;

import controller.GameManager;

import javax.swing.*;
/**
 *  <b>[🔍view] - HUDFrame.java</b><br>
 *  HUD 프레임 객체를 정의하는 View 클래스<br>
 */
public class HUDFrame extends JFrame {
    private HUDPanel hudPanel; // HUD 패널 객체
    /**
     * @param gameManager 게임 매니저 객체
     */
    public HUDFrame(GameManager gameManager) {
        this.hudPanel = new HUDPanel(gameManager); // HUD 패널 만들기
        setSize(350,100); // 창의 사이즈를 350, 100으로 설정
        add(hudPanel); // 패널을 프레임에 추가
        setResizable(false); // 창의 크기를 조정할 수 없도록 설정
        setDefaultCloseOperation(EXIT_ON_CLOSE); // 창을 닫으면 프로그램이 종료 되도록 설정
    }

}
