package utils;

import models.Point;

public class Utils {
    public static Point snapPoint(Point p1, Point p2, boolean forceSquare) {
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

        // Pokud je mode Box tak to přinutí pouze diagonální snap
        if (forceSquare) {
            int side = Math.max(Math.abs(distx), Math.abs(disty));
            return new Point(p1.getX() + (int) Math.signum(distx) * side, p1.getY() + (int) Math.signum(disty) * side);
        }

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

        // Počítání myšle k nejbližšímu bodu
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

    public static Point snapPoint(Point p1, Point p2) {
        return snapPoint(p1, p2, false);
    }

    // Výpočet vzdálenosti bodů
    public static long dist2(Point a, Point b) {
        long distanx = (long) a.getX() - b.getX();
        long distany = (long) a.getY() - b.getY();
        return distanx * distanx + distany * distany;
    }

}
