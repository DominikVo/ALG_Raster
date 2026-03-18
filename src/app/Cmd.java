package app;

import java.util.function.Consumer;

public class Cmd {
    // Simplified static command executor. Returns a short result message or null for OK.

    public static String execute(App app, String commandLine) {
        if (commandLine == null) return "Empty command";
        String line = commandLine.trim();
        if (line.isEmpty()) return "Empty command";

        String[] parts = line.split("\\s+");
        String cmd = parts[0].toLowerCase();

        try {
            switch (cmd) {
                case "help":
                    return "Commands: help, clear [hex], clearLines|c, dotted on|off|toggle, snap on|off|toggle, polygon on|off|close, fill on|off|toggle, echo <text>, color <hex>";
                case "echo":
                    return line.substring(Math.min(line.length(), 5)).trim();
                case "clear":
                    return handleClear(app, parts);
                case "clearlines":
                case "c":
                    app.clearLines();
                    app.refresh();
                    return "Lines cleared";
                case "dotted":
                    return handleBooleanOption(parts, app::setDotted, app::toggleDotted, app, "Dotted set");
                case "dashed":
                    return handleBooleanOption(parts, app::setDashed, app::toggleDashed, app, "Dashed set");
                case "snap":
                    return handleBooleanOption(parts, app::setSnap, app::toggleSnap, app, "Snap set");
                case "polygon":
                    return handlePolygon(app, parts);
                case "fill":
                    return handleBooleanOption(parts, app::setFill, app::toggleFill, app, "Fill updated");
                case "color":
                    return handleColor(app, parts);
                default:
                    return "Unknown command: " + cmd;
            }
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        }
    }

    private static String handleClear(App app, String[] parts) {
        int color = 0xaaaaaa; // default
        if (parts.length >= 2) {
            color = parseColor(parts[1]);
        }
        app.clear(color);
        app.refresh();
        return "Cleared";
    }

    private static String handleColor(App app, String[] parts) {
        if (parts.length < 2) return "Usage: color <hex|int>";
        int color = parseColor(parts[1]);
        app.setCurrentColor(color);
        return "Color set";
    }

    private static int parseColor(String arg) {
        if (arg.startsWith("0x") || arg.startsWith("#")) {
            arg = arg.replaceFirst("#", "0x");
            return Integer.parseUnsignedInt(arg.replaceFirst("0x", ""), 16);
        } else {
            return Integer.parseInt(arg);
        }
    }

    private static String handleBooleanOption(String[] parts, Consumer<Boolean> setter, Runnable toggler, App app, String successMsg) {
        if (parts.length < 2) return "Usage: <cmd> on|off|toggle";
        String arg = parts[1];
        if (arg.equalsIgnoreCase("on")) setter.accept(true);
        else if (arg.equalsIgnoreCase("off")) setter.accept(false);
        else if (arg.equalsIgnoreCase("toggle")) toggler.run();
        else return "Unknown argument";
        
        app.refresh();
        return successMsg;
    }

    private static String handlePolygon(App app, String[] parts) {
        if (parts.length < 2) return "Usage: polygon on|off|close";
        String arg = parts[1];
        if (arg.equalsIgnoreCase("on")) app.setPolygonMode(true);
        else if (arg.equalsIgnoreCase("off")) { app.setPolygonMode(false); app.finalizePolygonPublic(); }
        else if (arg.equalsIgnoreCase("close")) { app.finalizePolygonPublic(); app.setPolygonMode(false); }
        else return "Unknown arg for polygon";
        app.refresh();
        return "Polygon updated";
    }
}
