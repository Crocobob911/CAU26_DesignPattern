package com.holub.life;

import java.awt.Color;
import java.awt.Graphics;

/** Adds the outline of each block and marks blocks that are still changing. */
final class BlockBorderDecorator extends RendererDecorator {
    private final Graphics graphics;
    private final int cellSize;
    private final Color border;
    private final Color active;

    BlockBorderDecorator(CellVisitor inner, Graphics graphics, int cellSize, Color border, Color active) {
        super(inner);
        this.graphics = graphics;
        this.cellSize = cellSize;
        this.border = border;
        this.active = active;
    }

    @Override
    public void leaveNeighborhood(int x, int y, int width, boolean isActive) {
        super.leaveNeighborhood(x, y, width, isActive);
        int left = x * cellSize;
        int top = y * cellSize;
        int pixels = width * cellSize;
        graphics.setColor(border);
        graphics.drawRect(left, top, pixels, pixels);
        if (isActive && pixels > 2) {
            graphics.setColor(active);
            graphics.drawRect(left + 1, top + 1, pixels - 2, pixels - 2);
        }
    }
}
