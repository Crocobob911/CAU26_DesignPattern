package com.holub.life;

public class Pattern {
	
	private int width;
	private int height;
	private boolean[][] pattern;
	
	Pattern(boolean[][] _pattern){
		pattern = _pattern;
		width = pattern[0].length;
		height = pattern.length;
		
		for(boolean[] w_list:pattern) {
			if(w_list.length != width) {
				 System.out.println("크기가 맞지 않습니다. maximum을 기준으로 하여 다시 생성합니다.");
			}
		}
	}
	
	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}
	
	public boolean[][] getPattern() {
		return pattern;
	}
}
