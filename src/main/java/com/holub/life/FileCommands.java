package com.holub.life;

public class FileCommands {
    private final Command saveCommand;
    private final Command loadCommand;

    public FileCommands(UniverseModel model) {
        this.saveCommand = new SaveCommand(model);
        this.loadCommand = new LoadCommand(model);
    }

    public Command getSave() { return saveCommand; }
    public Command getLoad() { return loadCommand; }
}