# ALG_Rastr

Tento projekt je Java Swing aplikace pro kreslení základních 2D primitiv do rastrového bufferu. Implementuje vlastní rasterizéry bez použití standardních grafických funkcí jako `Graphics.drawLine`. Aplikace pracuje s rastrovým buffrem a umožňuje náhled objektů během kreslení.

## Hlavní funkce

- **Kreslení primitiv**: úsečky, kružnice, obdélníky, polygony a výplň oblastí
- **Styly čar**: tečkované (dotted) a čárkované (dashed) čáry
- **Práce s objekty**: editace (přesun, změna barvy a tloušťky), mazání
- **Náhled**: live preview vykreslování během tahu myší
- **Přichytávání**: snap na osy a diagonály
- **Barevná paleta**: předvolené barvy + výběr vlastní barvy
- **Příkazová konzole**: pro pokročilé operace

## Režimy kreslení

### Line Mode (Úsečka)
Kreslení přímých čar. Při tahu myší se zobrazuje náhled. Podporuje tečkované a čárkované čáry.

### Circle Mode (Kružnice)
Kreslení kružnic zadáním středu a poloměru. Náhled se aktualizuje během tahu myší.

### Box Mode (Obdélník)
Kreslení obdélníků. S klávesou Shift vytvoří čtverce.

### Polygon Mode (Polygon)
Kreslení mnohostěnů postupným klikáním na vrcholy. Dvojklik uzavře polygon. Vrcholy lze pohybovat během kreslení.

### Fill Mode (Výplň)
Vyplňování oblasti flood-fill algoritmem. Kliknutí na pixely stejné barvy je vyplní zvolenou barvou.

### Remove Mode (Smazání)
Kliknutí na objekty je odstraní z plátna.

### Edit Mode (Editace)
Výběr a úprava existujících objektů - přesun, změna barvy a tloušťky čáry.

## Ovládání

- **Myš - kliknutí a tažení**: Kreslení objektů v aktuálním režimu
- **Myš - polygon režim**: Klikáním se přidávají vrcholy polygonu
- **Ctrl**: Tečkovaná čára
- **Alt**: Čárkovaná čára
- **Shift**: Přichytávání na osy a 45° úhly; u Box režimu vynucuje čtverce
- **C**: Smazání všech objektů
- **Esc**: Otevření příkazové konzole (příkazy: `help`, `line`, `circle`, `polygon`, `box`, `fill`, `remove`, `edit`)

## Architektura

### Složka `models`
Datové modely reprezentující kreslené objekty (Line, Circle, Point) a canvas (LineCanvas). Obsahuje také LineType pro definici stylu čáry.

### Složka `modes`
Implementace jednotlivých režimů kreslení. Každý režim dědí z `BaseMode` a implementuje rozhraní `Mode`. `ModeManager` spravuje přepínání mezi režimy.

### Složka `rasterizers`
Jádro aplikace - vlastní rasterizéry bez standardních grafických funkcí:
- **TrivialRasterizer**: Implementuje algoritmy pro rasterizaci čar a kružnic
- **CanvasRasterizer**: Rasterizuje všechny objekty na canvasu

### Složka `rasters`
Abstrakce rastrového bufferu:
- **Raster**: Rozhraní pro práci s pixely
- **RasterBufferedImage**: Konkrétní implementace s BufferedImage

### Složka `ui`
Uživatelské rozhraní - panel s tlačítky režimů, výběrem barvy a nastavením tloušťky čáry.

### Složka `utils`
Pomocné funkce - zejména `snapPoint()` pro přichytávání na osy a úhly.

## Technické detaily

- **Rasterizace čar**: Triviální algoritmus s interpolací
- **Rasterizace kružnic**: Midpoint circle algorithm
- **Tečkované/čárkované čáry**: Interval-based skipping pixelů
- **Flood fill**: BFS algoritmus pro vyplňování oblastí
- **Snap funkce**: Výběr nejbližšího kandidáta z horizontální, vertikální a diagonální linky

