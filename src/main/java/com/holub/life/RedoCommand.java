package com.holub.life;

public class RedoCommand implements Command {
    private final UniverseModel model;

    public RedoCommand(UniverseModel model) {
        this.model = model;
    }

    @Override
    public void execute() {
        if(HistoryManager.canRedo()) {
            HistoryManager.pushUndo(new UniverseSnapshot(model.createMemento(),  model.generation()));
            UniverseSnapshot future = HistoryManager.popRedo();
            model.restore(future.getState(), future.getGeneration());
        }
    }

}
