package models;

import java.util.List;

public class Fill {
    private List<Point> points;
    private int color;

    public Fill(List<Point> points, int color) {
        this.points = points;
        this.color = color;
    }

    public List<Point> getPoints() {
        return points;
    }

    public int getColor() {
        return color;
    }
}
