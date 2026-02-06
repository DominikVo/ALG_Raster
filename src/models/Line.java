package models;

import java.awt.*;

public class Line {

    private Point p1, p2;
    private Color color;
    private boolean dotted = false;


    public Line(Point p1, Point p2, Color color) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
    }

    public Line(Point p1, Point p2, boolean dotted) {
        this.p1 = p1;
        this.p2 = p2;
        this.dotted = dotted;
    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1 = p1;
    }

    public Point getP2() {
        return p2;
    }

    public void setP2(Point p2) {
        this.p2 = p2;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isDotted() {
        return dotted;
    }
}
