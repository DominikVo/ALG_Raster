package modes;

import app.App;
import models.Line;
import models.LineType;
import models.Point;
import utils.Utils;

import java.awt.event.MouseEvent;

public class LineMode extends BaseMode {

    public LineMode(App app) {
        super(app);
    }

    private LineType getLineType() {
        if (app.isDottedMode()) {
            return LineType.DOTTED;
        } else if (app.isDashedMode()) {
            return LineType.DASHED;
        } else {
            return LineType.SOLID;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        app.setPPomocny(new Point(e.getX(), e.getY()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point p1 = app.getPPomocny();
        Point p2 = new Point(e.getX(), e.getY());

        if (app.isSnapMode() && p1 != null) {
            p2 = Utils.snapPoint(p1, p2);
        }

        Line line = new Line(p1, p2, app.getRasterizer().getColor(), getLineType(), app.getLineWidth());

        app.redrawCanvas();
        app.getLineCanvas().addLine(line);
        app.getCanvasRasterizer().rasterize(app.getLineCanvas());

        app.getPanel().repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p1 = app.getPPomocny();
        Point p2 = new Point(e.getX(), e.getY());

        if (app.isSnapMode() && p1 != null) {
            p2 = Utils.snapPoint(p1, p2);
        }

        Line line = new Line(p1, p2, app.getRasterizer().getColor(), getLineType(), app.getLineWidth());

        app.redrawCanvas();
        app.getRasterizer().rasterize(line);

        app.getPanel().repaint();
    }

    @Override
    public String getName() {
        return "Line";
    }
}
