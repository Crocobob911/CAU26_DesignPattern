package com.holub.life;

/**
 * E4 State pattern: what Run, Halt and a speed change mean depends on whether the clock is running.
 * {@link ClockControl} is the context and only forwards the request to its current state.
 */
interface ClockState {
    ClockState HALTED = new HaltedState();
    ClockState RUNNING = new RunningState();

    void run(ClockControl clock, int delay);

    void halt(ClockControl clock);

    void changeSpeed(ClockControl clock, int delay);

    boolean isRunning();

    String label();
}
