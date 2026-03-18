package modes;

import java.awt.event.MouseEvent;

/**
 * Defines the interface for a drawing mode.
 */
public interface Mode {
    void mousePressed(MouseEvent e);
    void mouseReleased(MouseEvent e);
    void mouseDragged(MouseEvent e);
    String getName();
}
