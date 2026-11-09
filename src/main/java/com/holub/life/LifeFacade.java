package com.holub.life;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Stable entry point for extensions. Use it from one owning thread (the EDT in the Swing app).
 * Coordinates are zero-based, with x as the column and y as the row.
 */
public final class LifeFacade {
    public enum Change { TICK, EDIT, CLEAR, RESTORE, LOAD, RULE, RESIZE }

    public record Event(Change change, long generation) {}

    /** An opaque board snapshot for Undo. The current rule is independent of the snapshot. */
    public static final class Snapshot {
        private final int width;
        private final long generation;
        private final Storable state;

        private Snapshot(int width, long generation, Storable state) {
            this.width = width;
            this.generation = generation;
            this.state = state;
        }
    }

    private final List<Consumer<Event>> listeners = new ArrayList<>();
    private UniverseModel model;

    LifeFacade(UniverseModel model) {
        this.model = model;
    }

    public int width() {
        return model.width;
    }

    public long generation() {
        return model.generation();
    }

    public boolean isAlive(int x, int y) {
        return model.isAlive(x, y);
    }

    public void tick() {
        model.tick();
        publish(Change.TICK);
    }

    public void toggle(int x, int y) {
        setAlive(x, y, !isAlive(x, y));
    }

    public void setAlive(int x, int y, boolean alive) {
        if (model.setAlive(x, y, alive))
            publish(Change.EDIT);
    }

    public void clear() {
        model.clear();
        publish(Change.CLEAR);
    }

    public Snapshot createSnapshot() {
        return new Snapshot(width(), generation(), model.createMemento());
    }

    public void restore(Snapshot snapshot) {
        if (snapshot.width != width())
            throw new IllegalArgumentException("Snapshot width does not match the current board");
        model.restore(snapshot.state, snapshot.generation);
        publish(Change.RESTORE);
    }

    public void setRule(LifeRule rule) {
        model.setRule(rule);
        publish(Change.RULE);
    }

    public void visitCells(CellVisitor visitor) {
        model.visitCells(Objects.requireNonNull(visitor));
    }

    /** Subscribe to committed changes, including ticks on a stable board. */
    public void addListener(Consumer<Event> listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    public void removeListener(Consumer<Event> listener) {
        listeners.remove(listener);
    }

    /** Load the original .life format into the current board. The caller owns the stream. */
    public void load(InputStream input) throws IOException {
        Storable state = Neighborhood.emptyMemento();
        state.load(input);
        model.restore(state, 0);
        publish(Change.LOAD);
    }

    /** Store live coordinates in the original .life format. The caller owns the stream. */
    public void store(OutputStream output) throws IOException {
        model.createMemento().flush(output);
    }

    void resize(UniverseBuilder builder) {
        model = builder.buildModel(model.rule());
        publish(Change.RESIZE);
    }

    int blockSize() {
        return model.blockSize;
    }

    int levels() {
        return model.levels;
    }

    int cellPixels() {
        return model.cellPixels;
    }

    private void publish(Change change) {
        Event event = new Event(change, generation());
        for (Consumer<Event> listener : List.copyOf(listeners))
            listener.accept(event);
    }
}
