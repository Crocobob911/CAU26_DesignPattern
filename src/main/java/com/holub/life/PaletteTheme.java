package com.holub.life;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Objects;

/** Shared factory code for themes whose parts differ only by color. */
abstract class PaletteTheme implements BoardTheme {
    private final String name;
    private final Color background;
    private final Color live;
    private final Color dead;
    private final Color gridLine;
    private final Color blockBorder;
    private final Color activeBlock;

    PaletteTheme(String name, Color background, Color live, Color dead,
            Color gridLine, Color blockBorder, Color activeBlock) {
        this.name = Objects.requireNonNull(name);
        this.background = Objects.requireNonNull(background);
        this.live = Objects.requireNonNull(live);
        this.dead = Objects.requireNonNull(dead);
        this.gridLine = Objects.requireNonNull(gridLine);
        this.blockBorder = Objects.requireNonNull(blockBorder);
        this.activeBlock = Objects.requireNonNull(activeBlock);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Color background() {
        return background;
    }

    @Override
    public CellVisitor createCellPainter(Graphics graphics, int cellSize) {
        return new CellRenderer(graphics, cellSize, live, dead);
    }

    @Override
    public CellVisitor createGridLines(CellVisitor inner, Graphics graphics, int cellSize) {
        return new GridLineDecorator(inner, graphics, cellSize, gridLine);
    }

    @Override
    public CellVisitor createBlockBorders(CellVisitor inner, Graphics graphics, int cellSize) {
        return new BlockBorderDecorator(inner, graphics, cellSize, blockBorder, activeBlock);
    }
}
