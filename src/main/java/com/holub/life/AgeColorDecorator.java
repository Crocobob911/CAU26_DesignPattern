package com.holub.life;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Objects;

/**
 * Repaints live cells with a color between {@code young} and {@code old} by age.
 * Goes right above the cell painter so grid lines and block borders are still drawn on top.
 */
final class AgeColorDecorator extends RendererDecorator {
    /** A cell this old or older gets the {@code old} color. */
    static final int FULL_AGE = 10;

    private final Graphics graphics;
    private final int cellSize;
    private final CellAges ages;
    private final Color young;
    private final Color old;

    AgeColorDecorator(CellVisitor inner, Graphics graphics, int cellSize, CellAges ages, Color young, Color old) {
        super(inner);
        this.graphics = graphics;
        this.cellSize = cellSize;
        this.ages = Objects.requireNonNull(ages);
        this.young = Objects.requireNonNull(young);
        this.old = Objects.requireNonNull(old);
    }

    @Override
    public void visitResident(int x, int y, boolean alive) {
        super.visitResident(x, y, alive);
        if (!alive)
            return;
        graphics.setColor(colorFor(ages.ageAt(x, y), young, old));
        graphics.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
    }

    /** Linear blend: age 1 (or less) is {@code young}, {@link #FULL_AGE} and above is {@code old}. */
    static Color colorFor(int age, Color young, Color old) {
        double t = Math.min(Math.max(age - 1, 0), FULL_AGE - 1) / (double) (FULL_AGE - 1);
        return new Color(
                (int) Math.round(young.getRed() + (old.getRed() - young.getRed()) * t),
                (int) Math.round(young.getGreen() + (old.getGreen() - young.getGreen()) * t),
                (int) Math.round(young.getBlue() + (old.getBlue() - young.getBlue()) * t));
    }
}
