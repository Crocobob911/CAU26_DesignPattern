package com.holub.life;

import java.awt.Point;
import java.util.Iterator;

public abstract class Stamp {
	
	private static Stamp current;
	
	private Pattern pattern;
	private Point pivot;
	protected LifeFacade facade;
	
	public abstract Pattern createPattern();
	
	
	public void stamp(UniverseModel board, int _x, int _y) {
		pattern = createPattern();
		changeMultipleCell(board, _x, _y);
	}
	
	public static void setCurr(Stamp target) {
		current = target;
	}
	
	public static Stamp getCurr() {
		return current;
	}
	
	private void changeMultipleCell(UniverseModel board, int _x, int _y) {
		// While the clock runs, saving is already off for the whole run; keep it that way after stamping.
		boolean wasIgnoringSave = HistoryManager.isIgnoringSave();
		HistoryManager.saveState(board);
		HistoryManager.setIgnoreSave(true);
		
		try {
			int leftTopX = _x-pattern.width()/2;
			int leftTopY = _y-pattern.height()/2;
			int currX = -1, currY = -1;
			Iterator<Point> iter = pattern.getIterator();
			while(iter.hasNext()) {
				Point point = iter.next();
				currX = leftTopX + point.x;
				currY = leftTopY + point.y;
				
				if(currX >= 0 && currX < facade.width() 
						&& currY >= 0 && currY < facade.width()) {
					facade.setAlive(currX, currY, true);
				}
			}
		}
		finally {
		HistoryManager.setIgnoreSave(wasIgnoringSave);
		}
	}
}
