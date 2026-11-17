package com.holub.life;
import java.util.Map;
import java.util.HashMap;

public class Rule implements LifeRule {

	private static final Map<String, Rule> RULES = new HashMap<>();
	
	// 기존 LifeRule의 Lambda를 통해 정의된 field를 LifeRule Map에 포함시키기위한 block
	static {
		new Rule("CONWAY", LifeRule.CONWAY);
	}
	
	String name = ".";
	String ruleDescript = "B/S";
	
	LifeRule rule;
	
	boolean Birth[];
	boolean Survive[];
	
	public static void createRule(String _name, String ruleDesc) {
		new Rule(_name, ruleDesc);
	}
	
	public static Rule getRule(String _name) {
		return Rule.RULES.get(_name);
	}
	
	Rule(String _name, LifeRule _rule){
		Birth = new boolean[9];
		Survive = new boolean[9];
		
		name = _name;
		rule = _rule;
		ruleDescript = reConstructRuleDesc();
		registerStatic();
	}
	
	Rule(String _name, String ruleDesc){
		Birth = new boolean[9];
		Survive = new boolean[9];
		
		name = _name;
		ruleDescript = ruleDesc;
		int res = constructRule();
		if(res == -1) {
			System.out.println("rule 생성 중 오류가 발생하였습니다. false 반환 객체로 변환합니다. 또한, 규칙이 등록되지 않습니다.");
			rule = (alive,neighbors)->false;
		}
		else {
			rule = (alive, neighbors) -> alive ? Survive[neighbors] : Birth[neighbors];
			registerStatic();
		}
		
	}
	
	/*
	 * register
	 * static field의 Map에 regiter시킴
	 * static field Map의 key와 객체의 이름이달라지는 경우를 방지하기 위해 객체 내에서 등록과정을 진행.
	 */
	
	
	private void registerStatic() {
		RULES.put(name, this);
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
	
	//기존 인터페이스에 핵심동작 위임 - decorator
	@Override
	public boolean nextState(boolean alive, int neighbors) {
		return rule.nextState(alive, neighbors);
	}

}

//Decorator pattern.
//Extended to getData