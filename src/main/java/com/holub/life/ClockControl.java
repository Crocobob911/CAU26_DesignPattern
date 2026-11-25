package com.holub.life;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Context of the E4 State pattern. Holds the current {@link ClockState} and the tick delay,
 * and drives the singleton {@link Clock}. Commands and the speed slider talk to this class only.
 */
public final class ClockControl {
    public static final int MIN_DELAY = 10;
    public static final int MAX_DELAY = 500;
    public static final int DEFAULT_DELAY = 150;

    private final UniverseModel model;
    private final List<Runnable> listeners = new ArrayList<>();
    private ClockState state = ClockState.HALTED;
    private int delay = DEFAULT_DELAY;

    ClockControl(UniverseModel model) {
        this.model = Objects.requireNonNull(model);
    }

    public void run(int delay) {
        state.run(this, checkDelay(delay));
        changed();
    }

    public void halt() {
        state.halt(this);
        changed();
    }

    public void changeSpeed(int delay) {
        state.changeSpeed(this, checkDelay(delay));
        changed();
    }

    public boolean isRunning() {
        return state.isRunning();
    }

    public String stateLabel() {
        return state.label();
    }

    public int delay() {
        return delay;
    }

    /** Called after every Run, Halt or speed change. */
    public void addListener(Runnable listener) {
        listeners.add(Objects.requireNonNull(listener));
    }

    UniverseModel model() {
        return model;
    }

    void startTicking(int delay) {
        this.delay = delay;
        Clock.instance().startTicking(delay);
    }

    void stopTicking() {
        Clock.instance().stop();
    }

    void rememberDelay(int delay) {
        this.delay = delay;
    }

    void changeState(ClockState next) {
        state = Objects.requireNonNull(next);
    }

    private static int checkDelay(int delay) {
        if (delay <= 0)
            throw new IllegalArgumentException("Delay must be positive: " + delay);
        return delay;
    }

    private void changed() {
        for (Runnable listener : List.copyOf(listeners))
            listener.run();
    }
}
