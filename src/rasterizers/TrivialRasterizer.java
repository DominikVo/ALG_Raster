package rasterizers;

import models.Circle;
import models.Line;
import models.LineType;
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
    public Color getColor() {
        return defaultColor;
    }

    @Override
    public Raster getRaster() {
        return raster;
    }

    private void drawFilledCircle(int centerX, int centerY, int radius, int color) {
        int width = raster.getWidth();
        int height = raster.getHeight();
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                if (x * x + y * y <= radius * radius) {
                    int px = centerX + x;
                    int py = centerY + y;
                    if (px >= 0 && px < width && py >= 0 && py < height) {
                        raster.setPixel(px, py, color);
                    }
                }
            }
        }
    }

    @Override
    public void rasterize(Line line) {
        Color color = line.getColor() != null ? line.getColor() : defaultColor;
        int c = color.getRGB();
        int lineWidth = line.getLineWidth();
        int radius = (lineWidth - 1) / 2;

        int x1 = line.getP1().getX();
        int y1 = line.getP1().getY();
        int x2 = line.getP2().getX();
        int y2 = line.getP2().getY();

        final int DOT_INTERVAL = 4 * lineWidth;
        final int DASH_INTERVAL = 10 * lineWidth;
        int segmentIndex = 0;

        int dx = x2 - x1;
        int dy = y2 - y1;

        if (dx == 0) {
            int ya = Math.min(y1, y2);
            int yb = Math.max(y1, y2);
            for (int y = ya; y <= yb; y++) {
                if (shouldSkipPixel(line.getLineType(), segmentIndex++, DOT_INTERVAL, DASH_INTERVAL)) continue;
                drawFilledCircle(x1, y, radius, c);
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
                if (shouldSkipPixel(line.getLineType(), segmentIndex++, DOT_INTERVAL, DASH_INTERVAL)) continue;
                drawFilledCircle(x, y, radius, c);
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
                if (shouldSkipPixel(line.getLineType(), segmentIndex++, DOT_INTERVAL, DASH_INTERVAL)) continue;
                drawFilledCircle(x, y, radius, c);
            }
        }
    }

    @Override
    public void rasterize(Circle circle) {
        Color color = circle.getColor() != null ? circle.getColor() : defaultColor;
        int c = color.getRGB();
        int lineWidth = circle.getLineWidth();

        int x0 = circle.getCenter().getX();
        int y0 = circle.getCenter().getY();
        int r = circle.getRadius();
        LineType lineType = circle.getLineType();

        final int DOT_INTERVAL = 4 * lineWidth;
        final int DASH_INTERVAL = 10 * lineWidth;
        int segmentIndex = 0;

        int x = 0;
        int y = r;
        int d = 3 - 2 * r;

        while (y >= x) {
            drawCirclePoints(x0, y0, x, y, c, lineType, DOT_INTERVAL, DASH_INTERVAL, segmentIndex, lineWidth);
            if (d < 0) {
                d = d + 4 * x + 6;
            } else {
                d = d + 4 * (x - y) + 10;
                y--;
            }
            x++;
            segmentIndex++;
        }
    }

    private void drawCirclePoints(int x0, int y0, int x, int y, int color, LineType lineType, int dotInterval, int dashInterval, int segmentIndex, int lineWidth) {
        int radius = (lineWidth - 1) / 2;
        if (!shouldSkipPixel(lineType, segmentIndex, dotInterval, dashInterval)) {
            drawFilledCircle(x0 + x, y0 + y, radius, color);
            drawFilledCircle(x0 + y, y0 + x, radius, color);
            drawFilledCircle(x0 - y, y0 + x, radius, color);
            drawFilledCircle(x0 - x, y0 + y, radius, color);
            drawFilledCircle(x0 - x, y0 - y, radius, color);
            drawFilledCircle(x0 - y, y0 - x, radius, color);
            drawFilledCircle(x0 + y, y0 - x, radius, color);
            drawFilledCircle(x0 + x, y0 - y, radius, color);
        }
    }

    private boolean shouldSkipPixel(LineType lineType, int index, int dotInterval, int dashInterval) {
        switch (lineType) {
            case DOTTED:
                return (index % dotInterval) != 0;
            case DASHED:
                return (index / dashInterval) % 2 != 0;
            default:
                return false;
        }
    }
}
