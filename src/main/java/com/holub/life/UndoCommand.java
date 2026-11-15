package com.holub.life;

public class UndoCommand implements Command {
    private final UniverseModel model;

    public UndoCommand(UniverseModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        if(HistoryManager.canUndo()) {
            HistoryManager.pushRedo(new UniverseSnapshot(model.createMemento(),  model.generation()));
            UniverseSnapshot past = HistoryManager.popUndo();
            model.restore(past.getState(), past.getGeneration());
        }
    }

}
