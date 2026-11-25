package com.holub.life;
import java.util.Map;
import java.util.HashMap;

import java.io.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;


public class Pattern {
	
	private int width;
	private int height;
	//private boolean[][] pattern;
	private List<Point> pattern;
	
	/*
	Pattern(String name, boolean[][] _pattern){
	 
		width = _pattern[0].length;
		height = _pattern.length;
		
		for(boolean[] w_list:_pattern) {
			if(w_list.length != width) {
				if(w_list.length>width) width = w_list.length;
				System.out.println("크기가 맞지 않습니다. maximum을 기준으로 하여 다시 설정합니다.");
			}
		}
		
		pattern = new boolean[height][width];
		
		for(int i = 0; i < _pattern.length; i++) {
			for(int q=0;q < _pattern[i].length; q++) 
				pattern[i][q] = _pattern[i][q];
		}//deep copy
		
		registry.put(name, this);
	}*/
	
	// 2차원 배열 설계에서 
	// List - iterator base 설계로 변경
	Pattern(List<Point> _pattern){
		pattern = _pattern;
		
		for(Point point : _pattern) {
			if(point.y>=height) height = point.y+1;
			if(point.x>=width) width = point.x+1;
		}
		
		//pattern = new boolean[height][width];
		//for(Point point : _pattern) pattern[point.y][point.x] = true;
		//2차원 배열 기반 코드
	}
	
	// TestToMementoAdapter 수정 이후, W와 H는 찾아오는게 아니라 받아온다.
	Pattern(TextToMementoAdapter loaded){
		pattern = loaded.pointList();
		width = loaded.getWidth();
		height = loaded.getHeight();
	}
	
	public Iterator<Point> getIterator(){
		return pattern.iterator();
	}
	
	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}
	
	/*
	public boolean getValue(int x, int y) {
		if(x>=width || x < 0 || y < 0 || y >= height) {
			System.out.println("유효하지 않은 index입니다.");
			return false;
		}
		return pattern[y][x];
	}
	*/
//	
//	public static Pattern get(String _name){
//		return registry.get(_name);
//	}
//	
	/*
	public void createPattern(UniverseModel board, int _x, int _y) {
		int startX = _x - width/2, startY = _y - height/2;
		int currX = 0, currY = 0;
		
		for(int y = 0; y<height;y++) {
			for (int x = 0; x<width; x++) {
				currX = startX + x; currY = startY+y;
				if(currX>=0 && currX<facade.width() 
						&& currY>=0 && currY<facade.width())
					facade.setAlive(x, y, pattern[y][x]);
			}
		}
		
		
	}
	*/

}
