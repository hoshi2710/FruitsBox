package model;

public class Vector {
    private Point point;
    private double length;
    public Vector(double x, double y) {
        this.point = new Point(x, y);
        this.length = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
    public double getLength() {
        return length;
    }
    public Point getPoint() {
        return point;
    }
    public Vector add(Vector vector) {
        return new Vector(point.getX() + vector.getPoint().getX(), point.getY() + vector.getPoint().getY());
    }
    public Vector subtract(Vector vector) {
        return new Vector(point.getX() - vector.getPoint().getX(), point.getY() - vector.getPoint().getY());
    }
    public double multiply(Vector vector) {
        return point.getX() * vector.getPoint().getX()+point.getY() * vector.getPoint().getY();
    }
    public Vector multiply(double value) {
        return new Vector(point.getX() * value, point.getY() * value);
    }
}
