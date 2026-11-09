package com.holub.life;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * The innermost board renderer: fills each cell with the live or dead color.
 * Lines and borders are added by decorators; colors come from a {@link BoardTheme}.
 */
final class CellRenderer implements CellVisitor {
    private final Graphics graphics;
    private final int cellSize;
    private final Rectangle clip;
    private final Color live;
    private final Color dead;

    CellRenderer(Graphics graphics, int cellSize, Color live, Color dead) {
        this.graphics = graphics;
        this.cellSize = cellSize;
        this.clip = graphics.getClipBounds();
        this.live = live;
        this.dead = dead;
    }

    @Override
    public boolean enterNeighborhood(int x, int y, int width) {
        int pixels = width * cellSize;
        return clip == null || clip.intersects(x * cellSize, y * cellSize, pixels + 1, pixels + 1);
    }

    @Override
    public void visitResident(int x, int y, boolean alive) {
        graphics.setColor(alive ? live : dead);
        graphics.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
    }
}
