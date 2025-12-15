package controller;

import enums.GameStatus;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
/**
 *  <b>[⚙️Controller] - KeyboardHandler.java</b><br>
 *  게임의 키 입력을 제어하는 핸들러<br>
 */
public class KeyboardHandler implements KeyListener {
    private GameManager gameManager; // 상위 객체로 부터 전달 받은 게임 매니저 객체

    /**
     * @param gm 게임 매니저 객체
     */
    public KeyboardHandler(GameManager gm) {
        this.gameManager = gm;
    } // 게임 매니저 객체 변수에 저장

    /**
     *  키가 타이핑 되었을때 진행할 작업
     * @param e 키 이벤트 객체
     */
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) { // ESC 키가 타이핑 되었다면
            // 게임이 진행중이라면 정지, 정지된 상태라면 이어서 플레이하는 상태로 만들도록 한다.
            if(gameManager.getGameStatus() == GameStatus.PAUSED)
                gameManager.setGameStatus(GameStatus.PLAYING);
            else
                gameManager.setGameStatus(GameStatus.PAUSED);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
