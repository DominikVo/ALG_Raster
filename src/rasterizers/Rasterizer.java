package rasterizers;

import models.Circle;
import models.Line;
import rasters.Raster;

import java.awt.*;

public interface Rasterizer {
    void setColor(Color color);
    Color getColor();
    void rasterize(Line line);
    void rasterize(Circle circle);
    Raster getRaster();
}
