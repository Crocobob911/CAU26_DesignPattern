package com.holub.life;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;

public class BSRule implements LifeRule {
	private static final Map<String, BSRule> RULES = new LinkedHashMap<>();
	
	// 기존 LifeRule의 Lambda를 통해 정의된 field를 LifeRule Map에 포함시키기위한 block
	static {
		BSRule.register("CONWAY", "B3/S23");
		BSRule.register("HighLife", "B36/S23");
		BSRule.register("Seeds", "B2/S");
		BSRule.register("Day&Night", "B3678/S34678");
	}
	
	private String name = ".";
	private String ruleDescript = "B/S";
	
	
	boolean Birth[];
	boolean Survive[];

	/*
	 * register
	 * static field의 Map에 regiter시킴
	 * static field Map의 key와 객체의 이름이달라지는 경우를 방지하기 위해 객체 내에서 등록과정을 진행.
	 */
	public static void register(String _name, String ruleDesc) {
		BSRule.RULES.put(_name , new BSRule(_name, ruleDesc));
	}
	
	public static BSRule get(String _name) {
		return BSRule.RULES.get(_name);
	}
	
	public static Iterator<String> getKeys() {
		return BSRule.RULES.keySet().iterator();
	}
	
	BSRule(String _name, String ruleDesc){
		Birth = new boolean[9];
		Survive = new boolean[9];
		
		name = _name;
		ruleDescript = ruleDesc;
		try { 
			constructRule();
		}catch(Exception e){
			throw new RuntimeException("rule 생성 중 오류가 발생하였습니다. 규칙이 등록되지 않습니다.");
		}
	}
	
	
	/*
	 * ConstructRule()
	 * RuleDest(string)으로 정의시, rule을 정의하기위해 boolean table을 초기화.
	 */
	private void constructRule() {
		
		String[] BandS = ruleDescript.split("/", -1);
		if(BandS.length != 2) {
			throw new IllegalArgumentException("형식이 맞지 않습니다.");
		}
		if(BandS[0].length() < 1 || BandS[0].charAt(0) != 'B'
				|| BandS[1].length()<1 || BandS[1].charAt(0)!= 'S') {
			throw new IllegalArgumentException("형식이 맞지 않습니다.");
		}
		
		
		for(int i=1;i<BandS[0].length();i++) {
			int current = Character.getNumericValue(BandS[0].charAt(i));
			if(current<0 || current>8) {
				throw new IllegalArgumentException("입력범위 초과 혹은 잘못된 형식입니다.");
			}
			Birth[current] = true;
		}		
		
		for(int i=1;i<BandS[1].length();i++) {
			int current = Character.getNumericValue(BandS[1].charAt(i));
			if(current<0 || current>8) {
				throw new IllegalArgumentException("입력범위 초과 혹은 잘못된 형식입니다.");
			}
			Survive[current] = true;
		}		
		
		
	}
	
	
	public String notation() {
		return ruleDescript;
	}
	
	public String name() {
		return name;
	}
	
	//nestState의 작동방식을 boolean table로 일반화
	@Override
	public boolean nextState(boolean alive, int neighbors) {
		return alive ? Survive[neighbors] : Birth[neighbors];
	}
}
