package view;

import controller.ButtonManager;
import controller.GameManager;
import controller.MouseHandler;
import enums.AppleType;
import enums.GameStatus;
import model.*;
import model.Button;
import model.Point;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
/**
 *  <b>[🔍view] - PlayPanel.java</b><br>
 *  플레이 패널 객체를 정의하는 View 클래스<br>
 */
public class PlayPanel extends JPanel {
    private GameManager gameManager; // 게임 매니저 객체
    private MouseHandler mouseHandler; // 마우스 핸들러 객체
    private BufferedImage appleImg, goldenImg, bombImg; // 사과 이미지, 황금 사과 이미지, 폭탄 사과 이미지
    private BufferedImage backgroundImg; // 게임 배경 이미지
    private FontMetrics fm = null; // 폰트 매트릭스 변수
    private Button playButton, restartButton,resumeButton; // 시작, 다시시작, 재개 버튼
    private ButtonManager buttonManager; // 버튼 매니저 객체

    /**
     * @param gameManager 게임 매니저 객체
     * @param mouseHandler 마우스 핸들러 객체
     * @param buttonManager 버튼 매니저 객체
     */
    public PlayPanel(GameManager gameManager, MouseHandler mouseHandler, ButtonManager buttonManager) {
        this.gameManager = gameManager; // 게임 매니저 객체 설정
        this.mouseHandler = mouseHandler; // 마우스 핸들러 객체 설정
        this.buttonManager = buttonManager; // 버튼 매니저 객체 설정
        // 버튼 관련 변수들은 모두 null값으로 일단 초기화
        this.playButton = null;
        this.restartButton = null;
        this.resumeButton = null;
        //각 사과 타입에 맞는 이미지 가져오기
        try {
            this.appleImg = ImageIO.read(new File("assets/apple.png"));
            this.goldenImg = ImageIO.read(new File("assets/goldenApple.png"));
            this.bombImg = ImageIO.read(new File("assets/bombApple.png"));
            this.backgroundImg = ImageIO.read(new File("assets/back.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLayout(null); // 레이아웃은 설정 하지 않기
        setVisible(true); // 패널 띄우기
    }

    /**
     * 사과들을 그립니다.
     * @param apples 사과 리스트들
     * @param g2d G2D 객체
     */
    public void drawApples(List<Apple> apples, Graphics2D g2d) {
        if (apples == null || apples.isEmpty()) return; // 사과 리스트가 존재 하지 않거나 비어있다면 그리지 않음
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // 불투명도를 1.0으로 설정
        // 임시 변수 생성
        BufferedImage img;
        Graphics2D g2;
        for (Apple apple : apples) { // 모든 사과를 순회
            if(apple.isUsed())continue; // 이미 파괴 / 사용된 사과라면 건너뛰기
            Point point = apple.getPosition(); // 사과의 위치 객체를 가져오기
            double size = apple.getSize(); // 사과의 사이즈 값을 가져오기
            if (apple.getIcon() == null || apple.getSelectedIcon() == null) { // 사과의 기본 아이콘 또는 선택 아이콘이 로드되지 않았을경우 로드 작업 진행 (캐싱)
                // 사과 의 타입에 맞추어서 원본 사과 이미지 불러오기
                BufferedImage srcImg = appleImg;
                if (apple.getAppleType() == AppleType.GOLDEN) srcImg = goldenImg;
                else if (apple.getAppleType() == AppleType.BOMB) srcImg = bombImg;
                // 임시 변수에 빈 팔렛트 만들기
                img = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
                g2 = img.createGraphics();
                // 먼저 원본 사과 이미지 그리기
                g2.drawImage(srcImg, 0, 0, srcImg.getWidth(), srcImg.getHeight(), null);
                // 가독성을 위해서 폭탄 사과이면 글씨 색을 흰색으로, 아니면 검은색으로 설정
                if (apple.getAppleType() != AppleType.BOMB) g2.setColor(Color.BLACK);
                else g2.setColor(Color.WHITE);
                // 폰트는 Arial, Bold, 500으로 설정
                g2.setFont(new Font("Arial", Font.BOLD, 500));
                // 폰트 매트릴스를 가져오고 이를 기반으로 텍스트를 중앙정렬하여 값 텍스트를 사과이미지 위에 그리기
                fm = g2.getFontMetrics();
                int ascent = fm.getAscent();
                String text = Integer.toString(apple.getValue());
                g2.drawString(text, (img.getWidth()  - fm.stringWidth(text)) / 2 ,(img.getHeight() + ascent) / 2);
                g2.dispose(); // 작업 마무리
                apple.setIcon(img); // 완성된 이미지를 사과 기본 아이콘으로 설정
                // 이번에는 선택되었을때 사과 이미지 만들기
                // 먼저 동일하게 빈 팔렛트 만들기
                img = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
                g2 = img.createGraphics();
                // 불투명도를 0.3 으로 설정
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                // 이미 그렸던 아이콘을 다시 가져와서 붙여넣기(불투명도만 달리하여 저장)
                g2.drawImage(apple.getIcon(), 0, 0, apple.getIcon().getWidth(), apple.getIcon().getHeight(), null);
                g2.dispose(); // 작업 마무리
                apple.setSelectedIcon(img); // 선택했을때 아이콘을 지정
            }
            else { // 이미 아이콘을 생성해 저장 해두었다면
                img = apple.isSelected() ? apple.getSelectedIcon() : apple.getIcon(); // 사과의 선택 여부에 따라서 그리는 아이콘의 종류를 다르게 가져가도록 함
            }
            // 이미지의 회적을 위해서 AffineTransform을 활용
            // 사과로부터 회전 각도와 사이즈 값을 가져와서 AffineTransform을 이용하여 적용후 화면에 그린다.
            AffineTransform at = new AffineTransform();
            at.translate(point.getX(), point.getY());
            at.rotate(apple.getRotation(), size/2.0, size/2.0);
            at.scale(size/img.getWidth(), size/img.getHeight());
            g2d.drawImage(img, at, null);
        }
    }

    /**
     * 사과 파괴 / 폭발시 발생하는 파편들을 그립니다.
     * @param sparkles 파편들 리스트
     * @param g2d G2D 객체
     */
    public void drawSparkles(List<List<Sparkle>> sparkles, Graphics2D g2d) {
        Random rand = new Random(); // 랜덤 객체 생성
        for(List<Sparkle> sparkle : sparkles) { // 파편 객체들이 담겨있는 이중 리스트를 순회
            for(Sparkle sparkle1 : sparkle) {
                if(sparkle1.getDone()) continue; // 해당 파편의 재생이 모두 완료되었다면 건너뛰기
                Point p = sparkle1.getPosition().getPoint(); // 파편의 현재 위치 가져오기
                g2d.setColor(sparkle1.getColor()); // 파편의 색을 가져온다.
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sparkle1.getOpacity())); // 파편의 불투명도 값을 가져와 적용한다.
                int randFrom = 1, randTo = 10; // 파편의 크기를 1~10으로 설정한다.
                if (sparkle1.getApple().getAppleType() == AppleType.BOMB) { // 만약 폭탄 사과라면 20~40으로 설정
                    randFrom = 20;
                    randTo = 40;
                }
                // 해당 위치에 불규칙한 타원의 형태로 파편을 그린다.
                g2d.fill(new Ellipse2D.Double(p.getX(),p.getY(), rand.nextInt(randFrom,randTo),rand.nextInt(randFrom,randTo)));
            }
        }
    }

    /**
     * 게임 배경을 그립니다.
     * @param g2d G2D 객체
     */
    public void drawBackground(Graphics2D g2d) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 불투명도 설정
        g2d.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null); // 배경 그리기
    }

    /**
     * 블러 오버레이를 화면에 그립니다.
     * @param g2d G2D 객체
     */
    public void drawBlurOverlay(Graphics2D g2d) {
        g2d.setColor(Color.WHITE); // 색을 흰색으로 설정
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // 불투명도 설정
        g2d.fillRect(0, 0, getWidth(), getHeight()); // 전체 화면으로 사각형 그리기
    }

    /**
     * 인트로 화면을 그립니다.
     * @param g2d G2D 객체
     */
    public void drawIntroOverlay(Graphics2D g2d) {
        buttonManager.disableAllButtons(); // 버튼 매니저를 통하여 모든 버튼들을 비활성화 시킨다.
        String title = "Fruits Box!"; // 타이틀 텍스트 변수
        g2d.setColor(new Color(35,35,35,255)); // 제목 텍스트 색상 설정
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // 불투명도를 1.0으로 설정
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 50)); // 폰트를 Comic Sans, Bold, 50으로 설정
        fm = g2d.getFontMetrics(); // 폰트 매트릭스 가져오기
        // 가져온 폰트 매트릭스를 기반으로 제목 텍스트 위치 잡고 그리기
        int titleX = getWidth()/2 - fm.stringWidth(title)/2;
        int titleY = getHeight()/4;
        g2d.drawString(title,titleX,titleY);
        // 버튼의 크기와 위치 지정
        int playButtonWidth = 225;
        int playButtonHeight = 75;
        int playButtonX = getWidth()/2 -  playButtonWidth/2;
        int playButtonY = getHeight()/4*3 - playButtonHeight;
        // 버튼 객체가 한번도 생성되지 않았다면 생성하기
        if(playButton == null) {
            // 새로운 버튼 객체 만들기
            playButton = new Button(playButtonX,playButtonY,playButtonWidth,playButtonHeight,new Color(192,99,72,255),"Play",30);
            // 새로운 버튼 작업 도 생성후 버튼에 연결
            playButton.setOnClick(() -> {
                // 버튼을 눌렀을때 게임을 진행중 상태로 만들고 시작 함수를 실행 시킨다.
                gameManager.setGameStatus(GameStatus.PLAYING);
                gameManager.startGame();
            });
            // 해당 버튼을 클릭 샘플링 대상 버튼으로 추가하여 클릭이 가능하도록 한다.
            mouseHandler.addSamplingButton(playButton);
        }
        // 해당 버튼을 활성화 시킨다.
        buttonManager.enableButton(playButton);
        // 버튼의 위치를 계산한 위치로 설정한다.
        playButton.setLocation(playButtonX,playButtonY);
        // 버튼을 표시한다.
        playButton.show(g2d);
    }
    /**
     * 일시 정지 화면을 그립니다.
     * @param g2d G2D 객체
     */
    public void drawPauseOverlay(Graphics2D g2d) {
        buttonManager.disableAllButtons(); // 버튼 매니저를 통하여 모든 버튼들을 비활성화 시킨다.
        String title = "Paused"; // 정지 화면 타이틀 텍스트
        g2d.setColor(new Color(35,35,35,255)); // 글씨 색상 설정
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // 불투명도 설정
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 50)); // 폰트 설정
        fm = g2d.getFontMetrics(); // 폰트 매트릭스 가져오기
        // 가져온 폰트 매트릭스를 기반으로 텍스트 배치 위치 계산 및 텍스트 그리기
        int titleX = getWidth()/2 - fm.stringWidth(title)/2;
        int titleY = getHeight()/4;
        g2d.drawString(title,titleX,titleY);
        // 버튼 사이즈와 위치 지정 / 계산
        int buttonWidth = 225;
        int buttonHeight = 50;
        int resumeButtonX = getWidth()/2 -  buttonWidth/2;
        int resumeButtonY = getHeight()/4*3 - buttonHeight;
        // 만약 재개 버튼이 생성된적이 없다면 생성하기
        if (resumeButton == null) {
            // 새로운 버튼 객체 생성
            resumeButton = new Button(resumeButtonX,resumeButtonY,buttonWidth,buttonHeight,new Color(192,99,72,255),"Resume",30);
            // 새로운 버튼 작업 도 생성후 버튼에 연결
            resumeButton.setOnClick(() -> {
                // 버튼을 클릭할 시 게임 상태를 진행중으로 변경하도록 하기
                gameManager.setGameStatus(GameStatus.PLAYING);
            });
            // 해당 버튼을 클릭 샘플링 대상 버튼으로 추가하여 클릭이 가능하도록 한다.
            mouseHandler.addSamplingButton(resumeButton);
        }
        // 재시작 버튼 또한 재개 버튼 바로 밑에 배치되도록 위치 계산
        int restartButtonX = resumeButtonX;
        int restartButtonY = resumeButtonY + buttonHeight;
        // 만약 재시작 버튼이 생성된적이 없다면 생성하기
        if(restartButton == null) {
            // 재시작 버튼 생성
            restartButton = new Button(restartButtonX,restartButtonY,buttonWidth,buttonHeight,new Color(108, 192,72,255),"Restart",30);
            // 새로운 버튼 작업 도 생성후 버튼에 연결
            restartButton.setOnClick(()-> {
                // 버튼을 클릭할 시 게임 상태를 진행중으로 변경하도록 하기
                gameManager.setGameStatus(GameStatus.PLAYING);
                // 시작함수를 실행하여 게임이 재시작 되도록 하기
                gameManager.startGame();
            });
            // 해당 버튼을 클릭 샘플링 대상 버튼으로 추가하여 클릭이 가능하도록 한다.
            mouseHandler.addSamplingButton(restartButton);
        }
        // 두 버튼 모두 활성화 한다.
        buttonManager.enableButton(resumeButton);
        buttonManager.enableButton(restartButton);
        // 두 버튼 모두 계산한 위치에 맞추어 위치를 설정한다.
        resumeButton.setLocation(resumeButtonX,resumeButtonY);
        restartButton.setLocation(restartButtonX,restartButtonY);
        // 두 버튼 모두 화면에 표시한다.
        resumeButton.show(g2d);
        restartButton.show(g2d);
        // 그리기 작업 마무리
        g2d.dispose();
    }
    /**
     * 게임 결과 화면을 그립니다.
     * @param g2d G2D 객체
     */
    public void drawResultOverlay(Graphics2D g2d) {
        buttonManager.disableAllButtons(); // 버튼 매니저를 통하여 모든 버튼들을 비활성화 시킨다.
        String title = "Game Over!"; // 게임 오버 텍스트 변수
        g2d.setColor(new Color(35,35,35,255)); // 텍스트 색상 설정
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // 불 투명도 설정
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 50)); // 폰트 설정
        fm = g2d.getFontMetrics(); // 폰트 매트릭스 가져오기
        // 가져온 폰트 매트릭스를 이용하여 제목 배치 위치 계산 및 그리기
        int titleX = getWidth()/2 - fm.stringWidth(title)/2;
        int titleY = getHeight()/4;
        g2d.drawString(title,titleX,titleY);
        g2d.setFont(new Font("Comic Sans MS", Font.BOLD, 30)); // 폰트 설정
        // 가져온 폰트 매트릭스를 이용하여 제목 배치 위치 계산 및 그리기
        int scoreX = titleX + fm.stringWidth(title)/4;
        int scoreY = titleY + fm.getAscent();
        String scoreText = "Score : "+ gameManager.getScore(); // 점수 결과 텍스트 변수
        g2d.drawString(scoreText,scoreX,scoreY); // 점수 텍스트 그리기
        // 버튼 크기, 위치 계산 / 지정
        int buttonWidth = 225;
        int buttonHeight = 50;
        int restartButtonX = getWidth()/2 -  buttonWidth/2;
        int restartButtonY = getHeight()/4*3 - buttonHeight;
        // 재시작 버튼이 한번도 생성되지 않았다면 생성하기
        if(restartButton == null) {
            // 버튼 객체 생성
            restartButton = new Button(restartButtonX,restartButtonY,buttonWidth,buttonHeight,new Color(108, 192,72,255),"Restart",30);
            // 새로운 버튼 작업 도 생성후 버튼에 연결
            restartButton.setOnClick(()-> {
                // 버튼을 클릭할 시 게임 상태를 진행중으로 변경하도록 하기
                gameManager.setGameStatus(GameStatus.PLAYING);
                // 시작함수를 실행하여 게임이 재시작 되도록 하기
                gameManager.startGame();
            });
            // 해당 버튼을 클릭 샘플링 대상 버튼으로 추가하여 클릭이 가능하도록 한다.
            mouseHandler.addSamplingButton(restartButton);
        }
        // 해당 버튼 활성화
        buttonManager.enableButton(restartButton);
        // 버튼 위치를 계산했던 값으로 설정
        restartButton.setLocation(restartButtonX,restartButtonY);
        // 버튼 보여주기
        restartButton.show(g2d);
    }

    /**
     * paint 작업이 일어날때 발생하는 작업<br>
     * ⚠️ 위에 있는 함수 일수록 먼저 그려지므로 배경같은 함수를 먼저 작성해야 함
     * @param g 그래픽 객체
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g; // 그래픽 객체를 G2D 객체로 타입 캐스팅(변환)
        drawBackground(g2d); // 배경 그리기
        if (gameManager.getGameStatus() != GameStatus.PLAYING) { // 게임 플레이 중이 아닐때
            drawBlurOverlay(g2d); // 화면 블러 오버레이 그리기
            if(gameManager.getGameStatus() == GameStatus.STANDBY) { // 타이틀 화면이 라면 (대기 상태)
                drawIntroOverlay(g2d); // 타이틀 화면 그리기
            }
            else if(gameManager.getGameStatus() == GameStatus.PAUSED) { // 일시 정지 상태라면
                drawPauseOverlay(g2d); // 일시 정지 상태 그리기
            }
            else if(gameManager.getGameStatus() == GameStatus.ENDED) { // 게임 이 끝난 상태라면
                drawResultOverlay(g2d); // 게임 결과(게임 오버) 화면 그리기
            }
        }else { // 게임이 진행 중인 상태라면
            drawApples(gameManager.getApples(), g2d); // 사과들 그리기
            drawSparkles(gameManager.getSparkles(), g2d); // 사과 파편들 그리기
        }
    }
}
