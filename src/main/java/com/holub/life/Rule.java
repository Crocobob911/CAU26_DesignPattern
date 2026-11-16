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
		int res = constructRule();
		if(res == -1) {
			System.out.println("rule 생성 중 오류가 발생하였습니다. false 반환 객체로 변환합니다.");
			rule = (alive,neighbors)->false;
		}
		else rule = (alive, neighbors) -> alive ? Survive[neighbors] : Birth[neighbors];
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
				SurviveStr += i;
				Survive[i] = true;
			} else Survive[i] = false;
		}
		
		return BirthStr + "/" + SurviveStr;
	}
	
	
	/*
	 * ConstructRule()
	 * RuleDest(string)으로 정의시, rule을 정의하기위해 boolean table을 초기화.
	 */
	private int constructRule() {
		String[] BandS = ruleDescript.split("/", -1);
		if(BandS.length != 2) {
			System.out.println("형식이 맞지 않습니다.");
			return -1;
		}
		if(BandS[0].length() < 1 || BandS[0].charAt(0) != 'B'
				|| BandS[1].length()<1 || BandS[1].charAt(0)!= 'S') {
			System.out.println("형식이 맞지 않습니다.");
			return -1;
		}
		
		
		for(int i=1;i<BandS[0].length();i++) {
			int current = Character.getNumericValue(BandS[0].charAt(i));
			if(current<0 || current>8) {
				System.out.println("입력범위 초과 혹은 잘못된 형식입니다.");
				return -1;
			}
			Birth[current] = true;
		}		
		
		for(int i=1;i<BandS[1].length();i++) {
			int current = Character.getNumericValue(BandS[1].charAt(i));
			if(current<0 || current>8) {
				System.out.println("입력범위 초과 혹은 잘못된 형식입니다.");
				return -1;
			}
			Survive[current] = true;
		}		
		
		return 1;
		
	}
	
	
	public String getRule() {
		return ruleDescript;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public boolean nextState(boolean alive, int neighbors) {
		return rule.nextState(alive, neighbors);
	}

}

//Decorator pattern.
//Extended to getData