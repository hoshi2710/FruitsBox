package model;
/**
 *  <b>[🗿model] - Point.java</b><br>
 *  포인트 객체를 정의하는 model 클래스<br>
 */
public class Point {
    private double x; // x 좌표
    private double y; // y 좌표

    /**
     * @param x x 좌표
     * @param y y 좌표
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * x 좌표 반환
     * @return x 좌표
     */
    public double getX() {
        return x;
    }

    /**
     * y 좌표 반환
     * @return y 좌표
     */
    public double getY() {
        return y;
    }

    /**
     * x 좌표 변경
     * @param x 변경할 x 좌표
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * y 좌표 변경
     * @param y 변경할 y 좌표
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * 지정한 값만큼 좌표를 움직입니다.
     * @param x 움직일 x 값
     * @param y 움직일 y 값
     */
    public void move(double x, double y) {
        this.x += x;
        this.y += y;
    }
}
