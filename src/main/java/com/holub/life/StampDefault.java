package com.holub.life;

public class StampDefault extends Stamp {

	LifeMediator mediator;
	
	StampDefault(LifeMediator _mediator){
		mediator = _mediator;
	}
	
	@Override
	public Pattern createPattern() {
		return null;
	}
	
	@Override
	public void stamp(UniverseModel board, int _x, int _y) {
		mediator.toggle(_x, _y);
	}

}
