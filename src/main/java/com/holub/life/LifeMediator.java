package com.holub.life;

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

    public void load(InputStream input) throws IOException {
        pauseClock.run();
        facade.load(input);
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
