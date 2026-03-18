package modes;

import app.App;
import models.Line;
import models.LineType;
import models.Point;
import utils.Utils;

import java.awt.event.MouseEvent;

public class BoxMode extends BaseMode {

    public BoxMode(App app) {
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

        if (p1 != null) {
            if (e.isShiftDown() || app.isSnapMode()) {
                p2 = Utils.snapPoint(p1, p2, true);
            }
            addBoxToCanvas(p1, p2);
        }

        app.redrawCanvas();
        app.getCanvasRasterizer().rasterize(app.getLineCanvas());
        app.getPanel().repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p1 = app.getPPomocny();
        Point p2 = new Point(e.getX(), e.getY());

        if (p1 != null) {
            if (e.isShiftDown() || app.isSnapMode()) {
                p2 = Utils.snapPoint(p1, p2, true);
            }

            app.redrawCanvas();
            drawBox(p1, p2);
            app.getPanel().repaint();
        }
    }

    private void drawBox(Point p1, Point p2) {
        Point pTopRight = new Point(p2.getX(), p1.getY());
        Point pBottomLeft = new Point(p1.getX(), p2.getY());
        LineType lt = getLineType();
        int width = app.getLineWidth();
        java.awt.Color color = app.getRasterizer().getColor();

        app.getRasterizer().rasterize(new Line(p1, pTopRight, color, lt, width));
        app.getRasterizer().rasterize(new Line(pTopRight, p2, color, lt, width));
        app.getRasterizer().rasterize(new Line(p2, pBottomLeft, color, lt, width));
        app.getRasterizer().rasterize(new Line(pBottomLeft, p1, color, lt, width));
    }

    private void addBoxToCanvas(Point p1, Point p2) {
        Point pTopRight = new Point(p2.getX(), p1.getY());
        Point pBottomLeft = new Point(p1.getX(), p2.getY());
        LineType lt = getLineType();
        int width = app.getLineWidth();
        java.awt.Color color = app.getRasterizer().getColor();

        app.getLineCanvas().addLine(new Line(p1, pTopRight, color, lt, width));
        app.getLineCanvas().addLine(new Line(pTopRight, p2, color, lt, width));
        app.getLineCanvas().addLine(new Line(p2, pBottomLeft, color, lt, width));
        app.getLineCanvas().addLine(new Line(pBottomLeft, p1, color, lt, width));
    }

    @Override
    public String getName() {
        return "Box";
    }
}
