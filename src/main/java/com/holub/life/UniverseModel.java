package com.holub.life;

import java.awt.Point;
import java.util.Objects;

/** Owns the current Composite and runs its two-phase generation update. */
final class UniverseModel {
    private final Neighborhood board;
    final int width;
    final int blockSize;
    final int levels;
    final int cellPixels;
    private LifeRule rule = LifeRule.CONWAY;
    private boolean recalculate;
    private long generation;

    UniverseModel(Neighborhood board, int width, int blockSize, int levels, int cellPixels, LifeRule rule) {
        this.board = board;
        this.width = width;
        this.blockSize = blockSize;
        this.levels = levels;
        this.cellPixels = cellPixels;
        setRule(rule);
    }

    void tick() {
        if (recalculate)
            board.activateAll();

        Cell outside = Cell.DUMMY;
        if (board.figureNextState(outside, outside, outside, outside, outside, outside, outside, outside))
            board.transition();

        recalculate = false;
        generation++;
    }

    long generation() {
        return generation;
    }

    LifeRule rule() {
        return rule;
    }

    void setRule(LifeRule rule) {
        Objects.requireNonNull(rule);
        if (this.rule != rule) {
            this.rule = rule;
            board.setRule(rule);
            recalculate = true;
        }
    }

    boolean isAlive(int x, int y) {
        checkCoordinates(x, y);
        return board.isAliveAt(x, y);
    }

    boolean setAlive(int x, int y, boolean alive) {
        checkCoordinates(x, y);
        return board.setAliveAt(x, y, alive);
    }

    private void checkCoordinates(int x, int y) {
        Objects.checkIndex(x, width);
        Objects.checkIndex(y, width);
    }

    void clear() {
        board.clear();
        recalculate = rule != LifeRule.CONWAY;
        generation = 0;
    }

    void visitCells(CellVisitor visitor) {
        board.accept(visitor, 0, 0);
    }

    Storable createMemento() {
        return board.createMemento();
    }

    void restore(Storable state, long generation) {
        board.clear();
        board.transfer(state, new Point(0, 0), Cell.LOAD);
        recalculate = true;
        this.generation = generation;
    }
}
