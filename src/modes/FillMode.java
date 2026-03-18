package modes;

import app.App;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import models.Fill;
import models.Point;

public class FillMode extends BaseMode {

    public FillMode(App app) {
        super(app);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int targetColor = app.getRaster().getPixel(x, y);
        int replacementColor = app.getRasterizer().getColor().getRGB();

        if (targetColor != replacementColor) {
            List<Point> filledPoints = new ArrayList<>();
            floodFill(x, y, targetColor, replacementColor, filledPoints);
            if (!filledPoints.isEmpty()) {
                app.getLineCanvas().addFill(new Fill(filledPoints, replacementColor));
            }
            app.getPanel().repaint();
        }
    }

    private void floodFill(int x, int y, int targetColor, int replacementColor, List<Point> filledPoints) {
        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point p = queue.poll();
            int px = p.getX();
            int py = p.getY();

            if (px < 0 || px >= app.getRaster().getWidth() || py < 0 || py >= app.getRaster().getHeight()) {
                continue;
            }

            if (app.getRaster().getPixel(px, py) == targetColor) {
                app.getRaster().setPixel(px, py, replacementColor);
                filledPoints.add(new Point(px, py));
                queue.add(new Point(px + 1, py));
                queue.add(new Point(px - 1, py));
                queue.add(new Point(px, py + 1));
                queue.add(new Point(px, py - 1));
            }
        }
    }

    @Override
    public String getName() {
        return "Fill";
    }
}
