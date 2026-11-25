package com.holub.life;

public class GridCommands {
    private final Command undoCommand;
    private final Command redoCommand;
    private final Command clearCommand;

    public GridCommands(UniverseModel model) {
        this.undoCommand = new UndoCommand(model);
        this.redoCommand = new RedoCommand(model);
        this.clearCommand = new ClearCommand(model);
    }

    public Command getUndo() {
        return undoCommand;
    }

    public Command getRedo() {
        return redoCommand;
    }

    public Command getClear() {
        return clearCommand;
    }
}