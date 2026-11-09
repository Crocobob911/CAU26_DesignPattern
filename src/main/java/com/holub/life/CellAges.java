package com.holub.life;

/** Read-only view of how long each cell has been alive. */
@FunctionalInterface
public interface CellAges {
    /** 0 for a dead cell, 1 for a cell alive in this generation only, then +1 for each generation it survives. */
    int ageAt(int x, int y);
}
