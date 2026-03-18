package modes;

import app.App;
import models.Line;
import models.Point;
import utils.Utils;

import java.awt.event.MouseEvent;

public class PolygonMode extends BaseMode {

    public PolygonMode(App app) {
        super(app);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        app.setPPomocny(new Point(e.getX(), e.getY()));

        if (!app.getPolygonPoints().isEmpty()) {
            int idx = app.getVertexIndexNear(app.getPPomocny());
            if (idx != -1) {
                app.setMovingVertexIndex(idx);
                return;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point p2 = new Point(e.getX(), e.getY());

        if (app.getMovingVertexIndex() != -1) {
            app.setMovingVertexIndex(-1);
            app.getPanel().repaint();
            return;
        }

        if (app.isSnapMode() && app.getPPomocny() != null) {
            p2 = Utils.snapPoint(app.getPPomocny(), p2);
        }

        if (app.getPPomocny() != null) {
            if (app.getPolygonPoints().isEmpty() || !app.samePoint(app.getPolygonPoints().get(app.getPolygonPoints().size() - 1), app.getPPomocny())) {
                app.getPolygonPoints().add(app.getPPomocny());
            }
        }

        app.getPolygonPoints().add(p2);
        app.redrawPolygon();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        Point p2 = new Point(e.getX(), e.getY());

        if (app.getMovingVertexIndex() != -1) {
            Point target = app.getPolygonPoints().get(app.getMovingVertexIndex());
            target.setX(p2.getX());
            target.setY(p2.getY());
            app.redrawPolygon();
            return;
        }

        if (app.isSnapMode() && app.getPPomocny() != null) {
            p2 = Utils.snapPoint(app.getPPomocny(), p2);
        }

        app.redrawCanvas();
        app.drawPolygonEdges();

        Point last = app.getPolygonPoints().isEmpty() ? app.getPPomocny() : app.getPolygonPoints().get(app.getPolygonPoints().size() - 1);
        if (last != null) {
            app.getRasterizer().rasterize(new Line(last, p2, app.isDottedMode()));
        }

        if (!app.getPolygonPoints().isEmpty()) {
            Point first = app.getPolygonPoints().get(0);
            app.getRasterizer().rasterize(new Line(p2, first, app.isDottedMode()));
        }

        app.getPanel().repaint();
    }

    @Override
    public String getName() {
        return "Polygon";
    }
}
