package com.holub.life;

import java.awt.Point;
import java.util.List;

public class StampBlink extends Stamp {

	StampBlink(LifeMediator Mediator){
		facade = Mediator.facade();
	}
	@Override
	public Pattern createPattern() {
		return new Pattern(List.of(
				new Point(1, 0),
			    new Point(1, 1),
			    new Point(1, 2)
			));
	}

}
