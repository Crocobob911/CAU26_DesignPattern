package com.holub.life;

public class HaltCommand implements Command {
    @Override
    public void execute() {
        Clock.instance().stop();
    }
}
