# ALG_Rastr

Tento projekt je Java Swing aplikace pro kreslení základních 2D primitiv do rastrového bufferu. Implementuje vlastní rasterizéry bez použití standardních grafických funkcí jako `Graphics.drawLine`.

## Funkce

- Kreslení úseček, kružnic, obdélníků, polygonů a výplň oblastí
- Náhled během kreslení
- Tečkované a čárkované čáry (Ctrl/Alt)
- Šířka čáry
- Editace objektů (posun, změna barvy/tloušťky)
- Mazání objektů
- Přichytávání na osy (Shift)
- Command console (ESC)

## Ovládání

- **Myš**: Kreslení kliknutím a tažením, v režimu Polygon klikáním pro vrcholy.
- **Ctrl**: Tečkovaná čára (Dotted).
- **Alt**: Čárkovaná čára (Dashed).
- **Shift**: Přichytávání (Snap) na osy a úhly.
- **C**: Smazat vše (Clear).
- **Esc**: Otevření příkazové konzole (zkuste `help`).

