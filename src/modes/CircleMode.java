package modes;

import app.App;
import models.Circle;
import models.LineType;
import models.Point;
import java.awt.event.MouseEvent;

public class CircleMode extends BaseMode {

    public CircleMode(App app) {
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
        if (app.getPPomocny() == null) return;
        
        Point center = app.getPPomocny();
        Point edge = new Point(e.getX(), e.getY());
        
        double radius = Math.sqrt(Math.pow(edge.getX() - center.getX(), 2) + Math.pow(edge.getY() - center.getY(), 2));
        Circle circle = new Circle(center, (int) radius, getLineType(), app.getRasterizer().getColor(), app.getLineWidth());
        
        app.getLineCanvas().addCircle(circle);
        
        app.redrawCanvas();
        app.getPanel().repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (app.getPPomocny() == null) return;

        Point center = app.getPPomocny();
        Point edge = new Point(e.getX(), e.getY());
        double radius = Math.sqrt(Math.pow(edge.getX() - center.getX(), 2) + Math.pow(edge.getY() - center.getY(), 2));
        Circle circle = new Circle(center, (int) radius, getLineType(), app.getRasterizer().getColor(), app.getLineWidth());

        app.redrawCanvas();
        app.getRasterizer().rasterize(circle);
        app.getPanel().repaint();
    }

    @Override
    public String getName() {
        return "Circle";
    }
}
