package com.holub.life;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/** Coordinates UI and clock actions against the shared facade. */
public final class LifeMediator {
    private final LifeFacade facade;
    private final Runnable pauseClock;

    public LifeMediator(LifeFacade facade, Runnable pauseClock) {
        this.facade = Objects.requireNonNull(facade);
        this.pauseClock = Objects.requireNonNull(pauseClock);
    }

    public LifeFacade facade() {
        return facade;
    }

    public void tick() {
        facade.tick();
    }

    public void toggle(int x, int y) {
        facade.toggle(x, y);
    }

    public void clear() {
        facade.clear();
    }

    /**
     * A loaded board starts over: the clock halts, the Undo history is emptied, then the board is replaced.
     * The LOAD event that follows makes the view redraw and the statistics and cell ages restart.
     */
    public void load(Storable state) {
        Objects.requireNonNull(state);
        pauseClock.run();
        HistoryManager.clearHistory();
        facade.load(state);
    }

    public void load(InputStream input) throws IOException {
        Storable state = Neighborhood.emptyMemento();
        state.load(input);
        load(state);
    }

    /** The clock halts first so the file holds a single generation, as the original doStore did. */
    public void store(FileFormat format, File file) throws Exception {
        pauseClock.run();
        format.save(file, facade.model());
    }

    public void store(OutputStream output) throws IOException {
        pauseClock.run();
        facade.store(output);
    }

    public void resize(UniverseBuilder builder) {
        builder.width();
        pauseClock.run();
        facade.resize(builder);
    }
}
