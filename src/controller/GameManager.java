package controller;

import Physics.PhysicsEngine;
import enums.AppleType;
import enums.GameStatus;
import model.Apple;
import model.Sparkle;
import view.ComboFrame;
import view.HUDFrame;
import view.MainFrame;
import view.PlayPanel;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.*;
import java.util.*;

/**
 *  <b>[⚙️Controller] - GameManager.java</b><br>
 *  게임의 전반적인 로직을 총괄하는 매니저 클래스<br>
 */
public class GameManager {
    private int frameRate; // 게임 메니져를 초기화 할때 설정한 프레임 레이트 값
    private List<Timer> timers; // 타이머 객체들을 모아놓는 리스트
    protected List<Apple> apples; // 게임 중 나타나는 모든 사과 객체들을 담는 리스트
    protected List<Apple> selectedApples; // 사용자가 선택한 사과 객체들을 담는 리스트
    protected List<List<Sparkle>> sparkles; // 사과가 소멸, 폭발할때 발생하는 부스러기, 잔여물들에 대한 객체들을 담는 이중 리스트
    protected int score; // 현재 점수값을 저장하는 변수
    protected PhysicsEngine engine; // 물리 엔진 객체를 담는 변수
    // 남은 시간, 초기화되는 시간, 마지막으로 상호자용한 시간, 마지막으로 점수를 낸 시간, 콤보 카운팅 변수
    private int remainTime, initTime, lastInteractedTime, lastPangTime, combo;
    private ButtonManager buttonManager; // 버튼 매니저 객체 변수
    private MainFrame mainFrame; // 메인 프레임 객체 변수
    private HUDFrame hudFrame; // HUD 프레임 객체 변수
    private PlayPanel playPanel; // 플레이 패널 객체 변수
    private MouseHandler mouseHandler; // 마우스 핸들러 객체 변수
    private KeyboardHandler keyboardHandler; // 키(키보드) 핸들러 객체 변수
    private AudioManager audioManager; // 오디오(효과음, BGM) 매니저 객체 변수
    private GameStatus gameStatus; // 현재 게임 상태(대기중, 시작, 정지, 게임오버) 상태를 이넘(ENUM)의 형태로 저장
    private List<ComboFrame> comboFrames; // 콤보를 발생시킬때마다 띄워지는 콤보 창 객체들을 담을 리스트
    public GameManager(int frameRate) {
        this.mouseHandler = new MouseHandler(this); // 새 마우스 핸들러 생성
        this.keyboardHandler = new KeyboardHandler(this); // 새 키 핸들러 생성
        this.audioManager = new AudioManager(frameRate); // 새 오디오 매니저 생성
        this.buttonManager = new ButtonManager(); // 새 버튼 매니저 생성
        this.mainFrame = new MainFrame(this, buttonManager); // 새 메인 프레임 객체 생성
        this.hudFrame = new HUDFrame(this); // 새 HUD 프레임 생성
        this.frameRate = frameRate; // 입력 받은 프레임레이트 저장
        this.initTime = 200; // 처음 시작 할때 제한 시간 설정
        this.gameStatus = GameStatus.STANDBY; // 기본 상태를 STANDBY로 설정
        this.apples = new ArrayList<>(); // 사과 객체를 담을 새로운 ArrayList 생성
        this.selectedApples = new ArrayList<>(); // 선택된 사과 객체를 담을 새로운 ArrayList 생성
        this.timers = new ArrayList<>(); // 타이머들을 모두 모아놓을 ArrayList 생성
        this.engine = new PhysicsEngine( this.playPanel, this.mainFrame); // 물리 엔진 객체 생성
        this.sparkles = new ArrayList<>(); // 파편 객체들을 담을 ArrayList 생성
        this.comboFrames = new ArrayList<>(); // 콤보 창 객체를 담을 ArrayList 생성
        this.combo = 0; // 콤보 변수 0으로 초기화
        timers.add(new RenderingTimer(1000/this.frameRate)); // 렌더링 타이머 생성 / 추가
        timers.add(new SamplingTimer(1000/(this.frameRate))); // 샘플링 타이머 생성 / 추가
        timers.add(new AppleDropTimer(15,3000,10000,this)); // 사과 드롭 타이머 생성 / 추가
        timers.add(new GameTimer(1000)); // 게임 타이머 생성 / 추가
        initGame(); // 게임 초기화
    }

    /**
     * 게임을 초기화 합니다.
     * 모든 타이머들을 시작하고 메인 프레임을 띄웁니다.
     */
    public void initGame() {
        for(Timer timer : timers) timer.start(); // 모든 타이머를 시작 합니다.
        this.mainFrame.setVisible(true); // 메인 프레임을 띄웁니다.
    }

    /**
     * 게임을 시작합니다.
     * 게임 시작을위해 필요한 초기 작업을 진행합니다.
     */
    public void startGame() {
        this.apples.clear(); // 생성된 모든 사과를 지웁니다.
        this.selectedApples.clear(); // 선택된 모든 사과를 지웁니다.
        this.score = 0; // 점수를 0으로 초기화 합니다.
        this.combo = 0; // 콤보를 0으로 초기화 합니다.
        this.remainTime = this.initTime; // 초기화 시간을 기반으로 남은 시간을 초기화 합니다.
        this.lastInteractedTime = this.initTime; // 첫 상호작용 시간을 초기화 한 시간으로 초기화 합니다.
        this.lastPangTime = this.initTime; // 마지막 득점 시간도 초기화 한 시간으로 초기화 합니다.
        mainFrame.setVisible(true); // 메인 프레임을 띄웁니다.
        hudFrame.setVisible(true); // HUD 프레임을 띄웁니다.
        for(int i=0; i<30; i++) AppleDropTimer.dropApples(this); // 처음으로 사과 30개를 드롭합니다.
        audioManager.resetBGM(); // 배경음악을 처음부터 다시 재생하도록 합니다.
    }

    /**
     *  게임 현재 상태를 반환합니다.
     * @return 게임 상태 ENUM 값
     */
    public GameStatus getGameStatus() {
        return this.gameStatus;
    }

    /**
     *  게임 상태를 입력한 ENUM 값으로 변경합니다.
     * @param status 변경할 상태 ENUM 값
     */
    public void setGameStatus(GameStatus status){
        this.gameStatus = status; // 입력받은 상태 ENUM값으로 게임 상태를 변경
        // 게임이 정지했거나 완전히 끝났다면 렌더링 타이머와 샘플링 타이머를 제외한 모든 타이머를 정지하고 BGM 도 정지한다.
        for(Timer timer : this.timers) {
            if(timer.getClass() == RenderingTimer.class || timer.getClass() == SamplingTimer.class) continue;
            if (this.gameStatus == GameStatus.PAUSED || this.gameStatus == GameStatus.ENDED){
                timer.stop();
                audioManager.stopBGM();
            }
            // 게임이 진행중으로 바뀌었다면 모든 타이머를 시작 상태로 바꾸고 BGM 을 재생한다.
            else{
                timer.start();
                audioManager.playBGM();
            }
        }

    }

    /**
     * 현재 점수값을 반환한다.
     * @return 현재 점수 값
     */
    public int getScore() {
        return this.score;
    }

    /**
     * 새로운 사과를 생성하고 화면에 드롭 시킨다.
     * @param value 사과의 값
     * @param size 사과의 사이즈
     * @param x 드롭을 시작할 x 값
     * @param vx 시작 x축 속도
     * @param appleType 사과 타입(일반, 골드, 폭탄)
     */
    public void createApple(int value, double size, int x, double vx, AppleType appleType) {
        apples.add(new Apple(value, size,x,vx, appleType)); // 사과를 생성하고 사과 리스트에 추가합니다.
    }

    /**
     * 플레이 패널 객체를 반환 한다.
     * @return 플레이 패널 객체
     */
    public PlayPanel getPlayPanel() {
        return this.playPanel;
    }

    /**
     * 현재 생성되어 있는 사과 리스트를 가져옵니다.
     * @return 사괴 리스트
     */
    public List<Apple> getApples() {
        return apples;
    }

    /**
     * 사과 파편 리스트를 가져옵니다.
     * @return 사과 파편 리스트
     */
    public List<List<Sparkle>> getSparkles() {
        return sparkles;
    }

    /**
     * 해당 게임의 남은 시간 값을 반환합니다.
     * @return 남은 시간 값
     */
    public int getRemainTime() {
        return remainTime;
    }

    /**
     * 해당 게임의 남은 시간 값을 설정합니다.
     * @param remainTime 변경할 남은 시간 값
     */
    public void setRemainTime(int remainTime) {
        this.remainTime = remainTime;
    }

    /**
     * 초기화 하는데 사용되는 시간 값을 가져옵니다.
     * @return 초기화 시간 값
     */
    public int getInitTime() {
        return initTime;
    }

    /**
     * 게임 매니저에서 저장하여 관리할 플레이 패널을 설정합니다.
     * @param playPanel 플레이 패널 객체
     */
    public void setPlayPanel(PlayPanel playPanel) {
        this.playPanel = playPanel;
    }

    /**
     * 현재 선택된 사과 객체 리스트를 가져옵니다.
     * @return 선택된 사과 리스트
     */
    public List<Apple> getSelectedApple() {
        return selectedApples;
    }

    /**
     * 물리엔진 객체를 가져옵니다.
     * @return 물리 엔진 객체
     */
    public PhysicsEngine getEngine() {
        return engine;
    }

    /**
     * 현재 게임에 대한 점수 값을 설정합니다.
     * @param score 변경할 점수 값
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * 마지막으로 상호작용 한 시간을 기록합니다.
     */
    public void loggingLastInteractedTime() {
        this.lastInteractedTime = remainTime;
    }
    /**
     * 마우스 핸들러 객체를 반환합니다.
     * @return 마우스 핸들러 객체
     */
    public MouseHandler getMouseHandler() {
        return mouseHandler;
    }
    /**
     * 키보드 핸들러 객체를 반환합니다.
     * @return 키보드 핸들러 객체
     */
    public KeyboardHandler getKeyboardHandler() {
        return keyboardHandler;
    }
    /**
     * 오디오 매니저 객체를 반환합니다.
     * @return 오디오 매니저 객체
     */
    public AudioManager getAudioManager() {
        return audioManager;
    }
    /**
     * 마지막 득점 시간을 수정합니다.
     */
    public void updateLastPangTime() {
        this.lastPangTime = this.remainTime;
    }

    /**
     * 콤보 카운트를 업데이트 합니다.
     */
    public void updateCombo() {
        this.combo++; // 콤보 +1
        int x = this.mainFrame.getX() + this.mainFrame.getWidth(); // 창의 우측 하단 끝점 (x좌표)
        int y = this.mainFrame.getY() + this.mainFrame.getHeight(); // 창의 우측 하단 끝점 (y좌표)
        // 콤보를 2를 넘어가기 시작하면 콤보 프레임을 생성하고 리스트에 추가하여 띄운다.
        if(this.combo >= 2) this.comboFrames.add(new ComboFrame(this.combo,x,y));
    }

    /**
     * 사과를 드롭하는 역할을 하는 타이머 클래스
     */
    class AppleDropTimer extends Timer implements ActionListener {
        private int minApples, idleTime; // 사과 갯수 최소 하한선,
        private GameManager gm; // 게임 매니저 객체
        /**
         * @param minApples 사과 갯수 최소 하한선
         * @param interval 타이머 동작 간격
         * @param idleTime 사과 드롭 대기 시간
         * @param gm 게임 매니저 객체
         */
        public AppleDropTimer(int minApples, int interval, int idleTime, GameManager gm) {
            super(interval,null); // 부모 클래스인 Timer 초기화
            this.minApples = minApples; // 최소 사과 갯수 설정
            this.idleTime = idleTime / 1000; // 최소 대기 시간 설정
            this.gm = gm; // 게임 매니저 객체 설정
            addActionListener(this); // 액션 리스너 추가
        }

        /**
         * 사과를 생성하고 드롭합니다.
         * @param gm 게임 매니저 객체
         */
        static void dropApples(GameManager gm) {
            Random rand = new Random(); // 랜덤 객체 생성
            int value = (int) (Math.random() * 4 + 1); // 1~5 값을 가진 사과를 생성
            int size = (int) (Math.random() * 70 + 30); // 30~100 사이즈를 가진 사과를 생성
            int x = (int)(Math.random() * gm.getPlayPanel().getWidth()); // 0 ~ <창 가로 크기> 위치에서 랜덤하게 사과 드롭
            int vx = (int)(Math.random() * 2 - 1); // -1 ~ 1 의 초기 X 축 속도를 설정
            int dice = rand.nextInt(100); // 주사위 굴리기 (랜덤하게 사과 타입을 설정하기 위함)
            AppleType appleType = AppleType.DEFAULT; // 기본적으로 일반 사과로 생성되도록 설정
            if (dice > 80 && dice <= 92) appleType = AppleType.GOLDEN; // 12 % 확률로 황금 사과 생성
            if (dice > 92) appleType = AppleType.BOMB; // 8 % 확률로 폭탄 사과 생성
            gm.createApple(value,size,x,vx, appleType); // 위에서 생성한 값들을 바탕으로 사과 생성
        }

        /**
         * 타이머가 일정간격마다 진행할 작업
         * @param e 이벤트 객체
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameStatus == GameStatus.PAUSED) return; // 게임이 일시정지 중 일때는 사과를 드롭하지 않음
            // 현재 보여지는 사과의 갯수가 최소 사과 갯수 미만으로 떨어지거나 유저가 상호작용하지 않고 일정 시간이 지나면 자동으로 사과가 생성되고 드롭되도록 설정
            if (apples.size() > minApples && lastInteractedTime - remainTime < idleTime) return;
            // 대기 시간이 지난게 아니고 최소 사과 관련 조건으로 사과를 드롭하는 경우 2 ~ 5 사이의 사과를 랜덤으로, 아니라면 1개의 사과를 드롭하도록 설정
            int qty = lastInteractedTime - remainTime < idleTime ? (int)(Math.random() * 3 + 2) : 1;
            for(int i=0; i<qty; i++) dropApples(gm); // 반복문을 이용하여 사과를 드롭
            lastInteractedTime = remainTime; // 상호작용한 시간(타임스탬프)를 업데이트
        }
    }

    /**
     * 일정 간격으로 사과 객체들 전체를 샘플링하여 물리 요소를 적용하는 타이머
     */
    class SamplingTimer extends Timer implements ActionListener {
        private int samplingRate; // 샘플링 레이트

        /**
         * @param samplingRate 샘플링 레이트
         */
        public SamplingTimer(int samplingRate) {
            super(samplingRate, null); // 부모 클래스의 Timer를 샘플링 레이트를 간격으로 초기화
            addActionListener(this); // 액션 리스너 추가
            this.samplingRate = samplingRate; // 샘플링 레이트 변수에 저장
        }
        /**
         * 타이머가 일정간격마다 진행할 작업
         * @param e 이벤트 객체
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if(apples == null || apples.isEmpty()) return; // 사과 리스트가 초기화 되지 않았거나 비어있다면 작업을 진행하지 않음
            // 창에 대한 현재 속도와 이전 좌표를 기록하는 코드 (관성 물리력을 적용하기 위함)
            double currentX = mainFrame.getLocationOnScreen().getX();
            double currentY = mainFrame.getLocationOnScreen().getY();
            double currentVx = mainFrame.getVx();
            double currentVy = mainFrame.getVy();
            mainFrame.setVx(mainFrame.getPrevX() - currentX);
            mainFrame.setVy(mainFrame.getPrevY() - currentY);
            mainFrame.setAx(mainFrame.getPrevVx() - currentVx);
            mainFrame.setAy(mainFrame.getPrevVy() - currentVy);
            mainFrame.setPrevX(currentX);
            mainFrame.setPrevY(currentY);
            mainFrame.setPrevVx(currentVx);
            mainFrame.setPrevVy(currentVy);
            // 각 사과마다 득점하여 파괴되었는지 여부를 확인하고 true라면 파편 객체들을 리스트에 추가하고 사과객체를 제거 한다.
            // 아직 파괴되지 않은 사과라면 물리력을 적용하고 이에 대한 결과 값으로 위치를 업데이트 한다.
            for(Iterator<Apple> iterator = apples.iterator(); iterator.hasNext();) {
                Apple apple = iterator.next();
                if(apple.isUsed()) {
                    sparkles.add(apple.getSparkles());
                    iterator.remove();
                    continue;
                }
                engine.applyPhysics(apple, apples);
                apple.update(this.samplingRate);
            }
            // 파편 객체들의 움직임을 업데이트 하고 일정 시간 이상 재생후 사라지게 하기 위한 반복문
            for (Iterator<List<Sparkle>> iterator = sparkles.iterator(); iterator.hasNext();) { // 각 사과에 대한 파편 객체들 리스트 모음 Iterator 반복
                List<Sparkle> sparkles = iterator.next();
                for(Iterator<Sparkle> iterator1 = sparkles.iterator(); iterator1.hasNext();) { // 사과들의 각 파편 객체들에 대해서 순회
                    Sparkle sparkle = iterator1.next();
                    if(sparkle.getDone()) { // 파편 객체의 재생이 모두 완료 되었다면 객체 삭제
                        iterator1.remove();
                        continue;
                    }
                    sparkle.update(this.samplingRate); // 아직 재생이 완료되지 않았다면 위치 업데이트
                }
                if(sparkles.isEmpty()) { // 파편 리스트가 비었다면 해당 사과에 대한 모든 파편의 재생이 완료된것으로 판단하고 해당 리스트 객체를 지운다.
                    iterator.remove();
                }
            }

        }
    }

    /**
     * 그래픽을 일정 간격마다 지우고 다시 그리는 것을 반복하는 타이머
     */
    class RenderingTimer extends Timer implements ActionListener {
        /**
         * @param frameRate 프레임 레이트
         */
        public RenderingTimer(int frameRate) {
            super(frameRate, null); // 프레임 레이트를 간격으로 타이머 초기화
            addActionListener(this); // 액션 리스너 추가
        }
        /**
         * 타이머가 일정간격마다 진행할 작업
         * @param e 이벤트 객체
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            mainFrame.repaint(); // 메인 프레임과 하위 요소들 다시 그리기 (재 렌더링)
            hudFrame.repaint(); // HUD 프레임과 하위 요소들 다시 그리기 (재 렌더링)
        }
    }

    /**
     * 게임의 남은 시간 타이머, 게임의 상태 등의 흐름을 제어하는 타이머
     */
    class GameTimer extends Timer implements ActionListener {
        /**
         * @param interval 트리거 간격
         */
        public GameTimer(int interval) {
            super(interval, null); // 입력된 트리거 간격을 기반으로 타이머 초기화
            addActionListener(this); // 액션 리스너 추가
        }
        /**
         * 타이머가 일정간격마다 진행할 작업
         * @param e 이벤트 객체
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            int time = getRemainTime(); // 현재 게임의 남은 시간을 가져오기
            if (lastPangTime - time>5) { // 마지막 득점 시간으로 부터 5초가 지났다면 콤보 체인을 끊는다. (콤보 초기화)
                for(ComboFrame frame : comboFrames) frame.setVisible(false); // 콤보 프레임을 모두 닫는다.
                comboFrames.clear(); // 콤보 프레임 객체를 리스트에서 모두 삭제
                combo = 0; // 콤보 값을 0 으로 초기화
            }
            if(time <= 0 && gameStatus == GameStatus.PLAYING){ // 게임 진행중에 제한시간이 모두 다 지나 0이 되었다면
                gameStatus = GameStatus.ENDED; // 게임 상태를 ENDED로 설정
                for(ComboFrame frame : comboFrames) frame.setVisible(false); // 모든 콤보 프레임들을 닫기
                hudFrame.setVisible(false); // HUD 프레임 숨기기
                comboFrames.clear(); // 콤보 프레임 객체들을 리스트에서 모두 삭제
                this.stop(); // 이 타이머도 정지하기
                audioManager.stopBGM(); // BGM도 멈추기
                return;
            }
            // 위 조건에 해당사항이 없다면 현재 남은시간을 일정 간격마다(1초마다) 1씩 내리기
            setRemainTime(time-1);
        }
    }
}
