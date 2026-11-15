package com.holub.life;

public class GridCommands {
    private final Command undoCommand;
    private final Command redoCommand;

    public GridCommands(UniverseModel model) {
        this.undoCommand = new UndoCommand(model);
        this.redoCommand = new RedoCommand(model);
    }

    public Command getUndo() {
        return undoCommand;
    }

    public Command getRedo() {
        return redoCommand;
    }
}