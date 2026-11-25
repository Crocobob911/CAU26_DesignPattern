package com.holub.life;

public class BoardStatistic {
	private long generation = 0;
	private int aliveCount = 0;
	private LifeMediator mediator;
	
	
	BoardStatistic(LifeMediator _mediator){
		mediator = _mediator;
		mediator.facade().addListener(this::update);
		generation = mediator.facade().generation();
		updateAlive();
	}
	
	public int getAlive() {
		return aliveCount;
	}
	
	public long getGeneration() {
		return generation;
	}
	
	void updateAlive(){
		aliveCount = 0;
		mediator.facade().visitCells((x, y, alive) ->{
			if(alive) aliveCount++;
		});
	}
	
	void update(LifeFacade.Event event) {
		generation = event.generation();
		updateAlive();
	}
	
}
