// src/rasterizers/CanvasRasterizer.java
package rasterizers;

import models.Line;
import models.LineCanvas;

public class CanvasRasterizer {

    private Rasterizer lineRasterizer;
    private Rasterizer dottedLineRasterizer;

    public CanvasRasterizer(Rasterizer lineRasterizer, Rasterizer dottedLineRasterizer) {
        this.lineRasterizer = lineRasterizer;
        this.dottedLineRasterizer = dottedLineRasterizer;
    }

    public void rasterize(LineCanvas lineCanvas) {
        for (Line line : lineCanvas.getLines()) {
            if (line.isDotted()) {
                dottedLineRasterizer.rasterize(line);
            } else {
                lineRasterizer.rasterize(line);
            }
        }
    }

}
