package models;

import java.util.ArrayList;
import java.util.List;

public class LineCanvas {

    private List<Line> lines;
    private List<Circle> circles;
    private List<Fill> fills;

    public LineCanvas() {
        lines = new ArrayList<>();
        circles = new ArrayList<>();
        fills = new ArrayList<>();
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public List<Line> getLines() {
        return lines;
    }

    public void addCircle(Circle circle) {
        circles.add(circle);
    }

    public List<Circle> getCircles() {
        return circles;
    }

    public void addFill(Fill fill) {
        fills.add(fill);
    }

    public List<Fill> getFills() {
        return fills;
    }
}
