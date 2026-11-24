package com.holub.life;

import java.awt.Point;
import java.util.List;

public class StampLWSS extends Stamp {

	StampLWSS(LifeMediator Mediator){
		facade = Mediator.facade();
	}
	@Override
	public Pattern createPattern() {
		return new Pattern(List.of(
				new Point(0, 1),
			    new Point(0, 4),
			    new Point(1, 0),
			    new Point(2, 0),
			    new Point(2, 4),
			    new Point(3, 0),
			    new Point(3, 1),
			    new Point(3, 2),
			    new Point(3, 3)
			));
	}

}
