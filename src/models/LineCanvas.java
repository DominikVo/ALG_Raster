package models;

import java.util.ArrayList;
import java.util.List;

public class LineCanvas {

    private List<Line> lines;

    public LineCanvas() {
        lines = new ArrayList<>();
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public List<Line> getLines() {
        return lines;
    }

}
