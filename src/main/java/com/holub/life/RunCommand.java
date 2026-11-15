package com.holub.life;

public class RunCommand implements Command {
    private final int delay;
    private final UniverseModel model;

    public RunCommand(UniverseModel model,int delay) {
        this.model = model;
        this.delay = delay;
    }

    @Override
    public void execute() {
        HistoryManager.saveState(model);
        HistoryManager.setIgnoreSave(true);
        Clock.instance().startTicking(delay);
    }

}
