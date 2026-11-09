package com.holub.life;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Observer of {@link LifeFacade} that counts how many generations each cell has stayed alive.
 * Kept outside the model so the Life rules and the board classes do not change for a display feature.
 *
 * <ul>
 * <li>TICK: a surviving cell gets +1, a newborn cell gets 1, a dead cell gets 0.</li>
 * <li>EDIT: a cell turned on by the user gets 1, a cell turned off gets 0; other cells keep their age.</li>
 * <li>CLEAR, LOAD, RESTORE, RESIZE: history is gone, so every live cell starts again at 1.</li>
 * <li>RULE: ages are kept; the board itself did not change.</li>
 * </ul>
 */
public final class CellAgeTracker implements CellAges {
    private final LifeFacade facade;
    private final Consumer<LifeFacade.Event> listener = this::onChange;
    private int width;
    private int[] ages;

    public CellAgeTracker(LifeFacade facade) {
        this.facade = Objects.requireNonNull(facade);
        restart();
        facade.addListener(listener);
    }

    @Override
    public int ageAt(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= width)
            throw new IndexOutOfBoundsException("(" + x + ", " + y + ") is outside the " + width + " x " + width + " board");
        return ages[y * width + x];
    }

    /** Stop following the board, e.g. when the view that owns this tracker is discarded. */
    public void detach() {
        facade.removeListener(listener);
    }

    private void onChange(LifeFacade.Event event) {
        switch (event.change()) {
            case TICK -> facade.visitCells((x, y, alive) -> {
                int i = y * width + x;
                ages[i] = alive ? ages[i] + 1 : 0;
            });
            case EDIT -> facade.visitCells((x, y, alive) -> {
                int i = y * width + x;
                if (!alive)
                    ages[i] = 0;
                else if (ages[i] == 0)
                    ages[i] = 1;
            });
            case CLEAR, LOAD, RESTORE, RESIZE -> restart();
            case RULE -> { }
        }
    }

    private void restart() {
        width = facade.width();
        ages = new int[width * width];
        facade.visitCells((x, y, alive) -> ages[y * width + x] = alive ? 1 : 0);
    }
}
