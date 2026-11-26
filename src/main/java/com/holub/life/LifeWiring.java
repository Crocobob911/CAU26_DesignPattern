package com.holub.life;

/**
 * Composition root: connects the model, the Mediator, the command registries and the clock exactly as
 * the application uses them. {@link Life} adds the Swing window on top; integration tests use it as is.
 */
record LifeWiring(UniverseModel model, LifeMediator mediator, ClockCommands clockCommands,
                  FileCommands fileCommands, GridCommands gridCommands, RuleCommands ruleCommands,
                  StampCommands stampCommands) {

    static LifeWiring create() {
        UniverseModel model = new UniverseBuilder().buildModel(LifeRule.CONWAY);
        ClockCommands clockCommands = new ClockCommands(model);
        // The Mediator pauses through the Halt command so the E4 state and the Undo bypass stay in step.
        LifeMediator mediator = new LifeMediator(new LifeFacade(model), () -> clockCommands.getHalt().execute());
        LifeWiring wiring = new LifeWiring(model, mediator, clockCommands, new FileCommands(mediator),
                new GridCommands(model), new RuleCommands(model), new StampCommands(mediator));
        Clock.instance().addClockListener(mediator::tick);
        return wiring;
    }

    LifeFacade facade() {
        return mediator.facade();
    }
}
