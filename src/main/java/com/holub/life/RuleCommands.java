package com.holub.life;
//
import java.util.Map;
import java.util.LinkedHashMap;

public class RuleCommands {
	
	private final UniverseModel model;
	private final Map<String, Command> commandRegistry;
	
	public RuleCommands(UniverseModel _model) {
		model = _model;
		commandRegistry = new LinkedHashMap<String,Command>();
		
		for (BSRule rule : BSRule.all())
			putRegistry(rule.name());
	}
	
	public void putRegistry(String ruleName) {
		commandRegistry.put(ruleName, new SetRuleCommand(model, ruleName));
	}
	
	public Command setRule(String ruleName) {
		if(BSRule.get(ruleName) != null
				&& commandRegistry.get(ruleName)==null)
			putRegistry(ruleName);
		
		//return new SetRuleCommand(model, ruleName);
		return commandRegistry.get(ruleName);
	}
	
	
	public Command addRule(String ruleName, String ruleDesc) {
		return new AddRuleCommand(ruleName, ruleDesc);
	}
}
