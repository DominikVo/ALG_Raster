package rasterizers;

import models.*;

public class CanvasRasterizer {

    private Rasterizer rasterizer;

    public CanvasRasterizer(Rasterizer rasterizer) {
        this.rasterizer = rasterizer;
    }

    public void rasterize(LineCanvas lineCanvas) {
        for (Line line : lineCanvas.getLines()) {
            rasterizer.rasterize(line);
        }
        for (Circle circle : lineCanvas.getCircles()) {
            rasterizer.rasterize(circle);
        }
        for (Fill fill : lineCanvas.getFills()) {
            for (Point point : fill.getPoints()) {
                rasterizer.getRaster().setPixel(point.getX(), point.getY(), fill.getColor());
            }
        }
    }
}
