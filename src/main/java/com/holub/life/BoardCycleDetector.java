package com.holub.life;
import java.util.BitSet;
import java.util.Map;
import java.util.HashMap;

public class BoardCycleDetector {
	private int cycle;
	private int width;
	private int pivot, MaxLen, current;
	private LifeMediator mediator;
	private LifeFacade facade;
	private Map<Integer, BitSet> boardHash;
	
	
	BoardCycleDetector(LifeMediator _mediator){
		mediator = _mediator;
		facade = _mediator.facade();
		facade.addListener(this::update);
		boardHash = new HashMap<Integer, BitSet>();
		
		pivot = 0; 
		current = 0;
		MaxLen = 100;
		cycle = -1;
		createBitSet(true);
	}
	
	public boolean setMaxLen(int val) {
		if(val<10 || val>1000) {
			System.out.println("유효하지 않은 범위입니다.");
			return false;
		}
		MaxLen = val;
		return true;
	}
	
	public int getCycle() {
		return cycle;
	}
	
	public void update(LifeFacade.Event event){
		if(event.change() == LifeFacade.Change.TICK) {
			if(cycle == -1) {
				createBitSet(false);
				analyzeRecord();
			}
		}
		else {
			reInitialize();
		}
	}
	
	public void reInitialize() {
		boardHash.clear();
		pivot=0;
		current=0;
		cycle=-1;
		createBitSet(true);
		
	}
	
	/*
	 * analyzeRecord
	 * 그동안 기록된 것 중 같은 상태가 있는지 확인.
	 * BitSet의 equal() 활용.
	 */
	private void analyzeRecord() {
		boolean isequal = false;
		
		for(int i=pivot; i<current;i++) {
			isequal=boardHash.get(i).equals(boardHash.get(current));
		
			if(isequal) {
				cycle = current - i;
				return;
			}
		}
	}
	
	/*
	 * createBitSet
	 * 현재 board 상태를 해싱함.
	 * BitSet을 사용하여 해싱.
	 * 
	 */
	private void createBitSet(boolean init) {
		if (!init) {
			while(current - pivot + 1 >= MaxLen) {
				boardHash.remove(pivot);
				pivot ++;
				}
			current ++;
		}
		
		BitSet curr = new BitSet();
		width = facade.width();
		facade.visitCells((x, y, alive) ->{
			if(alive == true) {
				curr.set(y * width + x);
			}
		});
		
		boardHash.put(current, curr);
	}
}
