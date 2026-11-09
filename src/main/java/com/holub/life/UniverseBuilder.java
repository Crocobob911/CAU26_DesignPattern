package com.holub.life;

import java.util.Objects;

/** Builds a square board of blockSize raised to the number of levels. */
public final class UniverseBuilder {
    public static final int MAX_WIDTH = 256;

    private int blockSize = 8;
    private int levels = 2;
    private int cellPixels = 8;
    private LifeRule rule;

    public UniverseBuilder blockSize(int blockSize) {
        if (blockSize < 2 || blockSize > MAX_WIDTH)
            throw new IllegalArgumentException("Block size must be between 2 and " + MAX_WIDTH);
        this.blockSize = blockSize;
        return this;
    }

    public UniverseBuilder levels(int levels) {
        if (levels < 1 || levels > 8)
            throw new IllegalArgumentException("Levels must be between 1 and 8");
        this.levels = levels;
        return this;
    }

    public UniverseBuilder cellPixels(int cellPixels) {
        if (cellPixels < 2 || cellPixels > 32)
            throw new IllegalArgumentException("Cell size must be between 2 and 32 pixels");
        this.cellPixels = cellPixels;
        return this;
    }

    public UniverseBuilder rule(LifeRule rule) {
        this.rule = Objects.requireNonNull(rule);
        return this;
    }

    public int width() {
        int width = 1;
        for (int level = 0; level < levels; level++) {
            if (width > MAX_WIDTH / blockSize)
                throw new IllegalArgumentException("Board width must be at most " + MAX_WIDTH);
            width *= blockSize;
        }
        return width;
    }

    public LifeFacade build() {
        return new LifeFacade(buildModel(LifeRule.CONWAY));
    }

    UniverseModel buildModel(LifeRule selectedRule) {
        int width = width();
        LifeRule effectiveRule = rule == null ? selectedRule : rule;
        Cell cell = new Resident();
        for (int level = 0; level < levels; level++)
            cell = new Neighborhood(blockSize, cell);
        return new UniverseModel((Neighborhood) cell, width, blockSize, levels, cellPixels, effectiveRule);
    }
}
