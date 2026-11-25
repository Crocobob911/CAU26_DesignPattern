package com.holub.life;

public class ClearCommand implements Command {
    private final UniverseModel model;

    public ClearCommand(UniverseModel model) {
        this.model = model;
    }

    /** Like the original Grid > Clear, the clock keeps running; the cleared board can be undone. */
    @Override
    public void execute() {
        model.clear();
    }
}
