package ui;

import app.App;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UIPanel extends JPanel {

    private final App app;
    private final JSlider lineWidthSlider;
    private final JPanel currentColorDisplay;

    public UIPanel(App app) {
        this.app = app;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(150, 600));
        setAlignmentX(Component.LEFT_ALIGNMENT);


        add(new JLabel("Modes:"));
        add(Box.createVerticalStrut(5));
        addModeButton("Line", "line");
        addModeButton("Polygon", "polygon");
        addModeButton("Circle", "circle");
        addModeButton("Box", "box");
        addFillModeButton();
        addModeButton("Remove", "remove");
        addModeButton("Edit", "edit");

        add(Box.createVerticalStrut(20));
        add(new JLabel("Line Width:"));
        add(Box.createVerticalStrut(5));

        lineWidthSlider = new JSlider(JSlider.HORIZONTAL, 1, 20, 1);
        lineWidthSlider.setMajorTickSpacing(5);
        lineWidthSlider.setMinorTickSpacing(1);
        lineWidthSlider.setPaintTicks(true);
        lineWidthSlider.setPaintLabels(true);
        lineWidthSlider.addChangeListener(e -> app.setLineWidth(lineWidthSlider.getValue()));
        lineWidthSlider.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lineWidthSlider);


        add(Box.createVerticalStrut(20));
        add(new JLabel("Colors:"));
        add(Box.createVerticalStrut(5));

        currentColorDisplay = new JPanel();
        currentColorDisplay.setPreferredSize(new Dimension(130, 20));
        currentColorDisplay.setMaximumSize(new Dimension(130, 20));
        currentColorDisplay.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        currentColorDisplay.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(currentColorDisplay);
        add(Box.createVerticalStrut(5));


        JPanel colorGrid = new JPanel(new GridLayout(0, 2, 5, 5));
        colorGrid.setMaximumSize(new Dimension(130, 200));
        colorGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        addColorButton(colorGrid, Color.BLACK);
        addColorButton(colorGrid, Color.WHITE);
        addColorButton(colorGrid, Color.RED);
        addColorButton(colorGrid, Color.GREEN);
        addColorButton(colorGrid, Color.BLUE);
        addColorButton(colorGrid, Color.YELLOW);
        addColorButton(colorGrid, Color.CYAN);
        addColorButton(colorGrid, Color.MAGENTA);
        addColorButton(colorGrid, Color.ORANGE);
        addColorButton(colorGrid, Color.PINK);

        add(colorGrid);

        add(Box.createVerticalStrut(10));
        JButton colorChooserBtn = new JButton("More Colors...");
        colorChooserBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorChooserBtn.setMaximumSize(new Dimension(130, 30));
        colorChooserBtn.addActionListener(e -> app.openColorChooser());
        add(colorChooserBtn);

        add(Box.createVerticalGlue());
    }

    public void setLineWidthSlider(int value) {
        lineWidthSlider.setValue(value);
    }

    public void updateColorDisplay(Color color) {
        currentColorDisplay.setBackground(color);
    }

    private void addModeButton(String label, String modeName) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(130, 30));
        btn.addActionListener(e -> app.setMode(modeName));
        add(btn);
        add(Box.createVerticalStrut(5));
    }

    private void addFillModeButton() {
        JButton btn = new JButton("Fill");
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(130, 30));
        btn.addActionListener(e -> app.toggleFill());
        add(btn);
        add(Box.createVerticalStrut(5));
    }

    private void addColorButton(JPanel panel, Color color) {
        JButton btn = new JButton();
        btn.setBackground(color);
        btn.setPreferredSize(new Dimension(30, 30));
        btn.addActionListener(e -> app.setCurrentColor(color));
        panel.add(btn);
    }
}
