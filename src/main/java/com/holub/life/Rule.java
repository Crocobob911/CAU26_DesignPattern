package com.holub.life;

public class Rule implements LifeRule {

	String name = ".";
	String ruleDescript = "B/S";
	
	LifeRule rule;
	
	boolean Birth[];
	boolean Survive[];
	
	Rule(String _name, LifeRule _rule){
		Birth = new boolean[9];
		Survive = new boolean[9];
		
		name = _name;
		rule = _rule;
		ruleDescript = reConstructRuleDesc();		
	}
	
	Rule(String _name, String ruleDesc){
		Birth = new boolean[9];
		Survive = new boolean[9];
		
		name = _name;
		ruleDescript = ruleDesc;
		constructRule();
		rule = (alive, neighbors) -> alive ? Survive[neighbors] : Birth[neighbors];
	}
	
	/*
	 * reConstructRuleDesc()
	 * 기존 lamda로 정의된 LifeRule을 확장시킬 필요가 있는 경우 사용.
	 * 현재 lamda 미정의시 사용하는 boolean을 확인하는 코드 또한 포함되어있음.
	 * 필요없을경우 지워서 사용가능.
	 */
	private String reConstructRuleDesc() {
		String BirthStr = "B";
		String SurviveStr = "S";
		
		for(int i=0;i<9;i++) {
			if(rule.nextState(false, i)) {
				BirthStr += i;
				Birth[i] = true;
			}else Birth[i] = false;
			
			if(rule.nextState(true,  i)) {
				BirthStr += i;
				Survive[i] = true;
			} else Survive[i] = false;
		}
		
		return BirthStr + "/" + SurviveStr;
	}
	
	
	/*
	 * ConstructRule()
	 * RuleDest(string)으로 정의시, rule을 정의하기위해 boolean table을 초기화.
	 */
	private void constructRule() {
		String[] BandS = ruleDescript.split("/");
		if(BandS.length != 2) {
			System.out.println("형식이 맞지 않습니다.");
			return;
		}
		
		for(int i=1;i<BandS[0].length();i++) 
			Birth[Character.getNumericValue(BandS[0].charAt(i))] = true;
		
		for(int i=1;i<BandS[1].length();i++)
			Survive[Character.getNumericValue(BandS[1].charAt(i))] = true;
		
	}
	
	
	public String getRule() {
		return ruleDescript;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean nextState(boolean alive, int neighbors) {
		// TODO Auto-generated method stub
		return false;
	}

}

//Decorator pattern.
//Extended to getData