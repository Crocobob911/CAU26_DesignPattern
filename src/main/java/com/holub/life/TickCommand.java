package com.holub.life;

public class TickCommand implements Command {
    @Override
    public void execute() {
        Clock.instance().tick();
    }
}
