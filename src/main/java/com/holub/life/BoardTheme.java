package com.holub.life;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Abstract Factory for the look of the board.
 * One theme creates a matching family of drawing parts: the cell painter,
 * the age-color decorator, the grid-line decorator and the block-border decorator,
 * plus the panel background.
 * The panel only knows this interface, so a new theme never changes the panel.
 */
public interface BoardTheme {
    String name();

    Color background();

    /** The innermost renderer: fills each cell with the live or dead color. */
    CellVisitor createCellPainter(Graphics graphics, int cellSize);

    /** Repaints live cells over {@code inner} with a color that changes with their age. */
    CellVisitor createAgeColors(CellVisitor inner, Graphics graphics, int cellSize, CellAges ages);

    /** Draws the line on the top and left of every cell over {@code inner}. */
    CellVisitor createGridLines(CellVisitor inner, Graphics graphics, int cellSize);

    /** Draws block outlines and the active-block marker over {@code inner}. */
    CellVisitor createBlockBorders(CellVisitor inner, Graphics graphics, int cellSize);
}
