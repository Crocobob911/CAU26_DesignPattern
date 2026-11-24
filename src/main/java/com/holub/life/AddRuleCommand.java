package com.holub.life;

import java.util.Map;
import java.util.HashMap;

public class AddRuleCommand implements Command {

	private final String ruleName;
	private final String ruleDesc;
	
	AddRuleCommand(String _ruleName, String _ruleDesc){
		ruleName = _ruleName;
		ruleDesc = _ruleDesc;
	}
	
	@Override
	public void execute() {
		BSRule.register(ruleName, ruleDesc);
	}

}
