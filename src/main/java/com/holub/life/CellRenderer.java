package com.holub.life;

import com.holub.ui.Colors;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

/** Draws a Composite board without exposing graphics to the model. */
final class CellRenderer implements CellVisitor {
    private final Graphics graphics;
    private final int cellSize;
    private final Rectangle clip;

    CellRenderer(Graphics graphics, int cellSize) {
        this.graphics = graphics;
        this.cellSize = cellSize;
        this.clip = graphics.getClipBounds();
    }

    @Override
    public boolean enterNeighborhood(int x, int y, int width) {
        int pixels = width * cellSize;
        return clip == null || clip.intersects(x * cellSize, y * cellSize, pixels + 1, pixels + 1);
    }

    @Override
    public void visitResident(int x, int y, boolean alive) {
        int left = x * cellSize;
        int top = y * cellSize;
        graphics.setColor(alive ? Color.RED : Colors.LIGHT_YELLOW);
        graphics.fillRect(left + 1, top + 1, cellSize - 1, cellSize - 1);
        graphics.setColor(Colors.DARK_YELLOW);
        graphics.drawLine(left, top, left, top + cellSize);
        graphics.drawLine(left, top, left + cellSize, top);
    }

    @Override
    public void leaveNeighborhood(int x, int y, int width, boolean active) {
        int left = x * cellSize;
        int top = y * cellSize;
        int pixels = width * cellSize;
        graphics.setColor(Colors.LIGHT_ORANGE);
        graphics.drawRect(left, top, pixels, pixels);
        if (active && pixels > 2) {
            graphics.setColor(Color.BLUE);
            graphics.drawRect(left + 1, top + 1, pixels - 2, pixels - 2);
        }
    }
}
