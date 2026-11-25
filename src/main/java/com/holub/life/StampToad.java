package com.holub.life;

import java.awt.Point;
import java.util.List;

public class StampToad extends Stamp {

	StampToad(LifeMediator Mediator){
		facade = Mediator.facade();
	}
	@Override
	public Pattern createPattern() {
		return new Pattern(List.of(
				new Point(1, 0),
				new Point(2, 0),
				new Point(3, 0),
				new Point(0, 1),
				new Point(1, 1),
				new Point(2, 1)
			));
	}

}
