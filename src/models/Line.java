package models;

import java.awt.*;

public class Line {

    private Point p1, p2;
    private Color color;
    private LineType lineType = LineType.SOLID;
    private int lineWidth = 1;


    public Line(Point p1, Point p2, Color color) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
    }

    public Line(Point p1, Point p2, LineType lineType) {
        this.p1 = p1;
        this.p2 = p2;
        this.lineType = lineType;
    }

    public Line(Point p1, Point p2, Color color, LineType lineType) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
        this.lineType = lineType;
    }

    public Line(Point p1, Point p2, Color color, LineType lineType, int lineWidth) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
        this.lineType = lineType;
        this.lineWidth = lineWidth;
    }

    public Line(Point p1, Point p2, boolean isDotted) {
        this.p1 = p1;
        this.p2 = p2;
        this.lineType = isDotted ? LineType.DOTTED : LineType.SOLID;
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

    public LineType getLineType() {
        return lineType;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public boolean isDotted() {
        return lineType == LineType.DOTTED;
    }
}
