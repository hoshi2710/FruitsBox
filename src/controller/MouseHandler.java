package controller;

import Physics.PhysicsEngine;
import enums.AppleType;
import enums.GameStatus;
import model.Apple;
import model.Button;
import model.Point;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
/**
 *  <b>[⚙️Controller] - MouseHandler.java</b><br>
 *  게임의 마우스 입력을 제어하는 핸들러<br>
 */
public class MouseHandler implements MouseListener, MouseMotionListener {
    GameManager gameManager; // 게임 매니저 객체
    List<Button> buttons; // 클릭을 감지할 버튼 리스트

    /**
     * @param gameManager 게임 매니저 객체
     */
    public MouseHandler(GameManager gameManager) {
        this.gameManager = gameManager; // 게임 매니저 객체 변수에 저장
        buttons = new ArrayList<>(); // 클릭을 감지할 버튼 리스트를 위한 ArrayList 생성
    }
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * 마우스 버튼이 눌렸을때 진행할 작업
     * @param e 이벤트 객체
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if(gameManager.getGameStatus() == GameStatus.PLAYING) { // 게임 플레이가 진행중일 때
            List<Apple> apples = gameManager.getApples(); // 전체 사과 객체 리스트 가져오기
            List<Apple> selectedApples = gameManager.getSelectedApple(); // 선택된 전체 사과 객체 리스트 가져오기
            int x = e.getX(); // 클릭된 좌표 X
            int y = e.getY(); // 클릭된 좌표 Y
            Apple result = null; // 클릭한 사과 결과를 저장할 변수
            for(Apple apple : apples) { // 전체 사과를 순회하면서 클릭된 사과 검색
                if(apple.isUsed()) continue; // 이미 파괴된 사과라면 건너뛰기
                Point p = apple.getPosition(); // 사과 위치 객체 가져오기
                // X, Y 좌표를 각각 검사하여 사과를 클릭한 좌표가 맞는지 검사
                if (p.getX() > x || p.getX() + apple.getSize() < x) continue;
                if (p.getY() + 30 > y || p.getY() + apple.getSize() + 30 < y) continue;
                // 맞다면 결과 변수에 해당 사과를 저장하고 반복문 종료
                result = apple;
                break;
            }
            // 결과가 존재하고 사과가 선택되지 않은 상태로 설정되어 있다면
            if(result != null && !(result.isSelected())){
                result.setSelected(true); // 사과의 선택 상태를 true로 설정
                selectedApples.add(result); // 해당 사과를 선택한 사과 리스트에 추갘
                gameManager.getAudioManager().playSelectFx(); // 사과 선택 효과음 재생
            }
        }else{ // 게임 플레이 중이 아닐때는 버튼 클릭을 감지하도록 하기
            for(Button button : this.buttons){ // 버튼을 순회하면서 버튼의 클릭 여부를 검사
                if(!button.isEnabled()) continue; // 버튼이 비활성화 되어 있는 경우 건너뛰기
                Point point = button.getLocation(); // 버튼의 위치 객체 가져오기
                Point size = button.getSize(); // 버튼의 사이즈 객체 가져오기
                // X,Y 좌표를 이용하여 해당 버튼을 클릭 했는지 검사
                if(!(e.getX() >= point.getX() && e.getX() <= point.getX() + size.getX())) continue;
                if(!(e.getY() >= point.getY() + 30 && e.getY() <= point.getY() + size.getY() + 30)) continue;
                // 맞다면 해당 버튼이 눌렸음을 true로 설정
                button.setPressed(true);
            }
        }
    }

    /**
     * 마우스 버튼을 땠을때 진행할 작업
     * @param e 이벤트 객체
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if(gameManager.getGameStatus() == GameStatus.PLAYING) { // 게임이 플레이 중일 때
            List<Apple> apples = gameManager.getApples(); // 사과 전체 리스트 가져오기
            List<Apple> selectedApples = gameManager.getSelectedApple(); // 선택된 사과 전체 리스트 가져오기
            List<Apple> bombApples =  new ArrayList<>(); // 폭탄 사과 를 선별해 저장할 리스트 만들기
            PhysicsEngine engine = gameManager.getEngine(); // 물리 엔진 객체 가져오기
            int sum = 0, mul = 1; // 점수 합계와 배수
            for (Apple apple : selectedApples) { // 선택된 사과를 모두 순회하며 사과 값을 검사
                int value = apple.getValue(); // 사과의 값 가져오기
                if (apple.getAppleType() == AppleType.GOLDEN) mul *= 2; // 황금 사과라면 배수를 *2 하기
                sum += value; // 사과 값을 합계에 더하기
            }
            if (sum == 10) { // 만약 합계가 10 이라면 점수 득점과 사과 파괴 진행
                gameManager.loggingLastInteractedTime(); // 마지막 상호 작용 시간 기록
                for (Apple apple : selectedApples) { // 선택된 사과를 모두 순회하면서 폭탄 사과가 존재하는지 검사
                    if (apple.getAppleType() == AppleType.BOMB){ // 폭탄 사과가 발견되면
                        bombApples.add(apple); // 폭탄 사과 리스트에 해당 객체를 추가
                        engine.applyExplode(apple, apples); // 물리엔진에 폭발 물리력을 발생 시키도록 요청
                    }
                    gameManager.getAudioManager().playDestroyFx(apple.getAppleType()); // 해당 사과의 타입에 맞는 효과음 재생
                    apple.setUsed(); // 사과가 사용됨(파괴됨)으로 처리
                }
                // 폭탄 사과 리스트에 객체가 하나라도 존재한다면 모든 사과와 폭탄 사과를 비교하여 맞 닿아있는 사과를 조사하고 발견시 같이 파괴되도록 하기
                if (!bombApples.isEmpty()) {
                    for(Apple apple : apples) {  // 전체 사과 순회
                        if (apple.isUsed())continue; // 이미 파괴된 사과는 무시하기
                        for(Apple apple1 : bombApples) { // 폭탄 사과 리스트 순회
                            if (apple == apple1) continue; // 전체 사과 순회 중 같은 폭탄 사과 발견시 무시
                            if (!engine.isCollided(apple1, apple)) continue; // 폭탄 사과와 그외 임의의 사과 하나가 맞닿아 있는지 검사
                            apple.setUsed(); // 맞 닿아 있다면 사과를 파괴함
                            if (apple.getAppleType() == AppleType.GOLDEN) mul *= 2; // 파괴된 사과가 황금 사과라면 배수 *2 하기
                            sum += apple.getValue(); // 사과로 부터 값을 가져와 득점을 위한 합계에 더하기
                        }
                    }
                }
                sum *= mul; // 합계에 배수를 곱하기
                gameManager.setScore(gameManager.getScore() + sum); // 최종 합계를 현재 점수에 더하여 반영하기
                gameManager.updateLastPangTime(); // 마지막 득점 시간을 업데이트 하기
                gameManager.updateCombo(); // 콤보값을 +1 하여 업데이트 하기
            }
            for (Apple apple : selectedApples) apple.setSelected(false); // 선택한 사과 객체 모두 선택 해제
            selectedApples.clear(); // 선택한 사과 목록 리스트 모두 지우기
        }else{ // 플레이 중이 아닐 경우 버튼 클릭 여부 검사하도록 하기
            for(Button button : this.buttons){ // 버튼들을 모두 순회하면서 눌렀다가 뗀 버튼이 있는지 검사하기
                if(!button.isPressed()) continue; // 눨려 있던 버튼이 아니라면 건너뛰기
                if(!button.isEnabled()) continue; // 활성화 된 버튼이 아니라면 건너뛰기
                button.setPressed(false); // 위 조건에 해당사항이 없다면 버튼의 눌림 상태를 false로 설정
                Point point = button.getLocation(); // 버튼의 위치 객체 가져오기
                Point size = button.getSize(); // 버튼의 사이즈 객체 가져오기
                // X,Y좌표를 이용하여 버튼에서 손을 뗀 위치가 버튼 안인지 검사
                if(!(e.getX() >= point.getX() && e.getX() <= point.getX() + size.getX())) continue;
                if(!(e.getY() >= point.getY()+30 && e.getY() <= point.getY() + size.getY()+30)) continue;
                // 위 조건에 문제가 없다면 버튼에 할당된 동작 실행
                button.run();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    /**
     * 마우스를 드래그 했을때 진행할 작업
     * @param e 이벤트 객체
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        List<Apple> selectedApples = gameManager.getSelectedApple(); // 선택된 사과 리스트 가져오기
        // 게임 플레이가 진행중이고 선택된 사과가 하나라도 존재할때
        if(gameManager.getGameStatus() == GameStatus.PLAYING && !selectedApples.isEmpty()) {
            List<Apple> apples = gameManager.getApples(); // 전체 사과 리스트 가져오기
            PhysicsEngine engine = gameManager.getEngine(); // 물리 엔진 객체 가져오기
            // 이벤트가 발생한 X,Y 좌표 가져오기
            int x = e.getX();
            int y = e.getY();
            // 드래그한 사과 객체를 검색하고 저장할 변수 선언
            Apple result = null;
            for (Apple apple : apples) { // 모든 사과를 순회하면서 드래그하여 선택된 사과를 검색
                if (apple.isUsed()) continue; // 이미 파괴된 사과는 건너뛰기
                Point p = apple.getPosition(); // 사과의 위치 객체 가져오기
                // X,Y 좌표를 이용하여 사과 객체가 선택되었는지 검사
                if (p.getX() > x || p.getX() + apple.getSize() < x) continue;
                if (p.getY() + 30 > y || p.getY() + apple.getSize() + 30 < y) continue;
                // 가장 마지막으로 선택된 사과를 가져와서 해당 사과와 선택하고자 하는 사과의 거리가 오차범위 내로 맞닿아있는지 검사
                Apple lastApple = selectedApples.getLast();
                double r = lastApple.getSize() / 2 + apple.getSize() / 2;
                if (engine.getDistanceBetweenCenter(lastApple, apple) > r * 1.2) continue;
                // 위조건에 문제가 없다면 결과 변수에 해당 사과를 추가하고 반복문 종료
                result = apple;
                break;
            }
            if (result != null && !(result.isSelected())) { // 결과값이 존재하고 해당 사과 객체가 아직 선택이 안된 상태로 되어 있다면
                result.setSelected(true); // 해당 사과객체를 선택 된 것으로 설정
                selectedApples.add(result); // 선택한 사과 객체 리스트에 해당 사과를 추가
                gameManager.getAudioManager().playSelectFx(); // 사과 선택 효과음을 재생
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * 해당 버튼 객체를 샘플링 대기열에 추가합니다.
     * @param button 샘플링 대기열에 올릴 버튼 객체
     */
    public void addSamplingButton(Button button) {
        buttons.add(button); // 버튼 샘플링 대기열에 지정한 버튼 객체를 추가
    }
}
