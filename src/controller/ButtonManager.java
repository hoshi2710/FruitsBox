package controller;

import model.Button;

import java.util.HashSet;
import java.util.Set;

/**
 *  <b>[⚙️Controller] - ButtonManager.java</b><br>
 *  g2d 객체로 커스텀으로 그린 버튼을 관리하는 매니저<br>
 */
public class ButtonManager {
    Set<Button> buttons; // 생성된 버튼들을 모아놓을 Set 변수
    public ButtonManager() {
        this.buttons = new HashSet<>(); // 버튼을 모아놓을 새 해쉬 셋 생성
    }

    /**
     * 전달 받은 버튼을 활성화 하고 표시합니다.
     * @param button 활성화 할 버튼 객체
     */
    public void enableButton(Button button) {
        buttons.add(button); // 버튼을 Set에 추가
        button.setEnabled(true); // 버튼 활성화
    }

    /**
     * 현재 버튼 매니저에서 관리하는 모든 버튼을 비활성화 하고 숨깁니다.
     */
    public void disableAllButtons() {
        for (Button button : buttons) { // 모든 버튼 순회
            button.setEnabled(false); // 모든 버튼 비활성화
        }
        buttons.clear(); // 관리 중인 버튼 모음 Set 비우기
    }
}
