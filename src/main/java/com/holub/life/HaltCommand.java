package com.holub.life;

public class HaltCommand implements Command {
    private final ClockControl clock;

    public HaltCommand(ClockControl clock) {
        this.clock = clock;
    }

    @Override
    public void execute() {
        clock.halt();
    }
}
