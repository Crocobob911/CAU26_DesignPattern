package com.holub.life;

public class RunCommand implements Command {
    private final int delay;
    private final ClockControl clock;

    public RunCommand(ClockControl clock, int delay) {
        this.clock = clock;
        this.delay = delay;
    }

    /** Halted: save one Undo step and start. Running: only change the speed (see {@link ClockState}). */
    @Override
    public void execute() {
        clock.run(delay);
    }

}
