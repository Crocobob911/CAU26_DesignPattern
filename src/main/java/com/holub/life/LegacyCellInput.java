package com.holub.life;

import java.awt.Point;
import java.awt.Rectangle;

/** Translates the original pixel-based click API to board coordinates. */
final class LegacyCellInput {
    private LegacyCellInput() {
    }

    static void toggle(Cell board, Point here, Rectangle surface) {
        int width = board.widthInCells();
        if (width == 0)
            return;

        int pixelsPerCell = surface.width / width;
        if (pixelsPerCell <= 0 || here.x < 0 || here.y < 0)
            return;

        int x = here.x / pixelsPerCell;
        int y = here.y / pixelsPerCell;
        if (x < width && y < width)
            board.setAliveAt(x, y, !board.isAliveAt(x, y));
    }
}
