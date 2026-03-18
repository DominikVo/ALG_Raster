package modes;

import app.App;
import models.Circle;
import models.Line;
import models.LineCanvas;
import models.Point;

import java.awt.event.MouseEvent;
import java.util.Iterator;

public class RemoveMode extends BaseMode {

    private static final int THRESHOLD = 5;

    public RemoveMode(App app) {
        super(app);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point p = new Point(e.getX(), e.getY());
        LineCanvas canvas = app.getLineCanvas();

        // Remove lines
        Iterator<Line> lineIterator = canvas.getLines().iterator();
        while (lineIterator.hasNext()) {
            Line line = lineIterator.next();
            if (isPointNearLine(p, line)) {
                lineIterator.remove();
                app.redrawCanvas();
                app.getPanel().repaint();
                return;
            }
        }

        // Remove circles
        Iterator<Circle> circleIterator = canvas.getCircles().iterator();
        while (circleIterator.hasNext()) {
            Circle circle = circleIterator.next();
            if (isPointNearCircle(p, circle)) {
                circleIterator.remove();
                app.redrawCanvas();
                app.getPanel().repaint();
                return;
            }
        }

    }

    private boolean isPointNearLine(Point p, Line line) {
        Point p1 = line.getP1();
        Point p2 = line.getP2();

        if (p1.getX() == p2.getX() && p1.getY() == p2.getY()) {
            double dx = p.getX() - p1.getX();
            double dy = p.getY() - p1.getY();
            return Math.sqrt(dx * dx + dy * dy) < THRESHOLD;
        }

        double px = p2.getX() - p1.getX();
        double py = p2.getY() - p1.getY();
        double temp = (px * px) + (py * py);
        double u = ((p.getX() - p1.getX()) * px + (p.getY() - p1.getY()) * py) / temp;

        if (u > 1) {
            u = 1;
        } else if (u < 0) {
            u = 0;
        }

        double x = p1.getX() + u * px;
        double y = p1.getY() + u * py;

        double dx = x - p.getX();
        double dy = y - p.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);

        return dist < THRESHOLD;
    }

    private boolean isPointNearCircle(Point p, Circle circle) {
        Point center = circle.getCenter();
        double dx = p.getX() - center.getX();
        double dy = p.getY() - center.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return Math.abs(dist - circle.getRadius()) < THRESHOLD;
    }

    @Override
    public String getName() {
        return "Remove";
    }
}
