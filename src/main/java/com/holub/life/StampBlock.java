package com.holub.life;

import java.awt.Point;
import java.util.List;

public class StampBlock extends Stamp {

	StampBlock(LifeMediator Mediator){
		facade = Mediator.facade();
	}
	@Override
	public Pattern createPattern() {
		return new Pattern(List.of(
				new Point(0, 0),
			    new Point(0, 1),
			    new Point(1, 0),
			    new Point(1, 1)
			));
	}

}
