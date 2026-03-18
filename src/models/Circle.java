package models;

import java.awt.Color;

public class Circle {
    private Point center;
    private int radius;
    private LineType lineType;
    private Color color;
    private int lineWidth = 1;

    public Circle(Point center, int radius, LineType lineType) {
        this.center = center;
        this.radius = radius;
        this.lineType = lineType;
    }

    public Circle(Point center, int radius, LineType lineType, Color color) {
        this.center = center;
        this.radius = radius;
        this.lineType = lineType;
        this.color = color;
    }

    public Circle(Point center, int radius, LineType lineType, Color color, int lineWidth) {
        this.center = center;
        this.radius = radius;
        this.lineType = lineType;
        this.color = color;
        this.lineWidth = lineWidth;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public LineType getLineType() {
        return lineType;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }
}
