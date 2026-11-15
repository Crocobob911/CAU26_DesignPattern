package com.holub.life;

public class RunCommand implements Command {
    private final int delay;

    public RunCommand(int delay) {
        this.delay = delay;
    }

    @Override
    public void execute() {
        Clock.instance().startTicking(delay);
    }

}
