package model;

import enums.AppleType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
/**
 *  <b>[🗿model] - Apple.java</b><br>
 *  사과 객체를 정의하는 model 클래스<br>
 */
public class Apple {
    private Point position; // 사과 현재 위치
    private double size; // 사과 사이즈
    private double vx,vy,ax,ay; // 사과 X축 속도, Y축 속도, X축 가속도, Y축 가속도
    private double prevVy, prevVx; // 사과 직전 Y축 속도, X축 속도
    private Point prevPosition; // 사과의 직전(이전) 위치
    private int value; // 사과의 값
    private boolean used = false; // 사과의 사용 여부 (파괴 여부)
    private double rotation=0.0; // 사과의 회전 각도
    private boolean selected = false; // 사과 선택 여부
    private List<Sparkle> sparkles; // 사과 파괴시 발생하는 파편들 리스트
    private BufferedImage icon, selectedIcon; // 사과 이미지, 선택되었을때 이미지
    private AppleType appleType; // 사과 타입
    private HashMap<AppleType, Color[]> sparkleColors; // 사과 파괴시 파편 컬러 후보들

    /**
     * @param value 사과 값
     * @param size 사과 사이즈
     * @param x 사과 시작 X 위치
     * @param vx 사과 시작 X 속도
     * @param appleType 사과 타입
     */
    public Apple(int value, double size, int x,double vx, AppleType appleType) {
        this.position = new Point(x,-size); // 초기 위치 설정
        this.prevPosition = new Point(x,-size); // 이전 위치도 초기 위치와 동일하게 설정
        this.vx = vx; // 초기 X축 속도 설정
        this.vy = 0.0; // 초기 Y축 속도는 0으로 설정
        this.prevVx = this.vx; // 초기 X축 속도는 초기 속도와 동일하게 설정
        this.prevVy = this.vy; // 초기 Y축 속도는 초기 속도와 동일하게 설정
        // 초기 가속도는 모두 0으로 설정
        this.ax = 0.0;
        this.ay = 0.0;
        this.size = size; // 사이즈 설정
        this.value = value; // 값 설정
        this.sparkles = new ArrayList<>(); // 파편 객체들을 담을 ArrayList 생성
        this.appleType = appleType; // 사과 타입 설정
        sparkleColors = new HashMap<>(); // 파편 색 후보를 저장할 HashMap 생성
        sparkleColors.put(AppleType.DEFAULT, new Color[] { // 기본 사과 파편 색 후보 추가
                new Color(192,99,72,255),
                new Color(192, 140,72,255),
                new Color(192, 182,72,255)
        });
        sparkleColors.put(AppleType.GOLDEN, new Color[] { // 황금 사과 파편 색 후보 추가
                new Color(220,185,21,255),
                new Color(192, 140,72,255),
                new Color(211, 206, 158,255)
        });
        sparkleColors.put(AppleType.BOMB, new Color[] { // 폭탄 사과 파편 색 후보 추가
                new Color(218, 13, 13,255),
                new Color(248, 174, 66,255),
                new Color(50, 50, 50,255),
        });
        generateSparkles(); // 파편 객체 임의로 생성
    }

    /**
     * 파편 객체들을 생성하여 리스트에 저장합니다.
     */
    private void generateSparkles() {
        Random rand = new Random(); // 랜덤 객체 생성
        for(int i=0; i<100; i++) { // 100개의 파편 생성
            // 랜덤한 각도로 파편을 뿌리기 위해 cos,sin 삼각함수를 이용해서 랜덤한 방향으로 단위 벡터를 만들고 이 벡터를 바탕으로 파편 객체를 생성하여 리스트에 저장한다.
            // 또한 파편의 색도 Map에 있는 색 내에서 랜덤으로 선택한다.
            double dx = Math.cos(Math.toRadians(rand.nextInt(360)));
            double dy = Math.sin(Math.toRadians(rand.nextInt(360)));
            sparkles.add(new Sparkle(this,sparkleColors.get(appleType)[rand.nextInt(sparkleColors.size()-1)],new Vector(dx,dy)));
        }
    }

    /**
     * 파괴 파편 리스트를 반환합니다.
     * @return 파편 객체들 리스트
     */
    public List<Sparkle> getSparkles() {
        return sparkles;
    }

    /**
     * 사과 타입 정보를 가져옵니다.
     * @return 사과 타입 정보
     */
    public AppleType getAppleType() {
        return appleType;
    }

    /**
     * 사과 위치 객체를 가져옵니다.
     * @return 사과 위치 객체
     */
    public Point getPosition() {
        return position;
    }

    /**
     * 사이즈 값을 가져옵니다.
     * @return 사이즈 값
     */
    public double getSize() {
        return size;
    }

    /**
     * X 축방향 속도를 가져옵니다.
     * @return X 축 속도 값
     */
    public double getVx() {
        return vx;
    }

    /**
     * Y 축 방향 속도를 가져옵니다.
     * @return Y 축 속도 값
     */
    public double getVy() { return vy; }

    /**
     * X 축 방향 속도를 설정 합니다.
     * @param vx X축 속도 값
     */
    public void setVx(double vx) { this.vx = vx; }

    /**
     * Y 축 방향 속도를 설정 합니다.
     * @param vy Y축 속도 값
     */
    public void setVy(double vy) { this.vy = vy; }
    /**
     * Y 축 방향 가속도를 설정 합니다.
     * @param ay Y축 가속도 값
     */
    public void setAy(double ay) { this.ay = ay; }

    /**
     * Y 축 방향 가속도를 가져옵니다.
     * @return Y축 가속도 값
     */
    public double getAy() { return ay; }

    /**
     * 사과의 현재 위치를 변경 / 설정 합니다.
     * @param position 변경할 위치 객체
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * 사과 값을 가져옵니다.
     * @return 사과 값
     */
    public int getValue() {
        return value;
    }

    /**
     * 사과의 회전 각도를 가져옵니다.
     * @return 사과의 회전 각도를 가져옵니다.
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * 사과의 선택 여부를 가져옵니다.
     * @return 선택 여부
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 사과의 선택 여부를 변경합니다.
     * @param selected 변경할 사과 선택 여부
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * 사과가 사용 / 파괴 된것으로 설정합니다.
     */
    public void setUsed() {
        this.used = true;
    }

    /**
     * 사과가 사용 / 파괴되었는지 확인합니다.
     * @return 사과 사용 / 파괴 여부
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * 사과에 대한 이미지(아이콘)을 설정합니다.
     * @param icon 사과 이미지(아이콘)
     */
    public void setIcon(BufferedImage icon) {
        this.icon = icon;
    }

    /**
     * 설정한 사과 이미지(아이콘)을 가져옵니다.
     * @return 사과 이미지(아이콘)
     */
    public BufferedImage getIcon() {
        return icon;
    }

    /**
     * 사과를 선택했을때 변경되는 이미지를 설정합니다.
     * @param selectedIcon 사과를 선택했을때 변경되는 이미지(아이콘)
     */
    public void setSelectedIcon(BufferedImage selectedIcon) {
        this.selectedIcon = selectedIcon;
    }

    /**
     * 사과를 선택했을때 변경되는 이미지를 가져옵니다.
     * @return 사과를 선택했을때 변경되는 이미지(아이콘)
     */
    public BufferedImage getSelectedIcon() {
        return selectedIcon;
    }

    /**
     * 사과의 위치, 속도 등을 업데이트 합니다.
     * @param samplingRate 샘플링 레이트
     */
    public void update(int samplingRate) {
        double div = 1.0 / samplingRate; // 속도 / 가속도가 1초 기준이므로 샘플링 레이트로 나누어서 샘플링 레이트만큼의 초정도의 업데이트가 발생하도록 하기
        rotation += (position.getX() - prevPosition.getX())*0.02; // X축으로 움직인 거리만큼 사과가 회전하도록 하기
        // 이전 좌표를 기록하기
        this.prevPosition.setX(this.position.getX());
        this.prevPosition.setY(this.position.getY());
        // 속도에 가속도 값 더하기
        vx += ax / div;
        vy += ay / div;
        // 속도에 따라 사과 위치 움직이기
        position.move(vx/div, vy/div);
    }
}
