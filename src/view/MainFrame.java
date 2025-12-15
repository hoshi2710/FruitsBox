package view;

import controller.ButtonManager;
import controller.GameManager;
import controller.KeyboardHandler;
import controller.MouseHandler;

import javax.swing.*;
import java.awt.*;
/**
 *  <b>[🔍view] - MainFrame.java</b><br>
 *  메인 프레임 객체를 정의하는 View 클래스<br>
 */
public class MainFrame extends JFrame {
    private PlayPanel playPanel; // 플레이 패널 객체
    private double prevX, prevY; // 창의 이전 X,Y 좌표
    private double prevVx=0, prevVy=0; // 창의 이전 X,Y 방향 속도
    private double vx=0, vy=0,ax=0,ay=0; // 창의 X,Y 속도와 가속도

    /**
     * @param gameManager 게임 매니저 객체
     * @param buttonManager 버튼 매니저 객체
     */
    public MainFrame(GameManager gameManager, ButtonManager buttonManager) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // 화면 사이즈 가져오기
        MouseHandler mouseHandler = gameManager.getMouseHandler(); // 마우스 핸들러 가져오기
        KeyboardHandler keyboardHandler = gameManager.getKeyboardHandler(); // 키보드 핸들러 가져오기
        this.playPanel = new PlayPanel(gameManager,mouseHandler, buttonManager); // 새로운 플레이 패널을 생성
        gameManager.setPlayPanel(playPanel); // 생성된 플레이 패널을 게임 매니저에게 넘겨서 관리할 수 있도록 한다.
        this.addMouseListener(mouseHandler); // 마우스 핸들러를 프레임에 추가한다.
        this.addMouseMotionListener(mouseHandler); // 마우스 모션 핸들러를 프레임에 추가한다.
        this.addKeyListener(keyboardHandler); // 키보드 핸들러를 프레임에 추가한다.
        add(playPanel); // 플레이 패널을 프레임에 추가한다.
        setSize(500,400); // 처음 창 사이즈를 500, 400으로 설정한다.
        setLocation(screenSize.width/2-this.getWidth()/2,screenSize.height/2-this.getHeight()/2); // 게임 화면이 정중앙에 표시되도록 설정한다.
        setVisible(true); // 창을 띄운다.
        setDefaultCloseOperation(EXIT_ON_CLOSE); // 창을 닫으면 프로그램이 종료되도록 설정한다.
        // 이전 창 위치 X,Y 좌표도 초기 위치로 일단 초기화 시킨다.
        this.prevX = this.getLocationOnScreen().getX();
        this.prevY = this.getLocationOnScreen().getY();
    }

    /**
     * 가로 길이를 가졍옵니다.
     * @return 가로 길이
     */
    public int getWidth() {
        return this.getSize().width;
    }

    /**
     * 세로 길이를 가져옵니다.
     * @return 세로 길이
     */
    public int getHeight() {
        return this.getSize().height;
    }

    /**
     * X 축 속도를 가져옵니다.
     * @return X 축 속도
     */
    public double getVx() {
        return vx;
    }
    /**
     * X 축 속도를 가져옵니다.
     * @return X 축 속도
     */
    public double getVy() {
        return vy;
    }
    /**
     * X 축 속도를 설정 합니다.
     * @param vx X 축 속도
     */
    public void setVx(double vx) {
        this.vx = vx;
    }
    /**
     * Y 축 속도를 설정 합니다.
     * @param vy Y 축 속도
     */
    public void setVy(double vy) {
        this.vy = vy;
    }

    /**
     * 이전 X 좌표를 가져옵니다.
     * @return 이전 X 좌표
     */
    public double getPrevX() {
        return prevX;
    }
    /**
     * 이전 Y 좌표를 가져옵니다.
     * @return 이전 Y 좌표
     */
    public double getPrevY() {
        return prevY;
    }
    /**
     * 이전 X 좌표를 설정합니다.
     * @param prevX 이전 X 좌표
     */
    public void setPrevX(double prevX) {
        this.prevX = prevX;
    }
    /**
     * 이전 Y 좌표를 설정합니다.
     * @param prevY 이전 Y 좌표
     */
    public void setPrevY(double prevY) {
        this.prevY = prevY;
    }
    /**
     * 이전 X축 속도를 가져옵니다.
     * @return 이전 X 축 속도
     */
    public double getPrevVx() {
        return prevVx;
    }
    /**
     * 이전 Y축 속도를 가져옵니다.
     * @return 이전 Y 축 속도
     */
    public double getPrevVy() {
        return prevVy;
    }
    /**
     * 이전 X축 속도를 설정합니다.
     * @param prevVx 이전 X 축 속도
     */
    public void setPrevVx(double prevVx) {
        this.prevVx = prevVx;
    }
    /**
     * 이전 Y축 속도를 설정합니다.
     * @param prevVy 이전 Y 축 속도
     */
    public void setPrevVy(double prevVy) {
        this.prevVy = prevVy;
    }

    /**
     * X 축 가속도를 설정한다.
     * @param ax X 축 가속도
     */
    public void setAx(double ax) {
        this.ax = ax;
    }

    /**
     * Y 축 가속도를 설정한다.
     * @param ay Y 축 가속도
     */
    public void setAy(double ay) {
        this.ay = ay;
    }

    /**
     * X 축 가속도를 가져온다.
     * @return X 축 가속도
     */
    public double getAx() {
        return ax;
    }
    /**
     * Y 축 가속도를 가져온다.
     * @return Y 축 가속도
     */
    public double getAy() {
        return ay;
    }
}
