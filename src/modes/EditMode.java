package modes;

import app.App;
import models.Circle;
import models.Line;
import models.LineCanvas;
import models.Point;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class EditMode extends BaseMode {

    private static final int THRESHOLD = 5;

    private enum SelectedPart {
        NONE, LINE_BODY, LINE_P1, LINE_P2, CIRCLE_BODY, CIRCLE_CENTER, CIRCLE_EDGE
    }

    private static class SelectedObjectInfo {
        Object object;
        SelectedPart part;
        Point initialMousePosition;
        Point initialP1, initialP2, initialCenter;
        int initialRadius;

        public SelectedObjectInfo(Object object, SelectedPart part, Point mousePosition) {
            this.object = object;
            this.part = part;
            this.initialMousePosition = mousePosition;

            if (object instanceof Line) {
                Line line = (Line) object;
                this.initialP1 = new Point(line.getP1().getX(), line.getP1().getY());
                this.initialP2 = new Point(line.getP2().getX(), line.getP2().getY());
            } else if (object instanceof Circle) {
                Circle circle = (Circle) object;
                this.initialCenter = new Point(circle.getCenter().getX(), circle.getCenter().getY());
                this.initialRadius = circle.getRadius();
            }
        }
    }

    private SelectedObjectInfo selectedObjectInfo;

    public EditMode(App app) {
        super(app);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point mousePoint = new Point(e.getX(), e.getY());
        LineCanvas canvas = app.getLineCanvas();
        selectedObjectInfo = null; // Deselect any previously selected object

        // Iterate through lines and circles to find the closest object
        List<Line> lines = canvas.getLines();
        for (Line line : lines) {
            SelectedPart part = getLineSelectedPart(mousePoint, line);
            if (part != SelectedPart.NONE) {
                selectedObjectInfo = new SelectedObjectInfo(line, part, mousePoint);
                app.updateUIPanelProperties(line.getColor(), line.getLineWidth());
                app.setCurrentColor(line.getColor());
                app.setLineWidth(line.getLineWidth());
                return;
            }
        }

        List<Circle> circles = canvas.getCircles();
        for (Circle circle : circles) {
            SelectedPart part = getCircleSelectedPart(mousePoint, circle);
            if (part != SelectedPart.NONE) {
                selectedObjectInfo = new SelectedObjectInfo(circle, part, mousePoint);
                app.updateUIPanelProperties(circle.getColor(), circle.getLineWidth());
                app.setCurrentColor(circle.getColor());
                app.setLineWidth(circle.getLineWidth());
                return;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedObjectInfo == null) {
            return;
        }

        Point mousePoint = new Point(e.getX(), e.getY());
        int dx = mousePoint.getX() - selectedObjectInfo.initialMousePosition.getX();
        int dy = mousePoint.getY() - selectedObjectInfo.initialMousePosition.getY();

        Object selectedObject = selectedObjectInfo.object;
        SelectedPart selectedPart = selectedObjectInfo.part;

        if (selectedObject instanceof Line) {
            Line line = (Line) selectedObject;
            if (selectedPart == SelectedPart.LINE_BODY) {
                line.getP1().setX(selectedObjectInfo.initialP1.getX() + dx);
                line.getP1().setY(selectedObjectInfo.initialP1.getY() + dy);
                line.getP2().setX(selectedObjectInfo.initialP2.getX() + dx);
                line.getP2().setY(selectedObjectInfo.initialP2.getY() + dy);
            } else if (selectedPart == SelectedPart.LINE_P1) {
                line.getP1().setX(selectedObjectInfo.initialP1.getX() + dx);
                line.getP1().setY(selectedObjectInfo.initialP1.getY() + dy);
            } else if (selectedPart == SelectedPart.LINE_P2) {
                line.getP2().setX(selectedObjectInfo.initialP2.getX() + dx);
                line.getP2().setY(selectedObjectInfo.initialP2.getY() + dy);
            }
        } else if (selectedObject instanceof Circle) {
            Circle circle = (Circle) selectedObject;
            if (selectedPart == SelectedPart.CIRCLE_BODY || selectedPart == SelectedPart.CIRCLE_CENTER) {
                circle.getCenter().setX(selectedObjectInfo.initialCenter.getX() + dx);
                circle.getCenter().setY(selectedObjectInfo.initialCenter.getY() + dy);
            } else if (selectedPart == SelectedPart.CIRCLE_EDGE) {
                Point center = circle.getCenter();
                double newRadius = Math.sqrt(Math.pow(mousePoint.getX() - center.getX(), 2) + Math.pow(mousePoint.getY() - center.getY(), 2));
                circle.setRadius((int) newRadius);
            }
        }

        app.redrawCanvas();
        app.getPanel().repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        selectedObjectInfo = null;
        app.resetUIPanelProperties();
        app.redrawCanvas();
        app.getPanel().repaint();
    }

    private SelectedPart getLineSelectedPart(Point p, Line line) {
        if (isPointNearPoint(p, line.getP1())) {
            return SelectedPart.LINE_P1;
        } else if (isPointNearPoint(p, line.getP2())) {
            return SelectedPart.LINE_P2;
        } else if (isPointNearLine(p, line)) {
            return SelectedPart.LINE_BODY;
        } else {
            return SelectedPart.NONE;
        }
    }

    private SelectedPart getCircleSelectedPart(Point p, Circle circle) {
        if (isPointNearPoint(p, circle.getCenter())) {
            return SelectedPart.CIRCLE_CENTER;
        } else if (isPointNearCircleEdge(p, circle)) {
            return SelectedPart.CIRCLE_EDGE;
        } else if (isPointNearCircleBody(p, circle)) {
            return SelectedPart.CIRCLE_BODY;
        } else {
            return SelectedPart.NONE;
        }
    }

    private boolean isPointNearPoint(Point p1, Point p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy) < THRESHOLD;
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

    private boolean isPointNearCircleEdge(Point p, Circle circle) {
        Point center = circle.getCenter();
        double dx = p.getX() - center.getX();
        double dy = p.getY() - center.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return Math.abs(dist - circle.getRadius()) < THRESHOLD;
    }

    private boolean isPointNearCircleBody(Point p, Circle circle) {
        Point center = circle.getCenter();
        double dx = p.getX() - center.getX();
        double dy = p.getY() - center.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < circle.getRadius();
    }

    public void updateSelectedObjectColor(Color color) {
        if (selectedObjectInfo != null) {
            if (selectedObjectInfo.object instanceof Line) {
                ((Line) selectedObjectInfo.object).setColor(color);
            } else if (selectedObjectInfo.object instanceof Circle) {
                ((Circle) selectedObjectInfo.object).setColor(color);
            }
        }
    }

    public void updateSelectedObjectLineWidth(int lineWidth) {
        if (selectedObjectInfo != null) {
            if (selectedObjectInfo.object instanceof Line) {
                ((Line) selectedObjectInfo.object).setLineWidth(lineWidth);
            } else if (selectedObjectInfo.object instanceof Circle) {
                ((Circle) selectedObjectInfo.object).setLineWidth(lineWidth);
            }
        }
    }

    @Override
    public String getName() {
        return "Edit";
    }
}
