package modes;

import app.App;
import java.awt.event.MouseEvent;

public abstract class BaseMode implements Mode {
    protected App app;

    public BaseMode(App app) {
        this.app = app;
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}
}
