package com.holub.life;

public class FileCommands {
    private final Command saveCommand;
    private final Command loadCommand;

    public FileCommands(LifeMediator mediator) {
        this.saveCommand = new SaveCommand(mediator);
        this.loadCommand = new LoadCommand(mediator);
    }

    public Command getSave() { return saveCommand; }
    public Command getLoad() { return loadCommand; }
}