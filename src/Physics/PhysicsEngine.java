package Physics;

import enums.Direction;
import model.Apple;
import model.Point;
import model.Vector;
import view.MainFrame;
import view.PlayPanel;

import java.util.List;

/**
 *  <b>[🏓Physics] - PhysicsEngine.java</b><br>
 *  객체들의 물리 연산을 담당하는 엔진 객체 클래스 입니다.<br>
 */
public class PhysicsEngine {
    // 중력 가속도 값, 탄성계수, 마찰력, 관성력 값 변수
    final private double gravity,bounceFactor,frictionFactor,inertiaFactor;
    private PlayPanel panel; // 플레이 영역 패널 객체 변수
    private MainFrame frame; // 메인 창(프레임) 객체 변수
    /**
     * @param panel 플레이 패널 객체
     * @param frame 메인 창(프레임) 객체
     */
    public PhysicsEngine(PlayPanel panel, MainFrame frame) {
        gravity = 0.001; // 중력 가속도 값
        bounceFactor = 0.3; // 탄성 계수 값
        frictionFactor = 0.0005; // 마찰력 값
        inertiaFactor = 0.001; // 관성력 값
        this.panel = panel;
        this.frame = frame;
    }

    /**
     * 두 사과의 중심점 사이의 거리를 계산하여 반환합니다.
     * @param a1 1번 사과 객체
     * @param a2 2번 사과 객체
     * @return 두 사과 중신점 사이의 거리 값 (double)
     */
    public double getDistanceBetweenCenter(Apple a1, Apple a2) {
        // 1번 사과에 대한 중앙점 객체
        Point p1 = new Point(a1.getPosition().getX() + a1.getSize()/2, a1.getPosition().getY() + a1.getSize()/2);
        // 2번 사과에 대한 중앙점 객체
        Point p2 = new Point(a2.getPosition().getX() + a2.getSize()/2, a2.getPosition().getY() + a2.getSize()/2);
        // 두 중앙점의 x좌표 차이
        double xDiff = Math.abs(p2.getX() - p1.getX());
        // 두 중앙점의 y좌표 차이
        double yDiff = Math.abs(p2.getY() - p1.getY());
        // 피타고라스 정리를 활용하여 두 점의 거리를 구하기
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    /**
     * 두 사과가 충돌/접한 상태인지 확인후 결과를 반환합니다.
     * @param a1 1번 사과 객체
     * @param a2 2번 사과 객체
     * @return 충돌 여부 (boolean)
     */
    public boolean isCollided(Apple a1, Apple a2) {
        // 1번 사과의 반지름
        double a1Radius = a1.getSize() / 2;
        // 2번 사과의 반지르
        double a2Radius = a2.getSize() / 2;
        // 중심점사이의 거리가 두 반지름의 합보다 같거나 작으면 부딪히거나 접한걸로 판단하고 true 반환
        return getDistanceBetweenCenter(a1, a2) <= a1Radius + a2Radius;
    }

    /**
     * 입력한 사과가 창의 경계(벽)에 충돌했는지 여부를 반환합니다.
     * @param a 대상 사과 객체
     * @param direction 검사할 충돌 방향
     * @return 충돌 여부 (boolean)
     */
    public boolean isCollidedToBoundary(Apple a, Direction direction) {
        // 각 방향 별로 충돌 여부를 검사하고 여부를 불리언 타입으로 반환
        return switch (direction) {
            case NORTH -> a.getPosition().getY() <= 0; // 상단 충돌
            case SOUTH -> a.getPosition().getY() >= panel.getHeight() - a.getSize(); // 하단 충돌
            case EAST -> a.getPosition().getX() >= panel.getWidth() - a.getSize(); // 우측 충돌
            case WEST -> a.getPosition().getX() <= 0; // 좌츨 충돌
        };
    }

    /**
     * 사과 객체에 대해 모든 물리력 요소(중력, 충돌, 마찰력, 관성력 등)을 계산하고 적용합니다.
     * @param a 적용할 사과 객체
     * @param apples 비교할 사과 객체들
     */
    public void applyPhysics(Apple a, List<Apple> apples) {
        applyGravity(a); // 중력 가속도가 적용되지 않은 사과 객체들 찾고 적용한다.
        applyBounce(a, apples); // 모든 사과를 순회하며 사과 a가 충돌한 사과가 있는지 찾고 물리력을 계산 및 반영한다.
        applyInertia(apples); // 사과 a에 대해서 창의 움직임에 따른 관성력을 적용한다.
        for(Direction d : Direction.values()) { // 상하좌우 네방면을 모두 검사하여 충돌과 마찰력을 적용한다.
            if(!isCollidedToBoundary(a, d)) continue; // 해당 방향 벽면에 사과가 충돌하지 않았다면 건너뛰기
            applyBounceFromBoundary(a, d); // 사과가 벽면에 충돌해 튕겨나가는 힘을 적용한다.
            applyFriction(a,d); // 사과에 마찰력을 적용한다.
        }
    }

    /**
     * 창이 움직일때 발생하는 관성력을 사과 객체에 적용합니다.
     * @param apples - 적용할 사과 객체들
     */
    public void applyInertia(List<Apple> apples) {
        // 창에 대한 가속도를 벡터 객체로 변환하여 변수에 저장
        Vector inertiaPower = new Vector(frame.getAx(), frame.getAy());
        // 벡터의 방향을 반대로 바꾸고(음수) 계수를 곱한다.
        inertiaPower = inertiaPower.multiply(-inertiaFactor);
        for(Apple a : apples) {
            // 조정된 가속도 값을 사과 속도에 적용 하여 가속
            a.setVx(a.getVx()+inertiaPower.getPoint().getX());
            a.setVy(a.getVy()+inertiaPower.getPoint().getY());
        }
    }

    /**
     * 사과 객체들에 대해서 아랫방향으로 중력 가속도가 작용하도록 합니다.
     * @param a 적용할 사과 객체
     */
    public void applyGravity(Apple a) {
        if(a.getAy() == gravity) return; // 이미 중력 가속도가 적용된 사과라면 무시
        a.setAy(gravity); // 아니라면 중력 가속도를 사과 객체에 적용
    }
    /**
     * 사과 객체들에 대해서 마찰력이 작용하도록 합니다.
     * @param a 작용하는 사과 객체
     * @param d 작용하는 표면 방향
     */
    public void applyFriction(Apple a, Direction d) {
        if(d != Direction.SOUTH) return; // 바닥에 닿은 사과가 아니라면 무시
        // 현재 사과의 속도 방향에 따라서 마찰계수의 부호를 다르게 분기
        double factor = frictionFactor * (a.getVx() > 0 ? -1 : 1);
        // 만약 만약 속도값이 줄어들다가 0을지나 오히려 절댓값이 증가했다면 속도를 0으로 스냅
        if (a.getVx() * (a.getVx() + factor) < 0) a.setVx(0);
        // 아니라면 계속 속도 감속 시키기
        else a.setVx(a.getVx() + factor);
    }

    /**
     * 사과가 벽을 향해 입사할때 튕겨내는 물리력이 작용하도록 합니다.
     * @param a 입사하는 사과의 객체
     * @param enteredDirection 사과가 입사하는 방향
     */
    public void applyBounceFromBoundary(Apple a, Direction enteredDirection) {
        // 사과 입사 방향에 따라서 사과 스냅(위치 변경) 포인트를 설정하기
        Point snapPoint = switch(enteredDirection) {
                case NORTH -> new Point(a.getPosition().getX(), 0);
                case SOUTH -> new Point(a.getPosition().getX(), panel.getHeight() - a.getSize());
                case EAST -> new Point(panel.getWidth() - a.getSize(), a.getPosition().getY());
                case WEST -> new Point(0, a.getPosition().getY());
        };
        a.setPosition(snapPoint);
        // 만약 상, 하 방향에 부딪혔다면 각각 y축방향으로 속도를 반전시키고 탄성 계수를 적용한 속도값을 적용하기
        if (enteredDirection == Direction.SOUTH || enteredDirection == Direction.NORTH) {
            double nextVy = a.getVy() * -1 * bounceFactor;
            a.setVy(Math.abs(nextVy) < 0.05 ? 0 : nextVy);
        }
        else { // 아닌경우 좌, 우 방향에 부딪힌걸로 판단하고 각각 x 축방향으로 속도를 반전시키고 탄성 계수를 적용한 속도값을 적용하기
            double nextVx = a.getVx() * -1 * bounceFactor;
            a.setVx(Math.abs(nextVx) < 0.05 ? 0 : nextVx);
        }

    }

    /**
     * 사과 객체들끼리 튕겨내는 물리력을 적용합니다.
     * @param a1 대상 사과 객체
     * @param apples 비교할 사과 객체들
     */
    public void applyBounce(Apple a1, List<Apple> apples) {
        // 더 정밀한 충돌력 적용을 위해 같은 계산을 loop번 반복
        int loop = 4;
        for (int i = 0; i < loop; i++) {
            // 사과 객체 a1과 그외 모든 사과객체를 비교
            for(Apple a2 : apples) {
                // 같은 객체이거나 이미 사라진 객체이거나 충돌된 객체가 아니라면 충돌력을 계산하지 않는다.
                if(a1 == a2 || a2.isUsed() || !isCollided(a1,a2)) continue;
                // 위 예외에 해당하지 않는다면 충돌력을 계산하고 적용한다.
                bounce(a1,a2);
            }
        }
    }

    /**
     * 폭탄 사과가 폭발할때 주변 사과가 튕겨나가는 힘을 작용시킵니다.
     * @param a 폭탄 사과 객체
     * @param apples 전체 사과 객체들
     */
    public void applyExplode(Apple a, List<Apple> apples) {
        // p1 = 폭빌하는 사과 객체의 위치 벡터
        Vector p1 = new Vector(a.getPosition().getX(), a.getPosition().getY());
        // p2 = 폭발하는 사과 주변에 있어서 영향을 받아 날라가는 사과들 위치 벡터를 저장할 변수
        Vector p2;
        for (Apple apple : apples) { // 모든 사과에 대해서 폭발력 적용
            if(apple == a || apple.isUsed()) continue; // 이미 사라진 사과이거나 폭발하는 사과 객체와 같다면 건너뛰기
            // p2를 각 사과의 위치로 만든 벡터로 설정
            p2 = new Vector(apple.getPosition().getX(), apple.getPosition().getY());
            // 비교 대상 사과의 속도 벡터를 생성하고 ov 변수에 저장
            Vector ov = new Vector(apple.getVx(),apple.getVy());
            // p2에서 p1 벡터를 빼고 그 벡터 길이로 나누어서 방향만 남은 단위벡터 nh를 생성한다.
            Vector n = p2.subtract(p1);
            Vector nh = n.multiply(1/n.getLength());
            // 두 사과 객체의 거리를 계산하여 d 변수에 저장
            double d = getDistanceBetweenCenter(a,apple) - a.getSize()/2 - apple.getSize()/2;
            d = d<0 ? 0 : d; // 사과가 관통등 이슈로 거리가 음수가 나올때 모두 0으로 뭉개기
            // (1/2)^((d/60) * 1 / size) 이 식의 결과를 기존 속도에 더하는 방식으로 폭발할때 주변 사과가 가속되는 속도를 구현한다.
            Vector v = nh.multiply(Math.pow(0.5,d/60 * (1 / apple.getSize())));
            Vector av = ov.add(v);
            // 최종적으로 계산된 속도 벡터(av)를 각 사과의 x,y축 속도 적용 함수를 통해 각각 적용한다.
            apple.setVx(av.getPoint().getX());
            apple.setVy(av.getPoint().getY());
        }
    }

    /**
     * 사과 객체끼리 튕겨내는 힘의 연산을 진행합니다.
     * @param a1 사과 객체 1
     * @param a2 사과 객체 2
     */
    public void bounce(Apple a1, Apple a2) {
        // p1,p2 = 각각 a1,a2 사과 객체의 중심 좌표 벡터
        Vector p1 = new Vector(a1.getPosition().getX() + a1.getSize()/2, a1.getPosition().getY() + a1.getSize()/2);
        Vector p2 = new Vector(a2.getPosition().getX() + a2.getSize()/2, a2.getPosition().getY() + a2.getSize()/2);
        // 두 벡터를 빼고 길이로 나누어서 방향만 남긴 단위벡터(nh)를 만든다.
        Vector n = p1.subtract(p2);
        Vector nh = n.multiply(1/n.getLength());
        // 단위벡터(nh)에 수직인 법선 단위 벡터(th)도 같이 생성한다.
        Vector th = new Vector(nh.getPoint().getY(), nh.getPoint().getX() * -1);
        // v1,v2 = 두 사과의 현재 속도 벡터
        Vector v1 = new Vector(a1.getVx(), a1.getVy());
        Vector v2 = new Vector(a2.getVx(), a2.getVy());
        // r = 두 사과의 반지름의 합
        double r = a1.getSize()/2+a2.getSize()/2;
        // penetration = 두 사과가 얼마나 겹쳐졌는지 길이
        double penetration = r - getDistanceBetweenCenter(a1,a2);
        // 만약 사과가 조금이라도 겹쳐졌다면
        if(penetration > 0) {
            double slop = 1.0; // 오차 범위 (1.0까지 겹쳐지는 정도는 사실상 무시)
            // 조정할 거리 계산 (오차범위 내일경우 0으로 설정하여 조절하지 않음)
            double adjust = Math.max(penetration - slop, 0.0);
            // 두 사과의 크기 비율에 맞추어 뒤로 후퇴하는 비중을 조정
            double inv11 = 1.0 / (a1.getSize());
            double inv22 = 1.0 / (a2.getSize());
            double invSum = inv11 + inv22;
            // (해당 사과 크기 비율 / 전체 크기 비율) * 조정할 거리 를 계산후 원래 사과 위치 벡터에 더하여 사과 위치를 각각 조정한다.
            Vector adjustA1 = nh.multiply(adjust*(inv11/invSum));
            Vector adjustA2 = nh.multiply(adjust*(inv22/invSum)).multiply(-1);
            p1 = p1.add(adjustA1);
            p2 = p2.add(adjustA2);
            // 조정된 p1,p2 벡터를 이용하여 최종적으로 사과 위치를 setPosition 매소드를 이용하여 반영한다.
            a1.setPosition(new Point(p1.getPoint().getX() -  a1.getSize()/2, p1.getPoint().getY() - a1.getSize()/2));
            a2.setPosition(new Point(p2.getPoint().getX() - a2.getSize()/2, p2.getPoint().getY() - a2.getSize()/2));
        }
        double v1n = nh.multiply(v1); // 1번 사과 속도에서 사과의 충돌 방향 속도 벡터 만을 단위 벡터를 곱하여 추출
        double v1t = th.multiply(v1); // 1번 사과 속도에서 사과의 충돌 방향과 관계 없는 방향의 속도 벡터만을 법선 단위 벡터를 곱하여 추출
        double v2n = nh.multiply(v2); // 2번 사과 속도에서 사과의 충돌 방향 속도 벡터 만을 단위 벡터를 곲하여 추출
        double v2t = th.multiply(v2); // 2번 사과 속도에서 사과의 충돌 방향과 관계 없는 방향의 속도 벡터만을 법선 단위 벡터를 곱하여 추출
        // v1n, v2n 속도와 두 사과의 크기, 탄성 계수를 이용하여 1차원 충돌 공식을 이용하여 각각 충돌 이후 충돌 방향 선상의 속도를 각각 구한다.
        double v1n_p = (a1.getSize() * v1n + a2.getSize() * v2n - a2.getSize() * this.bounceFactor * (v1n - v2n)) / (a1.getSize() + a2.getSize());
        double v2n_p = (a1.getSize() * v1n + a2.getSize() * v2n + a1.getSize() * this.bounceFactor * (v1n - v2n)) / (a1.getSize() + a2.getSize());
        // 충돌 방향과 관계 없는 수직 방향은 속도의 변화가 없음으로 그대로 사용
        double v1t_p = v1t;
        double v2t_p = v2t;
        // 계산된 속도를 다시 결합하여 하나의 벡터로 만든다. 일차원 충돌 결과값이 스칼라 값이므로 단위 벡터를 곱하여 벡터 형태로 변환후 두 벡터를 더한다.
        Vector v1_p = nh.multiply(v1n_p).add(th.multiply(v1t_p));
        Vector v2_p = nh.multiply(v2n_p).add(th.multiply(v2t_p));
        // 이렇게 계산된 두 사과의 최종 속도를 두 사과의 x,y축 속도로 각각 적용한다.
        a1.setVx(v1_p.getPoint().getX());
        a1.setVy(v1_p.getPoint().getY());
        a2.setVx(v2_p.getPoint().getX());
        a2.setVy(v2_p.getPoint().getY());
    }

}
