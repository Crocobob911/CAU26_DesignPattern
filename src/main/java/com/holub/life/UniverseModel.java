package com.holub.life;

import java.awt.Point;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Owns the current Composite and runs its two-phase generation update. */
final class UniverseModel {
    private Neighborhood board;
    private final List<Consumer<LifeFacade.Change>> listeners = new ArrayList<>();
    int width;
    int blockSize;
    int levels;
    int cellPixels;
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
        HistoryManager.saveState(this);
        if (recalculate)
            board.activateAll();

        Cell outside = Cell.DUMMY;
        if (board.figureNextState(outside, outside, outside, outside, outside, outside, outside, outside))
            board.transition();

        recalculate = false;
        generation++;
        publish(LifeFacade.Change.TICK);
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
        publish(LifeFacade.Change.RULE);
    }

    boolean isAlive(int x, int y) {
        checkCoordinates(x, y);
        return board.isAliveAt(x, y);
    }

    boolean setAlive(int x, int y, boolean alive) {
        checkCoordinates(x, y);
        if (board.isAliveAt(x, y) == alive)
            return false;
        HistoryManager.saveState(this);
        board.setAliveAt(x, y, alive);
        publish(LifeFacade.Change.EDIT);
        return true;
    }

    private void checkCoordinates(int x, int y) {
        Objects.checkIndex(x, width);
        Objects.checkIndex(y, width);
    }

    void clear() {
        HistoryManager.saveState(this);
        board.clear();
        recalculate = rule != LifeRule.CONWAY;
        generation = 0;
        publish(LifeFacade.Change.CLEAR);
    }

    void visitCells(CellVisitor visitor) {
        board.accept(visitor, 0, 0);
    }

    Storable createMemento() {
        return board.createMemento();
    }

    void restore(Storable state, long generation) {
        restore(state, generation, LifeFacade.Change.RESTORE);
    }

    void restore(Storable state, long generation, LifeFacade.Change change) {
        board.clear();
        board.transfer(state, new Point(0, 0), Cell.LOAD);
        recalculate = true;
        this.generation = generation;
        publish(change);
    }

    void resize(UniverseBuilder builder) {
        UniverseModel replacement = builder.buildModel(rule);
        board = replacement.board;
        width = replacement.width;
        blockSize = replacement.blockSize;
        levels = replacement.levels;
        cellPixels = replacement.cellPixels;
        rule = replacement.rule;
        recalculate = replacement.recalculate;
        generation = 0;
        HistoryManager.clearHistory();
        HistoryManager.setIgnoreSave(false);
        publish(LifeFacade.Change.RESIZE);
    }

    void addListener(Consumer<LifeFacade.Change> listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    private void publish(LifeFacade.Change change) {
        for (Consumer<LifeFacade.Change> listener : List.copyOf(listeners))
            listener.accept(change);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return width;
    }
}
