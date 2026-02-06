// src/rasterizers/TrivialRasterizer.java
package rasterizers;

import models.Line;
import rasters.Raster;

import java.awt.*;

public class TrivialRasterizer implements Rasterizer {

    private Color defaultColor;
    private Raster raster;

    public TrivialRasterizer(Raster raster, Color defaultColor) {
        this.raster = raster;
        this.defaultColor = defaultColor;
    }

    @Override
    public void setColor(Color color) {
        defaultColor = color;
    }

    @Override
    public void rasterize(Line line) {
        Color color = line.getColor() != null ? line.getColor() : defaultColor;
        int c = color.getRGB();

        int x1 = line.getP1().getX();
        int y1 = line.getP1().getY();
        int x2 = line.getP2().getX();
        int y2 = line.getP2().getY();

        int width = raster.getWidth();
        int height = raster.getHeight();

        final int DOT_INTERVAL = 4;
        int dotIndex = 0;

        int dx = x2 - x1;
        int dy = y2 - y1;

        if (dx == 0) {
            int ya = Math.min(y1, y2);
            int yb = Math.max(y1, y2);
            for (int y = ya; y <= yb; y++) {
                if (line.isDotted() && (dotIndex++ % DOT_INTERVAL) != 0) continue;
                if (x1 >= 0 && x1 < width && y >= 0 && y < height) {
                    raster.setPixel(x1, y, c);
                }
            }
            return;
        }

        double k = dy / (double) dx;
        double q = y1 - k * x1;

        if (Math.abs(k) <= 1.0) {
            if (x1 > x2) {
                int tx = x1, ty = y1;
                x1 = x2; y1 = y2;
                x2 = tx; y2 = ty;
                dx = x2 - x1;
                dy = y2 - y1;
                k = dy / (double) dx;
                q = y1 - k * x1;
            }
            for (int x = x1; x <= x2; x++) {
                int y = (int) Math.round(k * x + q);
                if (line.isDotted() && (dotIndex++ % DOT_INTERVAL) != 0) continue;
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    raster.setPixel(x, y, c);
                }
            }
        } else {
            if (y1 > y2) {
                int tx = x1, ty = y1;
                x1 = x2; y1 = y2;
                x2 = tx; y2 = ty;
                dx = x2 - x1;
                dy = y2 - y1;
                k = dy / (double) dx;
                q = y1 - k * x1;
            }
            for (int y = y1; y <= y2; y++) {
                int x = (int) Math.round((y - q) / k);
                if (line.isDotted() && (dotIndex++ % DOT_INTERVAL) != 0) continue;
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    raster.setPixel(x, y, c);
                }
            }
        }
    }

}
