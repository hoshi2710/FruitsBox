package model;

import java.awt.*;

/**
 *  <b>[🗿Model] - Sparkle.java</b><br>
 *  사과 객체가 파괴될때 발생하는 각각의 입자에 대한 객체의 클래스 입니다.<br>
 */
public class Sparkle {
    Color color; // 입자 색상 변수
    Vector speed; // 입자 속도 (속력 + 방향) 변수
    Vector position; // 입자의 현재 위치 변수
    Apple apple; // 입자 대상 사과 객체 변수
    boolean activated = false; // 입자 활성화 여부 변수
    boolean done = false; // 입자 애니메이션 재생 완료 여부 변수
    float opacity = 1.0f; // 입자의 불투명도 값 변수
    /**
     * @param apple 대상 사과
     * @param color 입자 색상
     * @param speed 입자 속도 (속력 + 방향)
     */
    public Sparkle(Apple apple, Color color, Vector speed) {
        this.apple = apple;
        this.color = color;
        this.speed = speed;
        Point p = apple.getPosition();
        double r = apple.getSize() / 2.0;
        this.position = new Vector(p.getX() + r, p.getY() + r);
    }

    /**
     * 입자의 현재 위치를 가져옵니다.
     * @return 위치 벡터 객체 (Vector)
     */
    public Vector getPosition() {
        return position;
    }
    /**
     * 입자의 현재 색상을 가져옵니다.
     * @return 색상 객체 (Color)
     */
    public Color getColor() {
        return color;
    }
    /**
     * 입자의 현재 불투명도값을 가져옵니다.
     * @return 불투명도 값 (float)
     */
    public float getOpacity() {
        return opacity;
    }
    /**
     * 입자 애니메이션 재생 완료 여부를 가져옵니다.
     * @return 애니메이션 재생 완료 여부 (boolean)
     */
    public boolean getDone() {
        return done;
    }
    /**
     * 입자가 발생하는 대상 사과 객체를 가져옵니다.
     * @return 대상 사과 객체 (Apple)
     */
    public Apple getApple() {
        return apple;
    }

    /**
     * 입자들의 애니메이션(위치, 불투명도) 정보를 업데이트 합니다.
     * @param samplingRate 게임 매니저에 저장된 프레임 레이트
     */
    public void update(int samplingRate) {
        if (!activated) {
            Point p = apple.getPosition();
            double r = apple.getSize() / 2.0;
            this.position = new Vector(p.getX() + r, p.getY() + r);
            activated = true;
        }
        this.speed = this.speed.multiply(1/1.05);
        if (this.opacity > 0f) this.opacity -= Math.min(this.opacity, 0.02f);
        else done = true;
        double div = 1.0 / samplingRate;
        Vector s = this.speed.multiply(1/div);
        this.position = this.position.add(s);
    }

}
