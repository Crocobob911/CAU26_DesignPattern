package com.holub.life;

import java.awt.Color;
import java.awt.Graphics;

/** Adds the line on the top and left of every cell, drawn after the cell is filled. */
final class GridLineDecorator extends RendererDecorator {
    private final Graphics graphics;
    private final int cellSize;
    private final Color color;

    GridLineDecorator(CellVisitor inner, Graphics graphics, int cellSize, Color color) {
        super(inner);
        this.graphics = graphics;
        this.cellSize = cellSize;
        this.color = color;
    }

    @Override
    public void visitResident(int x, int y, boolean alive) {
        super.visitResident(x, y, alive);
        int left = x * cellSize;
        int top = y * cellSize;
        graphics.setColor(color);
        graphics.drawLine(left, top, left, top + cellSize);
        graphics.drawLine(left, top, left + cellSize, top);
    }
}
