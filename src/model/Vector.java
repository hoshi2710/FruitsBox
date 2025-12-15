package model;
/**
 *  <b>[🗿model] - Vector.java</b><br>
 *  수학 벡터 객체를 정의하는 model 클래스<br>
 */
public class Vector {
    private Point point; // 포인트 객체(좌표)
    private double length; // 길이 값

    /**
     * @param x x 좌표
     * @param y y 좌표
     */
    public Vector(double x, double y) {
        this.point = new Point(x, y); // 포인트 객체 초기화
        this.length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)); // 길이 값 연산 후 변수에 저장
    }

    /**
     * 벡터의 크기(길이)를 가져옵니다.
     * @return 벡터의 크기(길이)
     */
    public double getLength() {
        return length;
    }

    /**
     * 포인트 객체를 가져옵니다.(위치)
     * @return 포인트 객체(위치)
     */
    public Point getPoint() {
        return point;
    }

    /**
     * 두 벡터를 더합니다.
     * @param vector 더할 벡터 객체
     * @return 결과 벡터
     */
    public Vector add(Vector vector) {
        return new Vector(point.getX() + vector.getPoint().getX(), point.getY() + vector.getPoint().getY());
    }

    /**
     * 두 벡터를 뺍니다.
     * @param vector 뺄 벡터 객체
     * @return 결과 벡터
     */
    public Vector subtract(Vector vector) {
        return new Vector(point.getX() - vector.getPoint().getX(), point.getY() - vector.getPoint().getY());
    }

    /**
     * 두 벡터를 곱합니다. (내적)
     * @param vector 곱할 벡터 (내적할 벡터)
     * @return 곱한 결과(내적) (스칼라 값)
     */
    public double multiply(Vector vector) {
        return point.getX() * vector.getPoint().getX()+point.getY() * vector.getPoint().getY();
    }

    /**
     * 벡터에 스칼라 값을 곱합니다.
     * @param value 곱할 스칼라 값
     * @return 곱한 결과 벡터
     */
    public Vector multiply(double value) {
        return new Vector(point.getX() * value, point.getY() * value);
    }
}
