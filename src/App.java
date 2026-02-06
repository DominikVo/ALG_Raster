import models.Line;
import models.LineCanvas;
import models.Point;
import rasterizers.CanvasRasterizer;
import rasterizers.Rasterizer;
import rasterizers.TrivialRasterizer;
import rasters.Raster;
import rasters.RasterBufferedImage;

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
    private Rasterizer rasterizer;
    private MouseAdapter mouseAdapter;
    private KeyAdapter keyAdapter;
    private Point pPomocny;
    private LineCanvas lineCanvas;
    private CanvasRasterizer canvasRasterizer;
    private boolean dottedMode = false;
    private boolean snapMode = false;
    private boolean polygonMode = false;

    // Polygon building state
    private final List<Point> polygonPoints = new ArrayList<>();

    // Status bar components
    private final JPanel statusPanel;
    private final JLabel mainModeLabel;
    private final JLabel secondaryLabel;

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
        clear(0xaaaaaa);
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

        // Create status bar
        statusPanel = new JPanel(new BorderLayout());
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

        rasterizer = new TrivialRasterizer(raster, Color.white);

        createAdapters();
        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);
        panel.addKeyListener(keyAdapter);

        lineCanvas = new LineCanvas();
        canvasRasterizer = new CanvasRasterizer(rasterizer, rasterizer);

        updateStatus();
    }


    private void createAdapters() {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                pPomocny = new Point(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point pPomocny2 = new Point(e.getX(), e.getY());

                if (snapMode && pPomocny != null) {
                    pPomocny2 = snapPoint(pPomocny, pPomocny2);
                }

                if (polygonMode) {
                    // Add vertex(s) to polygon builder
                    if (pPomocny != null && polygonPoints.isEmpty()) {
                        // If first segment, include the press point as starting vertex
                        polygonPoints.add(pPomocny);
                    }
                    polygonPoints.add(pPomocny2);

                    // redraw: background, saved canvas, polygon edges preview
                    clear(0xaaaaaa);
                    canvasRasterizer.rasterize(lineCanvas);

                    // rasterize built polygon edges
                    for (int i = 1; i < polygonPoints.size(); i++) {
                        Point a = polygonPoints.get(i - 1);
                        Point b = polygonPoints.get(i);
                        rasterizer.rasterize(new Line(a, b, dottedMode));
                    }

                    panel.repaint();
                    return;
                }

                // normal line mode
                Line line = new Line(pPomocny, pPomocny2, dottedMode);

                clear(0xaaaaaa);

                lineCanvas.addLine(line);
                canvasRasterizer.rasterize(lineCanvas);

                panel.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point pPomocny2 = new Point(e.getX(), e.getY());

                if (snapMode && pPomocny != null) {
                    pPomocny2 = snapPoint(pPomocny, pPomocny2);
                }

                if (polygonMode) {
                    clear(0xaaaaaa);
                    canvasRasterizer.rasterize(lineCanvas);

                    // rasterize already added polygon edges
                    for (int i = 1; i < polygonPoints.size(); i++) {
                        Point a = polygonPoints.get(i - 1);
                        Point b = polygonPoints.get(i);
                        rasterizer.rasterize(new Line(a, b, dottedMode));
                    }

                    // preview edge from last vertex (or press point) to current mouse
                    Point last = polygonPoints.isEmpty() ? pPomocny : polygonPoints.get(polygonPoints.size() - 1);
                    if (last != null) {
                        rasterizer.rasterize(new Line(last, pPomocny2, dottedMode));
                    }

                    panel.repaint();
                    return;
                }

                // normal free line preview
                Line line = new Line(pPomocny, pPomocny2, dottedMode);

                clear(0xaaaaaa);

                canvasRasterizer.rasterize(lineCanvas);
                rasterizer.rasterize(line);

                panel.repaint();
            }
        };

        keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    dottedMode = true;
                    updateStatus();
                }
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    lineCanvas.getLines().clear();
                    polygonPoints.clear();
                    clear(0xaaaaaa);
                    panel.repaint();
                }
                // Zapíná přichycovací mód
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    snapMode = true;
                    updateStatus();
                }
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    polygonMode = !polygonMode;
                    if (!polygonMode) {
                        polygonMode();
                    } else {
                        polygonPoints.clear();
                    }
                    updateStatus();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    dottedMode = false;
                    updateStatus();
                }
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    snapMode = false;
                    updateStatus();
                }
            }
        };
    }

    private Point snapPoint(Point p1, Point p2) {
        // Získání bodů
        int x1 = p1.getX();
        int y1 = p1.getY();
        int x2 = p2.getX();
        int y2 = p2.getY();

        // Vzdálenost bodů
        int distx = x2 - x1;
        int disty = y2 - y1;

        // Ověření (Pokud jsou souřadnice na sobě)
        if (distx == 0 && disty == 0) return p2;

        // Horizontalní a Vertikální bod
        /*
        Bere opačné souřadnice a získává přesnou vertikální a horizontální souřadnici
        */
        Point horiz = new Point(x2, y1);
        Point vert = new Point(x1, y2);

        // Výpočet diagonalního bodu
        /*
        Vypočítává absulutní, minimální a počítá diagonální bod
         */
        int absDistx = Math.abs(distx);
        int absDisty = Math.abs(disty);
        int min = Math.min(absDistx, absDisty);
        int sx = Integer.signum(distx);
        int sy = Integer.signum(disty);
        Point diag = new Point(x1 + sx * min, y1 + sy * min);

        // Výběr bodů (Kandidáti na připnutí)
        Point[] candidates = new Point[]{horiz, vert, diag};

        // Počítání myšy k nejbližšímu bodu
        Point best = candidates[0];
        long bestDist = dist2(best, p2);
        // Dle vzdálenosti vybírá nejlepšího kandidáta
        for (int i = 1; i < candidates.length; i++) {
            long d = dist2(candidates[i], p2);
            if (d < bestDist) {
                bestDist = d;
                best = candidates[i];
            }
        }
        return best;
    }

    // Výpočet vzdálenosti bodů
    private long dist2(Point a, Point b) {
        long distanx = (long) a.getX() - b.getX();
        long distany = (long) a.getY() - b.getY();
        return distanx * distanx + distany * distany;
    }

    private void polygonMode() {
        if (polygonPoints.size() >= 3) {
            int n = polygonPoints.size();
            for (int i = 0; i < n; i++) {
                Point a = polygonPoints.get(i);
                Point b = polygonPoints.get((i + 1) % n);
                lineCanvas.addLine(new Line(a, b, dottedMode));
            }
        }
        polygonPoints.clear();
        clear(0xaaaaaa);
        canvasRasterizer.rasterize(lineCanvas);
        panel.repaint();
    }

    private void updateStatus() {
        String main = polygonMode ? "Mode: Polygon" : "Mode: Line";
        String dotted = dottedMode ? "Dotted: ON" : "Dotted: OFF";
        String snap = snapMode ? "Snap: ON" : "Snap: OFF";
        mainModeLabel.setText(main);
        secondaryLabel.setText(dotted + "   " + snap);
    }
}
