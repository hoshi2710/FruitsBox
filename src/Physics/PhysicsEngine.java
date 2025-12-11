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
        Point snapPoint = switch(enteredDirection) {
                case NORTH -> new Point(a.getPosition().getX(), 0);
                case SOUTH -> new Point(a.getPosition().getX(), panel.getHeight() - a.getSize());
                case EAST -> new Point(panel.getWidth() - a.getSize(), a.getPosition().getY());
                case WEST -> new Point(0, a.getPosition().getY());
        };
        a.setPosition(snapPoint);
        if (enteredDirection == Direction.SOUTH || enteredDirection == Direction.NORTH) {
            double nextVy = a.getVy() * -1 * bounceFactor;
            a.setVy(Math.abs(nextVy) < 0.05 ? 0 : nextVy);
        }
        else {
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
        int loop = 4;
        for (int i = 0; i < loop; i++) {
            for(Apple a2 : apples) {
                if(a1 == a2 || a2.isUsed() || !isCollided(a1,a2)) continue;
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
        Vector p1 = new Vector(a.getPosition().getX(), a.getPosition().getY());
        Vector p2;
        for (Apple apple : apples) {
            if(apple == a || apple.isUsed()) continue;
            p2 = new Vector(apple.getPosition().getX(), apple.getPosition().getY());
            Vector ov = new Vector(apple.getVx(),apple.getVy());
            Vector n = p2.subtract(p1);
            Vector nh = n.multiply(1/n.getLength());
            double d = getDistanceBetweenCenter(a,apple) - a.getSize()/2 - apple.getSize()/2;
            d = d<0 ? 0 : d;
            Vector v = nh.multiply(Math.pow(0.5,d/60 * (1 / apple.getSize())));
            Vector av = ov.add(v);
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
        Vector p1 = new Vector(a1.getPosition().getX() + a1.getSize()/2, a1.getPosition().getY() + a1.getSize()/2);
        Vector p2 = new Vector(a2.getPosition().getX() + a2.getSize()/2, a2.getPosition().getY() + a2.getSize()/2);
        Vector n = p1.subtract(p2);
        Vector nh = n.multiply(1/n.getLength());
        Vector th = new Vector(nh.getPoint().getY(), nh.getPoint().getX() * -1);
        Vector v1 = new Vector(a1.getVx(), a1.getVy());
        Vector v2 = new Vector(a2.getVx(), a2.getVy());
        double r = a1.getSize()/2+a2.getSize()/2;
        double penetration = r - getDistanceBetweenCenter(a1,a2);
        if(penetration > 0) {
            double slop = 0.2;
            double adjust = Math.max(penetration - slop, 0.0) * 0.6;
            double inv11 = 1.0 / (a1.getSize());
            double inv22 = 1.0 / (a2.getSize());
            double invSum = inv11 + inv22;
            Vector adjustA1 = nh.multiply(adjust*(inv11/invSum));
            Vector adjustA2 = nh.multiply(adjust*(inv22/invSum)).multiply(-1);
            p1 = p1.add(adjustA1);
            p2 = p2.add(adjustA2);
            a1.setPosition(new Point(p1.getPoint().getX() -  a1.getSize()/2, p1.getPoint().getY() - a1.getSize()/2));
            a2.setPosition(new Point(p2.getPoint().getX() - a2.getSize()/2, p2.getPoint().getY() - a2.getSize()/2));
        }
        double v1n = nh.multiply(v1);
        double v1t = th.multiply(v1);
        double v2n = nh.multiply(v2);
        double v2t = th.multiply(v2);
        double v1n_p = (a1.getSize() * v1n + a2.getSize() * v2n - a2.getSize() * this.bounceFactor * (v1n - v2n)) / (a1.getSize() + a2.getSize());
        double v2n_p = (a1.getSize() * v1n + a2.getSize() * v2n + a1.getSize() * this.bounceFactor * (v1n - v2n)) / (a1.getSize() + a2.getSize());
        double v1t_p = v1t;
        double v2t_p = v2t;
        Vector v1_p = nh.multiply(v1n_p).add(th.multiply(v1t_p));
        Vector v2_p = nh.multiply(v2n_p).add(th.multiply(v2t_p));
        a1.setVx(v1_p.getPoint().getX());
        a1.setVy(v1_p.getPoint().getY());
        a2.setVx(v2_p.getPoint().getX());
        a2.setVy(v2_p.getPoint().getY());
    }

}
