package modes;

import app.App;

import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class ModeManager {
    private final Map<String, Mode> modes = new HashMap<>();
    public Mode currentMode;

    public ModeManager(App app) {
        modes.put("line", new LineMode(app));
        modes.put("polygon", new PolygonMode(app));
        modes.put("fill", new FillMode(app));
        modes.put("box", new BoxMode(app));
        modes.put("circle", new CircleMode(app));
        modes.put("remove", new RemoveMode(app));
        modes.put("edit", new EditMode(app));
        currentMode = modes.get("line");
    }

    public void setMode(String modeName) {
        Mode newMode = modes.get(modeName.toLowerCase());
        if (newMode != null) {
            currentMode = newMode;
        }
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    public void mousePressed(MouseEvent e) {
        currentMode.mousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {
        currentMode.mouseReleased(e);
    }

    public void mouseDragged(MouseEvent e) {
        currentMode.mouseDragged(e);
    }
}
