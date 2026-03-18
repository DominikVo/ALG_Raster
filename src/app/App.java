package app;

import models.Line;
import models.LineCanvas;
import models.LineType;
import models.Point;
import modes.ModeManager;
import modes.EditMode;
import rasterizers.CanvasRasterizer;
import rasterizers.Rasterizer;
import rasterizers.TrivialRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;
import ui.UIPanel;
import utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class App {

    private final JPanel panel;
    private final Raster raster;
    private final Rasterizer rasterizer;
    private Point pPomocny;
    private final LineCanvas lineCanvas;
    private final CanvasRasterizer canvasRasterizer;
    private boolean dottedMode = false;
    private boolean dashedMode = false;
    private boolean snapMode = false;
    private boolean fillMode = false;
    private int lineWidth = 1;
    private final List<Point> polygonPoints = new ArrayList<>();
    private int movingVertexIndex = -1;
    private static final int VERTEX_HIT_RADIUS = 6;
    private final JLabel mainModeLabel;
    private final JLabel secondaryLabel;
    private final ModeManager modeManager;
    private final UIPanel uiPanel;

    // Color Palette
    JFrame cw;
    JColorChooser cc;
    JPanel colorChooserPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(800, 600).start());
    }

    public void clear(int color) {
        raster.setClearColor(color);
        raster.clear();
    }

    public void present(Graphics graphics) {
        raster.repaint(graphics);
    }

    public void start() {
        clear(0x000000); // Changed to black
        updateStatus();
        panel.repaint();
    }

    public App(int width, int height) {
        JFrame frame = new JFrame();

        frame.setLayout(new BorderLayout());

        frame.setTitle("Delta : " + this.getClass().getName());
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        raster = new RasterBufferedImage(width, height);
        rasterizer = new TrivialRasterizer(raster, Color.white);
        lineCanvas = new LineCanvas();
        canvasRasterizer = new CanvasRasterizer(rasterizer); // Updated constructor
        modeManager = new ModeManager(this);

        panel = new JPanel() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);

        uiPanel = new UIPanel(this);
        frame.add(uiPanel, BorderLayout.WEST);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setPreferredSize(new Dimension(width, 24));
        statusPanel.setBackground(new Color(0xf0f0f0));
        statusPanel.setOpaque(true);

        mainModeLabel = new JLabel();
        mainModeLabel.setBorder(new EmptyBorder(2, 6, 2, 6));
        secondaryLabel = new JLabel();
        secondaryLabel.setBorder(new EmptyBorder(2, 6, 2, 6));

        statusPanel.add(mainModeLabel, BorderLayout.WEST);
        statusPanel.add(secondaryLabel, BorderLayout.EAST);


        frame.add(statusPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        MouseAdapter mouseAdapter = new AppMouseAdapter();
        KeyAdapter keyAdapter = new AppKeyAdapter();
        
        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);
        panel.addKeyListener(keyAdapter);

        updateStatus();
    }

    public String executeCommand(String commandLine) {
        return Cmd.execute(this, commandLine);
    }
    private void openCmd() {
        Window owner = SwingUtilities.getWindowAncestor(panel);
        CommandConsole console = new CommandConsole(owner, this);
        console.setLocationRelativeTo(owner);
        console.setVisible(true);
        // when closed, return focus to panel
        panel.requestFocusInWindow();
    }

    public void openColorChooser() {
        Color newColor = JColorChooser.showDialog(
                panel,
                "Choose Background Color",
                rasterizer.getColor());
        if (newColor != null) {
            setCurrentColor(newColor);
        }
        panel.requestFocusInWindow();
    }

    public void setMode(String modeName) {
        String currentModeName = modeManager.getCurrentMode().getName();
        
        if (currentModeName.equals("Polygon") && !modeName.equalsIgnoreCase("polygon")) {
             finalizePolygon();
        }
        
        if (currentModeName.equals("Fill") && !modeName.equalsIgnoreCase("fill")) {
            fillMode = false;
        }

        modeManager.setMode(modeName);
        
        if (modeName.equalsIgnoreCase("polygon")) {
            polygonPoints.clear();
        }
         if (modeName.equalsIgnoreCase("fill")) {
            fillMode = true;
        }
        
        updateStatus();
        panel.requestFocusInWindow(); // Restore focus to panel
    }

    public void refresh() {
        updateStatus();
        panel.repaint();
    }

    public void setDotted(boolean v) { dottedMode = v; }
    public void toggleDotted() { dottedMode = !dottedMode; }
    public void setDashed(boolean v) { dashedMode = v; }
    public void toggleDashed() { dashedMode = !dashedMode; }
    public void setSnap(boolean v) { snapMode = v; }
    public void toggleSnap() { snapMode = !snapMode; }
    public void setPolygonMode(boolean v) {
        if (v) {
            setMode("polygon");
        } else {
            setMode("line");
        }
    }
    public void finalizePolygonPublic() { finalizePolygon(); }
     public void setFill(boolean v) {
        fillMode = v;
        if (fillMode) {
            setMode("fill");
        } else {
            setMode("line"); // Default to line mode when fill is off
        }
        panel.requestFocusInWindow(); // Restore focus to panel
    }
    public void toggleFill() {
        setFill(!fillMode);
    }
    
    public void clearLines() { 
        lineCanvas.getLines().clear(); 
        lineCanvas.getCircles().clear(); 
        lineCanvas.getFills().clear();
        polygonPoints.clear(); 
    }

    private LineType getLineType() {
        if (isDottedMode()) {
            return LineType.DOTTED;
        } else if (isDashedMode()) {
            return LineType.DASHED;
        } else {
            return LineType.SOLID;
        }
    }

    private void finalizePolygon() {
        if (polygonPoints.size() >= 3) {
            int n = polygonPoints.size();
            for (int i = 0; i < n; i++) {
                Point a = polygonPoints.get(i);
                Point b = polygonPoints.get((i + 1) % n);
                lineCanvas.addLine(new Line(a, b, rasterizer.getColor(), getLineType(), lineWidth));
            }
        }
        polygonPoints.clear();
        redrawCanvas();
        panel.repaint();
    }

    public void updateStatus() {
        String main = "Mode: " + modeManager.getCurrentMode().getName();
        String dotted = dottedMode ? "Dotted: ON" : "Dotted: OFF";
        String dashed = dashedMode ? "Dashed: ON" : "Dashed: OFF";
        String snap = snapMode ? "Snap: ON" : "Snap: OFF";
        mainModeLabel.setText(main);
        secondaryLabel.setText(dotted + "   " + snap + "   " + dashed);
    }

    public boolean samePoint(Point a, Point b) {
        if (a == null || b == null) return false;
        return a.getX() == b.getX() && a.getY() == b.getY();
    }

    public int getVertexIndexNear(Point p) {
        for (int i = 0; i < polygonPoints.size(); i++) {
            Point v = polygonPoints.get(i);
            long dx = (long) v.getX() - p.getX();
            long dy = (long) v.getY() - p.getY();
            if (dx * dx + dy * dy <= (long) VERTEX_HIT_RADIUS * VERTEX_HIT_RADIUS) return i;
        }
        return -1;
    }

    public void setCurrentColor(int color) {
        setCurrentColor(new Color(color));
    }

    public void setCurrentColor(Color color) {
        rasterizer.setColor(color);
        if (modeManager.getCurrentMode() instanceof EditMode) {
            ((EditMode) modeManager.getCurrentMode()).updateSelectedObjectColor(color);
        }
        redrawCanvas();
        panel.repaint();
        panel.requestFocusInWindow();
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        if (modeManager.getCurrentMode() instanceof EditMode) {
            ((EditMode) modeManager.getCurrentMode()).updateSelectedObjectLineWidth(lineWidth);
        }
        redrawCanvas();
        panel.repaint();
        panel.requestFocusInWindow();
    }
    
    public Color getRasterizerColor() {
        return rasterizer.getColor();
    }

    public int getRasterizerLineWidth() {
        return lineWidth;
    }

    public void updateUIPanelProperties(Color color, int lineWidth) {
        uiPanel.setLineWidthSlider(lineWidth);
        uiPanel.updateColorDisplay(color);
    }

    public void resetUIPanelProperties() {
        uiPanel.setLineWidthSlider(getRasterizerLineWidth());
        uiPanel.updateColorDisplay(getRasterizerColor());
    }

    public void redrawCanvas() {
        clear(0x000000); // Changed to black
        canvasRasterizer.rasterize(lineCanvas);
    }

    public void drawPolygonEdges() {
        for (int i = 1; i < polygonPoints.size(); i++) {
            Point a = polygonPoints.get(i - 1);
            Point b = polygonPoints.get(i);
            rasterizer.rasterize(new Line(a, b, getLineType()));
        }
    }

    public void drawPolygonClosingEdge() {
        if (polygonPoints.size() >= 2) {
            Point first = polygonPoints.get(0);
            Point last = polygonPoints.get(polygonPoints.size() - 1);
            rasterizer.rasterize(new Line(last, first, getLineType()));
        }
    }

    public void redrawPolygon() {
        redrawCanvas();
        drawPolygonEdges();
        drawPolygonClosingEdge();
        panel.repaint();
    }

    private class AppMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            modeManager.mousePressed(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            modeManager.mouseReleased(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            modeManager.mouseDragged(e);
        }
    }

    private class AppKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                dottedMode = true;
                updateStatus();
            }
            if (e.getKeyCode() == KeyEvent.VK_ALT) {
                dashedMode = true;
                updateStatus();
            }
            if (e.getKeyCode() == KeyEvent.VK_C) {
                clearLines();
                clear(0x000000); // Changed to black
                panel.repaint();
            }
            if (e.getKeyCode() == KeyEvent.VK_F) {
                toggleFill();
                updateStatus();
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                // open command console dialog
                openCmd();
            }
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                snapMode = true;
                updateStatus();
            }
            if (e.getKeyCode() == KeyEvent.VK_P) {
                setPolygonMode(modeManager.getCurrentMode().getName().equals("Line"));
                updateStatus();
            }
            if (e.getKeyCode() == KeyEvent.VK_B) {
                setMode("box");
                updateStatus();
            }
            if (e.getKeyCode() == KeyEvent.VK_V) {
                setMode("circle");
                updateStatus();
            }
            if (e.getKeyCode() == KeyEvent.VK_E){
                setMode("remove");
                updateStatus();
            }
            if (e.getKeyCode() == KeyEvent.VK_K) {
                openColorChooser();
                updateStatus();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                dottedMode = false;
                updateStatus();
            }
            if (e.getKeyCode() == KeyEvent.VK_ALT) {
                dashedMode = false;
                updateStatus();
            }
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                snapMode = false;
                updateStatus();
            }
        }
    }
    
    // getters for modes
    public Raster getRaster() { return raster; }
    public Rasterizer getRasterizer() { return rasterizer; }
    public LineCanvas getLineCanvas() { return lineCanvas; }
    public CanvasRasterizer getCanvasRasterizer() { return canvasRasterizer; }
    public boolean isDottedMode() { return dottedMode; }
    public boolean isDashedMode() { return dashedMode; }
    public List<Point> getPolygonPoints() { return polygonPoints; }
    public JPanel getPanel() { return panel; }
    public Point getPPomocny() { return pPomocny; }
    public void setPPomocny(Point p) { pPomocny = p; }
    public int getMovingVertexIndex() { return movingVertexIndex; }
    public void setMovingVertexIndex(int i) { movingVertexIndex = i; }
    public boolean isSnapMode() { return snapMode; }
}
