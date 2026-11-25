package com.holub.life;

public class SetRuleCommand implements Command {
	private final String ruleName;
	private final UniverseModel model;
	
	public SetRuleCommand(UniverseModel _model, String _name) {
		ruleName = _name;
		model = _model;
	}
	
	@Override
	public void execute() {
		model.setRule(BSRule.get(ruleName));
	}
}
